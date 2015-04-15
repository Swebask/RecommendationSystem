/**ItemProductRatingReader.java
 * 2:29:33 PM @author Arindam
 */
package edu.asu.sml.reco.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arindam
 *
 */
public class ItemProductRatingReader {

	private BufferedReader reader=null;
	private Map<String,Double> userScore;
	public ItemProductRatingReader() throws IOException{
		
		InputStream fileStream = new FileInputStream("./parsedReviewTraining.txt");
		Reader decoder = new InputStreamReader(fileStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
		this.userScore = new HashMap<String,Double>();
	}

	public Map<String,Double> read() throws IOException{
		String line = null;
		while((line=reader.readLine())!= null) {
			//product/productId: B00002066I
			String productID = getValueFromKVPair(line);


			// review/userId: unknown
			line = reader.readLine();
			String userID = getValueFromKVPair(line);

			//review/score: 5.0
			line = reader.readLine();
			Double score;
			try{
				score = Double.parseDouble(getValueFromKVPair(line));
			}catch(Exception e){
				score = null;
			}

			// review/text: <text>
			line = reader.readLine();
			line = reader.readLine();
			line = reader.readLine();
			line = reader.readLine();
			//empty line ignore

			if(score==null||userID==null){
				continue;
			}

			this.userScore.put(productID+"-"+userID, score);
		}
		
		return this.userScore;

	}

	private String getValueFromKVPair(String line) {
		return line.split(":")[1];
	}

}
