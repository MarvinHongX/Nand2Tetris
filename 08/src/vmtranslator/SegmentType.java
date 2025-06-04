package vmtranslator;

import java.io.PrintWriter;

public enum SegmentType {
    CONSTANT(null) {
        @Override
        public void writePush(PrintWriter writer, int index, String fileName) {
            writeImmediate(writer, index);
            pushDToStack(writer);
        }

        @Override
        public void writePop(PrintWriter writer, int index, String fileName) {
            throw new UnsupportedOperationException("Cannot pop to constant segment");
        }
    },

    LOCAL("LCL"),
    ARGUMENT("ARG"),
    THIS("THIS"),
    THAT("THAT") {
    },

    TEMP(null) {
        @Override
        public void writePush(PrintWriter writer, int index, String fileName) {
            writeFromFixedAddress(writer, 5 + index);
            pushDToStack(writer);
        }

        @Override
        public void writePop(PrintWriter writer, int index, String fileName) {
            writeAddressToR13(writer, 5 + index);
            popStackToR13Addr(writer);
        }
    },

    POINTER(null) {
        @Override
        public void writePush(PrintWriter writer, int index, String fileName) {
            writer.printf("@%s\nD=M\n", getPointerSymbol(index));
            pushDToStack(writer);
        }

        @Override
        public void writePop(PrintWriter writer, int index, String fileName) {
            writer.printf("@%s\nD=A\n@R13\nM=D\n", getPointerSymbol(index));
            popStackToR13Addr(writer);
        }
    },

    STATIC(null) {
        @Override
        public void writePush(PrintWriter writer, int index, String fileName) {
            writer.printf("@%s.%d\nD=M\n", fileName, index);
            pushDToStack(writer);
        }

        @Override
        public void writePop(PrintWriter writer, int index, String fileName) {
            writer.printf("@%s.%d\nD=A\n@R13\nM=D\n", fileName, index);
            popStackToR13Addr(writer);
        }
    };

    private final String base;

    SegmentType(String base) {
        this.base = base;
    }

    public void writePush(PrintWriter writer, int index, String fileName) {
        if (base == null) throw new UnsupportedOperationException("writePush not supported");
        pushFromSegmentBase(writer, base, index);
        pushDToStack(writer);
    }

    public void writePop(PrintWriter writer, int index, String fileName) {
        if (base == null) throw new UnsupportedOperationException("writePop not supported");
        popToSegmentBase(writer, base, index);
        popStackToR13Addr(writer);
    }

    public static SegmentType from(String segmentStr) {
        return switch (segmentStr.toLowerCase()) {
            case "constant" -> CONSTANT;
            case "local" -> LOCAL;
            case "argument" -> ARGUMENT;
            case "this" -> THIS;
            case "that" -> THAT;
            case "temp" -> TEMP;
            case "pointer" -> POINTER;
            case "static" -> STATIC;
            default -> throw new IllegalArgumentException("Unknown segment: " + segmentStr);
        };
    }

    protected static void pushFromSegmentBase(PrintWriter writer, String base, int offset) {
        writer.printf("@%s\nD=M\n@%d\nA=D+A\nD=M\n", base, offset);
    }

    protected static void popToSegmentBase(PrintWriter writer, String base, int offset) {
        writer.printf("@%s\nD=M\n@%d\nD=D+A\n@R13\nM=D\n", base, offset);
    }

    protected static void writeFromFixedAddress(PrintWriter writer, int address) {
        writer.printf("@%d\nD=M\n", address);
    }

    protected static void writeAddressToR13(PrintWriter writer, int address) {
        writer.printf("@%d\nD=A\n@R13\nM=D\n", address);
    }

    protected static void writeImmediate(PrintWriter writer, int value) {
        writer.printf("@%d\nD=A\n", value);
    }

    private static String getPointerSymbol(int index) {
        return switch (index) {
            case 0 -> "THIS";
            case 1 -> "THAT";
            default -> throw new IllegalArgumentException("Pointer index must be 0 or 1: got " + index);
        };
    }

    private static void pushDToStack(PrintWriter writer) {
        writer.println("@SP");
        writer.println("A=M");
        writer.println("M=D");
        writer.println("@SP");
        writer.println("M=M+1");
    }

    private static void popStackToR13Addr(PrintWriter writer) {
        writer.println("@SP");
        writer.println("AM=M-1");
        writer.println("D=M");
        writer.println("@R13");
        writer.println("A=M");
        writer.println("M=D");
    }
}
