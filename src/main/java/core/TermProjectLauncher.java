package core;

import com.programmer.igoodie.utils.benchmark.Performance;
import com.programmer.igoodie.utils.io.CommandLineArgs;
import com.programmer.igoodie.utils.io.FileUtils;

public class TermProjectLauncher {
	
	public static void main(String[] args) {
		CommandLineArgs cla = new CommandLineArgs(args); // Parse given args into a better representation
		
		// Check if mandatory arguments were given or not
		if(!cla.containsArgument("scriptpath")) {
			System.out.println("- Usage: java -jar Launcher -scriptpath:\"path/to/script.txt\" -questionspath:\"path/to/questions.txt\" -answerspath:\"path/to/answers.txt\"");
			System.out.println("\"scriptpath\" argument is missing.");
			System.exit(-1);
		} else if(!cla.containsArgument("questionspath")) {
			System.out.println("- Usage: java -jar Launcher -scriptpath:\"path/to/script.txt\" -questionspath:\"path/to/questions.txt\" -answerspath:\"path/to/answers.txt\"");
			System.out.println("\"questionspath\" argument is missing.");
			System.exit(-1);
		} else if(!cla.containsArgument("answerspath")) {
			System.out.println("- Usage: java -jar Launcher -scriptpath:\"path/to/script.txt\" -questionspath:\"path/to/questions.txt\" -answerspath:\"path/to/answers.txt\"");
			System.out.println("\"answerspath\" argument is missing.");
			System.exit(-1);
		} else if(!cla.getArgument("answerspath").endsWith(".txt")) {
			System.out.println("\"answerpath\" should be a '.txt' file path");
			System.exit(-1);
		}
		
		// Read script from given path
		String script = FileUtils.readString(cla.getArgument("scriptpath"));
		System.out.println("Read done script from " + cla.getArgument("scriptpath"));
		
		// Read questions from given path
		String[] questions = FileUtils.readString(cla.getArgument("questionspath")).split("\r?\n");
		System.out.println("Read done questions from " + cla.getArgument("questionspath"));
		
		System.exit(0); // Here for debug purposes :V
		
		// Build answerer and let it attempt to answer the questions, then save it to given path
		QuestionAnswerer answerer = new QuestionAnswerer(script);
		String[] answers = answerer.answer(questions);
		saveAnswers(answers, cla.getArgument("answerspath"), Performance.getOS().equals("windows"));
		
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
	
	private static void saveAnswers(String[] answers, String path, boolean includeLineFeed) {
		String saveData = String.join(includeLineFeed ? "\r\n" : "\n", answers);
		FileUtils.writeString(saveData, path);
	}
	
}
