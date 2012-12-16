package ema.knotomania.screens.levels;


import static com.badlogic.gdx.math.Interpolation.*;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Json;

import ema.knotomania.Knotomania;
import ema.knotomania.graph.Graph;
import ema.knotomania.graph.Node;

public class Level implements Screen {
	private Knotomania game;
	private OrthographicCamera camera;
	private SpriteBatch batch;
	boolean isDragging = false;
	Mode mode = Mode.Normal;
	static int TOUCH_RADIUS = 60;
	
	int lvlID;
	boolean isCustom;
	Graph g;
	Node nearest = null;
	int moves = 0;
	
	Stage ui;
    Dialog successDialog;	
	
	public void constructGraph() {
		Json json = new Json();
		if(isCustom) {
			g = json.fromJson(Graph.class, Gdx.files.local("customLevel/level" + lvlID + ".json"));
		} else {
			g = json.fromJson(Graph.class, Gdx.files.internal("levels/level" + lvlID + ".json"));
		}
	}
	
	public Level(Knotomania game, int lvlID, boolean isCustom) {
		this.game = game;
        ui = new Stage(0, 0, true);
        ui.setViewport(800, 480, true);
        this.lvlID = lvlID;
        this.isCustom = isCustom;
	}
	
	@Override
	public void show() {		
	    camera = new OrthographicCamera();
	    camera.setToOrtho(false, 800, 480);
	    batch = new SpriteBatch();
	    constructGraph();
	}

	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render(float delta) {		
		Gdx.gl.glClearColor(0.75f, 0.8f, 0.99f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		camera.update();
		
		switch (mode) {
		case Normal:
	        if (Gdx.input.isKeyPressed(Keys.BACK) || Gdx.input.isKeyPressed(Keys.ESCAPE)){
	        	try {
					Thread.sleep(120);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            game.setScreen(game.easyLevelsScreen);
	        }
			
			
			if(Gdx.input.isTouched() && !isDragging) {
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);
				int x = (int) touchPos.x;
				int y = (int) touchPos.y;
				nearest = g.getNearestNode(x, y, TOUCH_RADIUS);
				if (nearest == null) {
					isDragging = false;
				} else {
					isDragging = true;
				}
			}
			if(Gdx.input.isTouched() && isDragging) {
				g.computeIntersections();
				Vector3 touchPos = new Vector3();
				touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
				camera.unproject(touchPos);
				int x = (int) touchPos.x;
				int y = (int) touchPos.y;
				if (nearest != null) {
					nearest.x = x;
					nearest.y = y;
				}
			}
			if(!Gdx.input.isTouched() && isDragging) {
				isDragging = false;
				moves++;
			}
			
			
			if (g.getNumberOfIntersections() == 0 && !isDragging) {
				mode = Mode.Success;
			}
		
			break;
			
		case Success:
			g.draw(camera);
			drawMovesCount();
			
	        Table table = new Table();
	        table.setFillParent(true);
	        ui.addActor(table);
			
	        FileHandle skinFile = Gdx.files.internal( "skin/uiskin.json" );
	        Skin skin = new Skin(skinFile);
	        successDialog = new Dialog("Success", skin);
	        Label l = new Label("You needed " + moves + " moves!", skin);
	        successDialog.add(l);
	        successDialog.setOrigin(100, 0);
	        //TextButton button = new TextButton("Button 1", skin);
	        table.add(successDialog);
	        mode = Mode.Exit;
	        
	        successDialog.addAction(sequence(delay(1f), parallel(scaleBy(5f, 5f, 0.6f, pow2In), fadeOut(0.6f)), run(new Runnable() {
				public void run() {
					game.setScreen(game.easyLevelsScreen);
				}
	        })));
			break;
		case Menu:
			break;
		case Exit:
			break;
		}
		
		g.draw(camera);
		drawMovesCount();
        // update and draw the stage actors
        ui.act(delta);
        ui.draw();
	}
	
	private void drawMovesCount() {
		ShapeRenderer sr = new ShapeRenderer();
		
		sr.setProjectionMatrix(camera.combined);
		 
		sr.begin(ShapeType.Line);
		sr.setColor(0, 0.3f, 0, 1);
		int offset = 5;
		int x = offset;
		int y = 450;
		int h_line = 25;
		int d_line = 6;
		int d_block = 8;
		int w_block = d_line * 4;
		for (int i = 0; i < moves; i++) {
			if ((i+1) % 5 == 0) {
				int x0 = offset + d_line * i;
				sr.line(x - w_block, y, x0, y + h_line);
				offset += (d_block - d_line);
			} else {
				x = offset + d_line * i;
				sr.line(x, y, x, y + h_line);
			}
		}
		sr.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}
}
