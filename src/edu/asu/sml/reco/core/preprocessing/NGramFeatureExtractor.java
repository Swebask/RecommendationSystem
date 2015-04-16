/**NGramFeatureExtractor.java
 * 2:22:01 AM @author Arindam
 */
package edu.asu.sml.reco.core.preprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Arindam
 *
 */
public class NGramFeatureExtractor extends FeatureExtractor {
	private StanfordCoreNLP pipeline;
	public NGramFeatureExtractor(){
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		this.pipeline = new StanfordCoreNLP(props);
	}

	public Map<String,String> findFeatureValues(String text){
		Map<String,String> kv = new HashMap<String,String>();
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(text);

		// run all Annotators on this text
		pipeline.annotate(document);

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for(CoreMap sentence: sentences) {
			List<CoreLabel> tokens = sentence.get(TokensAnnotation.class);
			int i=0;
			for (CoreLabel token: tokens) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
				// this is the POS tag of the token
				String pos = token.get(PartOfSpeechAnnotation.class);
				// this is the NER label of the token
				String ne = token.get(NamedEntityTagAnnotation.class); 
				String lemma = token.lemma();
				String key="";
				String sentiment="";
				if(pos.startsWith("NN")){
					key = lemma.toLowerCase();
					List<CoreLabel> ngrams = tokens.subList(Math.max(i-4, 0), 
							Math.min(tokens.size(), i+6));
					for(CoreLabel label: ngrams){
						sentiment+=label.lemma()+" ";
					}
					
					if(!key.isEmpty()){
						if(kv.containsKey(key)){
							sentiment+="#"+kv.get(key);
						}
						kv.put(key, sentiment);
					}
				}
				i++;
			}
		}
		return kv;
	}
}
