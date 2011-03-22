package com.thoughtworks.mm;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;
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
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.sensor.accelerometer.AccelerometerData;
import org.anddev.andengine.sensor.accelerometer.IAccelerometerListener;

import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class MarbleMaze extends BaseExample implements IAccelerometerListener {

    public static final FixtureDef WALL_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f, false, CATEGORYBIT_WALL,
        MASKBITS_WALL, (short) 0);
    public static final FixtureDef CIRCLE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f, false, CATEGORYBIT_CIRCLE,
        MASKBITS_CIRCLE, (short) 0);
    public static final FixtureDef HOLE_FIXTURE_DEF = PhysicsFactory.createFixtureDef(0, 0.0f, Float.POSITIVE_INFINITY, false,
        CATEGORYBIT_HOLE, MASKBITS_HOLE, (short) 0);


    private TiledTextureRegion mCircleFaceTextureRegion;

    private PhysicsWorld mPhysicsWorld;

    public PhysicsWorld getmPhysicsWorld() {
		return mPhysicsWorld;
	}

	public void setmPhysicsWorld(PhysicsWorld mPhysicsWorld) {
		this.mPhysicsWorld = mPhysicsWorld;
	}

	private TiledTextureRegion mHoleTextureRegion;

    private AnimatedSprite ball;

    private TextureRegion mParallaxLayerBack;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(MarbleMaze.this, "You win!", Toast.LENGTH_SHORT).show();
        }
    };
	private Texture mTexture;

    public Texture getmTexture() {
		return mTexture;
	}

	public Engine onLoadEngine() {
        final Camera camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        final EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy( CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        engineOptions.getTouchOptions().setRunOnUpdateThread(true);
        return new Engine(engineOptions);
    }

    public void onLoadResources() {
        mTexture = new Texture(64, 64,
            TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        TextureRegionFactory.setAssetBasePath("gfx/");
        Texture mTexture1 = new Texture(128, 128,
            TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        this.mCircleFaceTextureRegion = TextureRegionFactory
            .createTiledFromAsset(mTexture, this, "ball.png", 0, 32, 1, 1); // 32x32
        this.mHoleTextureRegion = TextureRegionFactory.createTiledFromAsset(mTexture1, this, "hole.png", 32, 32, 1, 1); // 64x32
        this.mEngine.getTextureManager().loadTexture(mTexture);
        this.mEngine.getTextureManager().loadTexture(mTexture1);
        this.enableAccelerometerSensor(this);

        /* TextureRegions. */
  
        Texture mBackgroundTexture = new Texture(1024, 1024, TextureOptions.DEFAULT);
        this.mParallaxLayerBack = TextureRegionFactory.createFromAsset( mBackgroundTexture, this, "background.png", 0, 188);
        this.mEngine.getTextureManager().loadTextures(mTexture, mBackgroundTexture);

    }

    public Scene onLoadScene() {
        this.mEngine.registerUpdateHandler(new FPSLogger());

        final Scene scene = new Scene(2);
        // scene.setBackground(new ColorBackground(0, 0, 0));

        final AutoParallaxBackground autoParallaxBackground = new AutoParallaxBackground( 0, 0, 0, 5);
        autoParallaxBackground.attachParallaxEntity(new ParallaxEntity(0.0f,
            new Sprite(0, CAMERA_HEIGHT - this.mParallaxLayerBack.getHeight(), this.mParallaxLayerBack)));
        scene.setBackground(autoParallaxBackground);
        // scene.setOnSceneTouchListener(this);

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_DEATH_STAR_I), false);

        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
        final Shape hole = new AnimatedSprite(2, 2, this.mHoleTextureRegion);

        PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, WALL_FIXTURE_DEF);
        PhysicsFactory.createCircleBody(this.mPhysicsWorld, hole, BodyType.StaticBody, HOLE_FIXTURE_DEF);

        scene.getFirstChild().attachChild(ground);
        scene.getFirstChild().attachChild(roof);
        scene.getFirstChild().attachChild(left);
        scene.getFirstChild().attachChild(right);
        scene.getFirstChild().attachChild(hole);
        scene.registerUpdateHandler(this.mPhysicsWorld);

        scene.registerUpdateHandler(new TimerHandler(0.5f, true,
            new ITimerCallback() {
                public void onTimePassed(final TimerHandler pTimerHandler) {
                    if (hole.contains(ball.getX(), ball.getY())) {
                        handler.sendEmptyMessage(0);
                    }
                }
            }));

        new Level(this).createMaze(scene);
//        initJoints(scene);
        return scene;
    }

    

       public void onLoadComplete() {
        this.addFace(360, 240);
    }

    public void onAccelerometerChanged(
        final AccelerometerData pAccelerometerData) {
        final Vector2 gravity = Vector2Pool.obtain(pAccelerometerData.getY(), pAccelerometerData.getX());
        this.mPhysicsWorld.setGravity(gravity);
        Vector2Pool.recycle(gravity);
    }

    private void addFace(final float pX, final float pY) {
        final Scene scene = this.mEngine.getScene();
        final Body body;

        ball = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion);
        body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, ball, BodyType.DynamicBody, CIRCLE_FIXTURE_DEF);
        // face.animate(200);

        scene.getLastChild().attachChild(ball);
        this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(ball, body, true, true));
    }

//    private void initJoints(final Scene pScene) {
//		final int centerX = CAMERA_WIDTH / 2;
//		final int centerY = CAMERA_HEIGHT / 2;
//
//		final int spriteWidth = this.mBoxFaceTextureRegion.getTileWidth();
//		final int spriteHeight = this.mBoxFaceTextureRegion.getTileHeight();
//
//		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(10, 0.2f, 0.5f);
//
//		for(int i = 0; i < 1; i++) {
//			final float anchorFaceX = centerX - spriteWidth * 0.5f + 220 * (i - 1);
//			final float anchorFaceY = centerY - spriteHeight * 0.5f;
//
//			final AnimatedSprite anchorFace = new AnimatedSprite(anchorFaceX, anchorFaceY, this.mBoxFaceTextureRegion);
//			final Body anchorBody = PhysicsFactory.createBoxBody(this.mPhysicsWorld, anchorFace, BodyType.StaticBody, objectFixtureDef);
//
//			final AnimatedSprite movingFace = new AnimatedSprite(anchorFaceX, anchorFaceY + 90, this.mCircleFaceTextureRegion);
//			final Body movingBody = PhysicsFactory.createCircleBody(this.mPhysicsWorld, movingFace, BodyType.DynamicBody, objectFixtureDef);
//
////			anchorFace.animate(200);
////			anchorFace.animate(200);
//
//			pScene.getLastChild().attachChild(anchorFace);
//			pScene.getLastChild().attachChild(movingFace);
//
////			final Line connectionLine = new Line(anchorFaceX + spriteWidth / 2, anchorFaceY + spriteHeight / 2, anchorFaceX + spriteWidth / 2, anchorFaceY + spriteHeight / 2);
//			final Line connectionLine = new Line(100, 240, 99, 239);
//			pScene.getFirstChild().attachChild(connectionLine);
//			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(anchorFace, anchorBody, true, true){
//				@Override
//				public void onUpdate(final float pSecondsElapsed) {
//					super.onUpdate(pSecondsElapsed);
//					final Vector2 movingBodyWorldCenter = movingBody.getWorldCenter();
//					connectionLine.setPosition(connectionLine.getX1(), connectionLine.getY1(), movingBodyWorldCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, movingBodyWorldCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
//				}
//			});
//			this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(movingFace, movingBody, true, true));
//
//
//			final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
//			revoluteJointDef.initialize(anchorBody, movingBody, anchorBody.getWorldCenter());
//			revoluteJointDef.enableMotor = true;
//			revoluteJointDef.motorSpeed = 10;
//			revoluteJointDef.maxMotorTorque = 200;
//
//			this.mPhysicsWorld.createJoint(revoluteJointDef);
//		}
//	}

}
