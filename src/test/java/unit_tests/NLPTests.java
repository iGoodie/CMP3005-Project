package unit_tests;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import com.programmer.igoodie.utils.benchmark.Performance;
import com.programmer.igoodie.utils.math.Randomizer;

import answerer.ScriptLine;
import nlp.StopWords;
import nlp.stemmer.EnglishStemmer;
import util.StringSearcher;

/**
 * JUnit tests to test out Natural Language Processing (NPL)
 * functionalities included in "nlp" package.
 */
public class NLPTests {
	
	private void delimiterConsole() {
		System.out.println("\n=================================\n");
	}
	
	@Test
	public void shouldEliminateCorrectly() {
		HashMap<String, String> scriptMap = new HashMap<>();
		scriptMap.put("We have the most important feature",
				"we important feature");
		scriptMap.put("I'm sorry, but I don't want to be an emperor",
				"i'm sorry don't want emperor");
		scriptMap.put("Greed has poisoned men's souls, has barricaded the world with hate, has goose-stepped us into misery and bloodshed.", 
				"greed poisoned men's souls barricaded world hate goose-stepped misery bloodshed");
		scriptMap.put("Tell me a sentence", 
				"tell sentence");
		
		// Traverse map, test and assert true values
		for(String script : scriptMap.keySet()) {
			System.out.println("Testing for script: {" + script + "}");
			String eliminated = StopWords.eliminateWords(script);
			System.out.println("Eliminated to: {" + eliminated + "}");
			Assert.assertTrue("Invalid elimination" + script,
					eliminated.equals(scriptMap.get(script)));
			System.out.println();
		}
		
		// Test eliminating from random scripts N times, and get average
		int trials = 100_000;
		Object[] scripts = scriptMap.keySet().toArray();
		long t = Performance.testTimeAvg(() -> {
			String randomScript = (String) scripts[Randomizer.randomInt(0, scripts.length-1)];
			StopWords.eliminateWords(randomScript); // Execute only, no allocation
		}, trials);
		
		// Assert it is less than L seconds
		Assert.assertTrue("Average is more than 1 sec.", t < 1_000);
		
		System.out.println("Testing is done in " + t + " millisecond(s).");
		
		delimiterConsole();
	}

	@Test
	public void shouldStemCorrectly() {
		EnglishStemmer stemmer = new EnglishStemmer("someword");
		HashMap<String, String> stemMap = new HashMap<>();
		stemMap.put("consign", "consign");
		stemMap.put("consigned", "consign");
		stemMap.put("consigning", "consign");
		stemMap.put("consignment", "consign");
		stemMap.put("consist", "consist");
		stemMap.put("consisted", "consist");
		stemMap.put("consistency", "consist");
		stemMap.put("consistent", "consist");
		stemMap.put("consistently", "consist");
		stemMap.put("consisting", "consist");
		stemMap.put("consists", "consist");
		stemMap.put("consolation", "consol");
		stemMap.put("constancy", "constanc");
		stemMap.put("knots", "knot");
		
		for(String word : stemMap.keySet()) {
			System.out.println("Testing the word: " + word);
			stemmer.setWord(word);
			Assert.assertEquals(stemMap.get(word), stemmer.stem());
		}
		
		delimiterConsole();
	}

	@Test
	public void shouldMatchString() {
		String source = "Some testing foo";
		String pattern = " fo";
		
		Assert.assertTrue(StringSearcher.searchIndexNaive(source, pattern) != -1);
		Assert.assertTrue(StringSearcher.searchIndexRabinKarp(source, pattern) != -1);
	}

	@Test
	public void shouldParseScriptLine() {
		String line = "Some foo text right in place";
		ScriptLine scriptLine = new ScriptLine(line);
		
		System.out.println(scriptLine);
		System.out.println("foao".hashCode());
		System.out.println(scriptLine.contains("foo"));
	}
	
}
