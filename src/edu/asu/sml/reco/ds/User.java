package edu.asu.sml.reco.ds;

import edu.asu.sml.reco.scoring.SentimentScore;
import lombok.AccessLevel;
import lombok.Getter;

import java.io.IOException;

/**
 * This class defines an User. This representation of user will be obtained after
 * we have parsed all the comments, extracted features and set feature-values to
 * represent an user. At this level, we do not need comment-level attributes or
 * product-level attributes
 * @author somak
 *
 */
public class User implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
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
			String userID2, String[] featureKeys, String[] featurePhrases, SentimentScore sentimentScore) throws IOException {

		FeatureSet featuresForThisReview = new FeatureSet();
        double value;

		for(int i = 1; i < featureKeys.length; i++) {
            value = 0.0;
            
            if(featurePhrases.length <= i)
            	continue;
            String featureName = featureKeys[i];
            String[] phrases = featurePhrases[i].split("#");

            for(String phrase:phrases)
				value += sentimentScore.getScore(phrase);
			value =  value/phrases.length;

            featuresForThisReview.setFeature(featureName, value);
		}
		setOfFeatures.aggregateNewSetOfFeatures(featuresForThisReview);
		productItem.addUserIdAndFeatures(userID2, featuresForThisReview);
	}
}
