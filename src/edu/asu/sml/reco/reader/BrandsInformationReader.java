/**BrandsInformationReader.java
 * 1:09:04 PM @author Arindam
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
public class BrandsInformationReader {
	private BufferedReader reader=null;
	private String filePath = "C:\\Users\\Arindam\\Documents\\Recommendation System\\data\\brands.txt.gz";
	
	public BrandsInformationReader() throws IOException{
		InputStream fileStream = new FileInputStream(this.filePath);
		InputStream gzipStream = new GZIPInputStream(fileStream);
		Reader decoder = new InputStreamReader(gzipStream, "UTF-8");
		this.reader = new BufferedReader(decoder);
	}
	
	public void print() throws IOException{
		
		for(int i=0; i<10;i++){
			System.out.println(this.reader.readLine());
		}
	}
}
