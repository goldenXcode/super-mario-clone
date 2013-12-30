package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.graphics.Tiles;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;


public class Flag extends Actor {
	private Animation animation;
	private float stateTime;
	
	public Flag(float x, float y, float width, float height) {
		animation = Tiles.getAnimation(0.15f, "evil_flag");
		animation.setPlayMode(Animation.LOOP_PINGPONG);
		this.setOrigin(width / 2, height);
		this.setBounds(x, y, width, height);
		this.setTouchable(Touchable.disabled);
	}
	
	@Override
	public void act(float delta) {
		stateTime += delta;
		super.act(delta);
	}


	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(animation.getKeyFrame(stateTime), this.getX() + this.getOriginX(), this.getHeight() , 
				animation.getKeyFrame(stateTime).getRegionWidth() * World.scale, animation.getKeyFrame(stateTime).getRegionHeight() * World.scale);
	}


}
