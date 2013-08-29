package nl.arjanfrans.mario;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "mario-game";
		cfg.useGL20 = true;
		cfg.width = 512;
		cfg.height = 448;
		new LwjglApplication(new MarioGame(), cfg);
	}
}
