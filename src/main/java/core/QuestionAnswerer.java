package core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import nlp.StopWords;

// Truman - http://www.dailyscript.com/scripts/the-truman-show_early.html
public class QuestionAnswerer {

	private ArrayList<String> processedSentences = new ArrayList<>();

	public QuestionAnswerer(String script) {
		DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(new StringReader(script));

		// For every NLP preprocessed POJO, parse string
		for(List<HasWord> sentenceRepresentation : docPreprocessor) {
			String sentence = merge(sentenceRepresentation);
			if(sentence.split("\\s+").length < 3) continue;
			System.out.println(sentence + " " + sentence.split("\\s+").length);

			processedSentences.add(StopWords.eliminateWords(sentence));
			System.out.println(processedSentences.get(processedSentences.size()-1));
			System.out.println();
		}
	}

	private boolean isPunctuation(String ch) {
		switch(ch) {
		case ".":
		case "?":
		case "!":
		case "...":
			return true;
		default:
			return false;
		}
	}

	private String merge(List<HasWord> words) {
		String sentence = "";

		for(int i=0, l=words.size(); i<l; i++) {
			String word = words.get(i).word();
			if(!isPunctuation(word) && !word.matches("\\s+")) {
				if(!word.contains("'") && i!=0) // Append space, if it's not an apos nor the first word
					sentence += " ";
				
				sentence += word; // Append the word then.
			}
		}

		return sentence;
	}

	public String[] answer(String[] questions) {
		return null; // TODO
	}

}
