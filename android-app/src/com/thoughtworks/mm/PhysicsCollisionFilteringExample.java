package com.thoughtworks.mm;

import java.io.IOException;

import android.hardware.SensorManager;
import android.widget.Toast;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.level.LevelLoader;
import org.anddev.andengine.level.LevelLoader.IEntityLoader;
import org.anddev.andengine.level.util.constants.LevelConstants;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.util.Debug;
import org.anddev.andengine.util.SAXUtils;
import org.xml.sax.Attributes;

/**
 * @author Nicolas Gramlich
 * @since 14:33:38 - 03.10.2010
 */
public class PhysicsCollisionFilteringExample extends BaseExample implements
		IAccelerometerListener {

	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;

	/* The categories. */
	public static final short CATEGORYBIT_WALL = 1;
	public static final short CATEGORYBIT_BOX = 2;
	public static final short CATEGORYBIT_CIRCLE = 4;
	public static final short CATEGORYBIT_HOLE = 8;

	/* And what should collide with what. */
	public static final short MASKBITS_WALL = CATEGORYBIT_WALL
			+ CATEGORYBIT_BOX + CATEGORYBIT_CIRCLE;
	// CATEGORYBIT_CIRCLE
	public static final short MASKBITS_CIRCLE = CATEGORYBIT_WALL
			+ CATEGORYBIT_CIRCLE; // Missing: CATEGORYBIT_BOX
	public static final short MASKBITS_HOLE = 0; // Missing: everything

	public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL,
					MASKBITS_WALL, (short) 0);
	public static final FixtureDef CIRCLE_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_CIRCLE,
					MASKBITS_CIRCLE, (short) 0);
	public static final FixtureDef HOLE_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(0, 0.0f, Float.POSITIVE_INFINITY, false,
					CATEGORYBIT_HOLE, MASKBITS_HOLE, (short) 0);


    private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mCircleFaceTextureRegion;

	private PhysicsWorld mPhysicsWorld;

	private int mFaceCount = 0;
	private TiledTextureRegion mHoleTextureRegion;

	

	private static final String TAG_ENTITY = "entity";
	private static final String TAG_ENTITY_ATTRIBUTE_X = "x";
	private static final String TAG_ENTITY_ATTRIBUTE_Y = "y";
	private static final String TAG_ENTITY_ATTRIBUTE_WIDTH = "width";
	private static final String TAG_ENTITY_ATTRIBUTE_HEIGHT = "height";
	private static final String TAG_ENTITY_ATTRIBUTE_TYPE = "type";

	private TextureRegion mParallaxLayerBack;

	public Engine onLoadEngine() {
		 final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	public void onLoadResources() {
		/* Textures. */
        Texture mTexture = new Texture(64, 64,
            TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegionFactory.setAssetBasePath("gfx/");
		Texture mTexture1 = new Texture(128, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		 this.mCircleFaceTextureRegion = TextureRegionFactory
				.createTiledFromAsset(mTexture, this,
						"ball.png", 0, 32, 2, 1); // 64x32
		this.mHoleTextureRegion = TextureRegionFactory.createTiledFromAsset(
				mTexture1, this, "hole.png", 0, 0, 2, 1); // 64x32
		this.mEngine.getTextureManager().loadTexture(mTexture);
		this.mEngine.getTextureManager().loadTexture(mTexture1);
		this.enableAccelerometerSensor(this);

		/* TextureRegions. */
		this.mBoxFaceTextureRegion = TextureRegionFactory.createTiledFromAsset(
            mTexture, this, "box.png", 0, 0, 2, 1); // 64x32
		
		
		Texture mBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
		this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(mBackgroundTexture, this, "background.png", 0, 188);
		this.mEngine.getTextureManager().loadTextures(mTexture, mBackgroundTexture);


	}

	public Scene onLoadScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(2);
		// scene.setBackground(new ColorBackground(0, 0, 0));
		
		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f, new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
		scene.setBackground(autoParallaxBackground);
//		scene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_DEATH_STAR_I), false);

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH,
				2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
		final Shape center = new Rectangle(CAMERA_WIDTH / 2, CAMERA_HEIGHT / 2,
				50, CAMERA_WIDTH / 2);
		final Shape hole = new AnimatedSprite(2, 2, this.mHoleTextureRegion);

		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, center,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createCircleBody(this.mPhysicsWorld, hole,
				BodyType.StaticBody, HOLE_FIXTURE_DEF);
		scene.getFirstChild().attachChild(ground);
		scene.getFirstChild().attachChild(roof);
		scene.getFirstChild().attachChild(left);
		scene.getFirstChild().attachChild(right);
		scene.getFirstChild().attachChild(center);
		scene.getFirstChild().attachChild(hole);
		scene.registerUpdateHandler(this.mPhysicsWorld);


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
								PhysicsCollisionFilteringExample.this,
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

				PhysicsCollisionFilteringExample.this.addFace(scene, x, y,
						width, height);
			}
		});

		try {
			levelLoader.loadLevelFromAsset(this, "example.lvl");
		} catch (final IOException e) {
			Debug.e(e);
		}

		return scene;
	}

	private void addFace(final Scene pScene, final float pX, final float pY,
			final int pWidth, final int pHeight) {
		final AnimatedSprite face;

		face = new AnimatedSprite(pX, pY, pWidth, pHeight,
				this.mBoxFaceTextureRegion);

		face.animate(200);

		pScene.getLastChild().attachChild(face);
	}

	public void onLoadComplete() {
		this.addFace(360, 240);
	}

	public void onAccelerometerChanged(
			final AccelerometerData pAccelerometerData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getY(),
				pAccelerometerData.getX());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}


	private void addFace(final float pX, final float pY) {
		final Scene scene = this.mEngine.getScene();


		final AnimatedSprite face;
		final Body body;

		face = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion);
		body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face,
				BodyType.DynamicBody, CIRCLE_FIXTURE_DEF);
//		face.animate(200);

		scene.getLastChild().attachChild(face);
		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face,
				body, true, true));
	}

}
