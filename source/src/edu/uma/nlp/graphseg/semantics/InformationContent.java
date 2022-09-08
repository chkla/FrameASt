package edu.uma.nlp.graphseg.semantics;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;

import edu.uma.nlp.graphseg.utils.IOHelper;

public class InformationContent {
	private HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
    private double sumFrequencies = 0;
    private double minFreq = 1;
    private double divideFactor = 1; 

    public InformationContent(String path, double divideFactor)
    {
        this.divideFactor = divideFactor;

        frequencies = IOHelper.loadCounts(path);
        sumFrequencies = frequencies.values().stream().mapToDouble(x -> ((double)x)/divideFactor).sum();
        minFreq = frequencies.values().stream().mapToDouble(x -> ((double)x)/divideFactor).min().getAsDouble();
        if (minFreq == 0) minFreq = 1;
    }
    
    public InformationContent(InputStream stream, double divideFactor) throws UnsupportedEncodingException, IOException
    {
        this.divideFactor = divideFactor;

        frequencies = IOHelper.loadCounts(stream);
        sumFrequencies = frequencies.values().stream().mapToDouble(x -> ((double)x)/divideFactor).sum();
        minFreq = frequencies.values().stream().mapToDouble(x -> ((double)x)/divideFactor).min().getAsDouble();
        if (minFreq == 0) minFreq = 1;
    }

    public InformationContent(HashMap<String, Integer> frequenciesDictionary, double divideFactor)
    {
        this.divideFactor = divideFactor;

        frequencies = frequenciesDictionary;
        sumFrequencies = frequencies.values().stream().mapToDouble(x -> ((double)x)/divideFactor).sum();
        minFreq = frequencies.values().stream().mapToDouble(x -> ((double)x)/divideFactor).min().getAsDouble();
    }

    public double getInformationContent(String word)
    {
        if (frequencies.containsKey(word.toLowerCase())) return (-1) * Math.log(((((double)frequencies.get(word.toLowerCase())) + minFreq) / divideFactor) / sumFrequencies);
        else return (-1) * Math.log((minFreq / divideFactor) / sumFrequencies);
    }

    public double getRelativeInformationContent(String word)
    {
        double maxInfCont = (-1) * Math.log((minFreq / divideFactor) / sumFrequencies);
        double infCont = (frequencies.containsKey(word.toLowerCase())) ? (-1) * Math.log(((((double)frequencies.get(word.toLowerCase())) + minFreq) / divideFactor) / sumFrequencies) : maxInfCont;

        return infCont / maxInfCont;
    }

    public double getLogRelativeInformationContent(String word)
    {
        double maxInfCont = (-1) * Math.log((minFreq / divideFactor) / sumFrequencies);
        double infCont = (frequencies.containsKey(word.toLowerCase())) ? (-1) * Math.log(((((double)frequencies.get(word.toLowerCase())) + minFreq) / divideFactor) / sumFrequencies) : maxInfCont;

        return Math.log(infCont) / Math.log(maxInfCont);
    }

    public double getInformationContent(List<String> phrase)
    {
        double ic = 1;
        for(String w : phrase)
    	{
        	ic *= getInformationContent(w);    	
    	}
        return ic;
    }
}
