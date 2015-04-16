package edu.asu.sml.reco.ds;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map.Entry;

public class UserIDLookupTable {
	private static HashMap<String, Entry<Integer,Double>> usersToIndexMap = 
			new HashMap<String, Entry<Integer,Double>>();
	private static int size;
	static {
		try {
			UserIDLookupTable.populateFeatureNames();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void populateFeatureNames() throws IOException {
		
		InputStream gzipStream = new FileInputStream("./allUsersAverage.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		String line = null;
		int i=0;
		while((line=bufferedReader.readLine())!= null) {
			if(!line.isEmpty()){
				String[] parts = line.split(":");
				usersToIndexMap.put(parts[0].trim(), new AbstractMap.SimpleEntry<Integer,Double>(i++, 
						Double.valueOf(parts[1])));
			}
		}
		bufferedReader.close();
	}

	public static int getSize() {
		return usersToIndexMap.size();
	}

	public static Integer lookUp(String userID) {
		if(usersToIndexMap.get(userID)==null){
			return null;
		}
		return usersToIndexMap.get(userID).getKey();
	}
	
	public static Double getAveragerating(String userID){
		if(usersToIndexMap.get(userID)==null){
			return null;
		}
		return usersToIndexMap.get(userID).getValue();
	}
}
