package ema.knotomania.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Json;

import ema.knotomania.Knotomania;
import ema.knotomania.graph.Graph;
import ema.knotomania.graph.Node;

public class LevelEditorScreen extends AbstractScreen {

	private final int TOUCH_RADIUS = 10;
	
	private boolean wasTouched = false;
	private boolean isDragging = false;
	private boolean drawMode = true;
	
	private int lastTouchedX;
	private int lastTouchedY;
	private int lastTempIndex;
	
	private Graph graph;
	private Node startNode = null;
	private Node destNode = null;
	
	private OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	private Skin skin;
	private Button saveButton;
	private Button undoButton;
	private Window dialogWindow;
	private TextField goldTries;
	private TextField silverTries;
	private TextField bronzeTries;
	private Button dialogOkButton;
	private Button dialogCancelButton;
	
	public LevelEditorScreen(Knotomania game) {
		super(game);
		lastTempIndex = 0;
		shapeRenderer = new ShapeRenderer();
		skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
	    saveButton = new TextButton("Speichern", skin);
	    saveButton.setPosition(800-saveButton.getWidth(), 0);
	    saveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if(graph.isValidGraph()) {
					stage.addActor(dialogWindow);
					drawMode = false;
				}
			}});
	    undoButton = new TextButton("Undo", skin);
	    undoButton.setPosition(saveButton.getX()-undoButton.getWidth(),0);
	    undoButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				tempLoad();
			}});
	    stage.addActor(undoButton);
	    stage.addActor(saveButton);
	    
	    dialogWindow = new Window("Medaillen", skin);
		goldTries = new TextField("1", skin);
		silverTries = new TextField("2", skin);
		bronzeTries = new TextField("3", skin);
		dialogOkButton = new TextButton("Ok", skin);
		dialogOkButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int gold = Integer.parseInt(goldTries.getText());
				int silver = Integer.parseInt(silverTries.getText());
				int bronze = Integer.parseInt(bronzeTries.getText());
				graph.setGoldMoveCount(gold);
				graph.setSilverMoveCount(silver);
				graph.setBronzeMoveCount(bronze);
				saveGraph();
				dialogWindow.remove();
				drawMode = true;
			}});
		dialogCancelButton = new TextButton("Cancel", skin);
		dialogCancelButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				dialogWindow.remove();
				drawMode = true;
			}});
		dialogWindow.row().fill();
		dialogWindow.add(goldTries).minWidth(20).expandX().fillX().colspan(3);
		dialogWindow.row();
		dialogWindow.add(silverTries).minWidth(20).expandX().fillX().colspan(3);
		dialogWindow.row();
		dialogWindow.add(bronzeTries).minWidth(20).expandX().fillX().colspan(3);
		dialogWindow.row();
		dialogWindow.add(dialogOkButton);
		dialogWindow.add(dialogCancelButton);
		dialogWindow.setPosition(400 - dialogWindow.getWidth()/2, 240 - dialogWindow.getHeight()/2);
		
		graph = new Graph();
	}
	
	
	public void render(float delta) {
		super.render(delta);
		if(wasTouched) return;
		
		int x;
		int y;
		
		if(Gdx.input.isTouched() && drawMode) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			x = (int) touchPos.x;
			y = (int) touchPos.y;
			lastTouchedX = x;
			lastTouchedY = y;
			
			if(collides(saveButton, touchPos) || collides(undoButton, touchPos)) {
				graph.computeIntersections();
				graph.draw(camera);
				//TODO add saving stuff here
				return;
			}
			
			if(!isDragging) {
				startNode = graph.getNearestNode(x, y, TOUCH_RADIUS);
				isDragging = startNode != null;
				
				if(!isDragging) {
					Node n = new Node(x, y);
					graph.addNode(n);
					tempSave();
				}
			} else {
				shapeRenderer.setProjectionMatrix(camera.combined);
				shapeRenderer.begin(ShapeType.Line);
				shapeRenderer.setColor(0.4f, 0.6f, 0, 1);
				shapeRenderer.line(startNode.x, startNode.y, lastTouchedX, lastTouchedY);
				shapeRenderer.end();
			}
		}
		if(!Gdx.input.isTouched() && isDragging && drawMode) {
			isDragging = false;
			destNode = graph.getNearestNode(lastTouchedX, lastTouchedY, TOUCH_RADIUS);
			if(destNode != null && startNode != destNode) {
				graph.addEdge(startNode, destNode);
				startNode = null;
				destNode = null;
				tempSave();
			}
		}
		
		graph.computeIntersections();
		graph.draw(camera);
	}
	
    @Override
    public void show() {
    	super.show();
    	camera = new OrthographicCamera();
	    camera.setToOrtho(false, 800, 480);
    }
    
    @Override
    public void hide() {
        wasTouched = false;
    }
    
    private boolean collides(Button button, Vector3 pos) {
    	return button.getX() < pos.x && button.getY() < pos.y && button.getX()+button.getWidth() > pos.x && button.getY()+button.getHeight() > pos.y;
    }
    
    private void saveGraph() {
    	int nextLevelIndex = Gdx.files.local("customLevel/").list().length + 1;
    	Json json = new Json();
    	json.toJson(graph, Gdx.files.local("customLevel/level"+ nextLevelIndex +".json"));
    	System.out.println("Level " + nextLevelIndex + " saved!");
    	graph = new Graph();
    	for(int i = 1; i <= lastTempIndex; i++) {
    		Gdx.files.local("temp/save" + i + ".json").delete();
    	}
    	lastTempIndex = 0;
    }
    
    private void tempSave() {
    	lastTempIndex = Gdx.files.local("temp/").list().length + 1;
    	Json json = new Json();
    	json.toJson(graph, Gdx.files.local("temp/save"+ lastTempIndex +".json"));
    	System.out.println("saving..." + lastTempIndex);
    }
    
    private void tempLoad() {
    	if(lastTempIndex > 0) {
	    	Json json = new Json();
	    	graph = json.fromJson(Graph.class, Gdx.files.local("temp/save" + lastTempIndex + ".json"));
	    	Gdx.files.local("temp/save" + lastTempIndex + ".json").delete();
	    	lastTempIndex--;
    	} else {
    		graph = new Graph();
    		lastTempIndex = 0;
    	}
    	graph.computeIntersections();
    	graph.draw(camera);
    }

}
