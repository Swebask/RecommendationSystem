package edu.asu.sml.reco.scoring;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.TaggedWord;

public class SentimentScore2 {

	 public List<String> adjectiveSymbolList = new ArrayList<String>() {{
	    	add("JJ");
	    	add("JJR");
	    	add("JJS");
	    }};

	    public List<String> adverbSymbolList = new ArrayList<String>() {{
	    	add("RB");
	    	add("RBR");
	    	add("RBS");
	    }};

	    public List<String> verbSymbolList = new ArrayList<String>() {{
	        add("VB");
	        add("VBD");
	        add("VBG");
	        add("VBN");
	        add("VBP");
	        add("VBZ");
	    }};

	    private List<String> foundAdjectivesList;
	    private List<String> foundAdverbAdjectivePairList;
	    private List<String> foundVerbsList;
	    private SimpleOpinionScorer simpleOpinionScorer;
	    private double score;

	    public SentimentScore2()
	    {
	    	foundAdjectivesList=new ArrayList<String>();
	    	foundVerbsList=new ArrayList<String>();
	    	foundAdverbAdjectivePairList=new ArrayList<String>();
	    	score=0.0;
	        simpleOpinionScorer = new SimpleOpinionScorer();
	    }

		public void adjectivePolarity() throws IOException
		{
			for(String adj : foundAdjectivesList)
			{
				score += simpleOpinionScorer.extract(adj, "a");
			}
		}

		public void verbPolarity() throws IOException
		{
			for(String verb : foundVerbsList)
			{
				score += simpleOpinionScorer.extract(verb, "v");
			}
		}

		public void adverbAdjectivePolarity() throws IOException
		{
			double advScore, adjScore;
			String StronglyAffinedAdverbs="astronomically, exceedingly, really, extremely, immensely, very, inexplicably, absolutely, certainly, exactly, totally";
			String WeakOrDoubtAdverbs="barely, scarcely, weakly, hardly, slightly, possibly, roughly, apparently, seemingly, nearly";

	        for(String adverbAdjectivePhrase : foundAdverbAdjectivePairList) {
				String[] adverbAdjectivePair = adverbAdjectivePhrase.split(" ");
				advScore=simpleOpinionScorer.extract(adverbAdjectivePair[0], "r");
				adjScore=simpleOpinionScorer.extract(adverbAdjectivePair[1], "a");
				if(StronglyAffinedAdverbs.contains(adverbAdjectivePair[0]))
				{
					if(adjScore>0)
						score += adjScore + (1 - adjScore) * advScore;
					else if(adjScore<0)
						score += adjScore - (1 - adjScore) * advScore;
				}
				else if(WeakOrDoubtAdverbs.contains(adverbAdjectivePair[0]))
				{
					if(adjScore>0)
						score += adjScore - (1 - adjScore) * Math.abs(advScore);
					else if(adjScore<0)
						score += adjScore + (1 - adjScore) * advScore;
				}
				else {
					score += (advScore+adjScore);
				}
			}
		}

		public double getScore(String phrase)
		{
			
			SentimentScore2 scoreForPhrase = new SentimentScore2();
			scoreForPhrase.identifyAdjAdv(phrase);
			
			try {
				scoreForPhrase.adjectivePolarity();
				scoreForPhrase.verbPolarity();
				scoreForPhrase.adverbAdjectivePolarity();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return scoreForPhrase.score;
		}

	    public void prepareForScoring() throws IOException {
	        simpleOpinionScorer.initializeDictionary();
	    }
	    
	public void identifyAdjAdv(String phrase)
	{
	    List<TaggedWord> taggedWords = StanfordParserResource.getListOfTaggedWords(phrase);

	    int count =1;
	    for(TaggedWord taggedWord: taggedWords) {
	    	String word = taggedWord.toString();
	    	//System.out.println(word+"");
			String[] wordPos = word.split("/");
	    	
			String pos1 = wordPos[1];
			 if(adverbSymbolList.contains(pos1) && count < taggedWords.size()) //not the last element
	    	 {
				 String[] wordPos2 = taggedWords.get(count).toString().split("/");
	    		 if(adjectiveSymbolList.contains(wordPos2[1]))
	    		 {
	    		    foundAdverbAdjectivePairList.add((wordPos[0].split("-"))[0] +" "+(wordPos2[0].split("-"))[0]);
	    		 }
	    	} else if(adjectiveSymbolList.contains(pos1)) {
	        	foundAdjectivesList.add((wordPos[0].split("-"))[0]);

	        } else if(verbSymbolList.contains(pos1)) {
                 foundVerbsList.add((wordPos[0].split("-"))[0]);
             }
			count++;
	    }
	}
	
    public static void main(String[] args) throws IOException {
        SentimentScore2 ss = new SentimentScore2();
        ss.prepareForScoring();
        System.out.println(ss.getScore("nice guitar play, nice quality"));
    }
}
