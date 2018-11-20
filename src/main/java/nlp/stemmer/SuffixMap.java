package nlp.stemmer;

import java.util.Comparator;
import java.util.TreeMap;

public class SuffixMap extends TreeMap<String, String> {

	private static final long serialVersionUID = 1_0_0L;

	private static final Comparator<String> COMPARATOR = new Comparator<String>() {
		public int compare(String str1, String str2) { // Descending length sorting comparator
			if(str1.length() > str2.length()) return -1;
			if(str1.length() < str2.length()) return 1;
			return str1.compareTo(str2); // len(str1) = len(str2)
		};
	};
	
	public SuffixMap(String lookupCSV) {
		super(COMPARATOR);
		
		for(String line : lookupCSV.split("\r?\n")) {
			if(line.isEmpty()) return;
			String[] cols = line.split("\\s*(,|=)\\s*", 2);
			put(cols[0], cols[1]);
		}
	}
	
}
