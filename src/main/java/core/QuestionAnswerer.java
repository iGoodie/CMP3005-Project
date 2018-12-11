package core;

import java.io.StringReader;
import java.util.ArrayList;
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
			answers[i] = answer(questions[i].toLowerCase());
			
			System.out.println("Question#" + i + " " + questions[i]);
			System.out.println("Answer#" + i + " " + answers[i]);
			System.out.println();
		}

		return answers;
	}

	private String answer(String question) {
		question = StopWords.eliminateQuestions(question); // Eliminate question words
		question = StopWords.eliminateWords(question); // Eliminate stop words
		question = EnglishStemmer.stemAll(question); // Stem each word
		String[] questionWords = question.split("\\s+");

		ArrayList<ScriptLine> possibleAnswerSentences = scriptLines;

		for(String questionWord : questionWords) {
			possibleAnswerSentences = queryWithWord(possibleAnswerSentences, questionWord);
			if(DEBUG_MODE) System.out.println(possibleAnswerSentences.size());
		}

		// Classify by final query size
		if(possibleAnswerSentences.size() > 1) {
			return null; // TODO: find a better way to eliminate
		}
		else if(possibleAnswerSentences.size() == 1) {
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
	
}
