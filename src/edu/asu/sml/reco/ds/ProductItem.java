package edu.asu.sml.reco.ds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * This class encodes a particular item -i.e. music/song/album. 
 * This also links the set of users that has commented/reviewed the product.
 * @author somak
 *
 */
public class ProductItem implements java.io.Serializable {

	@Getter (AccessLevel.PUBLIC) private String productID;
	@Getter (AccessLevel.PUBLIC) private String title;
	@Getter (AccessLevel.PUBLIC) private double price;
	
	@Getter (AccessLevel.PUBLIC) private HashMap<String,FeatureSet> userIDToFeatures;

	public ProductItem(String productID) {
		super();
		this.productID = productID;
		userIDToFeatures = new HashMap<String,FeatureSet>();
	}
	
	public ProductItem(String productID, String title, double price) {
		super();
		this.productID = productID;
		this.title = title;
		this.price = price;
		userIDToFeatures = new HashMap<String,FeatureSet>();
	}
	
	public void addUserIdAndFeatures(String userId, FeatureSet setOfFeatures) {
		userIDToFeatures.put(userId, setOfFeatures);
	}
	
	public FeatureSet getFeaturesForUserId(String userId) {
		return userIDToFeatures.get(userId);
	}
	
	public Set<Entry<String, FeatureSet>> getAllReviewsOnThisItem(){
		return this.userIDToFeatures.entrySet();
	}
}
