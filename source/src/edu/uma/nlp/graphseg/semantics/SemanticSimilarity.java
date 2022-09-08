package edu.uma.nlp.graphseg.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.javatuples.Triplet;

import edu.uma.nlp.graphseg.preprocessing.TokenAnnotation;
import edu.uma.nlp.graphseg.utils.VectorOperations;

public class SemanticSimilarity {
	
	private static List<String> stopwords;
	public static void setStopwords(List<String> stwrds)
	{
		stopwords = stwrds;
	}
	
	public static double greedyAlignmentOverlapFScore(List<TokenAnnotation> firstPhrase, List<TokenAnnotation> secondPhrase, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
        return greedyAlignmentOverlap(firstPhrase, secondPhrase, vectorSpace, informationContent, contentWordsOnly).getValue2();
    }

    public static double greedyAlignmentOverlapPrecision(List<TokenAnnotation> firstPhrase, List<TokenAnnotation> secondPhrase, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
        return greedyAlignmentOverlap(firstPhrase, secondPhrase, vectorSpace, informationContent, contentWordsOnly).getValue0();
    }

    public static double greedyAlignmentOverlapRecall(List<TokenAnnotation> firstPhrase, List<TokenAnnotation> secondPhrase, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
        return greedyAlignmentOverlap(firstPhrase, secondPhrase, vectorSpace, informationContent, contentWordsOnly).getValue1();
    }

    public static Triplet<Double, Double, Double> greedyAlignmentOverlap(List<TokenAnnotation> firstPhrase, List<TokenAnnotation> secondPhrase, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
        List<TokenAnnotation> firstPhraseCopy = new ArrayList<TokenAnnotation>();
        List<TokenAnnotation> secondPhraseCopy = new ArrayList<TokenAnnotation>();
        if (contentWordsOnly)
        {
            firstPhraseCopy.addAll(firstPhrase.stream().filter(x -> (contentWordsOnly ? x.getPartOfSpeech().isContent() : 1 != 2)).collect(Collectors.toList()));
            secondPhraseCopy.addAll(secondPhrase.stream().filter(x -> (contentWordsOnly ? x.getPartOfSpeech().isContent() : 1 != 2)).collect(Collectors.toList()));
        }
        else
        {
            firstPhraseCopy.addAll(firstPhrase);
            secondPhraseCopy.addAll(secondPhrase);
        }
        
        if (stopwords != null && stopwords.size() > 0)
        {
        	firstPhraseCopy = firstPhraseCopy.stream().filter(t -> !stopwords.contains(t.getLemma().toLowerCase()) && !stopwords.contains(t.getText().toLowerCase())).collect(Collectors.toList());
        	secondPhraseCopy = secondPhraseCopy.stream().filter(t -> !stopwords.contains(t.getLemma().toLowerCase()) && !stopwords.contains(t.getText().toLowerCase())).collect(Collectors.toList());
        }

        List<Double> pairSimilarities = new ArrayList<Double>(); 
        while(firstPhraseCopy.size() > 0 && secondPhraseCopy.size() > 0)
        {
            double maxSim = -1;
            TokenAnnotation firstToken = null;
            TokenAnnotation secondToken = null;
            for(TokenAnnotation nf : firstPhraseCopy)
            {
                for(TokenAnnotation ns : secondPhraseCopy)
                {
                    double sim = vectorSpace.similarity(nf.getText().toLowerCase(), ns.getText().toLowerCase());
                    if (sim < 0) sim = 0;

                    if (sim > maxSim)
                    {
                        firstToken = nf;
                        secondToken = ns;
                        maxSim = sim;
                    }
                }
            }

            if (informationContent != null) 
        	{
            		pairSimilarities.add(maxSim * Math.max(informationContent.getInformationContent(firstToken.getText().toLowerCase()), informationContent.getInformationContent(secondToken.getText().toLowerCase())));
        	}
            else pairSimilarities.add(maxSim);

            firstPhraseCopy.remove(firstToken);
            secondPhraseCopy.remove(secondToken);
        }

        double precision = 0;
        double recall = 0;
        double overlap = pairSimilarities.stream().mapToDouble(s -> s).sum();

        if (informationContent != null)
        {
            double infContentFirst = contentWordsOnly ? 
            	   firstPhrase.stream().filter(x -> x.getPartOfSpeech().isContent()).mapToDouble(t -> informationContent.getInformationContent(t.getText().toLowerCase())).sum() : 
            	   firstPhrase.stream().mapToDouble(t -> informationContent.getInformationContent(t.getText().toLowerCase())).sum();
            	   
    	    double infContentSecond = contentWordsOnly ? 
            	   secondPhrase.stream().filter(x -> x.getPartOfSpeech().isContent()).mapToDouble(t -> informationContent.getInformationContent(t.getText().toLowerCase())).sum() : 
            	   secondPhrase.stream().mapToDouble(t -> informationContent.getInformationContent(t.getText().toLowerCase())).sum();
            
            precision = overlap / infContentFirst;
            recall = overlap / infContentSecond;
        }
        else
        {
            precision = overlap / firstPhrase.size();
            recall = overlap / secondPhrase.size();
        }
        
        double fScore = 0;
        if (precision == 0 && recall == 0) fScore = 0;
        else fScore = (2 * precision * recall) / (precision + recall);
        if (Double.isNaN(fScore)) fScore = 0;

        return new Triplet<Double, Double, Double>(precision, recall, fScore);
    }
    
    public static double embeddingSumSimilarity(List<TokenAnnotation> first, List<TokenAnnotation> second, WordVectorSpace vectorSpace, int embeddingLength, Boolean content, List<InformationContent> infContents)
    {
        double[] embeddingFirst = new double[embeddingLength];
        double[] embeddingSecond = new double[embeddingLength];

        if (content)
        {
            first = first.stream().filter(x -> x.getPartOfSpeech().isContent()).collect(Collectors.toList());
            second = second.stream().filter(x -> x.getPartOfSpeech().isContent()).collect(Collectors.toList());
        }

        first.forEach(x -> 
        {
            double[] wordEmbedding = vectorSpace.getEmbedding(x.getText().trim());
            if (wordEmbedding == null) 
        	{
            	wordEmbedding = vectorSpace.getEmbedding(x.getText().trim().toLowerCase());
        	}
            if (wordEmbedding != null)
            {
                double ic = 1;
                for(InformationContent inco : infContents)
                {
                	ic *= inco.getInformationContent(x.getText().trim().toLowerCase());
                };
                VectorOperations.multiply(wordEmbedding, ic);
                VectorOperations.addVector(embeddingFirst, wordEmbedding);
            }
        });

        second.forEach(x ->
        {
            double[] wordEmbedding = vectorSpace.getEmbedding(x.getText().trim());
            if (wordEmbedding == null)
        	{
            	wordEmbedding = vectorSpace.getEmbedding(x.getText().trim().toLowerCase());
        	}
            if (wordEmbedding != null)
            {
                double ic = 1;
                for(InformationContent inco : infContents)
                {
                	ic *= inco.getInformationContent(x.getText().trim().toLowerCase());
                };
                VectorOperations.multiply(wordEmbedding, ic);
                VectorOperations.addVector(embeddingSecond, wordEmbedding);
            }
        });

        double res;
		try {
			res = VectorOperations.cosine(embeddingFirst, embeddingSecond);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res = 0;
		}
        if (Double.isNaN(res))
        {
            res = 0;
        }
        
        return res;
    }
    
    public static double averagePhraseGreedyAlignmentOverlap(List<List<TokenAnnotation>> firstPhrases, List<List<TokenAnnotation>> secondPhrases, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
    	double sum = 0;
    	double counter = 0;
    	
    	for(List<TokenAnnotation> fp : firstPhrases){
    		for(List<TokenAnnotation> sp : secondPhrases){
    			double sim = greedyAlignmentOverlapFScore(fp, sp, vectorSpace, informationContent, contentWordsOnly);
    			sum += sim;
    			counter++;
    		}
    	}
    	
    	double score = sum/counter;
    	if (Double.isNaN(score) || Double.isInfinite(score)) return 0;
    	else return score;
    }
    
    public static double maxPhraseGreedyAlignmentOverlap(List<List<TokenAnnotation>> firstPhrases, List<List<TokenAnnotation>> secondPhrases, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
    	double maxSim = 0;
    	
    	for(List<TokenAnnotation> fp : firstPhrases){
    		for(List<TokenAnnotation> sp : secondPhrases){
    			double sim = greedyAlignmentOverlapFScore(fp, sp, vectorSpace, informationContent, contentWordsOnly);
    			if (sim > maxSim)
    			{
    				maxSim = sim;
    			}
    		}
    	}   	
    	return maxSim;
    }
    
    public static int numSufficientlySimilarPhrasesGreedyAlignmentOverlap(List<List<TokenAnnotation>> firstPhrases, List<List<TokenAnnotation>> secondPhrases, double treshold, WordVectorSpace vectorSpace, InformationContent informationContent, Boolean contentWordsOnly)
    {
    	int counter = 0;
    	
    	for(List<TokenAnnotation> fp : firstPhrases){
    		for(List<TokenAnnotation> sp : secondPhrases){
    			double sim = greedyAlignmentOverlapFScore(fp, sp, vectorSpace, informationContent, contentWordsOnly);
    			if (sim >= treshold)
    			{
    				counter++;
    			}
    		}
    	}   	
    	return counter;
    }
    
    public static HashMap<String, Double> allToAllSimilarity(WordVectorSpace vectorSpace, List<String> vocabulary)
    {
    	HashMap<String, Double> similarities = new HashMap<String, Double>();
    	for(int i = 0; i < vocabulary.size() - 1; i++)
    	{
    		if (i % 100 == 0) System.out.println("Outer loop: " + String.valueOf(i + 1) + "/" + String.valueOf(vocabulary.size() - 1));
    		for(int j = i+1; j < vocabulary.size(); j++)
    		{
    			double sim = vectorSpace.similarity(vocabulary.get(i), vocabulary.get(j));
    			if (sim >= -1)
    			{
    				similarities.put(vocabulary.get(i).compareTo(vocabulary.get(j)) < 0 ? vocabulary.get(i) + "<=>" + vocabulary.get(j) : vocabulary.get(j) + "<=>" + vocabulary.get(i), sim);
    			}
    		}
    	}
    	return similarities;
    }
}
