package edu.asu.sml.reco.scoring;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SimpleOpinionScorer {

	private static Map<String, Double> dictionaryOfScores;
    private String pathToSentimentDictionary;

	public SimpleOpinionScorer() {
        pathToSentimentDictionary = "src/resources/SentiWordNet.txt";
	}

    public void initializeDictionary() throws IOException {
        // This is our main dictionary representation
        dictionaryOfScores = new HashMap<String, Double>();

        // From String to list of doubles.
        HashMap<String, HashMap<Integer, Double>> tempDictionary = new HashMap<String, HashMap<Integer, Double>>();

        BufferedReader sentimentWordNetFile = null;
        try {
            sentimentWordNetFile = new BufferedReader(new FileReader(pathToSentimentDictionary));
            int lineNumber = 0;

            String line;
            while ((line = sentimentWordNetFile.readLine()) != null) {
                lineNumber++;

                // If it's a comment, skip this line.
                if (!line.trim().startsWith("#")) {
                    // We use tab separation
                    String[] data = line.split("\t");
                    String wordTypeMarker = data[0];

                    // Example line:
                    // POS ID PosS NegS SynsetTerm#sensenumber Desc
                    // a 00009618 0.5 0.25 spartan#4 austere#3 ascetical#2
                    // ascetic#2 practicing great self-denial;...etc

                    // Is it a valid line? Otherwise, through exception.
                    if (data.length != 6) {
                        throw new IllegalArgumentException(
                                "Incorrect tabulation format in file, line: "
                                        + lineNumber);
                    }

                    // Calculate synset score as score = PosS - NegS
                    Double synsetScore = Double.parseDouble(data[2])
                            - Double.parseDouble(data[3]);

                    String[] synTermsSplit = data[4].split(" ");

                    for (String synTermSplit : synTermsSplit) {
                        String[] synTermAndRank = synTermSplit.split("#");
                        String synTerm = synTermAndRank[0] + "#"
                                + wordTypeMarker;

                        int synTermRank = Integer.parseInt(synTermAndRank[1]);

                        if (!tempDictionary.containsKey(synTerm)) {
                            tempDictionary.put(synTerm,
                                    new HashMap<Integer, Double>());
                        }

                        tempDictionary.get(synTerm).put(synTermRank, synsetScore);
                    }
                }
            }

            for (Map.Entry<String, HashMap<Integer, Double>> entry : tempDictionary.entrySet()) {
                String word = entry.getKey();
                Map<Integer, Double> synSetScoreMap = entry.getValue();

                double score = 0.0;
                double sum = 0.0;
                for (Map.Entry<Integer, Double> setScore : synSetScoreMap
                        .entrySet()) {
                    score += setScore.getValue();
                    sum++;
                }
                score /= sum;

                dictionaryOfScores.put(word, score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sentimentWordNetFile != null) {
                sentimentWordNetFile.close();
            }
        }
    }

	public double extract(String word, String pos) {
        String key = word + "#" + pos;
        if(dictionaryOfScores.containsKey(key)) {
            return dictionaryOfScores.get(key);
        }
        return 0.0;
	}
}