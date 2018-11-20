package nlp;

import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.programmer.igoodie.utils.io.FileUtils;

public class StopWords {

	public static final String[] STOP_WORDS_ARRAY = FileUtils.readExternalString("stop-word-list.csv").split("\r?\n");
	public static final HashSet<String> STOP_WORDS = new HashSet<>(Arrays.asList(STOP_WORDS_ARRAY));
	
	public static final String eliminateSentence(String from) {
		StringBuilder eliminated = new StringBuilder();
		
		/* Following examples are considered as a word;
		 * 1) Something
		 * 2) I'm
		 * 3) Goose-stepped
		 */
		Pattern p = Pattern.compile("[a-zA-Z]+('|-)?[a-zA-Z]+");
		Matcher m = p.matcher(from);
		
		while(m.find()) { // While we have matching words
			String word = m.group();
			if(!STOP_WORDS.contains(word)) { // Fetching from HashSet: O(1)
				eliminated.append(word + " ");
			}
		}
		
		return eliminated.toString().toLowerCase().trim();
	}
	
}
