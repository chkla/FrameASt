package edu.uma.nlp.graphseg.preprocessing;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class NamedEntityAnnotation extends Annotation {

	private NamedEntityType namedEntityType;

    public NamedEntityType getNamedEntityType() {
		return namedEntityType;
	}

	public void setNamedEntityType(NamedEntityType namedEntityType) {
		this.namedEntityType = namedEntityType;
	}

    private String text;
    
	public String getText() {
		if ((text == null || StringUtils.isEmpty(text)) && getTokens().size() > 0)
		{
			text = "";
			for(int i = 0; i < tokens.size(); i++)
			{
				text += tokens.get(i).getText() + " ";
			}
			text = text.trim();
		}
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

	public Boolean isPerson()
    {       
        return namedEntityType == NamedEntityType.Person;
    }

	public Boolean isLocation()
    {       
        return namedEntityType == NamedEntityType.Location;
    }

	public Boolean isOrganization()
    {       
        return namedEntityType == NamedEntityType.Organization;
    }

    private List<TokenAnnotation> tokens;
    public List<TokenAnnotation> getTokens()
    {
    	if (tokens == null) tokens = loadTokens();
    	return tokens;
    }
    
    private List<TokenAnnotation> loadTokens()
    {
        if (childAnnotations.containsKey(AnnotationType.TokenAnnotation)) return childAnnotations.get(AnnotationType.TokenAnnotation).stream().map(x -> (TokenAnnotation)x).collect(Collectors.toList());
        else return null;
    }
    
    public NamedEntityAnnotation(NamedEntityType type)
    {
        namedEntityType = type;
    }
    
    public NamedEntityAnnotation(NamedEntityType type, String text, int startPosition)
    {
    	this.namedEntityType = type;
    	this.text = text;
    	this.startPosition = startPosition;
    }
}
