package ema.knotomania.screens.levels;

import com.badlogic.gdx.utils.Array;

import ema.knotomania.Knotomania;
import ema.knotomania.graph.Edge;
import ema.knotomania.graph.Graph;
import ema.knotomania.graph.Node;

public class Level01 extends AbstractLevel {
	
	public Level01(Knotomania game) {
		super(game);
	}
	
	public void constructGraph() {		
		Array<Node> ns = new Array<Node>();
		Node n1 = new Node(100, 400);
		Node n2 = new Node(700, 380);
		Node n3 = new Node(640, 20);
		Node n4 = new Node(90, 60);
		Node n5 = new Node(50, 200);
		ns.add(n1); ns.add(n2); ns.add(n3); ns.add(n4); ns.add(n5);
		
		Array<Edge> es = new Array<Edge>();
		es.add(new Edge(n1, n2));
		es.add(new Edge(n1, n3));
		es.add(new Edge(n2, n4));
		es.add(new Edge(n3, n4));
		es.add(new Edge(n5, n2));
		es.add(new Edge(n5, n4));
		
		g = new Graph(ns, es);
		g.computeIntersections();
	}

	public int getGoldMoveCount() {
		return 1;
	}

	public int getSilverMoveCount() {
		return 2;
	}

	public int getBronzeMoveCount() {
		return 3;
	}

}
