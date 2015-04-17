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

	public static double getFeatureSetSimilarity(FeatureSet feature1, FeatureSet feature2) {
        double sum = 0.0;

        Set<Integer> featureIndicesUser1 = feature1.getFeatureIndices();
        Set<Integer> featureIndicesUser2 = feature2.getFeatureIndices();
        Set<Integer> intersection = new HashSet<Integer>(featureIndicesUser1);

        intersection.retainAll(featureIndicesUser2);

        double normSum1 = 0.0;
        double normSum2 = 0.0;
        for (Integer featureIndex : intersection) {
            double scoreUser1 = feature1.getFeatureValue(featureIndex);
            double scoreUser2 = feature2.getFeatureValue(featureIndex);

            sum += scoreUser1*scoreUser2;
            normSum1 += scoreUser1*scoreUser1;
            normSum2 += scoreUser2*scoreUser2;
        }

        return intersection.size() == 0? 0.0 : sum/(Math.sqrt(normSum1)*Math.sqrt(normSum2));
	}
	
	public static double getFeatureSet_LpNormSimilarity(FeatureSet feature1, FeatureSet feature2, int power) {
        double sum = 0.0,sum1=0.0,sum2=0.0;

        Set<Integer> featureIndicesUser1 = feature1.getFeatureIndices();
        Set<Integer> featureIndicesUser2 = feature2.getFeatureIndices();
        Set<Integer> intersection = new HashSet<Integer>(featureIndicesUser1);

        intersection.retainAll(featureIndicesUser2);
        
        for (Integer featureIndex : intersection) {
            double scoreUser1 = feature1.getFeatureValue(featureIndex);
            double scoreUser2 = feature2.getFeatureValue(featureIndex);

            sum1+= Math.pow(scoreUser1, 2);
            sum2+= Math.pow(scoreUser2, 2);
        }

        for (Integer featureIndex : intersection) {
            double scoreUser1 = feature1.getFeatureValue(featureIndex);
            double scoreUser2 = feature2.getFeatureValue(featureIndex);

            sum += Math.pow(scoreUser1/sum1-scoreUser2/sum2,2);
           
        }

        Set<Integer> onlyFeature1Indices =  new HashSet<Integer>(featureIndicesUser1);
        onlyFeature1Indices.removeAll(intersection);
        for (Integer featureIndex : onlyFeature1Indices) {
            double scoreUser1 = feature1.getFeatureValue(featureIndex);

            sum += Math.pow(scoreUser1/sum1-0.5,2);
        }
        Set<Integer> onlyFeature2Indices =  new HashSet<Integer>(featureIndicesUser2);
        onlyFeature2Indices.removeAll(intersection);
        for (Integer featureIndex : onlyFeature2Indices) {
            double scoreUser2 = feature1.getFeatureValue(featureIndex);

            sum += Math.pow(0.5-scoreUser2/sum2,2);
        }
        return Math.sqrt(sum);
	}
	
	public static double getUserUserSimilarity(User user1, User user2) {
        FeatureSet setOfFeaturesUser1 = user1.getSetOfFeatures();
        FeatureSet setOfFeaturesUser2 = user2.getSetOfFeatures();
        double sum = 0.0;

        Set<Integer> featureIndicesUser1 = setOfFeaturesUser1.getFeatureIndices();
        Set<Integer> featureIndicesUser2 = setOfFeaturesUser2.getFeatureIndices();
        Set<Integer> intersection = new HashSet<Integer>(featureIndicesUser1);

        intersection.retainAll(featureIndicesUser2);

        double normSum1 = 0.0;
        double normSum2 = 0.0;
        for (Integer featureIndex : intersection) {
            double scoreUser1 = setOfFeaturesUser1.getFeatureValue(featureIndex);
            double scoreUser2 = setOfFeaturesUser2.getFeatureValue(featureIndex);

            sum += scoreUser1*scoreUser2;
            normSum1 += scoreUser1*scoreUser1;
            normSum2 += scoreUser2*scoreUser2;
        }

        double sim =  intersection.size() == 0? 0.0 : sum/(Math.sqrt(normSum1)*Math.sqrt(normSum2));//(Math.pow(intersection.size(), 2.0));
        if(sim<0){
        	sim = 0-sim;
        }
        return sim;
	}
}
