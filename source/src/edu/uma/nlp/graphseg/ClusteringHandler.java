package edu.uma.nlp.graphseg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClusteringHandler {
	
	public List<List<Integer>> getSequentialClusters(List<List<Integer>> cliques, Map<Integer, Map<Integer, Double>> allSimilarities, int largestTooSmallClusterSize)
	{
		List<List<Integer>> sequentialClusters = new ArrayList<List<Integer>>();
		
		System.out.println("Merging cliques...");
		mergeCliques(cliques, sequentialClusters);
		System.out.println("Merging singletons...");
		mergeSingletons(cliques, sequentialClusters, allSimilarities);
		System.out.println("Merging too small sequences...");
		mergeTooSmallSequences(sequentialClusters, allSimilarities, largestTooSmallClusterSize);
		
		return sequentialClusters;
	}
	
	private void mergeCliques(List<List<Integer>> cliques, List<List<Integer>> sequentialClusters)
	{
		boolean change = true;
		while(change)
		{
			change = false;
			for(List<Integer> clique : cliques)
			{
				for(int i = 0; i < clique.size() - 1; i++)
				{
					for(int j = i+1; j < clique.size(); j++)
					{
						int ind = i;
						int jond = j;
						Optional<List<Integer>> existingClusterFirst = sequentialClusters.stream().filter(sc -> sc.contains(clique.get(ind))).findFirst();
						Optional<List<Integer>> existingClusterSecond = sequentialClusters.stream().filter(sc -> sc.contains(clique.get(jond))).findFirst();
						
						// Both nodes from the clique already placed in clusters
						if (existingClusterFirst.isPresent() && existingClusterSecond.isPresent())
						{
							continue;
						}
						
						// Neither of the nodes is in the cluster
						else if (!existingClusterFirst.isPresent() && !existingClusterSecond.isPresent())
						{
							// if these are consecutive sentences, we make a new cluster
							if (Math.abs(clique.get(i) - clique.get(j)) == 1)
							{
								List<Integer> newCluster = new ArrayList<Integer>();
								newCluster.add(Math.min(clique.get(i), clique.get(j)));
								newCluster.add(Math.max(clique.get(i), clique.get(j)));
								
								int insertIndex = -1;
								for(int k = 0; k < sequentialClusters.size(); k++)
								{
									if (newCluster.get(newCluster.size() - 1) < sequentialClusters.get(k).get(0))
									{
										insertIndex = k;
										break;
									}
								}
								
								if (insertIndex >= 0) sequentialClusters.add(insertIndex, newCluster);
								else sequentialClusters.add(newCluster);
								
								change = true;
							}
						}
						
						// one node is in one cluster, the other isn't
						else
						{
							List<Integer> cluster = existingClusterFirst.isPresent() ? existingClusterFirst.get() : existingClusterSecond.get();
							int node = existingClusterFirst.isPresent() ? clique.get(j) : clique.get(i);
							
							if ((node == cluster.get(0) - 1) || (node == cluster.get(cluster.size()-1) + 1))
							{
								cluster.add(node);
								cluster.sort((e1, e2) -> e1 < e2 ? -1 : (e1 > e2 ? 1 : 0));
								
								change = true;
							}
						}						
					}
				}
			}
		}
	}
	
	private List<Integer> computeSingletons(List<List<Integer>> cliques, List<List<Integer>> sequentialClusters)
	{
		List<Integer> singletons = new ArrayList<Integer>();
		for(List<Integer> c : cliques)
		{	
			for(int n : c)
			{
				if (!sequentialClusters.stream().anyMatch(sc -> sc.contains(n))) singletons.add(n);
			}
		}
		
		singletons = singletons.stream().distinct().collect(Collectors.toList()); 
		singletons.sort((s1, s2) -> s1 < s2 ? -1 : (s1 > s2 ? 1 : 0));
		return singletons;
	}
	
	private void mergeTooSmallSequences(List<List<Integer>> sequentialClusters, Map<Integer, Map<Integer, Double>> allSimilarities, int largestSmallCluster)
	{
		boolean change = true;
		while(change)
		{
			change = false;
			Optional<List<Integer>> firstSmallCluster = sequentialClusters.stream().filter(c -> c.size() <= largestSmallCluster).findFirst();
			if (firstSmallCluster.isPresent())
			{
				int i = sequentialClusters.indexOf(firstSmallCluster.get());
				double similarityPrevious = (i == 0) ? 0 : averageClusterSimilarity(sequentialClusters.get(i-1), sequentialClusters.get(i), allSimilarities);
				double similarityNext = (i == (sequentialClusters.size() - 1)) ? 0 : averageClusterSimilarity(sequentialClusters.get(i), sequentialClusters.get(i+1), allSimilarities);
				
				List<Integer> clusterToMergeWith = (similarityPrevious > similarityNext) ? sequentialClusters.get(i-1) : sequentialClusters.get(i+1);
				List<Integer> newCluster = new ArrayList<Integer>();
				newCluster.addAll(clusterToMergeWith);
				newCluster.addAll(sequentialClusters.get(i));
				newCluster.sort((i1, i2) -> i1 > i2 ? 1 : (i1 < i2 ? -1 : 0));
				
				sequentialClusters.add((similarityPrevious > similarityNext) ? i-1 : i, newCluster);
				sequentialClusters.remove(firstSmallCluster.get());
				sequentialClusters.remove(clusterToMergeWith);
				
				change = true;
			}
		}
	}
	
	private double averageClusterSimilarity(List<Integer> first, List<Integer> second,  Map<Integer, Map<Integer, Double>> allSimilarities)
	{
		double sum = 0;
		for(int i = 0; i < first.size(); i++)
		{
			for(int j = 0; j < second.size(); j++)
			{
				sum += allSimilarities.get(Math.min(first.get(i), second.get(j))).get(Math.max(first.get(i), second.get(j)));
			}
		}
		return sum / ((double)(first.size() * second.size()));
	}
	
	private void mergeSingletons(List<List<Integer>> cliques, List<List<Integer>> sequentialClusters, Map<Integer, Map<Integer, Double>> allSimilarities)
	{
        List<Integer> singletons = computeSingletons(cliques, sequentialClusters); 
		
		while(singletons.size() > 0)
		{
			if (singletons.size() % 10 == 0) System.out.println("Remaining singletons: " + singletons.size());
			
			int node = singletons.get(0);
			Optional<List<Integer>> previousNodeCluster = sequentialClusters.stream().filter(sc -> sc.contains(node - 1)).findFirst();
			Optional<List<Integer>> nextNodeCluster = sequentialClusters.stream().filter(sc -> sc.contains(node + 1)).findFirst();
			
			double similarityPrevious = node == 0 ? -1.0 : (previousNodeCluster.isPresent() ? similarityNodeCluster(node, previousNodeCluster.get(), allSimilarities) : allSimilarities.get(node - 1).get(node));
            double similarityNext = node == allSimilarities.size() ? -1.0 : (nextNodeCluster.isPresent() ? similarityNodeCluster(node, nextNodeCluster.get(), allSimilarities) : allSimilarities.get(node).get(node + 1));

            boolean previous = similarityPrevious >= similarityNext;
            boolean mergeWithCluster = previous ? previousNodeCluster.isPresent() : nextNodeCluster.isPresent();
            
            if (mergeWithCluster)
            {
            	if (previous) previousNodeCluster.get().add(node);
            	else nextNodeCluster.get().add(0, node);
            }
            else
            {
            	List<Integer> newCluster = new ArrayList<Integer>();
				newCluster.add(previous ? node - 1 : node);
				newCluster.add(previous ? node : node + 1);
				
				int insertIndex = -1;
				
				for(int k = 0; k < sequentialClusters.size(); k++)
				{
					if (newCluster.get(newCluster.size() - 1) < sequentialClusters.get(k).get(0))
					{
						insertIndex = k;
						break;
					}
				}
				
				if (insertIndex >= 0) sequentialClusters.add(insertIndex, newCluster);
				else sequentialClusters.add(newCluster);
            }
            
            singletons = computeSingletons(cliques, sequentialClusters);
		}		
	}
	
	private double similarityNodeCluster(int node, List<Integer> cluster, Map<Integer, Map<Integer, Double>> allSimilarities)
	{
		double average = 0;
		for(Integer n2 : cluster) average += allSimilarities.get(Math.min(node,  n2)).get(Math.max(node,  n2));
		return average /((double)cluster.size());
	}
}
