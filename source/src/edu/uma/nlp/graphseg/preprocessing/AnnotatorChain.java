package edu.uma.nlp.graphseg.preprocessing;

import java.util.ArrayList;
import java.util.List;

public class AnnotatorChain {

    private List<IAnnotator> chain;

    public AnnotatorChain()
    {
    }

    
    public AnnotatorChain(List<IAnnotator> annotators)
    {
        chain = annotators;
    }

    public AnnotatorChain addAnnotator(IAnnotator annotator)
    {
        if (chain == null) chain = new ArrayList<IAnnotator>();
        chain.add(annotator);
        return this;
    }

    public void apply(Annotation textUnit)
    {
        for (int i = 0; i < chain.size(); i++)
        {
            chain.get(i).annotate(textUnit);
        }
    }

}
