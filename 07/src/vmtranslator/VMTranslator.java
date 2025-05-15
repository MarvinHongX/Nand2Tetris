package vmtranslator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * The VMTranslator class is the main entry point for the VM translator program.
 * It coordinates parsing VM code and translating it to Hack assembly code.
 */
public class VMTranslator {
    public static void main(String[] args) {
        System.out.println("Received argument[0]: " + args[0]);

        // Validate input
        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator <input_file.vm>");
            return;
        }

        // Ensure the input path is an absolute path
        String inputFileName = args[0];
        File inputFile = new File(inputFileName);

        // Debug output to confirm the input file path
        System.out.println("Received argument[0]: " + inputFile.getAbsolutePath());

        try {
            // Initialize parser
            Parser parser = new Parser(inputFile);

            // Initialize code writer (to be completed after determining output file name)
            CodeWriter codeWriter;

            // Determine output file name inside the try block
            String outputFileName = inputFileName.replace(".vm", ".asm");

            // Create writer for the output file
            try (PrintWriter outputFile = new PrintWriter(outputFileName)) {
                // Initialize CodeWriter with the output file
                codeWriter = new CodeWriter(outputFile);

                // Set the filename (used for static variable naming)
                codeWriter.setFileName(inputFile.getName());

                // Process VM commands one by one
                while (parser.hasMoreCommands()) {
                    parser.advance();
                    CommandType commandType = parser.commandType();

                    switch (commandType) {
                        case C_ARITHMETIC -> codeWriter.writeArithmetic(parser.arg1());
                        case C_PUSH, C_POP -> codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                        default -> {
                            // Skipping unimplemented command types for now
                        }
                    }
                }

                // Close the writer (flushes content)
                codeWriter.close();

                // Notify success
                System.out.println("Translation completed: " + outputFileName);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: File " + inputFileName + " not found.");
        }

    }
}
