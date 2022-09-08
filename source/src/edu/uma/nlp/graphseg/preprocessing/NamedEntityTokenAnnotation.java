package edu.uma.nlp.graphseg.preprocessing;

public class NamedEntityTokenAnnotation extends Annotation {
	
	private String namedEntityLabel;
	   
    public String getNamedEntityLabel() {
		return namedEntityLabel;
	}

	public void setNamedEntityLabel(String namedEntityLabel) {
		this.namedEntityLabel = namedEntityLabel;
	}

	public NamedEntityTokenAnnotation()
    {
    }

    public NamedEntityTokenAnnotation(String label)
    {
        namedEntityLabel = label;
    }

    public Boolean constitutesNamedEntity()
    {
        return startsNamedEntity() || insideNamedEntity();
    }

    public Boolean startsNamedEntity()
    {
        return namedEntityLabel.startsWith("B");
    }

    public Boolean insideNamedEntity()
    {
        return namedEntityLabel.startsWith("I");
    }
}
