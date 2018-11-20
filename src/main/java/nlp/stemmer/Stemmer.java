package nlp.stemmer;

public abstract class Stemmer {

	protected String word;
	
	public Stemmer(String word) {
		this.word = word.toLowerCase();
	}
	
	public void setWord(String word) {
		this.word = word.toLowerCase();
	}
	
	public abstract String stem();
	
}