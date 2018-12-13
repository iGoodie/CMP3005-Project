package unit_tests;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import util.StringSearcher;
import util.ChecksumUtils;

public class HelperTests {

	@Test
	public void RabinKarpImplShouldMatchString() {
		String source = "Some testing foo";
		String pattern = " fo";

		Assert.assertTrue(StringSearcher.searchIndexNaive(source, pattern) != -1);
		Assert.assertTrue(StringSearcher.searchIndexRabinKarp(source, pattern) != -1);
	}

	@Test
	public void checksumShouldHashCorrectly() {
		HashMap<String, String> md5Solutons = new HashMap<>();
		md5Solutons.put("foo", "ACBD18DB4CC2F85CEDEF654FCCC4A4D8");
		md5Solutons.put("The quick brown fox jumps over the lazy dog", "9E107D9D372BB6826BD81D3542A419D6");
		md5Solutons.put("The way he saved that was epic", "95495B0433B3B1552117A8BAF58DFB33");

		for (String key : md5Solutons.keySet()) {
			Assert.assertEquals(md5Solutons.get(key), ChecksumUtils.hashMD5(key));
		}
	}

}
