package com.thoughtworks.mm.entity;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.thoughtworks.mm.MarbleMazeActivity;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;


public class Pocket extends AnimatedSprite {

	public static final short CATEGORYBIT_POCKET = 2;
	public static final short MASKBITS_POCKET = 0; // Missing: everything
	public static final FixtureDef POCKET_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(0, 0.0f, Float.POSITIVE_INFINITY, false,
					CATEGORYBIT_POCKET, MASKBITS_POCKET, (short) 0);

	private final static Texture mTexture = new Texture(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

	public Pocket(float pX, float pY, MarbleMazeActivity marbleMazeActivity) {
		super(pX, pY, TextureRegionFactory.createTiledFromAsset(mTexture, marbleMazeActivity, "pocket.png", 32, 32, 1, 1));

		Body body = PhysicsFactory.createCircleBody(marbleMazeActivity
				.getmPhysicsWorld(), this, BodyType.StaticBody, POCKET_FIXTURE_DEF);

		marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(this, body, true, true));
		marbleMazeActivity.getEngine().getTextureManager().loadTexture(mTexture);

	}

}
