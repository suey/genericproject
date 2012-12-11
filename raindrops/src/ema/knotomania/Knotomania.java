package ema.knotomania;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

import ema.knotomania.screens.DifficultyScreen;
import ema.knotomania.screens.EasyLevelsScreen;
import ema.knotomania.screens.StartScreen;
import ema.knotomania.screens.levels.Level01;

public class Knotomania extends Game {
	
	public StartScreen startScreen;
	public DifficultyScreen difficultyScreen;
	public EasyLevelsScreen easyLevelsScreen;
	
	@Override
	public void create() {		
		Gdx.input.setCatchBackKey(true);
		startScreen = new StartScreen(this);
		difficultyScreen = new DifficultyScreen(this);
		easyLevelsScreen = new EasyLevelsScreen(this);
		this.setScreen(startScreen);
//		this.setScreen(new Level01(this));
	}

}
