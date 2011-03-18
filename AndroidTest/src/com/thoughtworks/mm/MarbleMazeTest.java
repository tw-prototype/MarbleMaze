package com.thoughtworks.mm;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import android.app.Activity;
@PrepareForTest({ Activity.class })
@RunWith(PowerMockRunner.class)
public class MarbleMazeTest {
	
	@Test
	public void shouldPass() {
		assertTrue(true);
		assertNotNull(new MarbleMaze());
	}
}
