package application;
	
import java.io.File;
import java.io.StringBufferInputStream;
import java.util.ArrayList;
import java.util.List;

import RingIndicator.RingProgressIndicator;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	BorderPane root;
	GADriver driver;
	static String fileName;
	static String filePath;
	static File in;
	static TextField pathField;
	int inc = 0;
	
	
	@Override
	public void start(Stage primaryStage) {
		
		root = new BorderPane();
		Scene scene = new Scene(root,800,600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		
		VBox topBox = new VBox(20);
		topBox.setId("TOP");
		topBox.setAlignment(Pos.CENTER);
		
		pathField = new TextField();
		pathField.setMinWidth(300);
		pathField.setMinHeight(30);
		Label pathLabel = new Label("File Path");
		HBox box = new HBox(80);
		topBox.setAlignment(Pos.CENTER);
		topBox.setPadding(new Insets(20,0,0,50));
		Button browse = new Button("Browse");
		browse.setMinWidth(100);
		box.getChildren().addAll(pathLabel,pathField,browse);
		topBox.getChildren().add(box);
		
		Label initialPop = new Label("Initial Population size");
		Label numOfGenerations = new Label("Number Of Generations");
		Label ratioOfMutation = new Label("Ration Of Mutation (%)");

		
		TextField initialPopField = new TextField();
		initialPopField.setText("20");
		TextField numOfGenerationsField = new TextField();
		numOfGenerationsField.setText("100");
		TextField ratioOfMutationField = new TextField();
		ratioOfMutationField.setText("70");
		
		Button generate = new Button("Generate Knapsack");
		generate.setId("generate");;
		Button print = new Button("Print");
		Button showGenerationsInfo = new Button("Show Generations Info");
		
		
		GridPane grid = new GridPane();
		grid.setVgap(20);
		grid.setHgap(20);
		grid.setAlignment(Pos.TOP_LEFT);
		grid.setPadding(new Insets(80,0,0,50));
		
		grid.add(initialPop, 0, 0);
		grid.add(initialPopField, 1, 0);
		
		grid.add(numOfGenerations, 0, 1);
		grid.add(numOfGenerationsField, 1, 1);
		

		
		grid.add(ratioOfMutation, 0, 2);
		grid.add(ratioOfMutationField, 1, 2);
		

		
		grid.add(generate, 0, 3);
		grid.add(print, 1, 3);
		grid.add(showGenerationsInfo, 0, 4);
		
		root.setLeft(grid);
		RingProgressIndicator ringProgress = new RingProgressIndicator();
		ringProgress.makeIndeterminate();
		VBox right = new VBox();
		
		right.setAlignment(Pos.CENTER);
		right.setPadding(new Insets(50,20,200,0));
		right.getChildren().add(ringProgress);
		root.setRight(right);
		ringProgress.setProgress(10);
		primaryStage.setScene(scene);
		primaryStage.show();
		root.setTop(topBox);
		browse.setOnAction(e -> readOriginFileName());
		print.setOnAction(e -> print(driver));
		showGenerationsInfo.setOnAction(e -> showGenerationInfos());
		
		generate.setOnAction(e -> {
			int popSize = Integer.parseInt(initialPopField.getText());
			int generationsSize = Integer.parseInt(numOfGenerationsField.getText());
			double mutationRatio = Double.parseDouble(ratioOfMutationField.getText()) / 100.0;
			
			
			
			
			 driver = new GADriver(popSize);
			 driver.run(fileName);
			 
			 int burst = 100 / generationsSize;
			 inc = burst;
			 
			 for (int i = 0 ; i < generationsSize-1 ; i++) {
		    		System.out.println("inc        " + inc);
		                	driver.getOptimizedSolution(driver.train, driver.test, driver.driver);
		                	inc += burst;
//		                	ringProgress.setProgress(inc);

		       }
			 System.out.println("done");
//			 Task task = new Task<Void>() {
//				    @Override
//				    public Void call() throws Exception {
//				    	for (int i = 0 ; i < generationsSize-1 ; i++) {
//				    		System.out.println("inc        " + inc);
//				            Platform.runLater(new Runnable() {			             	
//				                @Override
//				                public void run() {
//				                	driver.getOptimizedSolution(driver.train, driver.test, driver.driver);
//				                	inc += burst;
//				                	ringProgress.setProgress(inc);
//				                }
//				            });
//				            Thread.sleep(3000);
//				        }
//				    	ringProgress.setProgress(100);
//						return null;
//				    }
//				};
//				Thread th = new Thread(task);
//				th.start();
		

		});
		
//		GADriver driver = new GADriver(20);
//		driver.run("Exactly.dat");
		
		
	}
	
	void print(GADriver driver) {
		for (int i = 0 ; i < driver.generations.size() ; i++) {
			driver.generations.get(i).setMaxValue();
			System.out.println("Generation : " + driver.generations.get(i).generation + " Value : " +  driver.generations.get(i).maxValue);
		}
		
		Stage reportStage = new Stage();
		BorderPane reportPane = new BorderPane();
		reportPane.setTop(setLineChart(driver.generations));
		
		
	
		TextArea output = new TextArea();
		setTheReport(output);

		reportPane.setBottom(output);
		Scene scene = new Scene(reportPane,800,600);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		reportStage.setScene(scene);
		reportStage.show();
		
	}
	
	 public void showGenerationInfos() {
			Stage infoStage = new Stage();
			BorderPane infoPane = new BorderPane();
			HBox topBox = new HBox(100);
			List<Integer> list = new ArrayList<>();
			Button show = new Button("Show");
			
			
			for (int i = 0 ; i < driver.generations.size() ; i++) {
				list.add(i+1);
			}
			
			ObservableList<Integer> options = 
				    FXCollections.observableArrayList(list);
			ComboBox<Integer> selectionMethodField = new ComboBox<>(options);
			
			selectionMethodField.setMinWidth(80);
			selectionMethodField.setValue(1);
			
			topBox.getChildren().add(selectionMethodField);
			topBox.getChildren().add(show);
			topBox.setAlignment(Pos.CENTER);
			infoPane.setTop(topBox);
			
			show.setOnAction(e -> {
				int choice = selectionMethodField.getValue();
				ScatterChart<Number,Number> chart = prepareScatter(choice);
				infoPane.setBottom(chart);
			});
			
			Scene scene = new Scene(infoPane,800,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());


			infoStage.setScene(scene);
			infoStage.show();
			
			
		 }
	
	public ScatterChart<Number,Number> prepareScatter(int ch) {
		  NumberAxis xAxis = new NumberAxis(0, driver.generations.get(ch-1).solutions.size(), 
				  driver.generations.get(ch-1).solutions.size()/20);
	         NumberAxis yAxis = new NumberAxis(0, driver.generations.get(ch-1).maxValue, 100);        
	         ScatterChart<Number,Number> sc = new ScatterChart<Number,Number>(xAxis,yAxis);
	        xAxis.setLabel("Solutions");                
	        yAxis.setLabel("Maximum Value");
	        sc.setTitle("Generation " + driver.generations.get(ch-1).generation);
	       
	        XYChart.Series series1 = new XYChart.Series();
	        
	        series1.setName("Generation " + driver.generations.get(ch-1).generation);
	        
	        for (int i = 0 ; i < driver.generations.get(ch-1).solutions.size() ; i++) {
	        		double val = driver.generations.get(ch-1).solutions
	        				.get(driver.generations.get(ch-1).solutions.size()-1-i).fitness;
	        		series1.getData().add(new XYChart.Data(i+1, val));
	        }
	        
	        sc.getData().add(series1);
	        
	        for (XYChart.Series<Number, Number> series : sc.getData()) {
	            //for all series, take date, each data has Node (symbol) for representing point
	            for (XYChart.Data<Number, Number> data : series.getData()) {
	              // this node is StackPane
	              StackPane stackPane =  (StackPane) data.getNode();
	              stackPane.setPrefWidth(3);
	              stackPane.setPrefHeight(3);
	            }
	          }

	        
	        return sc;
	 }
	
	 void setTheReport(TextArea area) {
		double maxValue = driver.generations.get(driver.generations.size()-1).maxValue;
		int ind = driver.generations.get(driver.generations.size()-1).ind;
		double accuracy = driver.generations.get(driver.generations.size()-1).solutions.get(ind).accuracy;
		int numberOfGenerations = driver.generations.size();
		
		area.appendText("Review : \n");
		area.appendText("Maximum Revinue : " + maxValue + "\n");
		area.appendText("Maximum Accuracy : " + accuracy + "\n");
		area.appendText("----------------------------------\n");
		
		area.appendText("Number Of generations used is : " + numberOfGenerations + "\n");
		area.appendText("Population Size is : " + driver.INITIAL_POPULATION +"\n");
		area.appendText("Mutation Ratio is : %" + (driver.MUTATION_RATIO * 100) + "\n" );
		area.appendText("Acuuracy of the dataset is " + driver.initialChromo.accuracy + "\n");
		area.appendText("Fitness of the dataset is " + driver.initialChromo.fitness + "\n");
		
		
		System.out.println(ind);
		
		System.out.println(driver.generations.get(driver.generations.size()-1).maxValue);
		
		int sizeee = driver.generations.get(driver.generations.size()-1).solutions.get(ind).genes.length;
		area.appendText("Features\n");
		for (int i = 0 ; i < sizeee ; i++) {
			int id = driver.generations.get(driver.generations.size()-1).solutions.get(ind).genes[i];
			if (id == 1) {
				area.appendText("Feature : " + i + "\n");
			}
		}
//		area.appendText(driver.generations.get(driver.generations.size()-1).solutions.get(ind).toString() + "\n");
	}
	
	static LineChart<Number, Number> setLineChart(ArrayList<Generation> generations) {
	  	NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Generations");
        yAxis.setLabel("Revinue");
        //creating the chart
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(xAxis,yAxis);
                
        lineChart.setTitle("Generations Values");
        //defining a series
        XYChart.Series series = new XYChart.Series();
        series.setName("Maximum Revinue in the Generation");
        //populating the series with data
        for (int i = 0 ; i < generations.size() ; i++) {
        		series.getData().add(new XYChart.Data<>(i+1,generations.get(i).maxValue));
        }
    
        lineChart.getData().add(series);
        return lineChart;
}
	
	static void readOriginFileName() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		in = fileChooser.showOpenDialog(null);
		filePath = in.getPath();
		String[] strings = filePath.split("/");
		fileName = strings[strings.length-1];
		pathField.setText(in.getPath());
		System.out.println(in.getPath());
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
