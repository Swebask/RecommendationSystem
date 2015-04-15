/**WriteUsersAverage.java
 * 5:18:03 PM @author Arindam
 */
package edu.asu.sml.reco.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Arindam
 *
 */
public class WriteUsersAverage {
	private BufferedWriter writerFeature=null;
	private BufferedReader reader=null;
	private Map<String,Entry<Integer,Double>> userScore;
	public WriteUsersAverage() throws IOException{
		writerFeature = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("allUsersAverage.txt"), "utf-8"));
		InputStream fileStream = new FileInputStream("C:\\Users\\Arindam\\git\\RecommendationSystem\\parsedReviewTraining.txt");
		Reader decoder = new InputStreamReader(fileStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
		this.userScore = new HashMap<String,Entry<Integer,Double>>();
	}

	public void write() throws IOException{
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

			if(score==null){
				continue;
			}
			
			Entry e;
			if(!this.userScore.containsKey(userID)){
				e = new AbstractMap.SimpleEntry<Integer,Double>(1,score);
			}else{
				Entry e1 = this.userScore.get(userID);
				e = new AbstractMap.SimpleEntry<Integer,Double>(((Integer)e1.getKey())+1,
						(Double) e1.getValue()+score);
				e1 = null;
			}
			
			this.userScore.put(userID, e);
		}
		
		for(Entry<String, Entry<Integer, Double>> e: this.userScore.entrySet()){
			this.writerFeature.write(e.getKey()+":"+e.getValue().getValue()/e.getValue().getKey()+"\n");
		}
		
		this.writerFeature.close();
	}

	private String getValueFromKVPair(String line) {
		return line.split(":")[1];
	}
	
	public static void main(String args[]) throws IOException{
		WriteUsersAverage uAvg = new WriteUsersAverage(); 
		uAvg.write();
	}
}
