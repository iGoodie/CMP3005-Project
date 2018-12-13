package answerer;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.programmer.igoodie.utils.io.FileUtils;

import answerer.TextLine.TextWord;
import core.GlobalConstants;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;
import nlp.StopWords;
import nlp.stemmer.EnglishStemmer;
import util.ChecksumUtils;
import util.ObjectIO;
import util.StringSearcher;

// Truman Script - http://www.dailyscript.com/scripts/the-truman-show_early.html
public class QuestionAnswerer implements GlobalConstants, Serializable {

	private static final long serialVersionUID = 0x000_001_000L;
	
	public static QuestionAnswerer fetch(String text, String cachePath) {
		if(!CACHE_ENABLED) return new QuestionAnswerer(text);
		
		// Fetch checksum, if doesn't exist previously build new
		String cachedMd5 = FileUtils.readString(cachePath + "\\prev_checksum.txt");
		String currentMd5 = ChecksumUtils.hashMD5(text);

		if(cachedMd5 == null) {
			System.out.println("Answerer doesn't exist in cache. A new one is being built with read text.\n");
			QuestionAnswerer answerer = new QuestionAnswerer(text);
			ObjectIO.writeObject(answerer, cachePath + "\\answerer.cache");
			FileUtils.writeString(currentMd5, cachePath + "\\prev_checksum.txt");
			return answerer;
		}
		
		// Calculate current MD5, then compare
		if(cachedMd5.equals(cachedMd5)) {
			System.out.println("Answerer existed in cache. Fetching it from the cache.\n");
			return (QuestionAnswerer) ObjectIO.readObject(cachePath + "\\answerer.cache");
		} else {
			System.out.println("Answerer doesn't exist in cache. A new one is being built with read text.\n");
			QuestionAnswerer answerer = new QuestionAnswerer(text);
			ObjectIO.writeObject(answerer, cachePath + "\\answerer.cache");
			FileUtils.writeString(currentMd5, cachePath + "\\prev_checksum.txt");
			return answerer;
		}
	}

	public ArrayList<TextLine> textLines = new ArrayList<>();

	public QuestionAnswerer(String text) {
		DocumentPreprocessor docPreprocessor = new DocumentPreprocessor(new StringReader(text));

		// For every NLP preprocessed POJO, parse sentence string
		for (List<HasWord> sentenceRepresentation : docPreprocessor) {
			String sentence = merge(sentenceRepresentation); // Merge processed sentences by eliminating non-semantical tokens

			// Skip sentences with less than 3 semantical tokens
			if (sentence.split("\\s+").length < 3)
				continue;

			// Process and add the raw sentence to textLines
			TextLine line = new TextLine(sentence);
			textLines.add(line);

			if (DEBUG_MODE) {
				System.out.println("Processed");
				System.out.println("\tFrom: " + sentence + " " + sentence.split("\\s+").length);
				System.out.println("\tTo: " + line);
				System.out.println();
			}
		}

		System.out.println("Included Sentence Count: " + textLines.size());
	}

	private boolean isPunctuation(String ch) {
		switch (ch) {
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

		for (int i = 0, l = words.size(); i < l; i++) {
			String word = words.get(i).word();
			if (!isPunctuation(word) && !word.matches("\\s+")) {
				if (!StringSearcher.containsRabinKarp(word, "'") && i != 0) // Append space, if it's not an apos nor the first word
					sentence += " ";

				sentence += word; // Append the word then.
			}
		}

		return sentence;
	}

	public String[] answerAll(String[] questions) {
		String[] answers = new String[questions.length];

		for (int i = 0; i < questions.length; i++) {
			answers[i] = answer(questions[i].toLowerCase());

			System.out.println("Question#" + i + " = " + questions[i]);
			System.out.println("Answer#" + i + " = " + answers[i]);
			System.out.println();
		}

		return answers;
	}

	private String answer(String question) {
		question = StopWords.eliminateQuestions(question); // Eliminate question
		// words
		question = StopWords.eliminateWords(question); // Eliminate stop words
		question = EnglishStemmer.stemAll(question); // Stem each word
		String[] questionWords = question.split("\\s+");

		ArrayList<TextLine> possibleAnswerSentences = textLines;

		for (String questionWord : questionWords) {
			possibleAnswerSentences = queryWithWord(possibleAnswerSentences, questionWord);
			if (DEBUG_MODE)
				System.out.println("len(Possible sentences) = " + possibleAnswerSentences.size());
		}

		// Classify by final query size
		if (possibleAnswerSentences.size() > 1) {
			return "Undecided answer"; // TODO: find a way to eliminate

		} else if (possibleAnswerSentences.size() == 1) {
			ArrayList<TextWord> excludedOnes = possibleAnswerSentences.get(0).getExcluded(questionWords);
			excludedOnes.sort((w1, w2) -> w1.index - w2.index); // Sort them before joining, so the word order is preserved.
			return excludedOnes.size() == 1 ? excludedOnes.get(0).getRaw() : join(excludedOnes, " ");

		} else {
			return "No answer";
		}
	}

	private String join(ArrayList<TextWord> words, String delimiter) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < words.size(); i++) {
			sb.append(words.get(i).getRaw());
			if (i != words.size() - 1) {
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	private ArrayList<TextLine> queryWithWord(ArrayList<TextLine> from, String word) {
		ArrayList<TextLine> query = new ArrayList<>();

		for (TextLine line : from) {
			if (line.contains(word))
				query.add(line);
		}

		return query;
	}

}
