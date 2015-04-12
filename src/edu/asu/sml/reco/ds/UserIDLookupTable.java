package edu.asu.sml.reco.ds;

import java.util.HashMap;

public class UserIDLookupTable {
	private static HashMap<String, Integer> usersToIndexMap = new HashMap<String, Integer>();
	private static int size;
	
	public static void populateFeatureNames() {
		//TODO iterate over feature names and populate hashmap.
	}
	
	public static int getSize() {
		return size;
	}
	
	public static int lookUp(String userID) {
		return usersToIndexMap.get(userID);
	}
}
