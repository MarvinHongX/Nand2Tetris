package assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * The Assembler class is the main entry point for the assembler program.
 * It coordinates the parsing, translation, and output of assembly code to machine code.
 */
public class Assembler {
    public static void main(String[] args) {
        System.out.println("Received argument[0]: " + args[0]);
        // Check if an argument is provided
        if (args.length != 1) {
            System.out.println("Usage: java Assembler <input_file.asm>");
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

            // Initialize code generator
            Code code = new Code();

            // Initialize symbol table
            SymbolTable symbolTable = new SymbolTable();

            // First pass: build the symbol table
            parser.firstPass(symbolTable);

            // Second pass: write the output file
            String outputFileName = inputFileName.replace(".asm", ".hack");

            // Initialize PrintWriter to write the output .hack file
            try (PrintWriter outputFile = new PrintWriter(new File(outputFileName))) {
                parser.secondPass(symbolTable, code, outputFile, inputFile);
                System.out.println("Assembling completed: " + outputFileName);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: File " + inputFileName + " not found.");
        }
    }
}