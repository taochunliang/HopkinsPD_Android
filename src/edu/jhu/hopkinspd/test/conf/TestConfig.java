package edu.jhu.hopkinspd.test.conf;

import java.io.BufferedWriter;
import java.util.ArrayList;

import android.view.MotionEvent;
import edu.jhu.hopkinspd.GlobalApp;
import edu.jhu.hopkinspd.R;
import edu.jhu.hopkinspd.test.TestActivity;
import edu.jhu.hopkinspd.test.TestPrepActivity;

public abstract class TestConfig {

	public static final int[] ALL_TEST_NAMES = {
			R.string.test_voice,
			R.string.test_balance,
			R.string.test_gait,
			R.string.test_dexterity,
			R.string.test_dexterity_left,
			R.string.test_dexterity_right,
			R.string.test_reaction,
			R.string.test_rest_tremor,
			R.string.test_rest_tremor_left,
			R.string.test_rest_tremor_right,
			R.string.test_postural_tremor,
			R.string.test_postural_tremor_left,
			R.string.test_postural_tremor_right,
	};
	
	public static final TestConfig[] ALL_TESTS = {
			new VoiceTestConfig(),
			new BalanceTestConfig(),
			new GaitTestConfig(),
			new TapTestConfig(),
			new TapTestConfig("left"),
			new TapTestConfig("right"),
			new ReactionTestConfig(),
			new RestTremorTestConfig(),
			new RestTremorTestConfig("left"),
			new RestTremorTestConfig("right"),
			new PosturalTremorTestConfig(),
			new PosturalTremorTestConfig("left"),
			new PosturalTremorTestConfig("right")
	};
	
	public int test_name;
	public int pre_test_text, pre_icon;
	public int test_text, test_view;
	public int preTestPauseDur = 2;
	public int postTestPauseDur = 0;
	public int testCaptureDur = 20;
	public boolean preTestVibrate = false;
	public boolean postTestVibrate = false;
	public int pre_test_layout = R.layout.testpreppage;
	public String help_link;
	
	private static ArrayList<TestConfig> enabled_tests = null;
	public static boolean gyro_on = false; 
	
	public static ArrayList<TestConfig> getEnabledTests(){
		if(enabled_tests == null){
			GlobalApp app = GlobalApp.getApp();
			enabled_tests = new ArrayList<TestConfig>();
			for(int i = 0; i < ALL_TEST_NAMES.length; i++){
				int test = ALL_TEST_NAMES[i];
				if(app.getBooleanPref(app.getString(test))){
					enabled_tests.add(ALL_TESTS[i]);
				}
			}
			gyro_on = app.getBooleanPref(app.getString(R.string.test_gait));
		}
		return enabled_tests;
	}
	
	public static int getNumberOfTests() {
		ArrayList<TestConfig> enabled_tests = getEnabledTests(); 
		return enabled_tests.size();
	}
	
	public static TestConfig getTestConfig(int testNumber){
		ArrayList<TestConfig> enabled_tests = getEnabledTests();
		return enabled_tests.get(testNumber);
	}
	
	
	public abstract void runTest(TestActivity activity, BufferedWriter logWriter);
	public abstract void completeTest();
	public abstract void cancelTest();

	public void createTest(TestActivity activity){}

	public void dispatchTouchEvent(MotionEvent me) {}

	public void onInTestTimerTick(TestActivity activity) {}

    public void createPreTest(TestPrepActivity testPrepActivity) {}
	
	
}
