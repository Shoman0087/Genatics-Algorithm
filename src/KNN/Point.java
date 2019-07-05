package KNN;
import java.util.Comparator;

public class Point implements Comparable<Point>{

	public double[] cord;
	public String label;
	public double dist;


	@Override
	public int compareTo(Point o) {
		if (this.dist > o.dist)
			return 1;
		else
			return -1;
	}

	
	
	
}
