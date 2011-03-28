package com.thoughtworks.mm.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;

import com.thoughtworks.mm.MarbleMazeActivity;
import com.thoughtworks.mm.level.GameDecisionEngine;

public class EntityBuilder {

	private float x0;
	private float y0;

	private float width;
	private float height;

	private String type;

	private MarbleMazeActivity marbleMazeActivity;
	private GameDecisionEngine gameDecisionEngine;

	public EntityBuilder(MarbleMazeActivity marbleMazeActivity,
			GameDecisionEngine gameDecisionEngine) {
		this.marbleMazeActivity = marbleMazeActivity;
		this.gameDecisionEngine = gameDecisionEngine;
	}

	public EntityBuilder setInitialCoordinates(float x, float y) {
		x0 = x;
		y0 = y;
		return this;
	}

	public EntityBuilder setDimensions(float width, float height) {
		this.width = width;
		this.height = height;
		return this;
	}

	public EntityBuilder type(String type) {
		this.type = type;
		return this;
	}

	public void build() {
		if (type == null) {
			throw new NullPointerException("Type can't be null");
		}

		AnimatedSprite animatedSprite = null;

		if (type.equals("box")) {
			animatedSprite = new Box(x0, y0, width, height, marbleMazeActivity);

		} else if (type.equals("pocket")) {
			Pocket pocket = new Pocket(x0, y0, marbleMazeActivity);
			animatedSprite = pocket;
			gameDecisionEngine.setPocket(pocket);

		} else if (type.equals("trap")) {
			Trap trap = new Trap(x0, y0, marbleMazeActivity);
			animatedSprite = trap;
			gameDecisionEngine.addTrap(trap);

		} else if (type.equals("swing")) {
			SwingingBall swingingBall = new SwingingBall(x0, y0,
					marbleMazeActivity);
			swingingBall.initJoints(marbleMazeActivity.getScene());

		} else if (type.equals("ball")) {
			Ball ball = new Ball(x0, y0, marbleMazeActivity);
			gameDecisionEngine.setBall(ball);
			animatedSprite = ball;
		}

		if (animatedSprite != null) {
			marbleMazeActivity.getScene().getLastChild().attachChild(
					animatedSprite);
		}

	}

}
