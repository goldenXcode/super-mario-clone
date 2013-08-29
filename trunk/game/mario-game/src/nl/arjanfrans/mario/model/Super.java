package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.graphics.Tiles;
import nl.arjanfrans.mario.model.MovingActor.Direction;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Super extends Mushroom {
	private static TextureRegion texture;
	
	public Super (World world, float x, float y, float max_velocity) {
		super(world, x, y, max_velocity);
		texture = Tiles.getTile("mushroom_super");
		this.setBounds(x, y, texture.getRegionWidth() * World.scale, texture.getRegionHeight() * World.scale);
		direction = Direction.RIGHT;
		moving = false;
		this.setVisible(false);
	}

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		batch.draw(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		move(direction);
		if(moving) applyPhysics();
	}

	@Override
	protected void hitBlock(int x, int y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void dieByFalling() {
		if(this.getY() < -3f) {
			setDead(true);
		}
	}

	@Override
	protected void collisionXAction() {
		if(direction == Direction.LEFT) {
			direction = Direction.RIGHT;
		}
		else {
			direction = Direction.LEFT;
		}
	}

	@Override
	public void dispose() {
		texture.getTexture().dispose();
	}


}
