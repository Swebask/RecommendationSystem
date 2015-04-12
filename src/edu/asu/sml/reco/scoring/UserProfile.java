package edu.asu.sml.reco.scoring;

import edu.asu.sml.reco.ds.User;

public class UserProfile {
	// {feature : [score, numberOfOccurances]}
//	public void updateUserProfileFor(User user, String feature, Double currentSentimentScore) {
//		Double[] scoreFrequencyArray = user.getFeature(feature);
//		double existingScore = scoreFrequencyArray[0];
//		double existingFrequnecy = scoreFrequencyArray[1];
//		double newFrequnecy = existingFrequnecy+1;
//		double newScore = (existingScore+currentSentimentScore);
//
//		user.setFeature(feature, new Double[] {newScore, newFrequnecy});
//	}
//
//	public Double getScoreFor(String feature) {
//		Double[] scoreFrequencyArray = user.getFeature(feature);
//		double existingScore = scoreFrequencyArray[0];
//		double existingFrequnecy = scoreFrequencyArray[1];
//
//		return existingScore/existingFrequnecy; //average of all the scores across reviews for this feature
//	}
}