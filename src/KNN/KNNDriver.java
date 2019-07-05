package KNN;

import java.util.PriorityQueue;

public class KNNDriver {
	
	
	public KNNDriver() {
		// TODO Auto-generated constructor stub
	}
	

	
	public double calcAccuracy(Point[] data, Point[] test, int[] selected) {
			double right = 0;
			double wrong = 0;
			for (int j = 0 ; j < test.length ; j++) {
				String label = getLabel(data, test[j], 11, 20, selected);
				if (label.equals(test[j].label))
					right++;
				else
					wrong++;	
					
			}

			double accurcy = 0;
//			System.out.println(right + "  " + wrong);
			accurcy = (right) / (right + wrong);
			return accurcy;
		
	}
	
	static String getLabel (Point[] set, Point newPoint , int k, int Labels, int[] selected) {
		PriorityQueue<Point> order = new PriorityQueue<>();
		for (int i = 0 ; i < set.length ; i++) {
			set[i].dist = 0;
			Eluciden_Distance(set[i], newPoint, selected);
			order.add(set[i]);
		}
				

		int[] lab = new int[Labels];
		for (int i = 0 ; i < k ; i++) {
			Point p = order.poll();
//			System.out.println("distanceeeeeeee    " + p.dist + "  ");
//			System.out.println(p.label);
			lab[(int)Double.parseDouble(p.label)]++;

		}

	
		int max = lab[0];
		double maxInd = 0;
		for (int i = 0 ; i < Labels ; i++) {
			if (lab[i] > max) {
				max = lab[i];
				maxInd = i;
			}
		}
//		System.out.println(maxInd + "  " + max);
		return maxInd + "";
				
		
	}
	
	static double Eluciden_Distance(Point x , Point y, int[] selected) {
		double ans = 0;
		for (int i = 0 ; i < x.cord.length ; i++) {
			if (selected[i] == 1)
				ans += Math.pow(x.cord[i] - y.cord[i], 2);
		}
		x.dist = Math.sqrt(ans);
		return Math.sqrt(ans);
	}

}
