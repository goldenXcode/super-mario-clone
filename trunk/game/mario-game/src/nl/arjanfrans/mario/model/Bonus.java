package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.audio.Audio;
import nl.arjanfrans.mario.graphics.Tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Bonus extends StaticActor {
	private Animation animation;
	private float stateTime;
	private int hitcount = 0;
	private int maxhits = 1;
	private boolean destructable = false;
	private boolean destroyed;
	private TextureRegion texture;
	private TextureRegion empty_texture;

	public Bonus(World world, float x, float y, float speed, String name) {
		super(world);
		this.setOrigin(x, y);
		this.setBounds(x, y, 16 * (1/16f), 16 * (1/16f));
		animation = Tiles.getAnimation(speed, name);
		animation.setPlayMode(Animation.LOOP_PINGPONG);
		//TODO houd rekening met blue brick
		empty_texture = Tiles.getTile("brick_empty");
	}

	@Override
	public void act(float delta) {
		stateTime += delta;
		super.act(delta);
		
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if(texture != null) {
			batch.draw(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		else {
			batch.draw(animation.getKeyFrame(stateTime), this.getX(), this.getY(), this.getWidth(), this.getHeight());

		}
	}
	
	public void hit(int mario_level) {
		if(hitcount < maxhits) {
			this.addAction(Actions.sequence(Actions.moveTo(this.getX(), this.getY() + 0.2f, 0.1f, Interpolation.linear),
					Actions.moveTo(this.getX(), this.getY(), 0.1f, Interpolation.linear)));
			Audio.bump.play();
			//TODO keep items in consideration
			if(mario_level > 0) hitcount++;
		}
		else {
			if(mario_level > 0) {
				if(destructable) {
					destroyed = true;
				}
				else {
					texture = empty_texture;
				}
			}
		}
	}
}
