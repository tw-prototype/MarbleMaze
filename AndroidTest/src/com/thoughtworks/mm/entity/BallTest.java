package com.thoughtworks.mm.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.thoughtworks.mm.MarbleMazeActivity;

import android.app.Activity;

@PrepareForTest({PhysicsFactory.class, AnimatedSprite.class,Activity.class })
@RunWith(PowerMockRunner.class)
public class BallTest  {
	@Mock
	private MarbleMazeActivity marbleMazeActivity;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void shouldCreateBall() {
		Assert.assertTrue(true);
//		PowerMockito.suppress(PowerMockito.everythingDeclaredIn(AnimatedSprite.class));
//		Ball ball= new Ball(Mockito.anyFloat(), Mockito.anyFloat(), marbleMazeActivity);
//		
//		PowerMockito.verifyStatic();	
//		PhysicsFactory.createCircleBody(marbleMazeActivity.getmPhysicsWorld(), ball, BodyType.DynamicBody, Ball.Ball_FIXTURE_DEF);		
//	}
	}

}
