package com.thoughtworks.mm.level;

import java.io.IOException;

import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.LevelLoader.IEntityLoader;
import org.anddev.andengine.level.util.constants.LevelConstants;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

import android.widget.Toast;

import com.thoughtworks.mm.MarbleMazeActivity;
import com.thoughtworks.mm.entity.EntityBuilder;

public class Level {

	static final String TAG_ENTITY = "entity";
	static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	static final String TAG_ENTITY_ATTRIBUTE_WIDTH = "width";
	static final String TAG_ENTITY_ATTRIBUTE_HEIGHT = "height";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";


	final MarbleMazeActivity marbleMazeActivity;
	private final GameDecisionEngine gameDecisionEngine = new GameDecisionEngine();

	public Level(MarbleMazeActivity maze) {
		this.marbleMazeActivity = maze;


	}

	public void createMaze(final Scene scene) {
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
								marbleMazeActivity,
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
				final String type = SAXUtils.getAttributeOrThrow(pAttributes,
						TAG_ENTITY_ATTRIBUTE_TYPE);
				addObject(scene, x, y, width, height, type);
			}
		});

		try {
			levelLoader.loadLevelFromAsset(marbleMazeActivity, "1.lvl");
		} catch (final IOException e) {
			Debug.e(e);
		}

		gameDecisionEngine.registerActivity(marbleMazeActivity);

	}

	void addObject(final Scene pScene, final float x, final float y,
			final int width, final int height, String type) {
		EntityBuilder builder = new EntityBuilder(marbleMazeActivity,gameDecisionEngine);		
		builder.setInitialCoordinates(x, y);
		builder.setDimensions(width, height);
		builder.type(type);
		builder.build();		
	}

}
