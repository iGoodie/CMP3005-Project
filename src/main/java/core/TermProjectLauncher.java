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

		// TODO: Use caching with MD5 checksum

		// Read questions from given path
		String[] questions = FileUtils.readString(cla.getArgument("questionspath")).split("\r?\n");
		System.out.println("Read done questions from " + cla.getArgument("questionspath"));

		// Build an answerer with read script
		QuestionAnswerer answerer = new QuestionAnswerer(script);
		System.out.println("Answerer built with read script.");
		
		// Answer the questions via the answerer, then save them all
		String[] answers = answerer.answerAll(questions);
		saveAnswers(answers, cla.getArgument("answerspath"), Performance.getOS().equals("windows"));
		System.out.println("Answers are saved to " + cla.getArgument("answerspath"));
	}

	private static void saveAnswers(String[] answers, String path, boolean includeLineFeed) {
		String saveData = String.join(includeLineFeed ? "\r\n" : "\n", answers);
		FileUtils.writeString(saveData, path);
	}

}
