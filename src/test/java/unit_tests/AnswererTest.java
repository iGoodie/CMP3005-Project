package unit_tests;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.programmer.igoodie.utils.io.CommandLineArgs;
import com.programmer.igoodie.utils.io.FileUtils;

import answerer.QuestionAnswerer;

public class AnswererTest {

	@Test
	public void programShouldAnswerCorrectly() {
		String testingArgs = "-scriptpath:.\\samples\\script_in.txt " + "-questionspath:.\\samples\\questions_in.txt";

		CommandLineArgs cla = new CommandLineArgs(testingArgs.split("\\s+"));

		// Read script from given path
		String script = FileUtils.readString(cla.getArgument("scriptpath"));
		String[] questions = FileUtils.readString(cla.getArgument("questionspath")).split("\r?\n");

		// Read comparing answers
		String[] expectedAnswers = FileUtils
				.readString("E:\\eclipse\\workspace\\CMP3005-Project\\samples\\answers_actual_out.txt").split("\r?\n");

		// Build answerer and get answers
		QuestionAnswerer answerer = new QuestionAnswerer(script);
		String[] answers = answerer.answerAll(questions);

		// Actual assertions
		System.out.println("Expected answers: " + Arrays.toString(expectedAnswers));
		System.out.println("Generated answers: " + Arrays.toString(answers));
		Assert.assertArrayEquals(expectedAnswers, answers);
	}

}
