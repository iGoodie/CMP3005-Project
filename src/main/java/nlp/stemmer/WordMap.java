package nlp.stemmer;

import java.util.HashMap;

public class WordMap extends HashMap<String, String> {

	private static final long serialVersionUID = 1_0_0L;

	public WordMap(String csvData) {
		for(String line : csvData.split("\r?\n")) {
			if(line.isEmpty()) return;
			String[] cols = line.split("\\s*(,|=)\\s*", 2);
			put(cols[0], cols[1]);
		}
	}
	
}