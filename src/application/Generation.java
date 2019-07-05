package application;

import java.util.ArrayList;

public class Generation {
	int generation;
	ArrayList<Chromosome> solutions = new ArrayList<>();
	double maxValue;
	int ind = 0;
	
	
	public Generation copy() {
		Generation gen = new Generation();
		gen.generation = this.generation;
		gen.maxValue = this.maxValue;
		for (int i = 0 ; i < solutions.size() ; i++) {
			gen.solutions.add(this.solutions.get(i).copy());
		}
		return gen;
	}
	
	public void setMaxValue() {
		double maxVal = 0;
		int local_ind = 0;
		for (int i = 0 ; i < solutions.size() ; i++) {
			if (solutions.get(i).fitness > maxVal) {
				maxVal = solutions.get(i).fitness;
				local_ind = i;
			}
		}
		maxValue = maxVal;
		this.ind = local_ind;
	}
}
