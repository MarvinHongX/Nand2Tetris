package assembler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * The Parser class reads and parses the input assembly file.
 * It manages the two passes over the input: one for building the symbol table,
 * and another for generating the machine code.
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
     * First pass: Reads through the file and builds the symbol table.
     */
    public void firstPass(SymbolTable symbolTable) {
        int lineNumber = 0;
        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine().trim();

            // Ignore empty lines and comments
            if (currentLine.isEmpty() || currentLine.startsWith("//")) {
                continue;
            }

            // Handle label declarations (e.g. (LOOP))
            if (currentLine.startsWith("(")) {
                String label = currentLine.substring(1, currentLine.indexOf(')'));
                symbolTable.addEntry(label, lineNumber);
                continue;
            }

            System.out.println(lineNumber + ": " + currentLine);
            lineNumber++;
        }
    }

    /**
     * Second pass: Reads the file again and generates machine code.
     * The symbol table is used to replace labels with memory addresses.
     */
    public void secondPass(SymbolTable symbolTable, Code code, PrintWriter outputFile, File inputFile) throws FileNotFoundException {
        scanner = new Scanner(inputFile);
        int variableAddress = 16; // Start at memory address 16 for variables

        while (scanner.hasNextLine()) {
            currentLine = scanner.nextLine().trim();

            // Ignore empty lines and comments
            if (currentLine.isEmpty() || currentLine.startsWith("//")) {
                continue;
            }

            // Skip label lines (e.g. (ITSR0)) from being processed as A-instructions
            if (currentLine.startsWith("(")) {
                continue;  // Skip labels completely, they are processed in the first pass
            }

            // Process A-instructions (e.g. @value)
            if (currentLine.startsWith("@")) {
                String symbol = currentLine.substring(1);

                if (symbolTable.contains(symbol)) {
                    // If the symbol exists in the table, write the address
                    int address = symbolTable.getAddress(symbol);
                    outputFile.println(code.toBinaryA(address));  // Write the binary A instruction to file
                } else {
                    // Check if the symbol is a number
                    if (symbol.matches("\\d+")) {  // Matches only digits
                        int value = Integer.parseInt(symbol);  // Convert to integer directly
                        outputFile.println(code.toBinaryA(value));  // Write the binary A instruction to file
                    } else {
                        // If it's a variable that has not been seen before, assign a new address
                        // Only add to symbol table if it is not already there
                        if (!symbolTable.contains(symbol)) {
                            symbolTable.addEntry(symbol, variableAddress++);  // Assign new address for the variable
                        }
                        // Write the binary A instruction using the assigned address
                        outputFile.println(code.toBinaryA(symbolTable.getAddress(symbol)));
                    }
                }
            } else { // Process C-instructions (e.g. D=A)
                String dest = null;
                String comp = null;
                String jump = null;

                // Check if '=' exists (if not, there is no destination)
                if (currentLine.contains("=")) {
                    String[] parts = currentLine.split("=");
                    dest = parts[0];
                    String compJumpPart = parts[1];  // Get the comp and jump part after '='
                    comp = compJumpPart.split(";")[0];  // Get the comp part
                    jump = compJumpPart.contains(";") ? compJumpPart.split(";")[1] : null;  // If jump exists, get it
                } else {
                    // If '=' is missing, only comp and jump are present
                    comp = currentLine.split(";")[0];  // Just comp part
                    jump = currentLine.contains(";") ? currentLine.split(";")[1] : null;  // If jump exists, get it
                }

                outputFile.println(code.toBinaryC(dest, comp, jump));  // Write the binary C instruction to file
            }
        }

        // Print the final symbol table
//        symbolTable.printSymbolTable();

        scanner.close();
    }
}
