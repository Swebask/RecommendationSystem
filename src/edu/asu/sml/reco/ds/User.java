package edu.asu.sml.reco.ds;

import edu.asu.sml.reco.scoring.SentimentScore;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * This class defines an User. This representation of user will be obtained after
 * we have parsed all the comments, extracted features and set feature-values to
 * represent an user. At this level, we do not need comment-level attributes or
 * product-level attributes
 * @author somak
 *
 */
public class User {

	@Getter (AccessLevel.PUBLIC) private String userID;
	@Getter (AccessLevel.PUBLIC) private FeatureSet setOfFeatures;
	
	public double getFeature(int index) {
		return setOfFeatures.getFeatureValue(index);
	}
	
	public double getFeature(String featureName) {
		return setOfFeatures.getFeatureValue(featureName);
	}
	
	public void setFeature(String featureName, double value) {
		setOfFeatures.setFeature(featureName, value);
	}

	public User(String userID) {
		super();
		this.userID = userID;
		this.setOfFeatures = new FeatureSet();
	}

	/**
	 * 
	 * @param productItem
	 * @param userID2
	 * @param featureKeys
	 * @param featurePhrases
	 */
	public void addFeatureValuesToProfile(ProductItem productItem,
			String userID2, String[] featureKeys, String[] featurePhrases) {
		for(int i=0; i < featureKeys.length; i++) {
			String featureName = featureKeys[i];
			double value = SentimentScore.getScore(featurePhrases[i]);
			setFeature(featureName, value);
			
		}
	}

	public FeatureSet getSetOfFeatures() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
