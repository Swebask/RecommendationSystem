package edu.asu.sml.reco.scoring;

import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;

public class StanfordParserResource {
	private static LexicalizedParser lexicalizedParser;

	public static List<TaggedWord> getListOfTaggedWords(String inputText) {
		if (lexicalizedParser == null)
			lexicalizedParser = LexicalizedParser
					.loadModel("src/resources/englishPCFG.ser.gz");
		// get the parse tree
		Tree t = lexicalizedParser.parse(inputText );

		return t.taggedYield();
	}
	
}
