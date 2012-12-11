package ema.knotomania.graph;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Array;

public class Graph {
	Array<Node> nodes;
	Array<Edge> edges;
	Array<Intersection> intersections = new Array<Intersection>();
	
	ShapeRenderer sr = new ShapeRenderer();
	
	public Graph(Array<Node> nodes, Array<Edge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}
	
	public void draw(Camera camera) {
		 sr.setProjectionMatrix(camera.combined);
		 
		 sr.begin(ShapeType.Line);
		 sr.setColor(0.4f, 0.6f, 0, 1);
		 for (Edge e : edges) {
			 sr.line(e.n1.x, e.n1.y, e.n2.x, e.n2.y);
		 }
		 sr.end();
		 
		 sr.begin(ShapeType.FilledCircle);
		 sr.setColor(0.2f, 0.2f, 0.3f, 1);
		 for (Node n : nodes) {
			 sr.filledCircle(n.x, n.y, 6);
		 }
		 sr.setColor(1, 0.2f, 0.2f, 0.5f);
		 for (Intersection i : intersections) {
			 sr.filledCircle(i.x, i.y, 4);
		 }
		 sr.end();
	}
	
	// return the closest node to the center (x,y) of the circle with radius maxRadius,
	// that's also inside the circle. Return null if none exists.
	public Node getNearestNode(int x, int y, int maxRadius) {
		Node nearest = null;
		double minDistance = Double.MAX_VALUE;
		for (Node n : nodes) {
			double d = distance(x, y, n.x, n.y);
			if (d < minDistance) {
				nearest = n;
				minDistance = d;
			} 
		}

		return minDistance > maxRadius ? null : nearest;
	}
	 
	public void computeIntersections() {
		intersections.clear();
		for (int i = 0; i < edges.size; i++) {
			for (int j = i; j < edges.size; j++) {
				Intersection intersection = getIntersection(edges.get(i), edges.get(j));
				if (intersection != null) intersections.add(intersection);
			}
		}
	}
	
	public int getNumberOfIntersections() {
		return intersections.size;
	}
	
	
	// Utility
	
	private static double distance(int x1, int y1, int x2, int y2) {
		return Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
	}
	
	private static double det(double a, double b, double c, double d) {
	    return a * d - b * c;
	}
	
	// http://mathworld.wolfram.com/Line-LineIntersection.html
	// return intersection point or null if none
	private Intersection getIntersection(Edge e1, Edge e2) {
		double x1 = e1.n1.x, y1 = e1.n1.y,
	           x2 = e1.n2.x, y2 = e1.n2.y,
		       x3 = e2.n1.x, y3 = e2.n1.y,
		       x4 = e2.n2.x, y4 = e2.n2.y;
		
		if (!linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4)) {
			return null;
		}
		
		// Knoten sollen keine Uberschneidungen sein
		if (x2 == x3 && y2 == y3 || 
			x2 == x4 && y2 == y4 || 
			x1 == x3 && y1 == y3 ||
			x1 == x4 && y1 == y4) {
			return null;
		}
			
		double x = det(det(x1, y1, x2, y2), x1 - x2,
					   det(x3, y3, x4, y4), x3 - x4)/
		           det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
		double y = det(det(x1, y1, x2, y2), y1 - y2,
		               det(x3, y3, x4, y4), y3 - y4)/
		           det(x1 - x2, y1 - y2, x3 - x4, y3 - y4);
		
		return new Intersection((int) Math.round(x), (int) Math.round(y));
	}
	
	// http://www.java-gaming.org/index.php?topic=22590.0
	 private static boolean linesIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4){
	      // Return false if either of the lines have zero length
	      if (x1 == x2 && y1 == y2 ||
	            x3 == x4 && y3 == y4){
	         return false;
	      }
	      // Fastest method, based on Franklin Antonio's "Faster Line Segment Intersection" topic "in Graphics Gems III" book (http://www.graphicsgems.org/)
	      double ax = x2-x1;
	      double ay = y2-y1;
	      double bx = x3-x4;
	      double by = y3-y4;
	      double cx = x1-x3;
	      double cy = y1-y3;

	      double alphaNumerator = by*cx - bx*cy;
	      double commonDenominator = ay*bx - ax*by;
	      if (commonDenominator > 0){
	         if (alphaNumerator < 0 || alphaNumerator > commonDenominator){
	            return false;
	         }
	      }else if (commonDenominator < 0){
	         if (alphaNumerator > 0 || alphaNumerator < commonDenominator){
	            return false;
	         }
	      }
	      double betaNumerator = ax*cy - ay*cx;
	      if (commonDenominator > 0){
	         if (betaNumerator < 0 || betaNumerator > commonDenominator){
	            return false;
	         }
	      }else if (commonDenominator < 0){
	         if (betaNumerator > 0 || betaNumerator < commonDenominator){
	            return false;
	         }
	      }
	      if (commonDenominator == 0){
	         // This code wasn't in Franklin Antonio's method. It was added by Keith Woodward.
	         // The lines are parallel.
	         // Check if they're collinear.
	         double y3LessY1 = y3-y1;
	         double collinearityTestForP3 = x1*(y2-y3) + x2*(y3LessY1) + x3*(y1-y2);   // see http://mathworld.wolfram.com/Collinear.html
	         // If p3 is collinear with p1 and p2 then p4 will also be collinear, since p1-p2 is parallel with p3-p4
	         if (collinearityTestForP3 == 0){
	            // The lines are collinear. Now check if they overlap.
	            if (x1 >= x3 && x1 <= x4 || x1 <= x3 && x1 >= x4 ||
	                  x2 >= x3 && x2 <= x4 || x2 <= x3 && x2 >= x4 ||
	                  x3 >= x1 && x3 <= x2 || x3 <= x1 && x3 >= x2){
	               if (y1 >= y3 && y1 <= y4 || y1 <= y3 && y1 >= y4 ||
	                     y2 >= y3 && y2 <= y4 || y2 <= y3 && y2 >= y4 ||
	                     y3 >= y1 && y3 <= y2 || y3 <= y1 && y3 >= y2){
	                  return true;
	               }
	            }
	         }
	         return false;
	      }
	      return true;
	   }

}

class Intersection {
	public int x;
	public int y;
	
	public Intersection(int x, int y) {
		this.x = x;
		this.y = y;
	}
}