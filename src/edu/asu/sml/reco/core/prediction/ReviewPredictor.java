/**ReviewPredictor.java
 * 2:12:02 AM @author Arindam
 */
package edu.asu.sml.reco.core.prediction;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.core.UserSet;
import edu.asu.sml.reco.ds.FeatureSet;
import edu.asu.sml.reco.ds.ProductItem;
import edu.asu.sml.reco.ds.UserIDLookupTable;
import edu.asu.sml.reco.reader.ItemProductRatingReader;
import edu.asu.sml.reco.reader.Review;
import edu.asu.sml.reco.training.ModelTrainer;
import edu.ucla.sspace.matrix.Matrix;

/**
 * @author Arindam
 *
 */
public class ReviewPredictor {
	private ItemSet trainingModel;
	private Map<String,Double> rating;
	private Matrix userUserSim;
	public ReviewPredictor(){
		/**
		 *
		 */
		this.trainingModel = ItemSet.deserializeFile("./itemSetOutput");
	}
	public void init() throws ClassNotFoundException, IOException{
		/**
		 * init model
		 * read item, user, rating map
		 */
		this.rating = new ItemProductRatingReader().read();
		this.userUserSim = ModelTrainer.createUserUserMatrix(
				UserSet.deserializeFile("./userOutput.txt"));
	}
	
	public Review predict(String userId, String itemId){
		/**
		 * Read the user cluster
		 * From the user cluster get users 
		 * From ItemSet get the corresponding reviews
		 * 
		 */

		Review prediction = new Review();
		prediction.setProductId(itemId);
		prediction.setUserId(userId);

		ProductItem item = this.trainingModel.getLinkedItemProfile(itemId);
		Integer userIndex = UserIDLookupTable.lookUp(userId);
		if(item==null|| userIndex==null){
			prediction.setScore("");
			return prediction;
		}else{
			Double base = UserIDLookupTable.getAveragerating(userId);
			Double tot = 0.0;
			Double ratingShift = 0.0;
			FeatureSet values = new FeatureSet();
			for(Entry<String, FeatureSet> entry : item.getAllReviewsOnThisItem()){
				/**
				 * take the weighted average of the feature set
				 */
				Integer ind = UserIDLookupTable.lookUp(entry.getKey());
				Double avgRating = UserIDLookupTable.getAveragerating(entry.getKey());

				Double sim = this.userUserSim.get(userIndex, ind);
				tot+=sim;
				ratingShift += sim*(getRating(itemId,entry.getKey())-avgRating);
				values.merge(entry.getValue(), sim);
			}
			if(tot>0.0){
				values.scale(tot);
				ratingShift = ratingShift/ tot;
				base+=ratingShift;
			}
			prediction.setScore(base.toString());
			prediction.setFeatureVector(values);
		}
		return prediction;
	}

	private Double getRating(String userId, String itemId){
		return this.rating.get(itemId+"-"+userId);	
	}
}
