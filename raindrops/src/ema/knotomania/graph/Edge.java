package ema.knotomania.graph;


public class Edge {
	public int n1;
	public int n2;
	
	public Edge() {
		//This constructor is essential for deserialization!
	}
	
	public Edge(int n1, int n2) {
		this.n1 = n1;
		this.n2 = n2;
	}
}