package com.thoughtworks.mm.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.thoughtworks.mm.MarbleMazeActivity;

public class Ball extends AnimatedSprite {

	public static final short CATEGORYBIT_BALL = 4;
    public static final short MASKBITS_BAll = MarbleMazeActivity.CATEGORYBIT_WALL
			+ CATEGORYBIT_BALL;

	public static final FixtureDef Ball_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_BALL,
					MASKBITS_BAll, (short) 0);

	public Ball(float pX, float pY, MarbleMazeActivity marbleMazeActivity) {
		super(pX, pY, TextureRegionFactory.createTiledFromAsset(
				marbleMazeActivity.getmTexture(), marbleMazeActivity,
				"ball.png", 0, 32, 1, 1)); // 32x32);
		Body body = PhysicsFactory.createCircleBody(marbleMazeActivity
				.getmPhysicsWorld(), this, BodyType.DynamicBody,
				Ball_FIXTURE_DEF);

		marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this, body, true, true));

	}
}
