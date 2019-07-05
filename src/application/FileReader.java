package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReader {
	
	public ArrayList<Double>[] readFile(String fileName) {
		int featuresCount = preProcessing(fileName);
		ArrayList<Double>[] featuresValues = new ArrayList[featuresCount];
		File file = new File("Datasets/post/"+fileName);
		try {
			Scanner scan = new Scanner(file);
			int cc = 0;
			while (scan.hasNextLine()) {
				cc++;
				String line = scan.nextLine();
				String[] data = line.split(" ");
				for (int i = 0 ; i < data.length ; i++) {
					if (featuresValues[i] == null)
						featuresValues[i] = new ArrayList<>();
					featuresValues[i].add(Double.parseDouble(data[i]));
				}
//				if (cc == 1000)
//					break;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		for (int i = 0 ; i < featuresValues[0].size() ; i++) {
//			for (int j = 0 ; j < featuresValues.length ; j++) {
//				System.out.print(featuresValues[j].get(i) + " ");
//			}
//			System.out.println();
//		}
		return featuresValues;
	}
	
	public int preProcessing(String fileName) {
		System.out.println("5ara");
		File file = new File("Datasets/pre/" + fileName);
		int counter = 0;
		int max = 0;
		try {
			
			Scanner scan = new Scanner(file);
			PrintWriter out = new PrintWriter(new File("Datasets/post/" + fileName));
			int cc = 0;
			while (scan.hasNextLine()) {
				cc++;
				String line = scan.nextLine();
				String[] list = line.split(", ");
				
				
				String newLine = "";
				counter = 0;
				
//				for (int i = 0 ; i < list.length ; i++) {
//					counter++;
////					newLine += list[i];
//					if (i < list.length-1)
//						newLine +=list[i] +  " ";
//					else {
//						String num = list[i];
//						
//						num = num.substring(0,num.length()-1);
//						int nn = Integer.parseInt(num);
//						max = Math.max(nn, max);
//						newLine += num +"";
//					}
//				}
				
				for (int i = 0 ; i < line.length() ; i++) {
					if (Character.isDigit(line.charAt(i))) {
						newLine += line.charAt(i);
						if (i < line.length() - 1)
							newLine += " ";
						counter++;
					}
				}
				out.print(newLine + "\n");
//				if (cc == 1000)
//					break;
			}
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(max);
		return counter;
	}

}
