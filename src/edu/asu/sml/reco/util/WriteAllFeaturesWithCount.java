/**WriteAllFeaturesWithCount.java
 * 11:11:28 PM @author Arindam
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Arindam
 *
 */
public class WriteAllFeaturesWithCount {
	private BufferedWriter writerFeature=null;
	private BufferedReader reader=null;
	private Map<String,Integer> features;
	public WriteAllFeaturesWithCount() throws IOException{
		writerFeature = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("allFeatureWithCount.txt"), "utf-8"));
		InputStream fileStream = new FileInputStream("./parsedReviewTraining.txt");
		Reader decoder = new InputStreamReader(fileStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
		this.features = new HashMap<String,Integer>();
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
							if(this.features.containsKey(k)){
								this.features.put(k, this.features.get(k)+1);
							}else{
								this.features.put(k,1);
							}
					}
				}
			}catch(Exception e){
				continue;
			}
		}

		
		for(Entry<String,Integer>  e: this.features.entrySet()){
			this.writerFeature.write(e.getKey()+"\t"+e.getValue()+"\n");
		}

		this.writerFeature.close();
	}

	private String getValueFromKVPair(String line) {
		return line.split(":")[1];
	}

	public static void main(String args[]) throws IOException{
		WriteAllFeaturesWithCount f = new WriteAllFeaturesWithCount(); 
		f.write();
	}
}
