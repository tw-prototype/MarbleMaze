package com.thoughtworks.mm.entity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.thoughtworks.mm.MarbleMazeActivity;

import android.app.Activity;
import android.hardware.SensorManager;

@PrepareForTest({ Activity.class })
@RunWith(PowerMockRunner.class)
public class BallTest  {
	@Mock
	private MarbleMazeActivity marbleMazeActivity;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldBeAbleToCreateABallWithValidValues() {
		Ball ball= new Ball(Mockito.anyFloat(), Mockito.anyFloat(), marbleMazeActivity);
		
	}

}
