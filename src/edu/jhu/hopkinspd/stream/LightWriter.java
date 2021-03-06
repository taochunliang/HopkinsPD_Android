/*
 * Copyright (c) 2015 Johns Hopkins University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the
 *   distribution.
 * - Neither the name of the copyright holder nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.jhu.hopkinspd.stream;

import java.io.*;
import java.util.Date;

import edu.jhu.hopkinspd.GlobalApp;
import edu.jhu.hopkinspd.R;

import android.content.*;
import android.hardware.*;
import android.util.Log;

public class LightWriter extends StreamWriter
{
	private static final String STREAM_NAME = "light";

	private static final int SENSOR_TYPE = Sensor.TYPE_LIGHT;
	private int SENSOR_RATE;

	private static final String TAG = GlobalApp.TAG + "|" + STREAM_NAME;
	
	private SensorManager sensorManager = null;
	private Sensor sensor = null;
	private DataOutputStream sensorStream = null;
		
    public LightWriter(GlobalApp app)
    {
    	super(app);
		
		SENSOR_RATE = Integer.parseInt(getStringPref(app.getResources().getString(R.string.sensorRate)));
    	sensorManager = (SensorManager)app.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(SENSOR_TYPE);
        Log.i(TAG, "max light value " + sensor.getMaximumRange());
        logTextStream = app.openLogTextFile(STREAM_NAME);
	    writeLogTextLine("Created " + this.getClass().getName() + " instance");
    }

    public void init()
    {
        sensorManager.registerListener(this, sensor, SENSOR_RATE);
        Log.v(TAG,"lightWriter initialized");
    }

    public void destroy()
    {
    	sensorManager.unregisterListener(this);
    	sensorManager = null;
    	sensor = null;
    }
    
    public void start(Date startTime)
    {
//	    prevSecs = (double)System.currentTimeMillis()/1000.0d;
	    prevSecs = ((double)startTime.getTime())/1000.0d;
	    String timeStamp = timeString(startTime);
	    sensorStream = openStreamFile(STREAM_NAME, timeStamp, feature_stream_extension);
    	
    	isRecording = true;
	    writeLogTextLine("Light recording started");
    }

    public void stop(Date stopTime)
    {
    	isRecording = false;
    	if (closeStreamFile(sensorStream))
    	{
		    writeLogTextLine("Light recording successfully stopped");
		}
    }

    public void restart(Date time)
    {
    	DataOutputStream oldStream = sensorStream;
    	String timeStamp = timeString(time);
    	sensorStream = openStreamFile(STREAM_NAME, timeStamp, feature_stream_extension);
	    prevSecs = ((double)time.getTime())/1000.0d;
    	if (closeStreamFile(oldStream))
    	{
		    writeLogTextLine("Light recording successfully restarted");
    	}
    }
    
	@Override
	public void onSensorChanged(SensorEvent event)
	{
//		Log.v(TAG, "receive light event");
		if ((sensorStream != null) && isRecording)
		{
//	    	double currentSecs = ((double)event.timestamp)/1000000000.0d;
	    	double currentSecs = (double)System.currentTimeMillis()/1000.0d;
        	double diffSecs = currentSecs - prevSecs;
        	prevSecs = currentSecs;

        	double[] proxData = new double[2];
        	proxData[0] = diffSecs;
        	proxData[1] = event.values[0];
//        	Log.v(TAG, "receive light event " + event.values[0]);
        	writeFeatureFrame(proxData, sensorStream, dataOutputFormat);
		}
	}
    
    public String toString(){
    	return STREAM_NAME;
    }
}
