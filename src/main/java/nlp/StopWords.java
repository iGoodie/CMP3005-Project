package nlp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.programmer.igoodie.utils.io.FileUtils;

import util.StringSearcher;

public class StopWords {

	public static final String[] STOP_WORDS_ARRAY = FileUtils.readExternalString("stop-word-list.csv").split("\r?\n");
	public static final HashSet<String> STOP_WORDS = new HashSet<>(Arrays.asList(STOP_WORDS_ARRAY));
	
	public static final String eliminateWords(String from) {
		StringBuilder eliminated = new StringBuilder();
		
		/* Following examples are considered as a word;
		 * 1) Something
		 * 2) I'm
		 * 3) Goose-stepped
		 * 4) 12:04
		 */
		Pattern p = Pattern.compile("[a-zA-Z0-9]+(('|-|:)[a-zA-Z0-9]+)?"); // Will also ignore punctuations.
		Matcher m = p.matcher(from);
		
		while(m.find()) { // While we have matching words
			String word = m.group().toLowerCase();
			if(!STOP_WORDS.contains(word)) { // Fetching from HashSet: O(1)
				eliminated.append(word + " ");
			}
		}
		
		return eliminated.toString().trim();
	}
	
	public static final String eliminateQuestions(String from) {
		from = eliminateWord(from, "how many");
		from = eliminateWord(from, "how much");
		from = eliminateWord(from, "how often");
		if(from.startsWith("what")) {
			from = eliminateWord(from, "what ");
			from = from.substring(from.indexOf(" "));
		}
		
		return from;
	}
	
	private static final String eliminateWord(String from, String pattern) {
		int index = StringSearcher.searchIndexRabinKarp(from, pattern);
		return index==-1 ? from : from.replace(pattern, "");
	}
	
}
