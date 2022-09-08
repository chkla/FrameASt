package edu.uma.nlp.graphseg.preprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Document extends Annotation {
	
	private String id;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private String path;
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	private String text;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}


	// Lazy loading

    private List<TokenAnnotation> tokens;
    
    public List<TokenAnnotation> getTokens() {
		if (tokens == null) tokens = loadTokens();
    	return tokens;
	}

	private List<SentenceAnnotation> sentences;
	
    public List<SentenceAnnotation> getSentences() {
		if (sentences == null) sentences = loadSentences();
    	return sentences;
	}

	private List<NamedEntityAnnotation> namedEntities;
	
    public List<NamedEntityAnnotation> getNamedEntities() {
    	if (namedEntities == null) namedEntities = loadNamedEntities();
    	return namedEntities;
	}

	private List<TokenAnnotation> loadTokens()
    {
        if (getSentences() != null)
        {
            List<TokenAnnotation> toks = new ArrayList<TokenAnnotation>();
            for (int i = 0; i < sentences.size(); i++)
            {
                toks.addAll(sentences.get(i).getTokens());
            }
            
            toks.sort((t1, t2) -> (t1.getStartPosition() < t2.getStartPosition()) ? -1 : ((t1.getStartPosition() > t2.getStartPosition()) ? 1 : 0));
            return toks;
        }
        else return null;

    }

    private List<SentenceAnnotation> loadSentences()
    {	
        if (childAnnotations.containsKey(AnnotationType.SentenceAnnotation)) 
        	return childAnnotations.get(AnnotationType.SentenceAnnotation).stream().map(x -> (SentenceAnnotation)x).collect(Collectors.toList());
        else return null;
    }

    private List<NamedEntityAnnotation> loadNamedEntities()
    {
        if (childAnnotations.containsKey(AnnotationType.NamedEntityAnnotation)) 
        	return childAnnotations.get(AnnotationType.NamedEntityAnnotation).stream().map(x -> (NamedEntityAnnotation)x).collect(Collectors.toList());
        else return null;
    }
    
    // Ctors
    
    public Document()
    {
    }
    
    public Document(String text)
    {
    	this.text = text;
    }
    
    public Document(String id, String text)
    {
    	this.id = id;
    	this.text = text;
    }
}
