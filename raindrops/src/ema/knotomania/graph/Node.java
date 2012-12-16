package ema.knotomania.graph;

public class Node {
	public int x;
	public int y;
	
	public Node() {
		//This constructor is essential for deserialization!
	}
	
	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

