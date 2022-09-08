package edu.uma.nlp.graphseg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.generate.SimpleWeightedBipartiteGraphMatrixGenerator;
import org.jgrapht.generate.WeightedGraphGeneratorAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.SimpleWeightedGraph;

import edu.uma.nlp.graphseg.preprocessing.Document;
import edu.uma.nlp.graphseg.preprocessing.TokenAnnotation;
import edu.uma.nlp.graphseg.utils.MemoryStorage;

public class GraphHandler {
	
	private static List<String> stopwords;
	public static void setStopwords(List<String> stwrds)
	{
		stopwords = stwrds;
	}
	
	private static Map<Integer, Map<Integer, Double>> allSimilarities;
	public static Map<Integer, Map<Integer, Double>> getAllSimilarities()
	{
		return allSimilarities;
	}
	
	public static UndirectedGraph<Integer, DefaultEdge> constructGraph(List<Document> snippets, double similarityTreshold)
	{
		int localizationSize = 100;
		allSimilarities = new HashMap<Integer, Map<Integer, Double>>();
		
		UndirectedGraph<Integer, DefaultEdge> graph = new SimpleGraph<Integer, DefaultEdge>(DefaultEdge.class);
		
		snippets.forEach(s -> graph.addVertex(Integer.parseInt(s.getId())));
		for(int i = 0; i < snippets.size() - 1; i++)
		{
			if (i % 10 == 0) System.out.println("Constructing graph, outer loop " + i + "/" + snippets.size());
			allSimilarities.put(i, new HashMap<Integer, Double>());
			
			for(int j = i + 1; j < Math.min(snippets.size(), i + localizationSize); j++)
			{
				List<TokenAnnotation> contentTokenFirst = snippets.get(i).getTokens().stream().filter(t -> t.getPartOfSpeech().isContent() && !stopwords.contains(t.getLemma().toLowerCase())).collect(Collectors.toList());
				List<TokenAnnotation> contentTokenSecond = snippets.get(j).getTokens().stream().filter(t -> t.getPartOfSpeech().isContent() && !stopwords.contains(t.getLemma().toLowerCase())).collect(Collectors.toList());
				
				if (contentTokenFirst.size() == 0 || contentTokenSecond.size() == 0) 
				{
					allSimilarities.get(i).put(j, 0.0);
					continue;
				}
				
				// preparing for bipartite graph min matching 
				double[][] dissimilarities = new double[Math.max(contentTokenFirst.size(), contentTokenSecond.size())][Math.max(contentTokenFirst.size(), contentTokenSecond.size())];
				List<Integer> firstPartition = new ArrayList<Integer>();
				List<Integer> secondPartition = new ArrayList<Integer>();
				
				for(int k = 0; k < Math.max(contentTokenFirst.size(), contentTokenSecond.size()); k++)
				{
					for(int l = 0; l < Math.max(contentTokenFirst.size(), contentTokenSecond.size()); l++)
					{
						if (k >= contentTokenFirst.size() || l >= contentTokenSecond.size())
						{
							dissimilarities[k][l] = 1;
						}
						else
						{
							double icFactor = Math.max(MemoryStorage.getInformationContent().getRelativeInformationContent(contentTokenFirst.get(k).getLemma().toLowerCase()), MemoryStorage.getInformationContent().getRelativeInformationContent(contentTokenSecond.get(l).getLemma().toLowerCase()));
							double simTokens = MemoryStorage.getWordVectorSpace().similarity(contentTokenFirst.get(k).getLemma().toLowerCase(), contentTokenSecond.get(l).getLemma().toLowerCase());
							if (simTokens < 0) simTokens = 0;
							
							dissimilarities[k][l] = 1 - icFactor * simTokens;
						}
					}
				}
				for(int z = 0; z < Math.max(contentTokenFirst.size(), contentTokenSecond.size()); z++)
				{
					firstPartition.add(z);
					secondPartition.add(z + Math.max(contentTokenFirst.size(), contentTokenSecond.size()));
				}
				
				double bmScore = minimumAverageBipartiteGraphMatchingScore(dissimilarities, firstPartition, secondPartition) - (Math.abs(contentTokenFirst.size() - contentTokenSecond.size()));
				double similarityNonNormalized = Math.min(contentTokenFirst.size(), contentTokenSecond.size()) - bmScore;
				double similarity = ((similarityNonNormalized / contentTokenFirst.size()) + (similarityNonNormalized / contentTokenSecond.size())) / 2.0;
				
				//double similarity = SemanticSimilarity.greedyAlignmentOverlapFScore(snippets.get(i).getTokens(), snippets.get(j).getTokens(), MemoryStorage.getWordVectorSpace(), MemoryStorage.getInformationContent(), true);
				allSimilarities.get(i).put(j, similarity);
				
				if (similarity > similarityTreshold)
				{
					graph.addEdge(Integer.parseInt(snippets.get(i).getId()), Integer.parseInt(snippets.get(j).getId()));
				}
			}
		}
		
		return graph;
	}
	
	public static double minimumAverageBipartiteGraphMatchingScore(double[][] dissimilarities, List<Integer> firstPartition, List<Integer> secondPartition)
	{
		SimpleWeightedGraph<Integer, DefaultWeightedEdge> bipartiteGraph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		WeightedGraphGeneratorAdapter<Integer, DefaultWeightedEdge, Integer> generator = 
		          new SimpleWeightedBipartiteGraphMatrixGenerator<Integer, DefaultWeightedEdge>() 
		              .first  (firstPartition) 
		              .second (secondPartition) 
		              .weights(dissimilarities); 
		
		generator.generateGraph(bipartiteGraph, null, null);
		KuhnMunkresMinimalWeightBipartitePerfectMatching<Integer, DefaultWeightedEdge> bipartiteMatching = new KuhnMunkresMinimalWeightBipartitePerfectMatching<Integer, DefaultWeightedEdge>(bipartiteGraph, firstPartition, secondPartition);
		
		return bipartiteMatching.getMatchingWeight();
	}
	
	public static List<List<Integer>> getAllCliques(UndirectedGraph<Integer, DefaultEdge> graph)
	{
		BronKerboschCliqueFinder<Integer, DefaultEdge> finder = new BronKerboschCliqueFinder<Integer, DefaultEdge>(graph);
		return finder.getAllMaximalCliques().stream().map(x -> x.stream().collect(Collectors.toList())).collect(Collectors.toList());
	}
	
	public static List<List<Integer>> getAllConnectedComponents(UndirectedGraph<Integer, DefaultEdge> graph)
	{
		ConnectivityInspector<Integer, DefaultEdge> finder = new ConnectivityInspector<Integer, DefaultEdge>(graph);
		return finder.connectedSets().stream().map(x -> x.stream().collect(Collectors.toList())).collect(Collectors.toList());
	}
}
