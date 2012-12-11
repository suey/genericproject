package ema.knotomania.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import ema.knotomania.Knotomania;

/**
 * The base class for all game screens.
 */
public abstract class AbstractScreen implements Screen {
    protected final Knotomania game;
    protected final BitmapFont font;
    protected final SpriteBatch batch;
    protected final Stage stage;

    public AbstractScreen(Knotomania game) {
        this.game = game;
        this.font = new BitmapFont();
        this.batch = new SpriteBatch();
        this.stage = new Stage(0, 0, true);
    }

    protected String getName() {
        return getClass().getSimpleName();
    }

    // Screen implementation

    @Override
    public void show() {
        Gdx.app.log("Knotomania", "Showing screen: " + getName());
        stage.setViewport( 800, 480, true );
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("Knotomania", "Resizing screen: " + getName() + " to: " + width + " x " + height );

        // resize the stage
        stage.setViewport( width, height, true );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor( 0.7f, 0.75f, 0.95f, 1f );
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // update and draw the stage actors
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void hide() {
        Gdx.app.log("Knotomania", "Hiding screen: " + getName());
    }

    @Override
    public void pause() {
        Gdx.app.log("Knotomania", "Pausing screen: " + getName());
    }

    @Override
    public void resume() {
        Gdx.app.log("Knotomania", "Resuming screen: " + getName());
    }

    @Override
    public void dispose() {
        Gdx.app.log("Knotomania", "Disposing screen: " + getName());

        // dispose the collaborators
        stage.dispose();
        batch.dispose();
        font.dispose();
    }
}