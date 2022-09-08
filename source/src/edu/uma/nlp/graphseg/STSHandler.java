package edu.uma.nlp.graphseg;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;

import edu.uma.nlp.graphseg.preprocessing.Document;
import edu.uma.nlp.graphseg.semantics.InformationContent;
import edu.uma.nlp.graphseg.semantics.SemanticSimilarity;
import edu.uma.nlp.graphseg.semantics.WordVectorSpace;


public class STSHandler {
	public static List<Triplet<Document, Document, Double>> getSemanticSimilarities(List<Document> snippets, double simTreshold, WordVectorSpace vectorSpace, InformationContent informationContent)
	{
		List<Triplet<Document, Document, Double>> similarityGraph = new ArrayList<Triplet<Document, Document, Double>>();  
				
		for(int i = 0; i < snippets.size() - 1; i++)
		{
			System.out.println("Outer loop: " + String.valueOf(i+1) + "/" + String.valueOf(snippets.size() - 1));
			for(int j = i + 1; j < snippets.size(); j++)
			{
				//if (j % 100 == 0) System.out.println("Inner loop: " + String.valueOf(j+1) + "/" + String.valueOf(snippets.size()));
				
				double similarity = SemanticSimilarity.greedyAlignmentOverlapFScore(snippets.get(i).getTokens(), snippets.get(j).getTokens(), vectorSpace, informationContent, true);
				if (similarity > simTreshold)
				{
					similarityGraph.add(new Triplet<Document, Document, Double>(snippets.get(i), snippets.get(j), similarity));
				}
			}
		}
		
		similarityGraph.sort((i1, i2) -> i1.getValue2() > i2.getValue2() ? -1 : (i1.getValue2() < i2.getValue2() ? 1 : 0));
		return similarityGraph;
	}
}
