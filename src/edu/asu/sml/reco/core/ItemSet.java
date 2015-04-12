package edu.asu.sml.reco.core;

import java.util.HashMap;

import edu.asu.sml.reco.ds.ProductItem;

public class ItemSet {
	private HashMap<String, ProductItem> idToProductItemMap = new HashMap<String, ProductItem>();
	
	public ProductItem getLinkedItemProfile(String itemID) {
		return idToProductItemMap.get(itemID);
	}
	
	public void addProductItemToMap(String itemId, String title, double price) {
		idToProductItemMap.put(itemId, new ProductItem(itemId, title, price));
	}
	
	public void addProductItemToMap(String itemId) {
		idToProductItemMap.put(itemId, new ProductItem(itemId));
	}
}
