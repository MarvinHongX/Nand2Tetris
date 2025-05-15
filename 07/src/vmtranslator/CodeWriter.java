package vmtranslator;

import java.io.PrintWriter;

/**
 * The CodeWriter class translates parsed VM commands into Hack assembly code.
 * It provides methods to write assembly instructions for each command type.
 */
public class CodeWriter {
    private final PrintWriter writer;
    private String fileName;
    private int labelCounter = 0;

    /**
     * Constructs a CodeWriter that writes to the given output file.
     */
    public CodeWriter(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Sets the current VM file name (used for static variables).
     */
    public void setFileName(String fileName) {
        this.fileName = fileName.replace(".vm", "");
    }

    /**
     * Writes Hack assembly code for the given arithmetic command.
     */
    public void writeArithmetic(String command) {
        writer.println("// " + command);

        switch (command) {
            case "add" -> writeBinary("M+D");
            case "sub" -> writeBinary("M-D");
            case "and" -> writeBinary("M&D");
            case "or"  -> writeBinary("M|D");
            case "neg" -> writeUnary("-M");
            case "not" -> writeUnary("!M");
            case "eq"  -> writeCompare("JEQ");
            case "gt"  -> writeCompare("JGT");
            case "lt"  -> writeCompare("JLT");
        }
    }

    /**
     * Writes Hack assembly code for push and pop commands.
     */
    public void writePushPop(CommandType type, String segment, int index) {
        writer.printf("// %s %s %d\n", type == CommandType.C_PUSH ? "push" : "pop", segment, index);

        if (type == CommandType.C_PUSH) {
            switch (segment) {
                case "constant" -> writer.printf("@%d\nD=A\n", index);
                case "local" -> writer.printf("@LCL\nD=M\n@%d\nA=D+A\nD=M\n", index);
                case "argument" -> writer.printf("@ARG\nD=M\n@%d\nA=D+A\nD=M\n", index);
                case "this" -> writer.printf("@THIS\nD=M\n@%d\nA=D+A\nD=M\n", index);
                case "that" -> writer.printf("@THAT\nD=M\n@%d\nA=D+A\nD=M\n", index);
                case "temp" -> writer.printf("@%d\nD=M\n", 5 + index);
                case "pointer" -> writer.printf("@%s\nD=M\n", index == 0 ? "THIS" : "THAT");
                case "static" -> writer.printf("@%s.%d\nD=M\n", fileName, index);
            }
            writer.println("@SP\nA=M\nM=D\n@SP\nM=M+1");

        } else if (type == CommandType.C_POP) {
            switch (segment) {
                case "local" -> writer.printf("@LCL\nD=M\n@%d\nD=D+A\n@R13\nM=D\n", index);
                case "argument" -> writer.printf("@ARG\nD=M\n@%d\nD=D+A\n@R13\nM=D\n", index);
                case "this" -> writer.printf("@THIS\nD=M\n@%d\nD=D+A\n@R13\nM=D\n", index);
                case "that" -> writer.printf("@THAT\nD=M\n@%d\nD=D+A\n@R13\nM=D\n", index);
                case "temp" -> writer.printf("@%d\nD=A\n@R13\nM=D\n", 5 + index);
                case "pointer" -> writer.printf("@%s\nD=A\n@R13\nM=D\n", index == 0 ? "THIS" : "THAT");
                case "static" -> writer.printf("@%s.%d\nD=A\n@R13\nM=D\n", fileName, index);
            }
            writer.println("@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D");
        }
    }

    /**
     * Flushes the output and closes the writer.
     */
    public void close() {
        writer.flush();
    }

    // --- Private Helper Methods ---

    private void writeBinary(String operation) {
        writer.println("@SP");
        writer.println("AM=M-1");   // y: *--SP
        writer.println("D=M");      // D = y
        writer.println("A=A-1");    // x: *(SP-1)
        switch (operation) {
            case "M+D" -> writer.println("M=D+M");
            case "M-D" -> writer.println("M=M-D");
            case "M&D" -> writer.println("M=D&M");
            case "M|D" -> writer.println("M=D|M");
            default -> throw new IllegalArgumentException("Unknown binary operation: " + operation);
        }
    }

    private void writeUnary(String operation) {
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=" + operation);
    }

    private void writeCompare(String jump) {
        String trueLabel = "TRUE_" + labelCounter;
        String endLabel = "END_" + labelCounter;
        labelCounter++;

        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
        writer.println("A=A-1");
        writer.println("D=M-D");
        writer.println("@" + trueLabel);
        writer.println("D;" + jump);
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=0");
        writer.println("@" + endLabel);
        writer.println("0;JMP");
        writer.println("(" + trueLabel + ")");
        writer.println("@SP");
        writer.println("A=M-1");
        writer.println("M=-1");
        writer.println("(" + endLabel + ")");
    }
}