/**ReviewPredictor.java
 * 2:12:02 AM @author Arindam
 */
package edu.asu.sml.reco.core.prediction;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.reader.Review;

/**
 * @author Arindam
 *
 */
public class ReviewPredictor {
	private ItemSet trainingModel;
	public ReviewPredictor(){
		/**
		 *
		 */
		this.trainingModel = ItemSet.deserializeFile("");
	}
	public void init(){
		/**
		 * init model
		 */
	}
	public Review predict(String userId, String itemId){
		/**
		 * Read the user cluster
		 * From the user cluster get users 
		 * From ItemSet get the corresponding reviews
		 */
		
		return null;
	}
}
