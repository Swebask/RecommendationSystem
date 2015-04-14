/**Main.java
 * 1:20:48 PM @author Arindam
 */
package edu.asu.sml.reco.main;

import java.io.IOException;

import edu.asu.sml.reco.core.preprocessing.PreProcessor;

/**
 * @author Arindam
 *
 */
public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		PreProcessor prep = new PreProcessor();
		prep.init();
		prep.process();
	}

}
