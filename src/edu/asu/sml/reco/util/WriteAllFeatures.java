/**WriteAllFeatures.java
 * 6:52:36 PM @author Arindam
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
import java.util.HashSet;
import java.util.Set;

/**
 * @author Arindam
 *
 */
public class WriteAllFeatures {
	private BufferedWriter writerFeature=null;
	private BufferedReader reader=null;
	private Set<String> features;
	public WriteAllFeatures() throws IOException{
		writerFeature = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("allFeature.txt"), "utf-8"));
		InputStream fileStream = new FileInputStream("./parsedReviewTraining.txt");
		Reader decoder = new InputStreamReader(fileStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
		this.features = new HashSet<String>();
	}

	public void write() throws IOException{
		String line = null;
		while((line=reader.readLine())!= null) {
			//product/productId: B00002066I
			String productID = getValueFromKVPair(line);

			line = reader.readLine();
			line = reader.readLine();

			// review/text: <text>
			line = reader.readLine();
			line = reader.readLine();

			reader.readLine();
			reader.readLine();
			//empty line ignore
			try{
				String key = getValueFromKVPair(line);
				if(key==null || key.isEmpty()){
					continue;
				}else{
					String[] keys = key.split("@"); 
					for(String k : keys){
						if(!k.isEmpty() && (k.length()>1))
							this.features.add(k.toLowerCase());
					}
				}
			}catch(Exception e){
				continue;
			}
		}

		for(String  e: this.features){
			this.writerFeature.write(e+"\n");
		}

		this.writerFeature.close();
	}

	private String getValueFromKVPair(String line) {
		return line.split(":")[1];
	}

	public static void main(String args[]) throws IOException{
		WriteAllFeatures f = new WriteAllFeatures(); 
		f.write();
	}
}
