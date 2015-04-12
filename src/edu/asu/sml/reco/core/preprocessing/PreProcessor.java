/**PreProcessor.java
 * 6:53:21 PM @author Arindam
 */
package edu.asu.sml.reco.core.preprocessing;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Map.Entry;

import edu.asu.sml.reco.reader.FrequentUserReader;
import edu.asu.sml.reco.reader.ItemDescriptionReader;
import edu.asu.sml.reco.reader.MusicReviewReader;
import edu.asu.sml.reco.reader.Review;

/**
 * @author Arindam
 *
 */
public class PreProcessor {
	private FrequentUserReader freReader;
	private ItemDescriptionReader itemDescReader;
	private MusicReviewReader reviewReader;
	private Map<String, Integer> freqUser;
	private FeatureExtractor extractor;
	private Writer writer;
	public PreProcessor() throws IOException{
		this.freReader = new FrequentUserReader();
		this.itemDescReader = new ItemDescriptionReader();
		this.reviewReader = new MusicReviewReader();
		this.extractor = new FeatureExtractor();
		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("parsedReview.txt"), "utf-8"));
		} catch (IOException ex) {
			try {writer.close();} catch (Exception e) {}
			System.out.print(ex);
			throw ex;
		} 
	}
	
	public void init() throws IOException{
		this.freqUser = this.freReader.read();
		System.out.println(this.freqUser.size());
	}

	public void process() throws IOException{
		Review review = this.reviewReader.getNext();
		while(review!=null){
			if(review.getUserId()!=null
					|| review.getUserId().equalsIgnoreCase("unknown")){
				review.setFeatureValues(
						this.extractor.findFeatureValues(
								review.getText()));
				this.write(review);
			}
			review = this.reviewReader.getNext();
		}
		
		this.writer.close();
	}

	private void write(Review r) throws IOException{
		this.writer.write("productId:"+r.getProductId());
		this.writer.write("userId:"+r.getUserId());
		this.writer.write("score:"+r.getScore());
		this.writer.write("revew:"+ r.getText());
		
		String keys="@", value = "@";
		for(Entry<String, String> e: r.getFeatureValues().entrySet()){
			keys+="@"+e.getKey();
			value+="@"+ e.getValue();
		}
		
		this.writer.write("featureKeys:"+ keys.substring(1));
		this.writer.write("featureValues:"+value.substring(1));
	}
}
