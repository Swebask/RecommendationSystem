/**FrequentUserReader.java
 * 6:46:33 PM @author Arindam
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
import java.util.zip.GZIPInputStream;

/**
 * @author Arindam
 *
 */
public class FrequentUserReader {
	private BufferedReader reader=null;
	private String filePath = "C:\\Users\\Arindam\\Documents\\Recommendation System\\data\\freqUser.txt";
	
	public FrequentUserReader() throws IOException{
		InputStream fileStream = new FileInputStream(this.filePath);
		Reader decoder = new InputStreamReader(fileStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
	}
	
	public Map<String,Integer> read() throws IOException{
		
		Map<String,Integer> count = new HashMap<String,Integer>();
		String line;
		while((line=this.reader.readLine())!=null){
			String[] parts = line.trim().split("  *");
			count.put(parts[1].trim(), (int) (Integer.valueOf(parts[0].trim())*.8)
					);
		}
		return count;
	}
}
