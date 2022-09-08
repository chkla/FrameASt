package edu.uma.nlp.graphseg.preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.CharacterOffsetBeginAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

class Debug {
        public static boolean on = true;

        public static void print( String s ) {
                if (on) System.out.println(s);
        }
}

public class StanfordAnnotator implements IAnnotator {

	private List<String> stanfordAnnotators;
	private String stanfordAnnotatorsString;

	public void setStanfordAnnotators(List<String> stanfordAnnotators) {
		this.stanfordAnnotators = stanfordAnnotators;
		
		stanfordAnnotatorsString = "";
		for(int i = 0; i < this.stanfordAnnotators.size(); i++)
		{
			if (i == 0) stanfordAnnotatorsString += this.stanfordAnnotators.get(i);
			else stanfordAnnotatorsString += ", " + this.stanfordAnnotators.get(i);
		}
	}

	@Override
	public void annotate(Annotation textUnit) 
	{
		if (textUnit instanceof Document)
		{
	   	    Properties props = new Properties();
		    props.setProperty("annotators", stanfordAnnotatorsString);
		    props.setProperty("tokenize.language", "de");
		    props.setProperty("ssplit.eolonly", "true"); // Don't allow coreNLP to do it's own sentence splittin!
		    props.setProperty("pos.model", "edu/stanford/nlp/models/pos-tagger/german-ud.tagger"); 
		    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		    
		    edu.stanford.nlp.pipeline.Annotation docAnnotation = new edu.stanford.nlp.pipeline.Annotation(((Document)textUnit).getText());
		    pipeline.annotate(docAnnotation);
		    
		    List<CoreMap> sentences = docAnnotation.get(SentencesAnnotation.class);
		    
		    for(CoreMap stanfordSentence : sentences) 
		    {
			      SentenceAnnotation sentence = new SentenceAnnotation();
			      sentence.setText(stanfordSentence.get(TextAnnotation.class));
			      sentence.setStartPosition(stanfordSentence.get(CharacterOffsetBeginAnnotation.class));
			      
			      for (CoreLabel stanfordToken: stanfordSentence.get(TokensAnnotation.class)) 
			      {
			    	  	TokenAnnotation token = new TokenAnnotation(stanfordToken.get(TextAnnotation.class));
				        token.setStartPosition(stanfordToken.beginPosition());
				        token.setSentenceIndex(stanfordToken.sentIndex());
				        
				        if (stanfordAnnotators.contains("lemma"))
				        {
				        	token.setLemma(stanfordToken.lemma());
						//Debug.print("Lemma " + token.getLemma());
				        }
				        
				        if (stanfordAnnotators.contains("pos"))
				        {
					        PartOfSpeechAnnotation posAnnotation = new PartOfSpeechAnnotation();
					        posAnnotation.setTag(stanfordToken.get(edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation.class));
					        token.addChildAnnotation(posAnnotation, AnnotationType.PartOfSpeechAnnotation);
						//Debug.print("POS " + stanfordToken.get(edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation.class));
				        }
				        
				        if (stanfordAnnotators.contains("ner"))
				        {
				        	NamedEntityTokenAnnotation neta = new NamedEntityTokenAnnotation(stanfordToken.get(NamedEntityTagAnnotation.class));
				        	token.addChildAnnotation(neta, AnnotationType.NamedEntityTokenAnnotation);
				        }
				        
				        sentence.addChildAnnotation(token, AnnotationType.TokenAnnotation);
					String sentenceText = sentence.getText();
			    }
		        
		      	// linking continuous token-level NE annotations into whole named entity annotations
		      	if (stanfordAnnotators.contains("ner"))
		        {
		      		List<NamedEntityAnnotation> nes = new ArrayList<NamedEntityAnnotation>();
		      		NamedEntityAnnotation ne = null;
		      		for(int i = 0; i < sentence.getTokens().size(); i++)
		      		{
		      			String neLabel = sentence.getTokens().get(i).getNamedEntityLabel().getNamedEntityLabel();
		      			String neLabelPrevious = i > 0 ? sentence.getTokens().get(i-1).getNamedEntityLabel().getNamedEntityLabel() : "O";
		      			
		      			if (neLabel.compareTo("O") == 0)
		      			{
		      				if (ne != null)
		      				{
		      					nes.add(ne);
		      					ne = null;
		      				}
		      			}
		      			else if (neLabel.compareTo(neLabelPrevious) != 0)
		      			{	
      						NamedEntityType type = Arrays.stream(NamedEntityType.values()).filter(e -> e.name().equalsIgnoreCase(neLabel)).findAny().orElse(null);
      						if (type == null)
      						{
      							throw new UnsupportedOperationException("Unknown named entity type!");
      						}
      					
      						ne = new NamedEntityAnnotation(type);
      						ne.setStartPosition(sentence.getTokens().get(i).getStartPosition());
      						ne.addChildAnnotation(sentence.getTokens().get(i), AnnotationType.TokenAnnotation);
      						
		      			}
		      			else
		      			{
		      				ne.addChildAnnotation(sentence.getTokens().get(i), AnnotationType.TokenAnnotation);
		      			}
		      		}
		      		if (ne != null) nes.add(ne);
		      		
		      		nes.forEach(n -> textUnit.addChildAnnotation(n, AnnotationType.NamedEntityAnnotation));      		
		        }
		     
			    textUnit.addChildAnnotation(sentence, AnnotationType.SentenceAnnotation);
		    }
		    
		    // coreference, crosses sentence borders
	        if (stanfordAnnotators.contains("dcoref"))
	        {
	        	// TODO: coref annotations
	        }
		}
		else throw new UnsupportedOperationException("Only whole documents can be processed by Stanford's CoreNLP pipeline");
	}

	@Override
	public List<Annotation> annotate(String text) 
	{
		Document document = new Document(text);
		annotate(document);
		
		return new ArrayList<Annotation>(Arrays.asList(document));
	}
}
