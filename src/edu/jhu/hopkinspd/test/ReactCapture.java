package edu.jhu.hopkinspd.test;

import java.io.DataOutputStream;
import java.util.Date;

import edu.jhu.hopkinspd.GlobalApp;
import edu.jhu.hopkinspd.test.conf.TestConfig;
import android.os.CountDownTimer;
import android.view.MotionEvent;

public class ReactCapture
{
	public static final int OUTPUT_FORMAT = GlobalApp.OUTPUT_FORMAT_TXT;
	public static final String OUTPUT_EXT = GlobalApp.TXT_DATA_EXTENSION;

	private static final String CAPTURE_FILETYPE = "react";

	public static final int CAPTURE_BUFFER_LENGTH = 100;
	public static final int CAPTURE_BUFFER_ENTRIES = 5;

	public boolean isRecording = false;
	private int bufferItems = 0;
//	private int testNumber = 0;
	private TestConfig testConf;
	private GlobalApp app;
	private DataOutputStream testStreamFile = null;
	private CountDownTimer taskTimer = null;
	
	public ReactCapture(GlobalApp app, TestConfig testConf)
    {
		this.app = app;
		bufferItems = 0;
		this.testConf = testConf;
		app.allocateStreamBuffer(CAPTURE_BUFFER_LENGTH, CAPTURE_BUFFER_ENTRIES);
    }
    
    public void destroy()
    {
    }

    public void startRecording()
    {
		Date time = new Date();
		String filename = app.getTestDataFilename(time, testConf.test_name, 
				CAPTURE_FILETYPE, OUTPUT_EXT);
		testStreamFile = app.openTestStreamFile(filename);
    	bufferItems = 0;
    	isRecording = true;
    }
    
    public void stopRecording()
    {
    	isRecording = false;

    	// Write out remainder of buffer if anything left
    	if (bufferItems > 0)
    	{
        	app.writeTestStreamFrames(testStreamFile, bufferItems, OUTPUT_FORMAT);
    	}
    	app.closeTestStreamFile(testStreamFile);
    }

	public boolean handleTouchEvent(MotionEvent me, boolean buttonStatus)
	{
		if (isRecording)
		{
			double buttonVisible = 0.0d;
			double X = Double.NaN, Y = Double.NaN, buttonPressed = Double.NaN;
			double time = (double)(System.currentTimeMillis())/1000.0d;
			if (buttonStatus)
			{
				buttonVisible = 1.0d;
			}
			if (me != null)
			{
				buttonPressed = 0.0d;
				if (me.getAction() == MotionEvent.ACTION_DOWN || me.getAction() == MotionEvent.ACTION_MOVE)
				{
					buttonPressed = 1.0d;
				}
				X = me.getX();
				Y = me.getY();
//				time = (double)(me.getEventTime())/1000.0d;
			}
			GlobalApp.streamBuffer[bufferItems][0] = time;
			GlobalApp.streamBuffer[bufferItems][1] = X;
			GlobalApp.streamBuffer[bufferItems][2] = Y;
			GlobalApp.streamBuffer[bufferItems][3] = buttonVisible;
			GlobalApp.streamBuffer[bufferItems][4] = buttonPressed;
			
			bufferItems ++;
			if (bufferItems == CAPTURE_BUFFER_LENGTH)
			{
				app.writeTestStreamFrames(testStreamFile, bufferItems, OUTPUT_FORMAT);
				bufferItems = 0;
			}
		}
		return true;
    }

}
