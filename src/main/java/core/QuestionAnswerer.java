package core;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import answerer.ScriptLine;
import answerer.ScriptLine.ScriptWord;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import nlp.StopWords;
import nlp.stemmer.EnglishStemmer;
import util.StringSearcher;

// Truman - http://www.dailyscript.com/scripts/the-truman-show_early.html
public class QuestionAnswerer implements GlobalConstants {

	public ArrayList<ScriptLine> scriptLines = new ArrayList<>();
	//private ArrayList<HashSet<String>> processedSentences = new ArrayList<>();

	public QuestionAnswerer(String script) {
		DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(new StringReader(script));

		// For every NLP preprocessed POJO, parse sentence string
		for(List<HasWord> sentenceRepresentation : docPreprocessor) {
			String sentence = merge(sentenceRepresentation); // Merge processed sentences by eliminating non-semantical tokens

			// Skip sentences with less than 3 semantical tokens
			if(sentence.split("\\s+").length < 3) continue;

			// Process and add the raw sentence to scriptLines
			ScriptLine line = new ScriptLine(sentence);
			scriptLines.add(line);

			if(DEBUG_MODE) {
				System.out.println("Processed");
				System.out.println("\tFrom: " + sentence + " " + sentence.split("\\s+").length);
				System.out.println("\tTo: " + line);
				System.out.println();
			}
		}

		System.out.println("Included Sentence Count: " + scriptLines.size());
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
			System.out.println(questions[i]);
			answers[i] = answer(questions[i]);
		}

		return answers;
	}

	private String answer(String question) {
		question = StopWords.eliminateWords(question); // Eliminate stop words
		question = EnglishStemmer.stemAll(question); // Stem each word
		String[] questionWords = question.split("\\s+");

		ArrayList<ScriptLine> possibleAnswerSentences = scriptLines;

		for(String questionWord : questionWords) {
			possibleAnswerSentences = queryWithWord(possibleAnswerSentences, questionWord);
		}

		if(possibleAnswerSentences.size() > 1) {
			System.out.println("GREATER");
			return null; // TODO: find a better way to eliminate
		}
		else if(possibleAnswerSentences.size() == 1) {
			// TODO eliminate words in question, see if only 1 word remains
			// XXX OR join them with an "OR" keyword
			ArrayList<ScriptWord> excludedOnes = possibleAnswerSentences.get(0).getExcluded(questionWords);
			return excludedOnes.size()==1 ?
					excludedOnes.get(0).getRaw() :
						join(excludedOnes, " OR ");
		} else {
			return "No answer";
		}
	}

	private String join(ArrayList<ScriptWord> words, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<words.size(); i++) {
			sb.append(words.get(i).getRaw());
			if(i != words.size()-1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	private ArrayList<ScriptLine> queryWithWord(ArrayList<ScriptLine> from, String word) {
		ArrayList<ScriptLine> query = new ArrayList<>();

		for(ScriptLine line : from) {
			if(line.contains(word))
				query.add(line);
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
