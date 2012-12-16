package ema.knotomania;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import ema.knotomania.screens.DifficultyScreen;
import ema.knotomania.screens.EasyLevelsScreen;
import ema.knotomania.screens.LevelEditorScreen;
import ema.knotomania.screens.StartScreen;

public class Knotomania extends Game {
	
	public StartScreen startScreen;
	public DifficultyScreen difficultyScreen;
	public EasyLevelsScreen easyLevelsScreen;
	public LevelEditorScreen levelEditorScreen;
	
	@Override
	public void create() {		
		Gdx.input.setCatchBackKey(true);
		startScreen = new StartScreen(this);
		difficultyScreen = new DifficultyScreen(this);
		easyLevelsScreen = new EasyLevelsScreen(this);
		levelEditorScreen = new LevelEditorScreen(this);
		this.setScreen(levelEditorScreen);
//		this.setScreen(startScreen);
//		this.setScreen(new Level01(this));
	}

}
