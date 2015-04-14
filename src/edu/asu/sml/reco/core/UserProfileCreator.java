package edu.asu.sml.reco.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import edu.asu.sml.reco.ds.ProductItem;
import edu.asu.sml.reco.ds.User;
import edu.asu.sml.reco.scoring.SentimentScore;

public class UserProfileCreator {

	private UserSet userSet;
	private ItemSet itemSet;
	
	
	public UserProfileCreator(UserSet userSet, ItemSet itemSet) {
		super();
		this.userSet = userSet;
		this.itemSet = itemSet;
	}

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
	public void parseFileAndCreateUserProfiles(String filename) throws FileNotFoundException, IOException {
		InputStream gzipStream = new FileInputStream(filename);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		
		String line = null;
        SentimentScore sentimentScore = new SentimentScore();
        sentimentScore.prepareForScoring();

        while((line=bufferedReader.readLine())!= null) {
			//product/productId: B00002066I
			String productID = getValueFromKVPair(line);
			
			// product/title: ah
			//line = bufferedReader.readLine();
			//String title = getValueFromKVPair(line);
			
			// product/price: 15.99
			//line = bufferedReader.readLine();
			//double price = Double.parseDouble(getValueFromKVPair(line));
			
			// review/userId: unknown
			line = bufferedReader.readLine();
			String userID = getValueFromKVPair(line);
			
			// review/profileName: unknown
			//line = bufferedReader.readLine();
			//String profileName = getValueFromKVPair(line);
			
			// review/helpfulness: 3/4
			//line = bufferedReader.readLine();
			//String helpfulNess = getValueFromKVPair(line);
			
			//review/score: 5.0
			line = bufferedReader.readLine();
			double score = Double.parseDouble(getValueFromKVPair(line));
			
			// review/time: 939772800
			//line = bufferedReader.readLine();
			//long time = Long.parseLong(getValueFromKVPair(line));
			
			//review/summary: Inspiring
			line = bufferedReader.readLine();
			String summary = getValueFromKVPair(line);
			
			// review/text: <text>
			//line = bufferedReader.readLine();
			//String text = getValueFromKVPair(line);
			
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
			user.addFeatureValuesToProfile(productItem, userID, featureKeys.split("@"), 
					featurePhrases.split("@"), sentimentScore);
		}
		
		bufferedReader.close();
	}
}
