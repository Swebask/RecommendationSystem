/**TestDriver.java
 * 2:01:51 AM @author Arindam
 */
package edu.asu.sml.reco.core.prediction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

import edu.asu.sml.reco.core.ItemSet;
import edu.asu.sml.reco.core.UserSimilarityUtil;
import edu.asu.sml.reco.ds.FeatureSet;
import edu.asu.sml.reco.ds.ProductItem;
import edu.asu.sml.reco.ds.User;
import edu.asu.sml.reco.reader.Review;
import edu.asu.sml.reco.scoring.SentimentScore;

/**
 * @author Arindam
 *
 */
public class TestDriver {
	private ItemSet testModel;
	private ReviewPredictor predictor;
	private List<Double> errors;
	private List<Double> reviewErrorsCosine;
	private List<Double> reviewErrorsLp;
	public TestDriver() throws ClassNotFoundException, IOException{

		this.predictor = new ReviewPredictor();
		this.errors = new LinkedList<Double> ();
		this.reviewErrorsCosine = new LinkedList<Double> ();
		this.reviewErrorsLp = new LinkedList<Double> ();
		this.predictor.init();
	}



	public void test(String testFileName) throws IOException{
		/**
		 * Read each record from test set 
		 */
		InputStream gzipStream = new FileInputStream(testFileName);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		int N=1;
		double totalError=0,trv=0,trvlp=0;
		String line = null;
		SentimentScore sentimentScore = new SentimentScore();
        sentimentScore.prepareForScoring();
        
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
				N++;
				double actualScore = Double.valueOf(score);
				double predictedScore = Double.valueOf(review.getScore());
				if(predictedScore < 0.0){
					predictedScore = 0.0;
				}else if(predictedScore > 5.0){
					predictedScore = 5.0;
				}
				this.errors.add(actualScore-predictedScore);
				totalError+=Math.pow(actualScore-predictedScore, 2);
				
				FeatureSet givenFeatureSet = this.getFeatureSetForKeysPhrases(
						featureKeys.split("@"), featurePhrases.split("@"),
						sentimentScore);
				double rec = UserSimilarityUtil
						.getFeatureSetSimilarity(review.getFeatureVector(),
								givenFeatureSet);
				trv+=rec;
				this.reviewErrorsCosine.add(rec);
				
				double relp = UserSimilarityUtil
						.getFeatureSet_LpNormSimilarity(
								review.getFeatureVector(), givenFeatureSet, 2);
				this.reviewErrorsLp.add(relp);
				trvlp+=relp;
			}
			
		}
		
		totalError = Math.sqrt(totalError)/N;
		trv/=N;
		trvlp/=N;
		writeErrors(totalError,trv,trvlp);
		bufferedReader.close();
	}

	public FeatureSet getFeatureSetForKeysPhrases(String[] featureKeys,
			String[] featurePhrases, SentimentScore sentimentScore) throws IOException {

		FeatureSet featuresForThisReview = new FeatureSet();
        double value;

		for(int i = 1; i < featureKeys.length; i++) {
            value = 0.0;

            try{
            String featureName = featureKeys[i];
            
            String[] phrases = featurePhrases[i].split("#");

            for(String phrase:phrases)
				value += sentimentScore.getScore(phrase);
			value =  value/phrases.length;

            featuresForThisReview.setFeature(featureName, value);
            }catch(Exception e){
            	e.printStackTrace();
            }
		}
		return featuresForThisReview;
	}
	
	private String getValueFromKVPair(String line) {
		try{
		return line.split(":")[1];
		}catch(Exception e){
			return "";
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException {
		TestDriver testDriver;
		String testFileName = "./test.txt";
		try {
			testDriver = new TestDriver();
			testDriver.test(testFileName);
		} catch (FileNotFoundException e) {
		
		} catch (IOException e) {
			
		}
		
	}
	
	private void writeErrors(double err, double rec, double relp) throws IOException{
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("errorsDependencyBased.txt"), "utf-8"));
		writer.write(err+"\t"+rec+"\t"+relp+"\n");
		
		for(int i=0;i<this.errors.size();i++){
			writer.write(this.errors.get(i)+"\t"+this.reviewErrorsCosine.get(i)
					+"\t"+this.reviewErrorsLp.get(i)+"\n");
		}
		
		writer.close();
		
	}
	
}
