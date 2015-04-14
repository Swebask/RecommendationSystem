/**FeatureExtractor.java
 * 7:37:56 PM @author Arindam
 */
package edu.asu.sml.reco.core.preprocessing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.IndexedWord;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.trees.GrammaticalRelation;
import edu.stanford.nlp.util.CoreMap;

/**
 * @author Arindam
 *
 */
public class FeatureExtractor {
	private StanfordCoreNLP pipeline;
	public FeatureExtractor(){
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse");
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
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			SemanticGraph graph = 
					sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
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
					IndexedWord iword;
					try{
					iword = graph.getNodeByIndex(token.index());
					}catch(Exception e){
						continue;
					}
					if(graph.hasParentWithReln(iword
							, GrammaticalRelation.valueOf("nsubj"))){
						/*
						 * Extract the associated parent
						 * and other adjectives associated with that verb
						 */
						key=lemma;
						Set<IndexedWord> words = graph.getParentsWithReln(iword, 
								GrammaticalRelation.valueOf("nsubj"));
						for(IndexedWord w: words){
							if(!sentiment.isEmpty()){
								sentiment+="#";
							}
							if(graph.hasChildWithReln(w,
									GrammaticalRelation.valueOf("neg"))){
								sentiment+= "not ";
							}

							sentiment+=w.lemma()+" ";

							for(IndexedWord c: graph.
									getChildrenWithReln(w,
											GrammaticalRelation.valueOf("advmod"))){
								sentiment+= c.lemma()+" ";
							}
							for(IndexedWord c: graph.
									getChildrenWithReln(w,
											GrammaticalRelation.valueOf("amod"))){
								sentiment+= c.lemma()+" ";
							}


							for(IndexedWord c: graph.
									getChildrenWithReln(w,
											GrammaticalRelation.valueOf("dobj"))){
								sentiment+= c.lemma()+" ";
							}
						}



					}else if(graph.hasParentWithReln(iword
							, GrammaticalRelation.valueOf("dobj"))){
						key=lemma;
						IndexedWord iwrd = graph.getNodeByIndex(token.index());
						/*
						 * Extract the associated parent
						 * and other adjectives associated with that verb
						 */
						Set<IndexedWord> words = graph.getParentsWithReln(iwrd, 
								GrammaticalRelation.valueOf("dobj"));
						for(IndexedWord w: words){
							if(!sentiment.isEmpty()){
								sentiment+="#";
							}
							if(graph.hasChildWithReln(w,
									GrammaticalRelation.valueOf("neg"))){
								sentiment+= "not ";
							}

							sentiment+=w.lemma()+" ";
							for(IndexedWord c: graph.
									getChildrenWithReln(w,
											GrammaticalRelation.valueOf("amod"))){
								sentiment+= c.lemma()+" ";
							}
							for(IndexedWord c: graph.
									getChildrenWithReln(w,
											GrammaticalRelation.valueOf("advmod"))){
								sentiment+= c.lemma()+" ";
							}

						}
					}

					if(!key.isEmpty()){
						if(kv.containsKey(key)){
							sentiment+="#"+kv.get(key);
						}
						kv.put(key, sentiment);
					}
				}
			}
		}
		return kv;
	}
}
