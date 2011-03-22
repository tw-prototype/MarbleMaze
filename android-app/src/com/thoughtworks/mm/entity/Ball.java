package com.thoughtworks.mm.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Ball extends AnimatedSprite{

	public Ball(float pX, float pY,
			TiledTextureRegion pTiledTextureRegion) {
		super(pX, pY, pTiledTextureRegion);

	}

}
