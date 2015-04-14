package edu.asu.sml.reco.scoring;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

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
    private static StanfordCoreNLP pipeline;

    static {
        pipeline = new StanfordCoreNLP();
    }

    public SentimentScore()
    {
    	foundAdjectivesList=new ArrayList<String>();
    	foundVerbsList=new ArrayList<String>();
    	foundAdverbAdjectivePairList=new ArrayList<String>();
    	score=0.0;
        simpleOpinionScorer = new SimpleOpinionScorer();
    }
	
	public void identifyAdjAdv(String phrase)
	{
	    Annotation annotation = new Annotation(phrase);
	    pipeline.annotate(annotation);

	    List<CoreLabel> tokens= annotation.get(CoreAnnotations.TokensAnnotation.class);
	    String token1 = null, token2 = null;
	    int numberOfTokens = tokens.size();

	    for (int i=0;i<numberOfTokens-1;i++) {
	    	token1 = tokens.get(i).get(CoreAnnotations.PartOfSpeechAnnotation.class);
	    	token2 = tokens.get(i + 1).get(CoreAnnotations.PartOfSpeechAnnotation.class);

	    	 if(adverbSymbolList.contains(token1))
	    	 {
	    		 if(adjectiveSymbolList.contains(token2))
	    		 {
	    		    foundAdverbAdjectivePairList.add((tokens.get(i).toString().split(" "))[0] +" "+(tokens.get(i).toString().split(" "))[0]);
	    		    i++;
	    		 }
	    	} else if(adjectiveSymbolList.contains(token1)) {
	        	foundAdjectivesList.add((tokens.get(i).toString().split(" "))[0]);

	        } else if(verbSymbolList.contains(token1)) {
                 foundVerbsList.add((tokens.get(i).toString().split("-"))[0]);
             }
	    }
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
		String StronglyAffinedAdverbs="astronomically, exceedingly, extremely, immensely, very, absolutely, certainly, exactly, totally";
		String WeakOrDoubtAdverbs="barely, scarcely, weakly, slightly, possibly, roughly, apparently, seemingly";

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
					score += adjScore - (1 - adjScore) * advScore;
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
		
		SentimentScore scoreForPhrase = new SentimentScore();
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

    public static void main(String[] args) throws IOException {
        SentimentScore ss = new SentimentScore();
        ss.prepareForScoring();
        System.out.println(ss.getScore("I love this"));
    }
}