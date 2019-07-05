package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import KNN.KNNDriver;
import KNN.Point;

public class GADriver {
	
	public double FULL_ACCURACY = 0;
	public int INITIAL_POPULATION = 0;
	public double FULL_FITTNES = 0;
	public double MUTATION_RATIO = 0.3;
	ArrayList<Generation> generations = new ArrayList<>();
	Point[] test;
	Point[] train;
	KNNDriver driver;
	Chromosome initialChromo ;
	
	public GADriver() {
		// TODO Auto-generated constructor stub
	}
	
	public GADriver(int initialPOP) {
		this.INITIAL_POPULATION = initialPOP;
	}
	
	public void run(String fileName) {
		
		
		// read the data file
		FileReader reader = new FileReader();
		ArrayList<Double>[] featureValues = reader.readFile(fileName);
		
		// convert the data into points that fit with my algorithm
		Point[] data = convertToPoints(featureValues);
		System.out.println("data length = " + data.length);
		//split data into training set and testing set
		double trainRatio = 0.8;
		double testRatio = 1-trainRatio;
		int trainCount = (int)(trainRatio * data.length);
		int testCount = (int) (testRatio * data.length);
		
		
		 test = new Point[testCount];
		 train = new Point[trainCount];
		splitToTriningAndTest(data,train ,test, trainCount, testCount);
		
		System.out.println("train length = " + train.length);
		System.out.println("test data = " + test.length);

		
		// test the data without feature selection
		 driver = new KNNDriver();
		int[] selected = new int[test[0].cord.length];
		Arrays.fill(selected, 1);

		
		 initialChromo = new Chromosome();
		 initialChromo.genes = selected;
		double fitness = fittness(selected, train, test, driver, selected.length, initialChromo);
		initialChromo.fitness = fitness;
		FULL_FITTNES = fitness;
		System.out.println("accurcy = " + fitness);
		System.out.println(initialChromo.accuracy);
		System.out.println(INITIAL_POPULATION);
		generatePopulation(train,test,driver);

	}
	
	public void getOptimizedSolution(Point[] train, Point[] test,KNNDriver driver) {
		Generation gen = generations.get(generations.size()-1).copy();
		int size = gen.solutions.size();
		double maxVal = gen.maxValue;
		int generationId = gen.generation;
		boolean dropped = false;
		int count = 0;
		while (!(gen.maxValue > maxVal && dropped)) {
//			System.out.println(gen.maxValue);
			count++;
			Chromosome[] selected = new Chromosome[2];
			selected= rouletWheel(gen);
			
//			for (int i = 0 ; i < selected[0].genes.length ; i++) 
//				System.out.print(selected[0].genes[i] + " ");
//			System.out.println();
			
			crossover(selected[0], selected[1], gen,train,test,driver);
			
			if (gen.solutions.size() > INITIAL_POPULATION*2) {			
				Collections.sort(gen.solutions);
				gen.solutions =  new ArrayList<>(gen.solutions.subList(0, INITIAL_POPULATION));
				dropped = true;
				gen.setMaxValue();
			}	
			
			if (count > INITIAL_POPULATION*6) {
				Collections.sort(gen.solutions);
				gen.solutions =  new ArrayList<>(gen.solutions.subList(0, INITIAL_POPULATION));
				dropped = true;
				gen.setMaxValue();
				break;
			}
		}
		
		gen.generation = generationId+1;
		gen.setMaxValue();
//		System.out.println(gen.maxValue);
		if (gen.maxValue > generations.get(generations.size()-1).maxValue) {		
			System.out.println("New Max = " + gen.maxValue);
			generations.add(gen);
		} else {			
			
			generations.add(generations.get(generations.size()-1).copy());
			generations.get(generations.size()-1).generation = generationId+1;
			System.out.println("Current Max = " +(generationId+1)  + " with maximum value  " + generations.get(generations.size()-1).maxValue);
		}
		
	}
	
	public void crossover(Chromosome ch1, Chromosome ch2, Generation gen, Point[] train, Point[] test,KNNDriver driver) {
		int crossIndex = (int) (Math.random() * ch2.genes.length/2) + ch2.genes.length/4;
		int[] chromo1 = new int[ch1.genes.length];
		int[] chromo2 = new int[ch2.genes.length];
		
		
		
		for (int i = 0 ; i < crossIndex  ; i++) {
			chromo1[i] = ch1.genes[i];
			chromo2[i] = ch2.genes[i];
		}
		
		for (int i = crossIndex ; i < ch1.genes.length ; i++) {
			chromo1[i] = ch2.genes[i];
			chromo2[i] = ch1.genes[i];
		}
		
		int numOfItems1 = 0;
		int numOfItems2 = 0;
		
		for (int i = 0 ; i < chromo1.length ; i++) {
			if (chromo1[i] == 1)
				numOfItems1++;
		}
		for (int i = 0 ; i < chromo2.length ; i++) {
			if (chromo1[i] == 1)
				numOfItems2++;
		}
		
		
		Chromosome chromosom1 = new Chromosome();
		chromosom1.genes = chromo1;
		Chromosome chromosom2 = new Chromosome();
		chromosom2.genes = chromo2;

		
		
		
		double rand = Math.random();
		if(rand >= MUTATION_RATIO) {
			mutation(chromosom1, gen,train,test,driver,numOfItems1);
			mutation(chromosom2, gen,train,test,driver,numOfItems2);
		} else {
			
			//for chromosome1
			double fittness1 = fittness(chromosom1.genes, train, test, driver, numOfItems1, chromosom1);
			if (fittness1 >= FULL_FITTNES) {
				chromosom1.fitness = fittness1;
				gen.solutions.add(chromosom1);
			}
			
			double fittness2 = fittness(chromosom2.genes, train, test, driver, numOfItems2, chromosom2);
			if (fittness2 >= FULL_FITTNES) {
				chromosom2.fitness = fittness2;
				gen.solutions.add(chromosom2);
			}
		}
	}
	
	public void mutation(Chromosome solution, Generation gen, Point[] train, Point[] test,KNNDriver driver, int numOfItems) {
		int count = 0;
//		for (int i = 0 ; i < solution.genes.length ; i++) 
//			System.out.print(solution.genes[i] + " ");
//		System.out.println();
		double fittness = fittness(solution.genes, train, test, driver, numOfItems,solution);
		while (fittness < FULL_FITTNES) {
			int[] genes = solution.genes;
			int randInd = (int) (Math.random() * genes.length);
			if (genes[randInd] == 1)
				genes[randInd] = 0;
			else
				genes[randInd] = 1;
			solution.genes = genes;
			count++;
			if (count == 10 )
				break;
			int cc = 0;
			for (int i = 0 ; i < genes.length ; i++) {
				if (genes[i] == 1)
					cc++;
			}
			numOfItems = cc;
			
			fittness = fittness(solution.genes, train, test, driver, numOfItems,solution);
		}
		
		if (fittness >= FULL_FITTNES) {
			solution.fitness = fittness;
			solution.numOfFeatures = numOfItems;
			gen.solutions.add(solution);		
		}
		
		
	}
	
	public Chromosome[] rouletWheel(Generation gen) {
			
			double sum = 0;
			double start = 0;
			ArrayList<Individual> wheel = new ArrayList<>();
			for (int i = 0 ; i < gen.solutions.size() ; i++) {
				sum += gen.solutions.get(i).fitness;
			}
			
			Collections.sort(gen.solutions);
			for (int i = 0 ; i < gen.solutions.size() ; i++) {
				double ratio = ((double)gen.solutions.get(i).fitness / (double)sum) * 1000;
				
				wheel.add(new Individual(start,start+ratio,gen.solutions.get(i)));
				start += ratio;
			}
			
			double rand = (Math.random() * 1000);
			Chromosome[] selected = new Chromosome[2];
			for (int i = 0 ; i < wheel.size() ; i++) {
				if (wheel.get(i).start <= rand && wheel.get(i).end > rand) {
					selected[0] = wheel.get(i).chromo;
					break;
				}
					
			}
			 rand = (Math.random() * 1000);
			for (int i = 0 ; i < wheel.size() ; i++) {
				if (wheel.get(i).start <= rand && wheel.get(i).end > rand) {
					selected[1] = wheel.get(i).chromo;
					break;
				}
					
			}
			
			return selected;
				
	}
	
	public void generatePopulation(Point[] train, Point[] test,KNNDriver driver) {
		System.out.println("test");
		int counter = 0;
		int range = test[0].cord.length;
		Chromosome[] initialSolutions = new Chromosome[INITIAL_POPULATION];
		Generation gen = new Generation();
		double max = 0;
		while (counter != INITIAL_POPULATION) {
			
			int numOfItems = (int) Math.ceil(Math.random() * range);
			int[] randomIndexes = generateListOfIndexes(range, numOfItems);
			Chromosome ch = new Chromosome();
			ch.genes = new int[range];
			for (int i = 0 ; i < randomIndexes.length ; i++) {
				ch.genes[randomIndexes[i]] = 1;
			}
			
			double selectedAccuracy = fittness(ch.genes, train, test, driver,numOfItems,ch);
			if (selectedAccuracy >= FULL_ACCURACY) {
//				System.out.println(selectedAccuracy);
				ch.fitness = selectedAccuracy;
				max = Math.max(max, selectedAccuracy);
				ch.id = counter;
				ch.numOfFeatures = numOfItems;
				initialSolutions[counter] = ch;
				gen.solutions.add(ch);
				counter++;
			}		
		}
		System.out.println("max  " + max);
		
		gen.generation = 0;
		gen.maxValue = max;
		gen.setMaxValue();
		generations.add(gen);

	}
	
	boolean contain(ArrayList<Chromosome> list, double acc) {
		for (int i = 0 ; i < list.size() ; i++) {
			if (list.get(i).fitness == acc)
				return true;
		}
		return false;
	}
	public int[] generateListOfIndexes(int range, int numOfIndexs) {
		int[] arr = new int[range];
		for (int i = 0 ; i < range ; i++)
			arr[i] = i;
		int last = range - 1;
		int[] randomIndexes = new int[numOfIndexs];
		for (int i = 0 ; i < numOfIndexs ; i++) {
			int randIndex = (int) Math.ceil(Math.random() * (last));
			randomIndexes[i] = arr[randIndex];
			arr[randIndex] = arr[last];
			last--;
		}
		return randomIndexes;
	}
	
	public double fittness(int[] selected, Point[] train, Point[] test,KNNDriver driver, int numOfItems, Chromosome ch) {
		double accuracy = driver.calcAccuracy(train, test,selected);
		ch.accuracy = accuracy;
		double alpha = 0.9;
		double beta = 0.1;
		double totalItems = selected.length;
		double fittness = (alpha * accuracy) + (beta * (1 - ((double)numOfItems/totalItems))) ;
		return fittness;

	}
	
	public Point[] convertToPoints(ArrayList<Double>[] featureValues) {
		System.out.println(featureValues.length);
		Point[] points = new Point[featureValues[0].size()];
		for (int i = 0 ; i < featureValues[0].size() ; i++) {
			points[i] = new Point();
			points[i].cord = new double[featureValues.length-1];
			for (int j = 0 ; j < featureValues.length-1 ; j++) {
				points[i].cord[j] = featureValues[j].get(i);
			}
			
			points[i].label = featureValues[featureValues.length-1].get(i) + "";
//			System.out.println(points[i].label);
		}
		return points;
		
	}
	
	public void splitToTriningAndTest(Point[] data,Point[] train, Point[] test, int trainCount, int testCount) {
		
		
		ArrayList<Point> allPoints = new ArrayList<>();
		ArrayList<Point> trainPoints = new ArrayList<>();
		ArrayList<Point> testPoints = new ArrayList<>();
		
		
		
		for (int i = 0 ; i < data.length ; i++) {
			allPoints.add(data[i]);
		}
		
		while (allPoints.size() > 0 && trainCount != 0) {
			int randId = (int)Math.floor(Math.random() * allPoints.size());
			trainPoints.add(allPoints.get(randId));
			allPoints.remove(randId);
			trainCount--;
		}
		
		while (allPoints.size() > 0 && testCount != 0) {
			int randId = (int)Math.floor(Math.random() * allPoints.size());
			testPoints.add(allPoints.get(randId));
			allPoints.remove(randId);
			testCount--;
		}
		
		
		
		for (int i = 0 ; i < trainPoints.size() ; i++)
			train[i] = trainPoints.get(i);
		
		for (int i = 0 ; i < testPoints.size() ; i++)
			test[i] = testPoints.get(i);
		
	}

}
