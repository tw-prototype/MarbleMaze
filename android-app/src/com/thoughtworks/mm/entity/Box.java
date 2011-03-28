package com.thoughtworks.mm.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.thoughtworks.mm.MarbleMazeActivity;

public class Box extends AnimatedSprite {

	public Box(float x, float y, float width, float height,
			MarbleMazeActivity marbleMazeActivity) {
		super(x, y, width, height, TextureRegionFactory.createTiledFromAsset(
				marbleMazeActivity.getTexture(), marbleMazeActivity, "box.png",
				0, 0, 2, 1));
		final Body body = PhysicsFactory.createBoxBody(marbleMazeActivity
				.getmPhysicsWorld(), this, BodyType.StaticBody,
				MarbleMazeActivity.WALL_FIXTURE_DEF);
		marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(
				new PhysicsConnector(this, body, true, true));

	}
}
