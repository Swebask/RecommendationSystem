/**ItemDescriptionReader.java
 * 1:08:42 PM @author Arindam
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
public class ItemDescriptionReader {
	private BufferedReader reader=null;
	private String filePath = "C:\\Users\\Arindam\\Documents\\Recommendation System\\data\\descriptions.txt.gz";
	
	public ItemDescriptionReader() throws IOException{
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
	
	public void print(String ASIN) {
		try{
		String line;
		
		while((line=this.reader.readLine())!=null){
			String[] parts = line.split(":");
			line = this.reader.readLine();
			
		}
		}catch(Exception e){
			System.err.append(e.toString());
			
		}
	}
}
