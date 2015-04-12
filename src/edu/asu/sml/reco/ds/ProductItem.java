package edu.asu.sml.reco.ds;

import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * This class encodes a particular item -i.e. music/song/album. 
 * This also links the set of users that has commented/reviewed the product.
 * @author somak
 *
 */
public class ProductItem {

	@Getter (AccessLevel.PUBLIC) private String productID;
	@Getter (AccessLevel.PUBLIC) private String title;
	@Getter (AccessLevel.PUBLIC) private double price;
	
	@Getter (AccessLevel.PUBLIC) private Set<String> userIDSet;

	public ProductItem(String productID) {
		super();
		this.productID = productID;
		userIDSet = new HashSet<String>();
	}
	
	public ProductItem(String productID, String title, double price) {
		super();
		this.productID = productID;
		this.title = title;
		this.price = price;
		userIDSet = new HashSet<String>();
	}
	
	public void addUserId(String userId) {
		userIDSet.add(userId);
	}
	
}
