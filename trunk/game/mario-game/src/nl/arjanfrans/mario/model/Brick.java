package nl.arjanfrans.mario.model;

import nl.arjanfrans.mario.audio.Audio;
import nl.arjanfrans.mario.graphics.Tiles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Array;

public class Brick extends StaticActor {
	private TextureRegion texture;
	private TextureRegion empty_texture;
	private TextureRegion shatter;
	private int hitcount = 0;
	private int maxhits = 1;
	private boolean destructable = true;
	private Array<Actor> items;

	public Brick(World world, float x, float y, String color) {
		super(world);
		this.setOrigin(x, y);
		this.setBounds(x, y, 16 * (1/16f), 16 * (1/16f));
		items = new Array<Actor>();
		if(color.equals("brown")) {
			texture = Tiles.getTile("brick");
			empty_texture = Tiles.getTile("brick_empty");
			shatter = Tiles.getTile("brick_shatter");
		}
		else if(color.equals("blue")) {
			//TODO make blue tile
		}
	}
	

	@Override
	public void draw(SpriteBatch batch, float parentAlpha) {
		if(destroyed) {
			batch.draw(shatter, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		else {
			batch.draw(texture, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
	}

	public void hit(int mario_level) {
		if(items.size > 0) {
			if(items.peek() instanceof Mushroom) {
				((Mushroom) items.pop()).appear();
			}
		}
		if(mario_level > 1) {	
			if(items.size < 1) {
				if(destructable) {
					destroyed = true;
				}
			}
			this.addAction(Actions.sequence(Actions.moveTo(this.getOriginX(), this.getOriginY() + 0.2f, 0.1f, Interpolation.linear),
					Actions.moveTo(this.getOriginX(), this.getOriginY(), 0.1f, Interpolation.linear)));
			Audio.bump.play();
		}
		else {
			this.addAction(Actions.sequence(Actions.moveTo(this.getOriginX(), this.getOriginY() + 0.2f, 0.1f, Interpolation.linear),
					Actions.moveTo(this.getOriginX(), this.getOriginY(), 0.1f, Interpolation.linear)));
			Audio.bump.play();
		}
	}
	
	

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return Math.round(super.getX() * 100.0f) / 100.0f;
	}


	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return Math.round(super.getY()  * 100.0f) / 100.0f;
	}



	public Array<Actor> getItems() {
		return items;
	}
	
	public Actor popItem() {
		return items.pop();
	}


	public void addItem(Actor item) {
		items.add(item);
	}




}
