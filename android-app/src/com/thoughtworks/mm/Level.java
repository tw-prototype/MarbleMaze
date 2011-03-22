package com.thoughtworks.mm;

import java.io.IOException;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.LevelLoader.IEntityLoader;
import org.anddev.andengine.level.util.constants.LevelConstants;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

import android.widget.Toast;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Level {

	static final String TAG_ENTITY = "entity";
	static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	static final String TAG_ENTITY_ATTRIBUTE_WIDTH = "width";
	static final String TAG_ENTITY_ATTRIBUTE_HEIGHT = "height";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";
	
	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mHoleTextureRegion;
	
	final MarbleMazeActivity maze;
	public Level(MarbleMazeActivity maze) {
		   this.maze = maze;
	        Texture mTexture1 = new Texture(128, 128,
	                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		this.mBoxFaceTextureRegion = TextureRegionFactory.createTiledFromAsset(
				maze.getmTexture(), maze, "box.png", 0, 0, 2, 1); // 64x32
	    this.mHoleTextureRegion = TextureRegionFactory.createTiledFromAsset(mTexture1, maze, "hole.png", 32, 32, 1, 1); 
	    maze.getEngine().getTextureManager().loadTexture(mTexture1);
	}

	void createMaze(final Scene scene) {
		final LevelLoader levelLoader = new LevelLoader();
		levelLoader.setAssetBasePath("level/");

		levelLoader.registerEntityLoader(LevelConstants.TAG_LEVEL,
				new IEntityLoader() {
					public void onLoadEntity(final String pEntityName,
							final Attributes pAttributes) {
						final int width = SAXUtils.getIntAttributeOrThrow(
								pAttributes,
								LevelConstants.TAG_LEVEL_ATTRIBUTE_WIDTH);
						final int height = SAXUtils.getIntAttributeOrThrow(
								pAttributes,
								LevelConstants.TAG_LEVEL_ATTRIBUTE_HEIGHT);
						Toast.makeText(
								maze,
								"Loaded level with width=" + width
										+ " and height=" + height + ".",
								Toast.LENGTH_LONG).show();
					}
				});

		levelLoader.registerEntityLoader(TAG_ENTITY, new IEntityLoader() {
			public void onLoadEntity(final String pEntityName,
					final Attributes pAttributes) {
				final int x = SAXUtils.getIntAttributeOrThrow(pAttributes,
						TAG_ENTITY_ATTRIBUTE_X);
				final int y = SAXUtils.getIntAttributeOrThrow(pAttributes,
						TAG_ENTITY_ATTRIBUTE_Y);
				final int width = SAXUtils.getIntAttributeOrThrow(pAttributes,
						TAG_ENTITY_ATTRIBUTE_WIDTH);
				final int height = SAXUtils.getIntAttributeOrThrow(pAttributes,
						TAG_ENTITY_ATTRIBUTE_HEIGHT);
				final String type = SAXUtils.getAttributeOrThrow(pAttributes, TAG_ENTITY_ATTRIBUTE_TYPE);
				addObject(scene, x, y, width, height,type);
			}
		});

		try {
			levelLoader.loadLevelFromAsset(maze, "example.lvl");
		} catch (final IOException e) {
			Debug.e(e);
		}
	}

	void addObject(final Scene pScene, final float pX, final float pY,
			final int pWidth, final int pHeight, String type) {
		final AnimatedSprite face;

		if(type.equals("box")) {
			face = new AnimatedSprite(pX, pY, pWidth, pHeight,
					this.mBoxFaceTextureRegion);
		} else {
			face = new AnimatedSprite(pX, pY, pWidth, pHeight,
					this.mHoleTextureRegion);
		}
		


		// face.animate(200);
		final Body body;

		body = PhysicsFactory.createBoxBody(maze.getmPhysicsWorld(), face,
				BodyType.StaticBody, MarbleMazeActivity.WALL_FIXTURE_DEF);
		// face.animate(200);

		maze.getmPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(face,
				body, true, true));

		pScene.getLastChild().attachChild(face);
	}

}
