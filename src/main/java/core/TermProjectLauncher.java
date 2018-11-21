package core;

import com.programmer.igoodie.utils.io.CommandLineArgs;
import com.programmer.igoodie.utils.io.FileUtils;

public class TermProjectLauncher {
	
	public static void main(String[] args) {
		CommandLineArgs cla = new CommandLineArgs(args);
		
		if(!cla.containsArgument("scriptpath")) {
			System.out.println("- Usage: java -jar Launcher -scriptpath:\"path/to/script.txt\"");
			System.out.println("\"scriptpath\" argument is missing.");
			System.exit(-1);
		}
		
		String script = FileUtils.readString(cla.getArgument("scriptpath"));
		
		QuestionAnswerer qa = new QuestionAnswerer(script);
		
		//QuestionAnswerer answerer = new QuestionAnswerer(script);
		
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
