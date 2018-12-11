package answerer;

import java.util.ArrayList;
import java.util.HashSet;

import nlp.StopWords;
import nlp.stemmer.EnglishStemmer;

public class ScriptLine {

	public static class ScriptWord {
		int index;
		String raw;
		String processed;
		
		public String getRaw() {
			return raw;
		}
		
		@Override
		public int hashCode() {
			return processed.hashCode();
		}
		
		@Override
		public boolean equals(Object other) {
			if(this == other) return true;
			
			if(other instanceof String) {
				return this.processed.equals(other);
			}
			
			if(other instanceof ScriptWord) {
				ScriptWord sw2 = (ScriptWord) other;
				return sw2.processed.equals(this.processed);
			}
				
			return false;
		}
		
		@Override
		public String toString() {
			return "[" + index + " " + raw + " | " + processed + "]";
		}
	}
	
	String[] rawWords;
	HashSet<ScriptWord> words = new HashSet<>();
	
	public ScriptLine(String sentence) {
		rawWords = sentence.split("\\s+");
		
		// Eliminate stop words
		sentence = StopWords.eliminateWords(sentence);
		
		// Stem every word
		String[] eliminatedWords = sentence.split("\\s+");
		for(int i=0; i<eliminatedWords.length; i++) {
			ScriptWord scriptWord = new ScriptWord();
			scriptWord.index = i;
			scriptWord.raw = eliminatedWords[i];
			scriptWord.processed = new EnglishStemmer(eliminatedWords[i]).stem();
			words.add(scriptWord);
		}
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		for(ScriptWord sw : words)
			h += sw.hashCode();
		return h;
	}
	
	public ArrayList<ScriptWord> getExcluded(String[] exclusionWords) {
		ArrayList<ScriptWord> excluded = new ArrayList<>();
		
		scriptWordLoop: for(ScriptWord sw : this.words) {
			for(String exclusionWord : exclusionWords) {
				if(sw.processed.equals(exclusionWord)) {
					continue scriptWordLoop;
				}
			}
			excluded.add(sw);
		}
		
		return excluded;
	}
	
	public boolean contains(String stemmedWord) {
		ScriptWord sw = new ScriptWord();
		sw.processed = stemmedWord;
		return words.contains(sw);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for(ScriptWord sw : words) {
			sb.append(sw);
		}
		sb.append("}");
		return sb.toString();
	}
	
}
