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
    private MarbleMazeActivity marbleMazeActivity;
    private TiledTextureRegion swingingBallTextureRegion;
    private float anchorX;
    private float anchorY;


    public SwingingBall(float anchorX, float anchorY, MarbleMazeActivity marbleMazeActivity) {
        this.anchorX = anchorX;
        this.anchorY = anchorY;
        this.marbleMazeActivity = marbleMazeActivity;
        Texture boxTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        Texture ballTexture = new Texture(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        marbleMazeActivity.getEngine().getTextureManager().loadTexture(boxTexture);
        marbleMazeActivity.getEngine().getTextureManager().loadTexture(ballTexture);
        TextureRegionFactory.setAssetBasePath("gfx/");
        this.boxTextureRegion = TextureRegionFactory.createTiledFromAsset(boxTexture, marbleMazeActivity, "box.png", 0, 32, 1, 1);
        this.swingingBallTextureRegion = TextureRegionFactory.createTiledFromAsset(ballTexture, marbleMazeActivity, "swingingBall.png", 0, 32, 1, 1);

    }


    public void initJoints(final Scene pScene) {

        final int spriteWidth = this.boxTextureRegion.getTileWidth();
        final int spriteHeight = this.boxTextureRegion.getTileHeight();

        final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(10, 0.2f, 0.5f);


        final AnimatedSprite anchorBox = new AnimatedSprite(anchorX, anchorY, this.boxTextureRegion);
        final Body anchorBody = PhysicsFactory.createBoxBody(marbleMazeActivity.getmPhysicsWorld(), anchorBox, BodyDef.BodyType.StaticBody, objectFixtureDef);

        final AnimatedSprite movingBall = new AnimatedSprite(anchorX, anchorY + 90, this.swingingBallTextureRegion);
        final Body movingBody = PhysicsFactory.createCircleBody(marbleMazeActivity.getmPhysicsWorld(), movingBall, BodyDef.BodyType.DynamicBody, objectFixtureDef);

        pScene.getLastChild().attachChild(anchorBox);
        pScene.getLastChild().attachChild(movingBall);

        final Line connectionLine = new Line(anchorX + spriteWidth / 2, anchorY + spriteHeight / 2, anchorX + spriteWidth / 2, anchorY + spriteHeight / 2);
        pScene.getFirstChild().attachChild(connectionLine);
        marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(anchorBox, anchorBody, true, true) {
            @Override
            public void onUpdate(final float pSecondsElapsed) {
                super.onUpdate(pSecondsElapsed);
                final Vector2 movingBodyWorldCenter = movingBody.getWorldCenter();
                connectionLine.setPosition(connectionLine.getX1(), connectionLine.getY1(),
                    movingBodyWorldCenter.x * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT,
                    movingBodyWorldCenter.y * PhysicsConstants.PIXEL_TO_METER_RATIO_DEFAULT);
            }
        });
        marbleMazeActivity.getmPhysicsWorld().registerPhysicsConnector(new PhysicsConnector(movingBall, movingBody, true, true));


        final RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(anchorBody, movingBody, anchorBody.getWorldCenter());
        revoluteJointDef.enableMotor = true;
        revoluteJointDef.motorSpeed = 20;
        revoluteJointDef.maxMotorTorque = 150;

        marbleMazeActivity.getmPhysicsWorld().createJoint(revoluteJointDef);

    }
}
