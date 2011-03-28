package com.thoughtworks.mm.entity;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.thoughtworks.mm.MarbleMazeActivity;

public class Ball extends AnimatedSprite {

	public static final short CATEGORY_BIT_BALL = 4;
    public static final short MASKBITS_BAll = MarbleMazeActivity.CATEGORYBIT_WALL + CATEGORY_BIT_BALL;

	public static final FixtureDef Ball_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORY_BIT_BALL,
					MASKBITS_BAll, (short) 0);

    public Ball(float pX, float pY, MarbleMazeActivity marbleMazeActivity) {
		super(pX, pY, TextureRegionFactory.createTiledFromAsset(
				marbleMazeActivity.getTexture(), marbleMazeActivity, "ball.png", 0, 32, 1, 1)); // 32x32);
		createBodyAndRegisterWithPhysicsWorld(marbleMazeActivity.getmPhysicsWorld());

	}

	public  void createBodyAndRegisterWithPhysicsWorld(PhysicsWorld physicsWorld) {
        Body body = PhysicsFactory.createCircleBody(physicsWorld, this, BodyType.DynamicBody,
            Ball_FIXTURE_DEF);
		PhysicsConnector ballPhysicsConnector = new PhysicsConnector(this, body, true, true);
		physicsWorld.registerPhysicsConnector(ballPhysicsConnector);
	}

	public void remove(MarbleMazeActivity marbleMazeActivity) {
		final Scene scene = marbleMazeActivity.getEngine().getScene();

		PhysicsWorld physicsWorld = marbleMazeActivity.getmPhysicsWorld();
		final PhysicsConnector facePhysicsConnector = physicsWorld.getPhysicsConnectorManager().findPhysicsConnectorByShape(this);

		physicsWorld.unregisterPhysicsConnector(facePhysicsConnector);
		physicsWorld.destroyBody(facePhysicsConnector.getBody());

		scene.getLastChild().detachChild(this);
	}


}
