package edu.uma.nlp.graphseg.preprocessing;

public class TokenAnnotation extends Annotation {
	
	// Fields & properties
	
    private String text; 
    
    public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	private String lemma;
	
	public String getLemma() {
		return lemma;
	}
	public void setLemma(String lemma) {
		this.lemma = lemma;
	}
	
	private int startPosition;
	
	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
    
	public int getEndPosition() {
		return text != null ? startPosition + text.length() - 1 : startPosition;
	}
	
    private int startPositionSentence;

    public int getStartPositionSentence() {
		return startPositionSentence;
	}

	public void setStartPositionSentence(int startPositionSentence) {
		this.startPositionSentence = startPositionSentence;
	}
	
	public int getEndPositionSentence() {
		return text != null ? startPositionSentence + text.length() - 1 : startPositionSentence;
	}
	
    private int sentenceIndex;
    
    public int getSentenceIndex() {
		return sentenceIndex;
	}

	public void setSentenceIndex(int sentenceIndex) {
		this.sentenceIndex = sentenceIndex;
	}
	
	// Lazy loading properties
   
    private PartOfSpeechAnnotation partOfSpeech;
	public PartOfSpeechAnnotation getPartOfSpeech()
    {
    	if (partOfSpeech == null) partOfSpeech = loadPartOfSpeech();
    	return partOfSpeech;
    }
    

    private NamedEntityTokenAnnotation namedEntityLabel;
    public NamedEntityTokenAnnotation getNamedEntityLabel()
    {
        if (namedEntityLabel == null) namedEntityLabel = loadTokenNELabel();
        return namedEntityLabel;
    }

    private PartOfSpeechAnnotation loadPartOfSpeech()
    {
        if (!childAnnotations.containsKey(AnnotationType.PartOfSpeechAnnotation)) this.addChildAnnotation(new PartOfSpeechAnnotation(), AnnotationType.PartOfSpeechAnnotation); 
    	return ((PartOfSpeechAnnotation)(getChildAnnotations(AnnotationType.PartOfSpeechAnnotation).get(0)));
    }

    private NamedEntityTokenAnnotation loadTokenNELabel()
    {
        if (!childAnnotations.containsKey(AnnotationType.NamedEntityTokenAnnotation)) return null; //this.addChildAnnotation(new NamedEntityTokenAnnotation(), AnnotationType.NamedEntityTokenAnnotation); 
        else return ((NamedEntityTokenAnnotation)(childAnnotations.get(AnnotationType.NamedEntityTokenAnnotation).get(0)));       
    }
    
    public TokenAnnotation(String text, int startPosition, int startPositionSentence, int sentenceIndex)
    {
    	this.text = text;
    	this.startPosition = startPosition;
    	this.startPositionSentence = startPositionSentence;
    	this.sentenceIndex = sentenceIndex;
    }
    
    public TokenAnnotation(String text)
    {
    	this.text = text;
    }
}
