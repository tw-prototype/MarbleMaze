package com.thoughtworks.mm.test;

import com.jayway.android.robotium.solo.Solo;
import com.thoughtworks.mm.MarbleMaze;

import android.test.ActivityInstrumentationTestCase2;

public class MarbleMazeTest extends
		ActivityInstrumentationTestCase2<MarbleMaze> {

	private Solo solo;

	public MarbleMazeTest(Class<MarbleMaze> activityClass) {
		super("com.thoughtworks.mm", activityClass);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
	}

	@Override
	protected void tearDown() throws Exception {
		try {
			solo.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		getActivity().finish();
		super.tearDown();
	}

}
