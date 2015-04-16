package edu.asu.sml.reco.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import edu.asu.sml.reco.ds.User;

public class UserSet {

	//private HashMap<String, User> idToUserMap = new HashMap<String, User>();
	private  ConcurrentNavigableMap<String,User> idToUserMap;
	private DB db;
	
	
	public UserSet(ConcurrentNavigableMap<String, User> idToUserMap, DB db) {
		super();
		this.idToUserMap = idToUserMap;
		this.db = db;
	}

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
	
	public void serializeToFile(String outputFileName) {
		try {
			OutputStream file = new FileOutputStream(outputFileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeInt(idToUserMap.size());
			for(Entry<String,User> entry:idToUserMap.entrySet()) {
				output.writeObject(entry);
			}
			output.close();
			System.out.println("User set exported to:" + outputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static UserSet deserializeFile(String inputFileName) {
		try {
			InputStream file = new FileInputStream(inputFileName);
		    InputStream buffer = new BufferedInputStream(file);
		    ObjectInput input = new ObjectInputStream (buffer);
		    int size = input.readInt();
		    DB db = DBMaker.newFileDB(new File("testdb"))
			           .closeOnJvmShutdown()
			           .make();
		    System.out.println("Total number of users:"+size);
		    ConcurrentNavigableMap<String,User> idToUserMap = db.getTreeMap("idToUserMap");
		    for(int i=0; i < size; i++) {
		    	Entry<String,User> entry = (Entry<String, User>) input.readObject();
		    	idToUserMap.put(entry.getKey(), entry.getValue());
		    	if(i%5000 == 0) {
		    		System.out.println(i + " users loaded...");
					db.commit();
		    	}
		    	
		    }
		    
		    input.close();
		    System.out.println("User set loading done...");
		    return new UserSet(idToUserMap, db);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return null;
	}
}
