/**PrintFile.java
 * 9:19:08 PM @author Arindam
 */
package edu.asu.sml.reco.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @author Arindam
 *
 */
public class PrintFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader reader=null;
		InputStream fileStream = new FileInputStream("./similarityMatrix.matrix");
		Reader decoder = new InputStreamReader(fileStream, "UTF-8");
		reader = new BufferedReader(decoder);
		
		int count =10;
		
		String line=null;
		
		while((line=reader.readLine())!=null&& count>0){
			System.out.println(line);
		}
		reader.close();
	}

}
