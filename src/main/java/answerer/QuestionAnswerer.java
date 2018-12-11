package answerer;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import core.GlobalConstants;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import nlp.StopWords;
import nlp.stemmer.EnglishStemmer;
import util.StringSearcher;

// Truman - http://www.dailyscript.com/scripts/the-truman-show_early.html
public class QuestionAnswerer implements GlobalConstants {

	private ArrayList<HashSet<String>> processedSentences = new ArrayList<>();

	public QuestionAnswerer(String script) {
		DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(new StringReader(script));

		// Read script
		// Eliminate stop words
		// Stem script
		// Split sentences

		// For every NLP preprocessed POJO, parse sentence string
		for(List<HasWord> sentenceRepresentation : docPreprocessor) {
			String sentence = merge(sentenceRepresentation); // Merge processed sentences by eliminating non-semantical tokens
			
			if(sentence.split("\\s+").length < 3) continue; // Skip sentences with less than 3 semantical tokens
			
			// Process the sentence and add to the list
			String processed = StopWords.eliminateWords(sentence);
			processed = EnglishStemmer.stemAll(processed);
			processedSentences.add(new HashSet<>(Arrays.asList(processed.split("\\s+"))));

			if(DEBUG_MODE) {
				System.out.println("Processed");
				System.out.println("\tFrom: " + sentence + " " + sentence.split("\\s+").length);
				System.out.println("\tTo: " + processedSentences.get(processedSentences.size()-1));
				System.out.println();
			}
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
				if(!StringSearcher.containsRabinKarp(word, "'") && i!=0) // Append space, if it's not an apos nor the first word
					sentence += " ";

				sentence += word; // Append the word then.
			}
		}

		return sentence;
	}

	public String[] answerAll(String[] questions) {
		String[] answers = new String[questions.length];
		
		for(int i=0; i<questions.length; i++) {
			answers[i] = answer(questions[i]);
		}
		
		return answers;
	}

	private String answer(String question) {
		question = StopWords.eliminateWords(question); // Eliminate stop words
		question = EnglishStemmer.stemAll(question); // Stem each word
		String[] questionWords = question.split("\\s+");
		ArrayList<HashSet<String>> possibleAnswerSentences = processedSentences;
		
		for(String questionWord : questionWords) {
			possibleAnswerSentences = queryWithWord(possibleAnswerSentences, questionWord);
		}
		
		if(possibleAnswerSentences.size() == 1) {
			return possibleAnswerSentences.get(0).toString();
		}
		
		return null;
	}
	
	private ArrayList<HashSet<String>> queryWithWord(ArrayList<HashSet<String>> from, String word) {
		ArrayList<HashSet<String>> query = new ArrayList<>();
		
		for(HashSet<String> sentenceRepresentation : from) {
			if(sentenceRepresentation.contains(word))
				query.add(sentenceRepresentation);
		}
		
		return query;
	}

	// For each question;
	// Eliminate stop words
	// Stem words
	// Set relevant results = script
	// - For each word in question;
	// - Query relevant results
	// - Set relevant results = query
	// If relevant results.len == 1 assume it has an anwer
}
