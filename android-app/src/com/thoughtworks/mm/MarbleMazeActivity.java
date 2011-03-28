package com.thoughtworks.mm;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.AutoParallaxBackground;
import org.anddev.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.util.Vector2Pool;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuItem;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.thoughtworks.mm.entity.Ball;
import com.thoughtworks.mm.level.Level;

public class MarbleMazeActivity extends BaseGameActivity implements
		IAccelerometerListener {

	private static final int MENU_TRACE = Menu.FIRST;

	public static final int CAMERA_WIDTH = 640;
	public static final int CAMERA_HEIGHT = 480;

	/* The categories. */
	public static final short CATEGORYBIT_WALL = 1;

	/* And what should collide with what. */
	public static final short MASKBITS_WALL = CATEGORYBIT_WALL
			+ Ball.CATEGORY_BIT_BALL;

	private TextureRegion mParallaxLayerBack;

	private Texture mTexture;

	@Override
	public boolean onCreateOptionsMenu(final Menu pMenu) {
		pMenu.add(Menu.NONE, MENU_TRACE, Menu.NONE, "Start Method Tracing");
		return super.onCreateOptionsMenu(pMenu);
	}

	@Override
	public boolean onPrepareOptionsMenu(final Menu pMenu) {
		pMenu.findItem(MENU_TRACE).setTitle(
				this.mEngine.isMethodTracing() ? "Stop Method Tracing"
						: "Start Method Tracing");
		return super.onPrepareOptionsMenu(pMenu);
	}

	@Override
	public boolean onMenuItemSelected(final int pFeatureId, final MenuItem pItem) {
		switch (pItem.getItemId()) {
		case MENU_TRACE:
			if (this.mEngine.isMethodTracing()) {
				this.mEngine.stopMethodTracing();
			} else {
				this.mEngine.startMethodTracing("AndEngine_"
						+ System.currentTimeMillis() + ".trace");
			}
			return true;
		default:
			return super.onMenuItemSelected(pFeatureId, pItem);
		}
	}

	public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory
			.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL,
					MASKBITS_WALL, (short) 0);

	private PhysicsWorld mPhysicsWorld;

	private IUpdateHandler pUpdateHandler;

	private Scene scene;

	public Scene getScene() {
		return scene;
	}

	public PhysicsWorld getmPhysicsWorld() {
		return mPhysicsWorld;
	}

	public Texture getTexture() {
		return mTexture;
	}

	public Engine onLoadEngine() {
		final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setRunOnUpdateThread(true);
		return new Engine(engineOptions);
	}

	public void onLoadResources() {
		mTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		TextureRegionFactory.setAssetBasePath("gfx/");

		Texture mBackgroundTexture = new Texture(1024, 1024,
				TextureOptions.DEFAULT);
		this.mParallaxLayerBack = TextureRegionFactory.createFromAsset(
				mBackgroundTexture, this, "background.png", 0, 188);
		this.mEngine.getTextureManager().loadTextures(mTexture,
				mBackgroundTexture);

		this.enableAccelerometerSensor(this);

	}

	public Scene onLoadScene() {
		pUpdateHandler = new FPSLogger();
		this.mEngine.registerUpdateHandler(pUpdateHandler);

		if (scene == null)
			scene = new Scene(2);
		// scene.setBackground(new ColorBackground(0, 0, 0));

		final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground(
				0, 0, 0, 5);
		autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f,
				new Sprite(0, CAMERA_HEIGHT
						- this.mParallaxLayerBack.getHeight(),
						this.mParallaxLayerBack)));
		scene.setBackground(autoParallaxBackground);
		// scene.setOnSceneTouchListener(this);

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0,
				SensorManager.GRAVITY_DEATH_STAR_I), false);

		final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH,
				2);
		final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
		final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
		final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left,
				BodyType.StaticBody, WALL_FIXTURE_DEF);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right,
				BodyType.StaticBody, WALL_FIXTURE_DEF);

		scene.getFirstChild().attachChild(roof);
		scene.getFirstChild().attachChild(left);
		scene.getFirstChild().attachChild(right);
		scene.registerUpdateHandler(this.mPhysicsWorld);

		new Level(this).loadLevel(1);
		return scene;
	}

	public void onLoadComplete() {

	}

	public void onAccelerometerChanged(
			final AccelerometerData pAccelerometerData) {
		final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getY(),
				pAccelerometerData.getX());
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

}
