package answerer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

import nlp.StopWords;
import nlp.stemmer.EnglishStemmer;

public class TextLine implements Serializable {
	
	private static final long serialVersionUID = 0x000_001_000L;

	public static class TextWord implements Serializable {
		
		private static final long serialVersionUID = TextLine.serialVersionUID;		
		
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
			
			if(other instanceof TextWord) {
				TextWord sw2 = (TextWord) other;
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
	HashSet<TextWord> words = new HashSet<>();
	
	public TextLine(String sentence) {
		rawWords = sentence.split("\\s+");
		
		// Eliminate stop words
		sentence = StopWords.eliminateWords(sentence);
		
		// Stem every word
		String[] eliminatedWords = sentence.split("\\s+");
		for(int i=0; i<eliminatedWords.length; i++) {
			TextWord textWord = new TextWord();
			textWord.index = i;
			textWord.raw = eliminatedWords[i];
			textWord.processed = new EnglishStemmer(eliminatedWords[i]).stem();
			words.add(textWord);
		}
	}
	
	@Override
	public int hashCode() {
		int h = 0;
		for(TextWord sw : words)
			h += sw.hashCode();
		return h;
	}
	
	public ArrayList<TextWord> getExcluded(String[] exclusionWords) {
		ArrayList<TextWord> excluded = new ArrayList<>();
		
		textWordLoop: for(TextWord sw : this.words) {
			for(String exclusionWord : exclusionWords) {
				if(sw.processed.equals(exclusionWord)) {
					continue textWordLoop;
				}
			}
			excluded.add(sw);
		}
		
		return excluded;
	}
	
	public boolean contains(String stemmedWord) {
		TextWord sw = new TextWord();
		sw.processed = stemmedWord;
		return words.contains(sw);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("{");
		for(TextWord sw : words) {
			sb.append(sw);
		}
		sb.append("}");
		return sb.toString();
	}
	
}
