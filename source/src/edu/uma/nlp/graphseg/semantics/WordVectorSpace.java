package edu.uma.nlp.graphseg.semantics;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.javatuples.Pair;

import edu.uma.nlp.graphseg.utils.VectorOperations;

public class WordVectorSpace {
	
	private HashMap<String, double[]> embeddings;
    private int dimension;
    
    public int getDimension() {
		return dimension;
	}

    public void load(InputStream stream, HashMap<String, Integer> filters) throws FileNotFoundException, IOException
    {
        embeddings = new HashMap<String, double[]>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF8"))) {
            String line;
            int counter = 0;
            while ((line = br.readLine()) != null) {
            	try
                {
                    String split[] = line.trim().split("\\s+");

                    if (filters == null || filters.containsKey(split[0].toLowerCase()))
                    {
                        dimension = split.length - 1;

                        if (!embeddings.containsKey(split[0])) embeddings.put(split[0], new double[split.length - 1]);
                        for (int i = 1; i < split.length; i++)
                        {
                            embeddings.get(split[0])[i - 1] = Double.parseDouble(split[i]);
                        }
                    }
                    counter++;
                    if (counter % 1000 == 0) 
                    {
                    	System.out.println("Loading vectors... " + String.valueOf(counter));
                	}
                }
                catch(Exception e) 
            	{ 
                	System.out.println("Error processing line!"); 
                	continue; 
            	};
            }
        }
    }

    public void save(String path) throws Exception
    {
    	File fout = new File(path);
		FileOutputStream fos = new FileOutputStream(fout);
	 
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
	 
		embeddings.forEach((key, value) -> {
			try {
				writer.write(key + " ");
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            for(int i = 0; i < value.length; i++)
            {
                try {
					writer.write(String.valueOf(value[i]) + " ");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            try {
				writer.newLine();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		writer.close();
    }

    public double similarity(String word1, String word2)
    {
        if (word1.compareTo(word2) == 0) return 1;
        if (embeddings.containsKey(word1) && embeddings.containsKey(word2))
        {
            try {
				return VectorOperations.cosine(embeddings.get(word1), embeddings.get(word2));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return -2;
			}
        }
        else return -2;
    }

    public double[] getEmbedding(String word)
    {
        if (embeddings.containsKey(word)) return embeddings.get(word);
        else return null;
    }

    public List<Pair<String, Double>> getMostSimilar(String word, int numMostSimilar)
    {
        List<Pair<String, Double>> mostSimilar = new ArrayList<Pair<String, Double>>();
        if (embeddings.containsKey(word))
        {
            embeddings.forEach((key, val) -> {
            	if (key.trim() != word)
                {
                    double sim;
					try {
						sim = VectorOperations.cosine(embeddings.get(word), val);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						sim = -2;
					}
                    if (mostSimilar.size() < numMostSimilar)
                    {
                        mostSimilar.add(new Pair<String, Double>(key, sim));
                        mostSimilar.sort((x,y) -> x.getValue1() > y.getValue1() ? -1 : (x.getValue1() < y.getValue1() ? 1 : 0));
                    }
                    else if (sim > mostSimilar.get(mostSimilar.size() - 1).getValue1())
                    {
                        mostSimilar.set(mostSimilar.size() - 1, new Pair<String, Double>(key, sim));
                        mostSimilar.sort((x,y) -> x.getValue1() > y.getValue1() ? -1 : (x.getValue1() < y.getValue1() ? 1 : 0));
                    }
                }
            });
            
            return mostSimilar;
        }
        else return null;
    }
}
