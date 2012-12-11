package ema.knotomania.screens;

import static com.badlogic.gdx.math.Interpolation.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import ema.knotomania.Knotomania;

public class StartScreen extends AbstractScreen {
    private Texture texture;
    boolean wasTouched = false;
    Image image;
    
    public StartScreen(Knotomania game) {
    	super(game);
        texture = new Texture(Gdx.files.internal("startscreen.png"));
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