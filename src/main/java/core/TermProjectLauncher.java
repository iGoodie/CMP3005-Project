package core;

import nlp.StopWords;

public class TermProjectLauncher {

	public static void main(String[] args) {
		String e = StopWords.eliminateSentence("We have the most important feature");
		System.out.println(e);
		
		// Read script
		// Eliminate stop words
		// Stem script
		// Split sentences
		
		// For each question;
		// Eliminate stop words
		// Stem words
		// Set relevant results = script
		// - For each word in question;
		// - Query relevant results
		// - Set relevant results = query
		
		// If relevant results.len == 1 assume it has an anwer
	}
	
}
