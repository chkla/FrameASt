package edu.uma.nlp.graphseg.utils;

import edu.uma.nlp.graphseg.semantics.InformationContent;
import edu.uma.nlp.graphseg.semantics.WordVectorSpace;

public class MemoryStorage {

    private static WordVectorSpace wordVectorSpace;
	
	public static WordVectorSpace getWordVectorSpace() {
		return wordVectorSpace;
	}
	public static void setWordVectorSpace(WordVectorSpace wordVectorSpace) {
		MemoryStorage.wordVectorSpace = wordVectorSpace;
	}
	
	private static InformationContent informationContent;
	
	public static InformationContent getInformationContent() {
		return informationContent;
	}
	public static void setInformationContent(InformationContent informationContent) {
		MemoryStorage.informationContent = informationContent;
	}
	
}
