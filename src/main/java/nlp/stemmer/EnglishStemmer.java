package nlp.stemmer;

import java.util.Arrays;
import java.util.HashSet;

import com.programmer.igoodie.utils.io.FileUtils;

/**
 * Stemming implementation of English (Parker2) Stemmeing algorithm
 * as specified by Snowball's stemming documentation <br>
 * <br>
 * 
 * <b>Terminology:</b> <br>
 * Vowel: a e i o u y <br>
 * Double: bb dd ff gg mm nn pp rr tt <br>
 * Valid li-ending: c d e g h k m n r t <br>
 * <br>
 * 
 * R1 is the region after the first non-vowel following a vowel, or the end of the word if there is no such non-vowel. <br>
 * R2 is the region after the first non-vowel following a vowel in R1, or the end of the word if there is no such non-vowel.<br>
 * <br>
 * 
 * short syllable in a word as either ; <br>
 * - (a) a vowel followed by a non-vowel other than w, x or Y and preceded by a non-vowel <br>
 * - (b) a vowel at the beginning of the word followed by a non-vowel. <br>
 * <br>
 * 
 * A word is "short" if it ends with a short syllable and R1 != null <br>
 * <br>
 * 
 * @see {@link <a href=" http://snowball.tartarus.org/algorithms/english/stemmer.html">Snowball English Stemmer Specification</a>}
 * @author Taha Anılcan Metinyurt
 */
public class EnglishStemmer extends Stemmer {

	private static final WordMap EXCEPTIONAL_WORDS = new WordMap(FileUtils.readExternalString("nlp-exception-lookup.csv"));
	private static final HashSet<String> STEP1A_INVARIANT_LIST = new HashSet<>(Arrays.asList(FileUtils.readExternalString("nlp-step4-list.csv").split("\\s*,\\s*")));
	
	private static final SuffixMap STEP2_SUFFIX_LOOKUP = new SuffixMap(FileUtils.readExternalString("nlp-step2-lookup.csv"));
	private static final SuffixMap STEP3_SUFFIX_LOOKUP = new SuffixMap(FileUtils.readExternalString("nlp-step3-lookup.csv"));
	private static final String[] STEP4_SUFFIX_LIST = FileUtils.readExternalString("nlp-step4-list.csv").split("\\s*,\\s*");

	public EnglishStemmer(String word) {
		super(word);

		if(word.startsWith("'")) // Eliminate initial ' character
			this.word = word.substring(1);
	}
	
	/* Helpers */
	private void appendSuffix(String suffix) {
		word = word.concat(suffix);
	}

	private void removeSuffix(int len) {
		word = word.substring(0, word.length()-len);
	}

	private void removeSuffix(String suffix) {
		word = sequenceBefore(suffix);
	}

	private int lastNonVowelIndex() {
		for(int i=word.length()-1; i>=0; i--) {
			if(!isVowel(word.charAt(i)))
				return i;
		}
		return -1;
	}

	private String sequenceBefore(String suffix) {
		return word.substring(0, word.lastIndexOf(suffix));
	}

	private String region1() {
		if(word.startsWith("gener"))
			return word.substring("gener".length());

		if(word.startsWith("commun "))
			return word.substring("commun ".length());
		
		if(word.startsWith("arsen"))
			return word.substring("arsen".length());
			
		for(int i=0, l=word.length()-1; i<l; i++) {
			if(isVowel(word.charAt(i)) && !isVowel(word.charAt(i+1)))
				return word.substring(i+2);
		}
		return "";
	}

	private String region2() {
		String region1 = region1();
		for(int i=0, l=region1.length()-1; i<l; i++) {
			if(isVowel(region1.charAt(i)) && !isVowel(region1.charAt(i+1)))
				return region1.substring(i+2);
		}
		return "";
	}

	private int firstVowelIndex() {
		for(int i=0, l=word.length(); i<l; i++) {
			if(isVowel(word.charAt(i)))
				return i;
		}
		return -1;
	}

	private boolean isVowel(char c) {
		switch(c) {
		case 'a':
		case 'e':
		case 'i':
		case 'o':
		case 'u':
		case 'y':
			return true;
		default: 
			return false;
		}
	}

	private boolean isShortSyllable(String seq) {
		System.out.println(seq);
		if(isVowel(seq.charAt(0)) || !isVowel(seq.charAt(1)))
			return false;
		
		if(seq.length() == 3) {			
			char lastChar = seq.charAt(seq.length()-1);
			return !isVowel(lastChar) && lastChar!='w' && lastChar!='x' && lastChar!='y';
		} else 
			return true;
	}
	
	private boolean endsWithDouble() {
		String lastChars = word.substring(word.length()-2);
		switch(lastChars) {
		case "bb":
		case "dd":
		case "ff":
		case "gg":
		case "mm":
		case "nn":
		case "pp":
		case "rr":
		case "tt":
			return true;
		default:
			return false;
		}
	}

	private boolean isValidLiEnding(char c) {
		switch(c) {
		case 'c':
		case 'd':
		case 'e':
		case 'g':
		case 'h':
		case 'k':
		case 'm':
		case 'n':
		case 'r':
		case 't':
			return true;
		default:
			return false;
		}
	}

	private boolean isShort() {
		if(!region1().isEmpty()) return false;

		if(word.length() >= 3)
			return isShortSyllable(word.substring(word.length()-3));
		else
			return isShortSyllable(word.substring(0, 2));
	}

	private boolean containsVowel(String sequence) {
		for(int i=0, l=sequence.length(); i<l; i++) {
			if(isVowel(sequence.charAt(i)))
				return true;
		}
		return false;
	}

	/* Steps */
	protected void step0() {
		if(word.endsWith("'s'")) {
			removeSuffix("'s'");
		} else if(word.endsWith("'s")) {
			removeSuffix("'s");
		} else if(word.endsWith("'")) {	
			removeSuffix("'");
		}
	}

	private void step1a() {
		if(word.endsWith("sses")) {
			removeSuffix("es"); // Replaces 'sses' with 'es'

		} else if(word.endsWith("ied") || word.endsWith("ies")) {
			removeSuffix(3); // Removes last 3 letters, which can be "ies" or "ied"
			appendSuffix(word.length()==1 ? "ie" : "i"); // Appends "ie" or "i". (ties -> tie, cries -> cri)

		} else if(word.endsWith("us") || word.endsWith("ss")) {
			return; // Do nothing.

		} else if(word.endsWith("s")) { // Delete "s", if there exists a vowel before it, which is not immidiately before it.
			int vowelIndex = firstVowelIndex();
			if(vowelIndex == -1) return; // Also do not delete if no vowel found.
			if(vowelIndex != word.length()-2)
				removeSuffix("s");
		}
	}

	private void step1b() {
		boolean secondCheck = false;

		if(word.endsWith("eedly")) {
			if(!region1().isEmpty())
				removeSuffix(3); // Remove "dly" if suffix is in R1

		} else if(word.endsWith("ingly")) {
			if(containsVowel(sequenceBefore("ingly")))
				removeSuffix("ingly");
			secondCheck = true;

		} else if(word.endsWith("edly")) {
			if(containsVowel(sequenceBefore("edly")))
				removeSuffix("edly");
			secondCheck = true;

		} else if(word.endsWith("eed")) {
			if(!region1().isEmpty())
				removeSuffix(1); // Remove "d" if suffix is in R1

		} else if(word.endsWith("ing")) {
			if(containsVowel(sequenceBefore("ing")))
				removeSuffix("ing");
			secondCheck = true;

		} else if(word.endsWith("ed")) {
			if(containsVowel(sequenceBefore("ed")))
				removeSuffix("ed");
			secondCheck = true;
		}

		if(secondCheck) {
			if(word.endsWith("at") || word.endsWith("bl") || word.endsWith("iz"))
				appendSuffix("e"); // Add "e" if ends with "at", "bl" or "iz"
			else if(endsWithDouble())
				removeSuffix(1); // Remove last character if ends with a double
			else if(isShort())
				appendSuffix("e");
		}
	}

	private void step1c() {
		if(word.endsWith("y")) {
			int nonVowelIndex = lastNonVowelIndex();
			if(nonVowelIndex==-1 || nonVowelIndex==0) return;
			removeSuffix("y");
			appendSuffix("i");
		}
	}

	protected void step2() {
		String r1 = region1();

		if(r1.isEmpty()) return; // No R1 = No suffix matching in R1

		if(r1.endsWith("li") && isValidLiEnding(word.charAt(word.length()-3))) {		
			removeSuffix("li"); // Remove "li" if preceded by a valid li-ending
			return;
		}

		for(String suffix : STEP2_SUFFIX_LOOKUP.keySet()) {
			if(r1.endsWith(suffix)) {
				removeSuffix(suffix);
				appendSuffix(STEP2_SUFFIX_LOOKUP.get(suffix));
				break;
			}
		}
	}

	protected void step3() {
		String r1 = region1();

		if(r1.isEmpty()) return; // No R1 = No suffix matching in R1
		
		if(region2().endsWith("ative"))
			removeSuffix("ative");
		
		else for(String suffix : STEP3_SUFFIX_LOOKUP.keySet()) {
			if(r1.endsWith(suffix)) {
				removeSuffix(suffix);
				appendSuffix(STEP3_SUFFIX_LOOKUP.get(suffix));
				break;
			}
		}
	}

	protected void step4() {
		String r2 = region2();
		
		if(r2.isEmpty()) return;
		
		if(r2.endsWith("sion") || r2.endsWith("tion"))
			removeSuffix("ion");
		
		else for(String suffix : STEP4_SUFFIX_LIST) {
			if(r2.endsWith(suffix)) {
				removeSuffix(suffix);
				return;
			}
		}
	}

	protected void step5() {
		String r2 = region2();
		
		if(r2.endsWith("e")) {
			removeSuffix("e");
		
		} else if(region1().endsWith("e")) {
			String seq;
			int len = word.length();
			if(len >= 4)
				seq = word.substring(len-4, len-1);
			else 
				seq = word.substring(0, len-1);
			if(isShortSyllable(seq))
				removeSuffix("e");
			
		} else if(r2.endsWith("l")) {
			if(r2.charAt(r2.length()-2) == 'l')
				removeSuffix("l");
		}
	}

	public String stem() {
		if(EXCEPTIONAL_WORDS.containsKey(word)) {
			String exception = EXCEPTIONAL_WORDS.get(word);
			return exception.isEmpty() ? word : exception;
		}
		
		step0();
		step1a();
		
		if(STEP1A_INVARIANT_LIST.contains(word))
			return word;
		
		step1b();
		step1c();
		
		step2();
		step3();
		step4();
		
		step5(); // Which is an additional step denoted by ('*')
		return word;
	};

}
