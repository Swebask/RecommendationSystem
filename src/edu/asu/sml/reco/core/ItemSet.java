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
import java.util.concurrent.ConcurrentNavigableMap;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import edu.asu.sml.reco.ds.ProductItem;

public class ItemSet {
	private ConcurrentNavigableMap<String, ProductItem> idToProductItemMap;
	private DB db;
	private static int counter=1;
	
	public ItemSet(ConcurrentNavigableMap<String, ProductItem> idToProductItemMap, DB db) {
		super();
		this.idToProductItemMap = idToProductItemMap;
		this.db = db;
	}
	
	public ItemSet() {
		db = DBMaker.newFileDB(new File("testdb"+counter++))
		           .closeOnJvmShutdown()
		           .make();
		idToProductItemMap = db.getTreeMap("idToProductItemMap");
	}
	
	public ProductItem getLinkedItemProfile(String itemID) {
		return idToProductItemMap.get(itemID);
	}
	
	public void addProductItemToMap(String itemId, String title, double price) {
		idToProductItemMap.put(itemId, new ProductItem(itemId, title, price));
	}
	
	public void addProductItemToMap(String itemId) {
		idToProductItemMap.put(itemId, new ProductItem(itemId));
	}
	
	public void serializeToFile(String outputFileName) {
		try {
			OutputStream file = new FileOutputStream(outputFileName);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			output.writeInt(idToProductItemMap.size());
			for(Entry<String,ProductItem> entry:idToProductItemMap.entrySet()) {
				output.writeObject(entry);
			}
			output.close();
			System.out.println("Item set exported to:" + outputFileName);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static ItemSet deserializeFile(String inputFileName) {
		try {
			InputStream file = new FileInputStream(inputFileName);
		    InputStream buffer = new BufferedInputStream(file);
		    ObjectInput input = new ObjectInputStream (buffer);
		    int size = input.readInt();
		    DB db = DBMaker.newFileDB(new File("testdb"+counter++))
			           .closeOnJvmShutdown()
			           .make();
		    ConcurrentNavigableMap<String, ProductItem> idToProductItemMap = db.getTreeMap("idToProductItemMap");
		    for(int i=0; i < size; i++) {
		    	Entry<String,ProductItem> entry = (Entry<String, ProductItem>) input.readObject();
		    	idToProductItemMap.put(entry.getKey(), entry.getValue());
		    	if(idToProductItemMap.size()%10000 == 0)
					db.commit();
		    }
		    
		    input.close();
		    return new ItemSet(idToProductItemMap, db);
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
