package com.thoughtworks.mm;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.modules.junit4.PowerMockRunner;

import android.hardware.Sensor;
import android.hardware.SensorManager;
/*@PrepareForTest({ Activity.class })
*/@RunWith(PowerMockRunner.class)
public class MarbleMazeTest {
	
	@Mock
	private SensorManager sensorManager;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldRegisterListenersWhenProcessIsResumed() {
		MarbleMazeSensor marbleMaze = new MarbleMazeSensor(sensorManager);
		marbleMaze.onResume();
		Mockito.verify(sensorManager, Mockito.times(2)).registerListener(Mockito.eq(marbleMaze), Mockito.any(Sensor.class), Mockito.anyInt());
	}
}
