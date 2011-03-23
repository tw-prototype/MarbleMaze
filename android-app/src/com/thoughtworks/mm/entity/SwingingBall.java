package com.thoughtworks.mm.entity;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.thoughtworks.mm.MarbleMazeActivity;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class SwingingBall {
    private TiledTextureRegion boxTextureRegion;
    private Texture mTexture;
    private MarbleMazeActivity marbleMazeActivity;
    private TiledTextureRegion swingingBallTextureRegion;


    public SwingingBall(float pX, float pY, MarbleMazeActivity marbleMazeActivity) {
        this.marbleMazeActivity = marbleMazeActivity;
        this.mTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        TextureRegionFactory.setAssetBasePath("gfx/");
        this.boxTextureRegion = TextureRegionFactory.createTiledFromAsset(marbleMazeActivity.getmTexture(), marbleMazeActivity,
            "box.png", 0, 32, 1, 1);
        this.swingingBallTextureRegion = TextureRegionFactory.createTiledFromAsset(marbleMazeActivity.getmTexture(), marbleMazeActivity,
            "swingingBall.png", 0, 32, 1, 1);

    }


    public void initJoints(final Scene pScene) {
        final int centerX = MarbleMazeActivity.CAMERA_WIDTH / 2;

        final int spriteWidth = this.boxTextureRegion.getTileWidth();
        final int spriteHeight = this.boxTextureRegion.getTileHeight();

        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(10, 0.2f, 0.5f);

        for (int i = 0; i < 1; i++) {
            final float anchorFaceX = centerX - spriteWidth * 0.5f + 200;
            final float anchorFaceY =   (MarbleMazeActivity.CAMERA_HEIGHT / 3 - spriteHeight * 0.5f)-30 ;
            System.out.println("anchorFaceX: "+anchorFaceX+",anchorFaceY: "+anchorFaceY);

            final AnimatedSprite anchorFace = new AnimatedSprite(anchorFaceX, anchorFaceY, this.boxTextureRegion);
            final Body anchorBody = PhysicsFactory.createBoxBody(marbleMazeActivity.getmPhysicsWorld(), anchorFace, BodyDef.BodyType.StaticBody, objectFixtureDef);

            final AnimatedSprite movingFace = new AnimatedSprite(anchorFaceX, anchorFaceY + 90, this.swingingBallTextureRegion);
            final Body movingBody = PhysicsFactory.createCircleBody(marbleMazeActivity.getmPhysicsWorld(), movingFace, BodyDef.BodyType.DynamicBody, objectFixtureDef);

            pScene.getLastChild().attachChild(anchorFace);
            pScene.getLastChild().attachChild(movingFace);

            final Line connectionLine = new Line(anchorFaceX + spriteWidth / 2, anchorFaceY + spriteHeight / 2, anchorFaceX + spriteWidth / 2, anchorFaceY + spriteHeight / 2);
            pScene.getFirstChild().attachChild(connectionLine);
            marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(anchorFace, anchorBody, true, true) {
                @Override
                public void onUpdate(final float pSecondsElapsed) {
                    super.onUpdate(pSecondsElapsed);
                    final Vector2 movingBodyWorldCenter = movingBody.getWorldCenter();
                    connectionLine.setPosition(connectionLine.getX1(), connectionLine.getY1(), movingBodyWorldCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT, movingBodyWorldCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
                }
            });
            marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(movingFace, movingBody, true, true));


            final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.initialize(anchorBody, movingBody, anchorBody.getWorldCenter());
            revoluteJointDef.enableMotor = true;
            revoluteJointDef.motorSpeed = 10;
            revoluteJointDef.maxMotorTorque = 200;

            marbleMazeActivity.getmPhysicsWorld().createJoint(revoluteJointDef);
        }
    }
}
