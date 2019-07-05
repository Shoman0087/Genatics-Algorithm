package application;

public class Chromosome implements Comparable<Chromosome>{
	int id;
	int numOfFeatures;
	double fitness;
	int[] genes;
	double accuracy;
	
	public Chromosome() {
		// TODO Auto-generated constructor stub
	}
	
	public Chromosome copy() {
		Chromosome ch = new Chromosome();
		ch.id = this.id;
		ch.numOfFeatures = this.numOfFeatures;
		ch.fitness = this.fitness;
		ch.genes = new int[this.genes.length];
		ch.accuracy = this.accuracy;
		for (int i = 0 ; i < genes.length ; i++)
			ch.genes[i] = this.genes[i];
		return ch;
	}
	
	@Override
	public int compareTo(Chromosome o) {
		if (this.fitness > o.fitness)
			return -1;
		else if (this.fitness < o.fitness)
			return 1;
		return 0;
	}
}
