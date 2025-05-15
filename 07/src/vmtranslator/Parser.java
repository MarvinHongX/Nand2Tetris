package vmtranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The Parser class reads and parses each VM command.
 * It provides access to command components and the command type.
 */
public class Parser {
    private Scanner scanner;
    private String currentLine;

    /**
     * Constructor to initialize the parser with the input file.
     */
    public Parser(File inputFile) throws FileNotFoundException {
        this.scanner = new Scanner(inputFile);
    }

    /**
     * Checks if there are more commands in the input.
     */
    public boolean hasMoreCommands() {
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (!line.isEmpty() && !line.startsWith("//")) {
                currentLine = line.split("//")[0].trim(); // Remove inline comment
                return true;
            }
        }
        return false;
    }

    /**
     * Advances the parser to the next command.
     */
    public void advance() {
        // currentLine already updated in hasMoreCommands()
    }

    /**
     * Returns the type of the current VM command.
     */
    public CommandType commandType() {
        String[] tokens = currentLine.split("\\s+");
        return switch (tokens[0]) {
            case "push" -> CommandType.C_PUSH;
            case "pop" -> CommandType.C_POP;
            case "label" -> CommandType.C_LABEL;
            case "goto" -> CommandType.C_GOTO;
            case "if-goto" -> CommandType.C_IF;
            case "function" -> CommandType.C_FUNCTION;
            case "call" -> CommandType.C_CALL;
            case "return" -> CommandType.C_RETURN;
            default -> CommandType.C_ARITHMETIC;
        };
    }

    /**
     * Returns the first argument of the current command.
     */
    public String arg1() {
        if (commandType() == CommandType.C_ARITHMETIC) {
            return currentLine;
        }
        return currentLine.split("\\s+")[1];
    }

    /**
     * Returns the second argument of the current command (only for push/pop/function/call).
     */
    public int arg2() {
        return Integer.parseInt(currentLine.split("\\s+")[2]);
    }
}
