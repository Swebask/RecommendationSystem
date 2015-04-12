package edu.asu.sml.reco.scoring;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.asu.sml.reco.scoring.SimpleOpinionScorer;

public class SentimentScore
{
 
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
    public List<String> foundAdjectivesList;
    public List<String> foundAdverbAdjectivePairList;
    public double score;
  
    public SentimentScore()
    {
    	foundAdjectivesList=new ArrayList<String>();
    	foundAdverbAdjectivePairList=new ArrayList<String>();
    	score=0.0;
    }
	
	public void identifyAdjAdv(String phrase)
	{
		StanfordCoreNLP pipeline = new StanfordCoreNLP();
	    Annotation annotation = new Annotation(phrase);
	    pipeline.annotate(annotation);

	    List<CoreLabel> tokens= annotation.get(CoreAnnotations.TokensAnnotation.class);
	    String token1 = null;
	    String token2 = null;
	    int numberOfTokens = tokens.size();

	    for (int i=0;i<numberOfTokens-1;i++) {
	    	token1=tokens.get(i).get(CoreAnnotations.PartOfSpeechAnnotation.class).toString();
	    	token2=tokens.get(i+1).get(CoreAnnotations.PartOfSpeechAnnotation.class).toString();

	    	 if(adverbSymbolList.contains(token1))
	    	 {	
	    		 if(adjectiveSymbolList.contains(token2))
	    		 {
	    		    foundAdverbAdjectivePairList.add(token1+" "+token2);
	    		    i++;
	    		 }
	    	} else if(adjectiveSymbolList.contains(token1)) {
	        		foundAdjectivesList.add(token1);
	        }
	    }
	}
	
	public void adjectivePolarity() throws IOException
	{
		for(String adj : foundAdjectivesList)
		{
			score+=SimpleOpinionScorer.extract(adj, "a");
		}
	}
	
	public void adverbPolarity() throws IOException
	{
		double advScore=0.0, adjScore=0.0;
		String StronglyAffinedAdverbs="astronomically, exceedingly, extremely, immensely, very, absolutely, certainly, exactly, totally";
		String WeakOrDoubtAdverbs="barely, scarcely, weakly, slightly, possibly, roughly, apparently, seemingly";
		for(String adverbAdjectivePhrase : foundAdverbAdjectivePairList)
		{
			String[] adverbAdjectivePair = adverbAdjectivePhrase.split(" ");	
			advScore=SimpleOpinionScorer.extract(adverbAdjectivePair[0], "r");
			adjScore=SimpleOpinionScorer.extract(adverbAdjectivePair[1], "a");
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
					score += adjScore - (1 - adjScore) * advScore;
				else if(adjScore<0)
					score += adjScore + (1 - adjScore) * advScore;
			}
			else {
				score+= (advScore+adjScore);
			}
		}
	}

	public static double getScore(String phrase)
	{
		
		SentimentScore scoreForPhrase = new SentimentScore();
		scoreForPhrase.identifyAdjAdv(phrase);
		
		try {
			scoreForPhrase.adjectivePolarity();
			scoreForPhrase.adverbPolarity();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return scoreForPhrase.score;
	}
}