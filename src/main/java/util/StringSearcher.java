package util;

public class StringSearcher {

	public static int searchIndexNaive(String source, String pattern) {
		int lenSource = source.length();
		int lenPattern = pattern.length();
		
		sourceLoop: for(int i=0; i<=lenSource-lenPattern; i++) {
			for(int j=0; j<lenPattern; j++) {
				if(source.charAt(i+j) != pattern.charAt(j))
					continue sourceLoop;
			}
			return i;
		}
		
		return -1;
	}
	
	public static int searchIndexRabinKarp(String source, String pattern) {
		int lenSource = source.length();
		int lenPattern = pattern.length();
		
		int hashPattern = hashString(pattern);
		for(int i=0; i<=lenSource-lenPattern; i++) {
			int subsHash = hashString(source.substring(i, i+lenPattern));
			if(subsHash == hashPattern)
				if(source.substring(i, i+lenPattern).equals(pattern))
					return i;
		}
		
		return -1;
	}
	
	public static boolean containsRabinKarp(String source, String pattern) {
		return searchIndexRabinKarp(source, pattern) != -1;
	}
	
	private static int hashString(String string) {
		int hash = 1097;
		for(int i=0; i<string.length(); i++)
			hash = hash * 1300963 + string.charAt(i);
		return hash;
	}
	
}
