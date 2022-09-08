package edu.uma.nlp.graphseg.preprocessing;

import java.util.List;
import java.util.stream.Collectors;

public class SentenceAnnotation extends Annotation {

	// Fields & properties
	
    private String text;

    public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	private int startPosition;
	
	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

    public int getEndPosition()
    { 
    	return text != null ? startPosition + text.length() - 1 : startPosition;
    }
    
    // Lazy properties
    
    private List<TokenAnnotation> tokens;
    public List<TokenAnnotation> getTokens()
    {
    	if (tokens == null) tokens = loadTokens();
    	return tokens;
    }
    
    public void setTokens(List<TokenAnnotation> tokens)
    {
    	this.tokens = tokens;
    }
    
    private List<TokenAnnotation> loadTokens()
    {
        if (childAnnotations.containsKey(AnnotationType.TokenAnnotation)) return childAnnotations.get(AnnotationType.TokenAnnotation).stream().map(x -> (TokenAnnotation)x).collect(Collectors.toList());
        else return null;
    }


    // Ctors
    public SentenceAnnotation()
    {
    }
    
    public SentenceAnnotation(String text, int startPosition)
    {
    	this.text = text;
    	this.startPosition = startPosition;
    }
}
