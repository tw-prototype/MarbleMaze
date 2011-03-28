package com.thoughtworks.mm.level;

import java.io.IOException;

import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.LevelLoader.IEntityLoader;
import org.anddev.andengine.level.util.constants.LevelConstants;
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

	public void loadLevel(int level) {
		final LevelLoader levelLoader = new LevelLoader();
		levelLoader.setAssetBasePath("level/");
		final EntityBuilder builder = new EntityBuilder(marbleMazeActivity,gameDecisionEngine);	
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
				builder.setInitialCoordinates(x, y).setDimensions(width, height).type(type).build();
			}
		});

		try {
			levelLoader.loadLevelFromAsset(marbleMazeActivity, level+".lvl");
		} catch (final IOException e) {
			Debug.e(e);
		}

		gameDecisionEngine.registerActivity(marbleMazeActivity);

	}

}
