/**ReadReview.java
 * 12:36:09 PM @author Arindam
 */
package edu.asu.sml.reco.reader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 * @author Arindam
 *
 */
public class MusicReviewReader {
	private BufferedReader reader=null;
	private String filePath = "C:\\Users\\Arindam\\Documents\\Recommendation System\\data\\music.txt.gz";

	public MusicReviewReader() throws IOException{
		InputStream fileStream = new FileInputStream(this.filePath);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
	}
	public void print() throws IOException{

		for(int i=0; i<20;i++){
			System.out.println(this.reader.readLine());
		}
	}
	
	public Review getNext(){
		Review review = new Review();
		boolean flag = false;
		int count=10;
		try {
			String line;
			while(count >0){
				line = this.reader.readLine();
				if(line!=null && !line.isEmpty()) count--;
				else if(line==null){
					flag=false;
					break;
				}
				//System.out.println(line);
				flag = true;
				String[] parts = line.split(":");
				if(parts[0].contains("productId")){
					review.setProductId(parts[1]);
				}else if(parts[0].contains("title")){
					review.setTitle(parts[1]);
				}else if(parts[0].contains("price")){
					review.setPrice(parts[1]);
				}else if(parts[0].contains("userId")){
					review.setUserId(parts[1].trim());
				}else if(parts[0].contains("helpfulness")){
					review.setHelpfulness(parts[1]);
				}else if(parts[0].contains("score")){
					review.setScore(parts[1]);
				}else if(parts[0].contains("time")){
					review.setTime(parts[1]);
				}else if(parts[0].contains("summary")){
					review.setSummary(parts[1]);
				}else if(parts[0].contains("text")){
					review.setText(parts[1]);
				}
				
			}
		} catch (IOException e) {
			review = null;
		}
		
		if(!flag)
			review = null;
		
		return review;
		
	}
}
