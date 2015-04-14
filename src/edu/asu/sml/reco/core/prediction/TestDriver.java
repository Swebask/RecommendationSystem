/**TestDriver.java
 * 2:01:51 AM @author Arindam
 */
package edu.asu.sml.reco.core.prediction;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.ds.ProductItem;
import edu.asu.sml.reco.ds.User;
import edu.asu.sml.reco.reader.Review;

/**
 * @author Arindam
 *
 */
public class TestDriver {
	private ItemSet testModel;
	private ReviewPredictor predictor;
	private List<Double> errors;
	public TestDriver() throws FileNotFoundException{

		this.predictor = new ReviewPredictor();
	}



	public void test() throws IOException{
		/**
		 * Read each record from test set 
		 */
		InputStream gzipStream = new FileInputStream("filename");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		int N=1;
		double totalError=0;
		String line = null;
		while((line=bufferedReader.readLine())!= null) {
			//product/productId: B00002066I
			String productID = getValueFromKVPair(line);


			// review/userId: unknown
			line = bufferedReader.readLine();
			String userID = getValueFromKVPair(line);

			//review/score: 5.0
			line = bufferedReader.readLine();
			double score = Double.parseDouble(getValueFromKVPair(line));

			// review/text: <text>
			line = bufferedReader.readLine();
			String text = getValueFromKVPair(line);

			line = bufferedReader.readLine();
			String featureKeys = getValueFromKVPair(line);

			line = bufferedReader.readLine();
			String featurePhrases = getValueFromKVPair(line);

			line = bufferedReader.readLine();
			//empty line ignore
			
			// call to predict
			Review review = this.predictor.predict(userID.trim(), productID.trim());
			
			//test accuracy between two review object
			if(!review.getScore().isEmpty()){
				double actualScore = Double.valueOf(score);
				double predictedScore = Double.valueOf(review.getScore());
				this.errors.add(actualScore-predictedScore);
				totalError+=Math.pow(actualScore-predictedScore, 2);
			}
			
		}
		bufferedReader.close();
	}

	private String getValueFromKVPair(String line) {
		return line.split(":")[1];
	}
}
