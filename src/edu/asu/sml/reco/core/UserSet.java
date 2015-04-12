package edu.asu.sml.reco.core;

import java.io.File;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import edu.asu.sml.reco.ds.User;

public class UserSet {

	//private HashMap<String, User> idToUserMap = new HashMap<String, User>();
	private  ConcurrentNavigableMap<String,User> idToUserMap;
	private DB db;
	
	public UserSet() {
		db = DBMaker.newFileDB(new File("testdb"))
		           .closeOnJvmShutdown()
		           .make();
		idToUserMap = db.getTreeMap("idToUserMap");
	}
	
	public User getLinkedUserProfile(String userID) {
		if(!idToUserMap.containsKey(userID))  {
			idToUserMap.put(userID, new User(userID));
		}
		return idToUserMap.get(userID);
	}
	
	public void putInMap(String userID) {
		idToUserMap.put(userID, new User(userID));
		if(idToUserMap.size()%10000 == 0)
			db.commit();
	}
	
	public Set<String> getUserIDIterator() {
		return idToUserMap.keySet();
	}
}
