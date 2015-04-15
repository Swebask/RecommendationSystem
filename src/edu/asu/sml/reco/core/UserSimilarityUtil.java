package edu.asu.sml.reco.core;

import edu.asu.sml.reco.ds.FeatureSet;
import edu.asu.sml.reco.ds.User;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author somak
 *
 */
public class UserSimilarityUtil {

	public static double getUserUserSimilarity(User user1, User user2) {
        FeatureSet setOfFeaturesUser1 = user1.getSetOfFeatures();
        FeatureSet setOfFeaturesUser2 = user2.getSetOfFeatures();
        double sum = 0.0;

        Set<Integer> featureIndicesUser1 = setOfFeaturesUser1.getFeatureIndices();
        Set<Integer> featureIndicesUser2 = setOfFeaturesUser2.getFeatureIndices();
        Set<Integer> intersection = new HashSet<Integer>(featureIndicesUser1);

        intersection.retainAll(featureIndicesUser2);

        for (Integer featureIndex : intersection) {
            double scoreUser1 = setOfFeaturesUser1.getFeatureValue(featureIndex);
            double scoreUser2 = setOfFeaturesUser1.getFeatureValue(featureIndex);

            sum += scoreUser1*scoreUser2;
        }

        return sum/(Math.pow(intersection.size(), 2.0));
	}
}
