package nl.arjanfrans.mario.model;

import java.util.Iterator;

import nl.arjanfrans.mario.audio.Audio;
import nl.arjanfrans.mario.graphics.Tiles;
import nl.arjanfrans.mario.view.WorldRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


public class World {
	private Mario player;
	private TiledMap map;
	private static final float GRAVITY = -2.5f;
	public static final float scale = 1/16f;
	private Array<Goomba> goombas;
	private Array<Mushroom> mushrooms;
	private Pool<Rectangle> rectPool = new Pool<Rectangle>()
	{
		@Override
		protected Rectangle newObject()
		{
			return new Rectangle();
		}
	};
	private static Tiles tiles = new Tiles();
	private Stage stage;
	private WorldRenderer wr;
	
	public World() {
		reset();
	}
	
	
	private Array<Goomba> generateEnemies() {
		Array<Goomba> goombas = new Array<Goomba>();
		MapLayer layer = map.getLayers().get("objects");
		MapObjects objects = layer.getObjects();
		Iterator<MapObject> objectIt = objects.iterator();
		while(objectIt.hasNext()) {
			MapObject obj = objectIt.next();
			String type = (String) obj.getProperties().get("type");
			if(type != null) {
				int x = (Integer) obj.getProperties().get("x");
				int y = (Integer) obj.getProperties().get("y");
				if(type.equals("goomba")) {
					Goomba goomba = new Goomba(this, x * (1/16f), y* (1/16f));
					goombas.add(goomba);
					stage.addActor(goomba);
				}
			}
		}
		return goombas;
	}
	
	public void removeActor(Actor a) {
		stage.getActors().removeValue(a, true);
	}
	
	private void reset() {
		map = new TmxMapLoader().load("data/level1.tmx");
		animateTiles((TiledMapTileLayer) map.getLayers().get("close_background"));
		animateTiles((TiledMapTileLayer) map.getLayers().get("walls"));
		player = new Mario(this, 10, 10);
		stage = new Stage();
		goombas = generateEnemies();
		mushrooms = new Array<Mushroom>();
		generateBricks((TiledMapTileLayer) map.getLayers().get("walls"));
		stage.addActor(player);
		String song = (String) map.getLayers().get("background").getObjects().get("background_image")
				.getProperties().get("audio");
		Audio.stopSong();
		Audio.playSong(song, true);
		wr = new WorldRenderer(this);
		wr.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	
	public void update() {
		Rectangle screen = rectPool.obtain();
		screen.set(wr.getCamera().position.x - wr.getCamera().viewportWidth/2, 
				wr.getCamera().position.y-wr.getCamera().viewportHeight/2,
				wr.getCamera().viewportWidth, wr.getCamera().viewportHeight);
		for(Goomba e : goombas) {
			if(screen.overlaps(e.rectangle())) {
				e.setMoving(true);
			}
			if(e.isDead()) {
				goombas.removeValue(e, true);
				stage.getActors().removeValue(e, true);
			}
		}
		rectPool.free(screen);
		stage.act(Gdx.graphics.getDeltaTime());
		if(player.isDead()) reset();
		
		wr.render();
	}
	
	private void generateBricks(TiledMapTileLayer layer) {
		for (int x = 1; x < layer.getWidth(); x++) {
			for (int y = 1; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					TiledMapTile oldTile = cell.getTile();
					if(oldTile.getProperties().containsKey("actor")) {
						String type = (String) oldTile.getProperties().get("actor");
						StaticActor actor = null;
						//TODO cleanup, double code!
						if(type.equals("Brick")) {
							String color = (String) oldTile.getProperties().get("color");
							actor = new Brick(this, x, y, color, false, true);
							itemsInBrick((Brick) actor, x, y);
						}
						else if(type.equals("Bonus")) {
							String color = (String) oldTile.getProperties().get("color");
							actor = new Brick(this, x, y, color, true, false);
						} 
						layer.setCell(x, y, null);
						stage.addActor(actor);
					}
				}
			}
		}
	}
	
	private void itemsInBrick(Brick brick, int x, int y) {
		MapLayer layer = map.getLayers().get("hidden_items");
		MapObjects objects = layer.getObjects();
		for(MapObject obj : objects) {
			
			int obj_x = (int) ((Integer) obj.getProperties().get("x") * (1/16f));
			int obj_y = (int) ((Integer) obj.getProperties().get("y") * (1/16f));
			if(obj_x == x && obj_y == y) {
				String type = (String) obj.getProperties().get("type");
				Actor item = null;
				if(type.equals("super_mushroom")) {
					item = new Super(this, x, y, 4f);
					mushrooms.add((Mushroom) item);
				}
				stage.addActor(item);				
				brick.addItem(item);
			}
		}
	}
	
	public Array<StaticActor> getStaticActors() {
		Array<StaticActor> staticActors = new Array<StaticActor>();
		for(Actor a : stage.getActors()) {
			if(a instanceof StaticActor) {
				staticActors.add((StaticActor) a);
			}
		}
		return staticActors;
	}
	
	private void animateTiles(TiledMapTileLayer layer) {
		for (int x = 1; x < layer.getWidth(); x++) {
			for (int y = 1; y < layer.getHeight(); y++) {
				Cell cell = layer.getCell(x, y);
				if(cell != null) {
					TiledMapTile oldTile = cell.getTile();
					if(oldTile.getProperties().containsKey("animation")) {
						String animation = (String) oldTile.getProperties().get("animation");
						float speed = 0.15f;
						if(oldTile.getProperties().containsKey("speed")) {
							speed = Float.parseFloat((String) oldTile.getProperties().get("speed"));
						}
						AnimatedTiledMapTile newTile = new AnimatedTiledMapTile(speed, 
								Tiles.getAnimatedTile(animation));
						newTile.getProperties().putAll(oldTile.getProperties());
						cell.setTile(newTile);
					}
				}
			}
		}
	}
	
	public WorldRenderer getRenderer() {
		return wr;
	}
	
	public Mario getPlayer() {
		return player;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public float getGravity() {
		return GRAVITY;
	}
	
	public Array<Goomba> getEnemies() {
		Array<Actor> actors = stage.getActors();
		Array<Goomba> enemies = new Array<Goomba>();
		for(Actor a : actors) {
			if(a instanceof Goomba) {
				enemies.add((Goomba) a);
			}
		}
		return enemies;
	}
	
	public Array<Mushroom> getMushrooms() {
		Array<Actor> actors = stage.getActors();
		Array<Mushroom> mushrooms = new Array<Mushroom>();
		for(Actor a : actors) {
			if(a instanceof Mushroom) {
				mushrooms.add((Mushroom) a);
			}
		}
		return mushrooms;
	}
	
	public Array<Rectangle> getTiles(int startX, int startY, int endX, int endY)
	{
		Array<Rectangle> tiles = new Array<Rectangle>();
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("walls");
		rectPool.freeAll(tiles);
		tiles.clear();
		for (int y = startY; y <= endY; y++)
		{
			for (int x = startX; x <= endX; x++)
			{
				Cell cell = layer.getCell(x, y);
				if (cell != null)
				{
					Rectangle rect = rectPool.obtain();
					rect.set(x, y, 1, 1);
					tiles.add(rect);
				}
			}
		}
		return tiles;
	}
	
	public void dispose() {
		map.dispose();
		tiles.dispose();
		player.dispose();
		for(Goomba g : getEnemies()) {
			g.dispose();
		}
		for(Mushroom m : getMushrooms()) {
			m.dispose();
		}
		wr.dispose();
		Audio.dispose();
		
	}



	public Stage getStage() {
		return stage;
	}

}
