package ema.knotomania.screens;

import static com.badlogic.gdx.math.Interpolation.linear;
import static com.badlogic.gdx.math.Interpolation.pow2;
import static com.badlogic.gdx.math.Interpolation.pow2In;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.delay;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.parallel;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.rotateTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.run;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleBy;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ema.knotomania.Knotomania;
import ema.knotomania.screens.levels.Level;

public class EasyLevelsScreen extends AbstractScreen {
    private Texture texture;
    boolean wasTouched = false;
    Image image;
    
    public EasyLevelsScreen(Knotomania game) {
    	super(game);
        texture = new Texture(Gdx.files.internal("easylevelsscreen.png"));
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        image = new Image(texture);
        image.setOrigin(400, 240);
        image.addAction(forever(sequence(rotateTo(1f, 3f, pow2), delay(0.2f), rotateTo(-1f, 3f, pow2), delay(0.2f))));
        stage.addActor(image);
    }

    public void render(float delta) {
    	super.render(delta);
    	if (wasTouched) return;
    	
        if(Gdx.input.isTouched()) {
        	final int x = Gdx.input.getX();
	        image.addAction(sequence(parallel(scaleBy(5f, 5f, 0.3f, pow2In), fadeOut(0.3f)), run(new Runnable() {
				public void run() {
				    if (x <= 400)	
						game.setScreen(new Level(game, 1, true));
				    else
				    	game.setScreen(new Level(game, 2, true));
				}
	        })));
	        wasTouched = true;
        }
        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)){
	        image.addAction(sequence(parallel(scaleBy(5f, 5f, 0.3f, pow2In), fadeOut(0.3f)), run(new Runnable() {
				public void run() {
					game.setScreen(game.difficultyScreen);
				}
	        })));
	        wasTouched = true;
        }
    }
    
    @Override
    public void show() {
    	super.show();
        image.getColor().a = 0f;
        image.setScale(1f);
        image.scale(.06f);
        image.addAction(sequence(fadeIn(0.4f, linear)));
    }
    
    @Override
    public void hide() {
        wasTouched = false;
    }

}