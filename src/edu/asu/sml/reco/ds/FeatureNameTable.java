package edu.asu.sml.reco.ds;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Stores individual feature names and their corresponding indices. If each individual feature
 * need more attributes, then we need to add another class called Feature and define a HashMap
 * from String to Feature.
 * @author somak
 *
 */
public class FeatureNameTable {

	private static HashMap<String, Integer> stringFeaturesToIndexMap = new HashMap<String, Integer>();
	private static int size;
	
	public static void populateFeatureNames() throws IOException {
		//TODO iterate over feature names and populate hashmap.
		InputStream gzipStream = new FileInputStream("./allFeature.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		String line = null;
		int i=0;
		while((line=bufferedReader.readLine())!= null) {
			if(!line.isEmpty()){
				stringFeaturesToIndexMap.put(line.trim(), i++);
			}
		}
		bufferedReader.close();
	}
	
	public static int getSize() {
		return size;
	}
	
	public static int lookUp(String featureName) {
		return stringFeaturesToIndexMap.get(featureName);
	}
	
	public static void main(String args[]) throws IOException{
		FeatureNameTable.populateFeatureNames();
	}
}
