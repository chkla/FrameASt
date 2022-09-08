package edu.uma.nlp.graphseg;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import edu.uma.nlp.graphseg.preprocessing.Document;
import edu.uma.nlp.graphseg.preprocessing.StanfordAnnotator;
import edu.uma.nlp.graphseg.semantics.InformationContent;
import edu.uma.nlp.graphseg.semantics.SemanticSimilarity;
import edu.uma.nlp.graphseg.semantics.WordVectorSpace;
import edu.uma.nlp.graphseg.utils.IOHelper;
import edu.uma.nlp.graphseg.utils.MemoryStorage;

public class Start {
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		// TODO Auto-generated method stub

		// checking the arguments 
		if (args.length < 4)
		{
			System.out.println("USAGE: java -jar graphseg.jar <input-dir> <output-dir> <rel-treshold> <min-segment>");
			return;
		}
		
		File inputDirFile = new File(args[0]);
		File outputDirFile = new File(args[1]);
		
		if (!inputDirFile.exists() || !outputDirFile.exists() || !inputDirFile.isDirectory() || !outputDirFile.isDirectory())
		{
			System.out.println("USAGE: java -jar graphseg.jar <input-dir> <output-dir> <rel-treshold (double, <0,1>)> <min-segment (int)>");
			return;
		}
		
		double treshold = 0;
		try
		{
			treshold = Double.parseDouble(args[2]);
			if (treshold < 0 || treshold > 1) 
			{
				throw new UnsupportedOperationException();
			}
		}
		catch(NumberFormatException ex)
		{
			System.out.println("USAGE: java -jar graphseg.jar <input-dir> <output-dir> <rel-treshold (double, <0,1>)> <min-segment (int)>");
			return;
		}
		
		int minseg = 0;
		try
		{
			minseg = Integer.parseInt(args[3]);
			if (minseg < 1) 
			{
				throw new UnsupportedOperationException();
			}
		}
		catch(NumberFormatException ex)
		{
			System.out.println("USAGE: java -jar graphseg.jar <input-dir> <output-dir> <rel-treshold (double, <0,1>)> <min-segment (int, >=1)>");
			return;
		}
		
		InputStream stopwordsStream = Start.class.getClassLoader().getResourceAsStream("stopwords.txt");
		List<String> stopwords = IOHelper.getAllLinesStream(stopwordsStream);
	
		InputStream embeddingsStream = Start.class.getClassLoader().getResourceAsStream("embeddings.txt");
		MemoryStorage.setWordVectorSpace(new WordVectorSpace());
		MemoryStorage.getWordVectorSpace().load(embeddingsStream, null);
		
		InputStream freqsStream = Start.class.getClassLoader().getResourceAsStream("freqs.txt");
		MemoryStorage.setInformationContent(new InformationContent(freqsStream, 1));
		
		
		SemanticSimilarity.setStopwords(stopwords);
		GraphHandler.setStopwords(stopwords);
		
		StanfordAnnotator annotator = new StanfordAnnotator();
    	
		
    	for(Path file : Files.walk(Paths.get(args[0])).filter(x -> (new File(x.toString()).isFile())).collect(Collectors.toList()))
    	{
			System.out.println("Segmenting file: " + file.toString());
			
			annotator.setStanfordAnnotators(new ArrayList<String>(Arrays.asList("tokenize", "ssplit")));
			
			String content = FileUtils.readFileToString(new File(file.toString()));
			Document doc = new Document();
			doc.setText(content);
			annotator.annotate(doc);
			
			annotator.setStanfordAnnotators(new ArrayList<String>(Arrays.asList("tokenize", "ssplit", "pos", "lemma")));
			
	    	List<Document> snippets = new ArrayList<Document>();
	    	for(int i = 0; i < doc.getSentences().size(); i++)
	    	{
	    		Document snippet = new Document(doc.getSentences().get(i).getText());
	    		annotator.annotate(snippet);
	    		snippet.setId(String.valueOf(i));
	    		snippets.add(snippet);
	    	}
	    	
	    	UndirectedGraph<Integer, DefaultEdge> graph = GraphHandler.constructGraph(snippets, treshold);
	    	System.out.println("Computing cliques..."); 
	    	List<List<Integer>> cliques = GraphHandler.getAllCliques(graph);
			
	    	ClusteringHandler clusterer = new ClusteringHandler();
	    	System.out.println("Constructing linear segments..."); 
	    	List<List<Integer>> sequentialClusters = clusterer.getSequentialClusters(cliques, GraphHandler.getAllSimilarities(), minseg);
	    	IOHandler.writeSegmentation(doc.getSentences().stream().map(x -> x.getText()).collect(Collectors.toList()), sequentialClusters, args[1] + (args[1].endsWith("/") ? "" : "/") + file.getFileName().toString());
		}	
	}
}
