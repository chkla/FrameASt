package edu.uma.nlp.graphseg.preprocessing;

import java.util.List;

public interface IAnnotator
{
    void annotate(Annotation textUnit);
    List<Annotation> annotate(String text);
}
