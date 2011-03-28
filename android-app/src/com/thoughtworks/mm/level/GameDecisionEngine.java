package com.thoughtworks.mm.level;

import java.util.ArrayList;
import java.util.List;

import org.anddev.andengine.engine.handler.timer.ITimerCallback;
import org.anddev.andengine.engine.handler.timer.TimerHandler;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.thoughtworks.mm.MarbleMazeActivity;
import com.thoughtworks.mm.entity.Ball;
import com.thoughtworks.mm.entity.Pocket;
import com.thoughtworks.mm.entity.Trap;

public class GameDecisionEngine {

	private Pocket pocket;
	private Ball ball;
	private List<Trap> traps = new ArrayList<Trap>();

	public Pocket getPocket() {
		return pocket;
	}

	public void setPocket(Pocket pocket) {
		this.pocket = pocket;
	}

	public void setBall(Ball ball) {
		this.ball = ball;
	}

	public void addTrap(Trap trap) {
		traps.add(trap);
	}

	private boolean isBallTrapped() {
		for (Trap trap : traps) {
			if (trap.contains(ball.getX(), ball.getY())) {
				return true;
			}
		}
		return false;
	}

	public void registerActivity(final MarbleMazeActivity marbleMazeActivity) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0) {
					Toast.makeText(marbleMazeActivity, "You win!",
							Toast.LENGTH_SHORT).show();
				} else {

					Toast.makeText(marbleMazeActivity, "You loose!",
							Toast.LENGTH_SHORT).show();
					marbleMazeActivity.resetLevel(ball);
					
				}

			}
		};

		marbleMazeActivity.getScene().registerUpdateHandler(
				new TimerHandler(0.5f, true, new ITimerCallback() {
					public void onTimePassed(final TimerHandler pTimerHandler) {
						if (isBallTrapped()) {
							handler.sendEmptyMessage(1);
						}
						if (pocket.contains(ball.getX(), ball.getY())) {
							handler.sendEmptyMessage(0);
						}
					}

				}));

	}

}
