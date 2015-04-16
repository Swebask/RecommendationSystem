/**TestSetGenerator.java
 * 1:12:59 AM @author Arindam
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
import java.io.Writer;
import java.util.HashMap;
import java.util.Map.Entry;

import edu.asu.sml.reco.core.preprocessing.FeatureExtractor;
import edu.asu.sml.reco.core.preprocessing.NGramFeatureExtractor;
import edu.asu.sml.reco.ds.UserIDLookupTable;
import edu.asu.sml.reco.reader.MusicReviewReader;
import edu.asu.sml.reco.reader.Review;

/**
 * @author Arindam
 *
 */
public class TestSetGenerator {
	private BufferedWriter writerTesting=null;
	private FeatureExtractor extractor;
	private HashMap<String, String> reviewMap;
	private HashMap<String, String> itemUserMap;
	
	public TestSetGenerator() throws IOException{
		writerTesting = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("parsedReviewTesting.txt"), "utf-8"));
		this.reviewMap = new HashMap<String,String>();
		this.itemUserMap = new HashMap<String,String>();
		this.extractor = new NGramFeatureExtractor();
		
		InputStream gzipStream = new FileInputStream("./itemUserMap.txt");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gzipStream));
		String line;
		while((line=bufferedReader.readLine())!= null) {
			String[] parts = line.split("\t");
			this.reviewMap.put(parts[0].trim(), "");
			this.itemUserMap.put(parts[0].trim()+"-"+parts[1].trim(), "");
		}
	}
	
	public void create() throws IOException{
		int size = 100;
		MusicReviewReader reviewReader = new MusicReviewReader();
		Review review = reviewReader.getNext();
		while(review!=null ){
			if((UserIDLookupTable.lookUp(review.getUserId())!=null)&&
					(this.reviewMap.containsKey(review.getProductId().trim()))){
				if(!this.itemUserMap.containsKey(review.getProductId().trim()+"-"+review.getUserId().trim())){
					review.setFeatureValues(
							this.extractor.findFeatureValues(
									review.getText()));
					this.write(review,writerTesting);
					size--;
				}
			}
			review = reviewReader.getNext();	
		}
		this.writerTesting.close();
	}
	
	/**
	 * write the review into a file
	 * @param r
	 * @param writer
	 * @throws IOException
	 */
	private void write(Review r, Writer writer) throws IOException{
		writer.write("productId:"+r.getProductId()+"\n");
		writer.write("userId:"+r.getUserId()+"\n");
		writer.write("score:"+r.getScore()+"\n");
		writer.write("review:"+ r.getText()+"\n");

		String keys="@", value = "@";
		for(Entry<String, String> e: r.getFeatureValues().entrySet()){
			keys+="@"+e.getKey();
			value+="@"+ e.getValue();
		}

		writer.write("featureKeys:"+ keys.substring(1)+"\n");
		writer.write("featureValues:"+value.substring(1)+"\n\n");
	}
	public static void main(String args[]) throws IOException{
		TestSetGenerator t =new TestSetGenerator();
		t.create();
	}
}
