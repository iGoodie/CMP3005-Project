package core;

import com.programmer.igoodie.utils.benchmark.Performance;
import com.programmer.igoodie.utils.io.CommandLineArgs;
import com.programmer.igoodie.utils.io.FileUtils;

import answerer.QuestionAnswerer;

public final class TermProjectLauncher {

	public static void main(String[] args) {
		// Parse given args into a better representation
		CommandLineArgs cla = new CommandLineArgs(args);

		// Check if mandatory arguments were given or not
		if (!properArgsPassed(cla)) {
			System.out.println("System is now terminating...");
			System.exit(-1);
		}

		// Read text from given path
		String text = FileUtils.readString(cla.getArgument("textpath"));
		System.out.println("Read done text from " + cla.getArgument("textpath"));

		// Read questions from given path
		String[] questions = FileUtils.readString(cla.getArgument("questionspath")).split("\r?\n");
		System.out.println("Read done questions from " + cla.getArgument("questionspath") + "\n");

		// Use caching with MD5 checksum before building answerer
		QuestionAnswerer answerer = QuestionAnswerer.fetch(text, FileUtils.getExternalDataPath() + "\\cache");

		// Answer the questions via the answerer, then save them all
		String[] answers = answerer.answerAll(questions);
		saveAnswers(answers, cla.getArgument("answerspath"), Performance.getOS().equals("windows"));
		System.out.println("Answers are saved to " + cla.getArgument("answerspath"));
	}

	private static boolean properArgsPassed(CommandLineArgs cla) {
		if (!cla.containsArgument("textpath")) { // No "textpath" passed.
			System.out.println("- Usage: java -jar Launcher -textpath:\"path/to/text.txt\" "
					+ "-questionspath:\"path/to/questions.txt\" -answerspath:\"path/to/answers.txt\"");
			System.out.println("\"textpath\" argument is missing.");
			return false;
		}
		if (!cla.containsArgument("questionspath")) { // No "questionspath" passed
			System.out.println("- Usage: java -jar Launcher -textpath:\"path/to/text.txt\" "
					+ "-questionspath:\"path/to/questions.txt\" -answerspath:\"path/to/answers.txt\"");
			System.out.println("\"questionspath\" argument is missing.");
			return false;
		}
		if (!cla.containsArgument("answerspath")) { // No "answerspath" passed
			System.out.println("- Usage: java -jar Launcher -textpath:\"path/to/text.txt\" "
					+ "-questionspath:\"path/to/questions.txt\" -answerspath:\"path/to/answers.txt\"");
			System.out.println("\"answerspath\" argument is missing.");
			return false;
		}
		if (!cla.getArgument("answerspath").endsWith(".txt")) { // Passed path doesn't contain valid extension
			System.out.println("\"answerpath\" should be a '.txt' file path");
			return false;
		}

		return true;
	}

	private static void saveAnswers(String[] answers, String path, boolean includeLineFeed) {
		String saveData = String.join(includeLineFeed ? "\r\n" : "\n", answers);
		FileUtils.writeString(saveData, path);
	}

}
