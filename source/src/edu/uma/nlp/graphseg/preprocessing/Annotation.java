package edu.uma.nlp.graphseg.preprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Annotation {
	 
     protected HashMap<AnnotationType, List<Annotation>> childAnnotations;

     public Annotation()
     {
         childAnnotations = new HashMap<AnnotationType, List<Annotation>>();
     }

     public List<Annotation> getChildAnnotations(AnnotationType type)
     {
         if (childAnnotations.containsKey(type)) return childAnnotations.get(type);
         else return new ArrayList<Annotation>();
     }

     public void addChildAnnotation(Annotation annotation, AnnotationType type)
     {
         if (!childAnnotations.containsKey(type)) childAnnotations.put(type, new ArrayList<Annotation>());
         childAnnotations.get(type).add(annotation);
     }
     
     public void removeChildAnnotation(Annotation annotation)
     {
    	 if (childAnnotations.containsKey(annotation))
    	 {
    		 childAnnotations.remove(annotation);
    	 }
     }

}
