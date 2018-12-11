package unit_tests;

import org.junit.Assert;
import org.junit.Test;

import answerer.ScriptLine;
import util.StringSearcher;

public class HelperTests {
	
	@Test
	public void shouldMatchString() {
		String source = "Some testing foo";
		String pattern = " fo";
		
		Assert.assertTrue(StringSearcher.searchIndexNaive(source, pattern) != -1);
		Assert.assertTrue(StringSearcher.searchIndexRabinKarp(source, pattern) != -1);
		
		System.out.println(StringSearcher.searchIndexNaive("how many foo", "how many"));
	}
	
	
}
