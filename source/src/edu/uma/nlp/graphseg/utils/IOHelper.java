package edu.uma.nlp.graphseg.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class IOHelper {
	public static List<String> getAllLines(String path)
	{
		try {
			return FileUtils.readLines(new File(path));
		} catch (IOException e) {
			System.out.println("File not found or error reading the file: " + path);
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static List<String> getAllLinesStream(InputStream stream)
	{
		List<String> doc = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).lines().collect(Collectors.toList());
		return doc;
	}
	
	public static List<String> getAllLinesWithoutEmpty(String path)
	{
		try {
			List<String> alllines = Files.readAllLines(Paths.get(path));
			List<String> noEmpty = new ArrayList<String>();
			
			for(int i = 0; i < alllines.size(); i++)
			{
				if (!StringUtils.isEmpty(alllines.get(i).trim())) 
				{
					noEmpty.add(alllines.get(i));
				}
			}
			
			return noEmpty;
			
		} catch (IOException e) {
			System.out.println("File not found or error reading the file: " + path);
			return null;
		}
	}
	
	
	public static void writeLines(List<String> lines, String path)
	{
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < lines.size(); i++)
		{
			builder.append(lines.get(i) + "\n");
		}
		
		try {
			FileUtils.writeStringToFile(new File(path), builder.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeCounts(Map<String, Integer> dictionary, String path, Boolean ordered)
	{
		writeCounts(dictionary.entrySet().stream().collect(Collectors.toList()), path, ordered);
	}
	
	public static void writeCounts(List<Map.Entry<String, Integer>> entries, String path, Boolean ordered)
	{
		try {
			File fout = new File(path);
			FileOutputStream fos;
			fos = new FileOutputStream(fout);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
			
			if (ordered) entries.sort((i1, i2) -> i1.getValue() > i2.getValue() ? -1 : (i2.getValue() > i1.getValue() ? 1 : 0));
			
			for(int i = 0; i < entries.size(); i++)
			{
				bw.write(entries.get(i).getKey() + " " + entries.get(i).getValue() + "\n");
			}
			
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static void writeScores(Map<String, Double> dictionary, String path, Boolean orderedDescending, Map<String, Integer> additionalData, Boolean mweUnderscore)
	{
		writeScores(dictionary.entrySet().stream().collect(Collectors.toList()), path, orderedDescending, additionalData, mweUnderscore);
	}
	
	public static void writeScores(List<Map.Entry<String, Double>> entries, String path, Boolean orderedDescending, Map<String, Integer> additionalData, Boolean mweUnderscore)
	{
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(path)), "UTF-8"));
			
			entries.sort((i1, i2) -> i1.getValue() > i2.getValue() ? (orderedDescending ? -1 : 1) : (i2.getValue() > i1.getValue() ? (orderedDescending ? 1 : -1) : 0));
			
			for(int i = 0; i < entries.size(); i++)
			{
				String line = "";
				if (mweUnderscore)
				{
					String[] split = entries.get(i).getKey().split("\\s+");
					StringBuilder singlePhrase = new StringBuilder();
					for(int j = 0; j < split.length; j++)
					{
						singlePhrase.append(split[j]);
						if (j < split.length - 1) singlePhrase.append("_");
					}
					line += singlePhrase.toString() + " ";
				}
				else line = entries.get(i).getKey() + " ";
				
				line += String.valueOf(entries.get(i).getValue());
				
				if (additionalData != null)
				{
					line += " " + String.valueOf(additionalData.get(entries.get(i).getKey()));
				}
				bw.write(line.trim() + "\n");
			}
			
			bw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public static HashMap<String, Integer> loadCounts(String path)
	{
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		List<String> lines = getAllLines(path);
		
		for(int i = 0; i < lines.size(); i++)
		{
			//if (i % 100 == 0) System.out.println("Loading counts: " + String.valueOf(i+1) + "/" + String.valueOf(lines.size()));
			String split[] = lines.get(i).split("\\s+");
			if (!dict.containsKey(split[0]))
			{
				dict.put(split[0], Integer.parseInt(split[1]));
			}
		}
		return dict;
	}
	
	public static HashMap<String, Integer> loadCounts(InputStream stream) throws UnsupportedEncodingException, IOException
	{
		HashMap<String, Integer> dict = new HashMap<String, Integer>();
		try(BufferedReader br = new BufferedReader(new InputStreamReader(stream, "UTF-8"))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if (StringUtils.isNotEmpty(line.trim()))
		    	{
		    		//if (i % 100 == 0) System.out.println("Loading counts: " + String.valueOf(i+1) + "/" + String.valueOf(lines.size()));
					String split[] = line.split("\\s+");
					if (!dict.containsKey(split[0]))
					{
						dict.put(split[0], Integer.parseInt(split[1]));
					}
		    	}
		    }
		}
		return dict;
	}
	
	public static HashMap<String, Double> loadScores(String path)
	{
		HashMap<String, Double> dict = new HashMap<String, Double>();
		List<String> lines = getAllLines(path);
		
		for(int i = 0; i < lines.size(); i++)
		{
			//if (i % 100 == 0) System.out.println("Loading counts: " + String.valueOf(i+1) + "/" + String.valueOf(lines.size()));
			String split[] = lines.get(i).split("\\s+");
			if (!dict.containsKey(split[0]))
			{
				dict.put(split[0], Double.parseDouble(split[1]));
			}
		}
		return dict;
	}
	
	public static void peekTopLines(String inputpath, String outputPath, int numLines)
	{	
		List<String> lines = new ArrayList<String>();
		try(BufferedReader br = new BufferedReader(new FileReader(inputpath))) {
		    for(int i = 0; i < numLines; i++) {
		    	lines.add(br.readLine());
	    	}
		    IOHelper.writeLines(lines, outputPath);
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map<String, Double> loadScoresLineByLine(String path)
	{
		Map<String, Double> dict = Collections.synchronizedMap(new HashMap<String, Double>());
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if (StringUtils.isNotEmpty(line.trim()))
		    	{
			    	String split[] = line.split("\\s+");
					if (!dict.containsKey(split[0]))
					{
						dict.put(split[0], Double.parseDouble(split[1]));
					}
		    	}
		    }
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dict;
	}
	
	public static Map<String, Integer> loadRanks(String path)
	{
		Map<String, Integer> dict = Collections.synchronizedMap(new HashMap<String, Integer>());

		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
			int counter = 0;
			for(String line; (line = br.readLine()) != null; ) {
		    	if (StringUtils.isNotEmpty(line.trim()))
		    	{
		    		counter++;
			    	String split[] = line.split("\\s+");
					if (!dict.containsKey(split[0]))
					{
						dict.put(split[0], counter);
					}
		    	}
		    }
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dict;
	}
	
	public static Map<String, Double> loadScoresLineByLine(String path, double treshold, Boolean sorted)
	{
		Map<String, Double> dict = Collections.synchronizedMap(new HashMap<String, Double>());
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
		    for(String line; (line = br.readLine()) != null; ) {
		    	if (StringUtils.isNotEmpty(line.trim()))
		    	{
			    	String split[] = line.split("\\s+");
					if (!dict.containsKey(split[0]))
					{
						Double score = Double.parseDouble(split[1]);
						if (score >= treshold) dict.put(split[0], score);
						else if (sorted) 
						{
							return dict;
						}
					}
		    	}
		    }
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dict;
	}
	
	public static Map<String, Double> loadScoresLineByLine(String path, int topN)
	{
		Map<String, Double> dict = Collections.synchronizedMap(new HashMap<String, Double>());
		
		try(BufferedReader br = new BufferedReader(new FileReader(path))) {
		    for(int i = 0; i < topN; i++) {
		    	String line = br.readLine();
		    	if (StringUtils.isNotEmpty(line.trim()))
		    	{
			    	String split[] = line.split("\\s+");
					if (!dict.containsKey(split[0]))
					{
						Double score = Double.parseDouble(split[1]);
						dict.put(split[0], score);
					}
		    	}
		    }
		    // line is not visible here.
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return dict;
	}
	
	public static HashMap<String, String> loadMappings(String path)
	{
		HashMap<String, String> dict = new HashMap<String, String>();
		List<String> lines = getAllLines(path);
		
		for(int i = 0; i < lines.size(); i++)
		{
			//if (i % 100 == 0) System.out.println("Loading mappings: " + String.valueOf(i+1) + "/" + String.valueOf(lines.size()));
			String split[] = lines.get(i).split("\\s+");
			if (!dict.containsKey(split[0]))
			{
				dict.put(split[0], split[1]);
			}
		}
		return dict;
	}
	
	public static HashMap<String, List<String>> loadMultiMappings(String path)
	{
		HashMap<String, List<String>> dict = new HashMap<String, List<String>>();
		List<String> lines = getAllLines(path);
		
		for(int i = 0; i < lines.size(); i++)
		{
			//if (i % 100 == 0) System.out.println("Loading mappings: " + String.valueOf(i+1) + "/" + String.valueOf(lines.size()));
			String split[] = lines.get(i).split("\\s+");
			if (!dict.containsKey(split[0]))
			{
				dict.put(split[0], new ArrayList<String>());
			}
			dict.get(split[0]).add(split[1]);
		}
		return dict;
	}
	
	public static void writeStringToFile(String content, String path) throws IOException
	{
		FileUtils.writeStringToFile(new File(path), content, "UTF-8");
	}
}
