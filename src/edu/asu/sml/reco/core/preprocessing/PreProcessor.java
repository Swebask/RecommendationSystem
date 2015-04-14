/**PreProcessor.java
 * 6:53:21 PM @author Arindam
 */
package edu.asu.sml.reco.core.preprocessing;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
	private Writer writerTraining,writerTesting, writerFeature;
	private Set<String> features;
	public PreProcessor() throws IOException{
		this.freReader = new FrequentUserReader();
		this.itemDescReader = new ItemDescriptionReader();
		this.reviewReader = new MusicReviewReader();
		this.extractor = new FeatureExtractor();
		this.features = new HashSet<String>();
		try {
			writerTraining = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("parsedReviewTraining.txt"), "utf-8"));
			writerTesting = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("parsedReviewTesting.txt"), "utf-8"));
			writerFeature = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream("allFeature.txt"), "utf-8"));

		} catch (IOException ex) {
			try {writerTraining.close();} catch (Exception e) {}
			System.out.print(ex);
			throw ex;
		} 
	}

	public void init() throws IOException{
		this.freqUser = this.freReader.read();
		System.out.println(this.freqUser.size());
	}
	/**
	 * Split dataset,
	 * call feature extractor 
	 * save the review
	 * @throws IOException
	 */
	public void process() throws IOException{
		int flag = 7;
		Review review = this.reviewReader.getNext();
		while(review!=null ){
			if(review.getUserId()!=null
					&& !review.getUserId().equalsIgnoreCase("unknown")){
				if(freqUser.containsKey(review.getUserId())&& review.getText()!=null){
					int count = freqUser.get(review.getUserId());
					review.setFeatureValues(
							this.extractor.findFeatureValues(
									review.getText()));
					
					this.features.addAll(review.getFeatureValues().keySet());
					if(count>0){
						this.write(review,writerTraining);
						freqUser.put(review.getUserId(), count-1);
					}else{
						this.write(review,writerTesting);
					}
					flag--;
				}else{
					/*
					if(review.getText()==null){
						System.out.println("text is null:"+review.getUserId());
					}else 
						System.out.println("user id is not frequent:"+review.getUserId());
					*/
				}	
			}
			review = this.reviewReader.getNext();	
		}
		
		this.writeFeature(writerFeature);
		this.writerTraining.close();
		this.writerTesting.close();
		this.writerFeature.close();
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
	
	private void writeFeature(Writer writer) throws IOException{
		for(String s: this.features){
			writer.write(s.trim()+"\n");
		}
	}
}
