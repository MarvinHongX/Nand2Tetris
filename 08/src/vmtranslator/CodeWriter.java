package vmtranslator;

import java.io.PrintWriter;
import java.util.Map;
import java.util.function.Consumer;



/**
 * The CodeWriter class translates parsed VM commands into Hack assembly code.
 */
public class CodeWriter {
    private final PrintWriter writer;
    private String fileName;
    private int labelCounter = 0;

    private final Map<String, Consumer<String>> opHandlers = Map.of(
        "add", op -> writeBinary("D+M"),
        "sub", op -> writeBinary("M-D"),
        "and", op -> writeBinary("D&M"),
        "or",  op -> writeBinary("D|M"),
        "neg", op -> writeUnary("-M"),
        "not", op -> writeUnary("!M"),
        "eq",  op -> writeCompare("JEQ"),
        "gt",  op -> writeCompare("JGT"),
        "lt",  op -> writeCompare("JLT")
    );

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

    public void writeInit() {
        writer.println("// Bootstrap code");
        writer.println("@256");
        writer.println("D=A");
        writer.println("@SP");
        writer.println("M=D");
        writeCall("Sys.init", 0);
    }

    /**
     * Writes Hack assembly code for the given arithmetic command.
     */
    public void writeArithmetic(String command) {
        writer.println("// " + command);
        Consumer<String> handler = opHandlers.get(command);
        if (handler != null) {
            handler.accept(command);
        } else {
            throw new IllegalArgumentException("Unknown arithmetic command: " + command);
        }
    }

    /**
     * Writes Hack assembly code for push and pop commands.
     */
    public void writePushPop(CommandType type, String segmentStr, int index) {
        SegmentType segment = SegmentType.from(segmentStr);
        writer.printf("// %s %s %d\n", type == CommandType.C_PUSH ? "push" : "pop", segmentStr, index);

        switch (type) {
            case C_PUSH -> segment.writePush(writer, index, fileName);
            case C_POP -> segment.writePop(writer, index, fileName);
            default -> throw new IllegalArgumentException("Invalid command type: " + type);
        }
    }

    public void writeLabel(String label) {
        writer.printf("// label %s\n(%s)\n", label, label);
    }

    public void writeGoto(String label) {
        writer.printf("// goto %s\n@%s\n0;JMP\n", label, label);
    }

    public void writeIf(String label) {
        writer.printf("// if-goto %s\n", label);
        writePopToD();
        writer.printf("@%s\nD;JNE\n", label);
    }

    public void writeFunction(String functionName, int numLocals) {
        writer.printf("// function %s %d\n(%s)\n", functionName, numLocals, functionName);
        for (int i = 0; i < numLocals; i++) {
            writer.println("@0\nD=A");
            pushDToStack();
        }
    }

    public void writeCall(String functionName, int numArgs) {
        String returnLabel = "RETURN_" + labelCounter++;
        writer.printf("// call %s %d\n", functionName, numArgs);

        pushLabel(returnLabel);
        pushSegment("LCL");
        pushSegment("ARG");
        pushSegment("THIS");
        pushSegment("THAT");

        writer.printf("@%d\nD=A\n@5\nD=D+A\n@SP\nD=M-D\n@ARG\nM=D\n", numArgs);
        writer.println("@SP\nD=M\n@LCL\nM=D");
        writer.printf("@%s\n0;JMP\n(%s)\n", functionName, returnLabel);
    }

    public void writeReturn() {
        writer.println("// return");
        writer.println("@LCL\nD=M\n@R13\nM=D");      // FRAME = LCL
        writer.println("@5\nA=D-A\nD=M\n@R14\nM=D"); // RET = *(FRAME-5)
        writer.println("@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D"); // *ARG = pop()
        writer.println("@ARG\nD=M+1\n@SP\nM=D");     // SP = ARG+1
        writer.println("@R13\nAM=M-1\nD=M\n@THAT\nM=D");
        writer.println("@R13\nAM=M-1\nD=M\n@THIS\nM=D");
        writer.println("@R13\nAM=M-1\nD=M\n@ARG\nM=D");
        writer.println("@R13\nAM=M-1\nD=M\n@LCL\nM=D");
        writer.println("@R14\nA=M\n0;JMP");          // goto RET
    }

    /**
     * Flushes the output and closes the writer.
     */
    public void close() {
        writer.flush();
    }

    // --- Private Helper Methods ---

    private void writeBinary(String operation) {
        writer.println("@SP\nAM=M-1\nD=M\nA=A-1");
        writer.printf("M=%s\n", operation);
    }

    private void writeUnary(String operation) {
        writer.println("@SP\nA=M-1");
        writer.printf("M=%s\n", operation);
    }

    private void writeCompare(String jump) {
        String trueLabel = "TRUE_" + labelCounter;
        String endLabel = "END_" + labelCounter++;
        writer.println("@SP\nAM=M-1\nD=M\nA=A-1\nD=M-D");
        writer.printf("@%s\nD;%s\n", trueLabel, jump);
        writer.println("@SP\nA=M-1\nM=0");
        writer.printf("@%s\n0;JMP\n(%s)\n@SP\nA=M-1\nM=-1\n(%s)\n",
                endLabel, trueLabel, endLabel);
    }

    private void pushDToStack() {
        writer.println("@SP\nA=M\nM=D\n@SP\nM=M+1");
    }

    private void writePopToD() {
        writer.println("@SP\nAM=M-1\nD=M");
    }

    private void pushLabel(String label) {
        writer.printf("@%s\nD=A\n", label);
        pushDToStack();
    }

    private void pushSegment(String segment) {
        writer.printf("@%s\nD=M\n", segment);
        pushDToStack();
    }
}