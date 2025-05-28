package vmtranslator;

import java.io.*;
import java.util.*;

/**
 * The VMTranslator class is the main driver for the VM to Hack assembly translator.
 * It processes a single .vm file or all .vm files in a directory.
 *
 * Responsibilities:
 * - Determine the input (file or directory).
 * - Parse each VM command using Parser.
 * - Translate commands into Hack assembly using CodeWriter.
 * - Write the output .asm file.
 *
 * This follows the structure of Project 8 from the Nand2Tetris course.
 */
public class VMTranslator {
    public static void main(String[] args) {
        // Check for a single command-line argument
        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator <inputFile.vm | directory>");
            return;
        }

        File input = new File(args[0]);
        if (!input.exists()) {
            System.out.println("Error: File or directory does not exist.");
            return;
        }

        List<File> vmFiles = new ArrayList<>();
        String outputFileName;

        // If input is a directory, collect all .vm files inside it
        if (input.isDirectory()) {
            File[] files = input.listFiles((dir, name) -> name.endsWith(".vm"));
            if (files != null) vmFiles.addAll(Arrays.asList(files));

            // Output file will have the same name as the directory
            outputFileName = input.getAbsolutePath() + "/" + input.getName() + ".asm";
        } else {
            // Input is a single .vm file
            if (!input.getName().endsWith(".vm")) {
                System.out.println("Error: Not a .vm file.");
                return;
            }

            vmFiles.add(input);

            // Output file will have the same base name but with .asm extension
            outputFileName = input.getAbsolutePath().replace(".vm", ".asm");
        }

        // Decide whether bootstrap code (writeInit) should be emitted
        boolean containsSysVm = vmFiles.stream()
                .anyMatch(file -> file.getName().equalsIgnoreCase("Sys.vm"));

        // Initialize the output writer and CodeWriter
        try (PrintWriter writer = new PrintWriter(outputFileName)) {
            CodeWriter codeWriter = new CodeWriter(writer);

            // Only write bootstrap code if Sys.vm is present
            if (containsSysVm) {
                codeWriter.writeInit();
            }

            // Process each .vm file
            for (File vmFile : vmFiles) {
                Parser parser = new Parser(vmFile);

                // Inform CodeWriter of the current VM file for static label generation
                codeWriter.setFileName(vmFile.getName());

                // Process all commands in the current file
                while (parser.hasMoreCommands()) {
                    parser.advance();
                    CommandType type = parser.commandType();

                    // Dispatch to appropriate CodeWriter method based on command type
                    switch (type) {
                        case C_ARITHMETIC -> codeWriter.writeArithmetic(parser.arg1());
                        case C_PUSH, C_POP -> codeWriter.writePushPop(type, parser.arg1(), parser.arg2());
                        case C_LABEL -> codeWriter.writeLabel(parser.arg1());
                        case C_GOTO -> codeWriter.writeGoto(parser.arg1());
                        case C_IF -> codeWriter.writeIf(parser.arg1());
                        case C_FUNCTION -> codeWriter.writeFunction(parser.arg1(), parser.arg2());
                        case C_CALL -> codeWriter.writeCall(parser.arg1(), parser.arg2());
                        case C_RETURN -> codeWriter.writeReturn();
                    }
                }
            }

            // Finalize and close output
            codeWriter.close();
            System.out.println("Translation completed: " + outputFileName);

        } catch (FileNotFoundException e) {
            System.err.println("Error creating output file: " + e.getMessage());
        }
    }
}
