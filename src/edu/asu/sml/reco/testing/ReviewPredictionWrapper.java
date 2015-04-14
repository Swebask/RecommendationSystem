package edu.asu.sml.reco.testing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.core.UserSet;
import edu.asu.sml.reco.ds.ClusterMembership;
import edu.asu.sml.reco.ds.FeatureSet;
import edu.asu.sml.reco.ds.ProductItem;
import edu.asu.sml.reco.ds.User;

public class ReviewPredictionWrapper {
	
	private UserSet userSet;
	private ItemSet itemSet;
	private ClusterMembership clusterMemberships;

	private String getValueFromKVPair(String line) {
		return line.split(":")[1];
	}
	
	/**
	 * This method reads the txt.gz file line by line, creates user profiles and
	 * populates user features.
	 * @param filename
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void parseTestReviewsAndPredict(String filename) throws FileNotFoundException, IOException {
		InputStream gzipStream = new GZIPInputStream(new FileInputStream(filename));
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		
		String line = null;
		while((line=bufferedReader.readLine())!= null) {
			//product/productId: B00002066I
			String productID = getValueFromKVPair(line);
			
			// review/userId: unknown
			line = bufferedReader.readLine();
			String userID = getValueFromKVPair(line);
			
			line = bufferedReader.readLine();
			String featureKeys = getValueFromKVPair(line);
			
			line = bufferedReader.readLine();
			String featurePhrases = getValueFromKVPair(line);
			
			line = bufferedReader.readLine();
			//empty line ignore
			
			User user = userSet.getLinkedUserProfile(userID);
			ProductItem productItem = itemSet.getLinkedItemProfile(productID);
			if(productItem == null) {
				itemSet.addProductItemToMap(productID);
				productItem = itemSet.getLinkedItemProfile(productID);
			}
			
			FeatureSet setOfPredictedFeatures = predictReview(user, productItem, clusterMemberships);
			
			calculateAccuracy(setOfPredictedFeatures, featureKeys, featurePhrases);
		}
		
		bufferedReader.close();
	}

	private void calculateAccuracy(FeatureSet setOfPredictedFeatures,
			String featureKeys, String featurePhrases) {
		// TODO Auto-generated method stub
		
	}

	private FeatureSet predictReview(User user, ProductItem productItem, 
			ClusterMembership clusterMemberships2) {
		// TODO Auto-generated method stub
		return null;
	}
}
