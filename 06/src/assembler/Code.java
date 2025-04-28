package assembler;

/**
 * The Code class contains methods to translate assembly code to binary machine code.
 */
public class Code {

    /**
     * Converts the A-instruction (symbol or value) to its 16-bit binary representation.
     */
    public String toBinaryA(int address) {
        String binary = "0" + String.format("%15s", Integer.toBinaryString(address)).replace(' ', '0');
//        System.out.println("address, binary = " + address + "," + binary);
        System.out.println(binary); // Output the binary instruction
        return binary;
    }

    /**
     * Converts the C-instruction (destination, computation, and jump) to its 16-bit binary representation.
     */
    public String toBinaryC(String dest, String comp, String jump) {
        String binaryDest = destToBinary(dest);
        String binaryComp = compToBinary(comp);
        String binaryJump = jumpToBinary(jump);

        String binary = "111" + binaryComp + binaryDest + binaryJump;
//        System.out.println("dest, binaryDest = " + dest + "," + binaryDest);
//        System.out.println("comp, binaryComp = " + comp + "," + binaryComp);
//        System.out.println("jump, binaryJump = " + jump + "," + binaryJump);
        System.out.println(binary); // Output the binary instruction
        return binary;
    }

    private String destToBinary(String dest) {
        if (dest == null || dest.isEmpty()) return "000";

        return switch (dest) {
            case "M" -> "001";
            case "D" -> "010";
            case "MD" -> "011";
            case "A" -> "100";
            case "AM" -> "101";
            case "AD" -> "110";
            case "AMD" -> "111";
            default -> "000";
        };
    }

    private String compToBinary(String comp) {
        return switch (comp) {
            case "0" -> "0101010";
            case "1" -> "0111111";
            case "-1" -> "0111010";
            case "D" -> "0001100";
            case "A" -> "0110000";
            case "!D" -> "0001101";
            case "!A" -> "0110001";
            case "-D" -> "0001111";
            case "-A" -> "0110011";
            case "D+1" -> "0011111";
            case "A+1" -> "0110111";
            case "D-1" -> "0001110";
            case "A-1" -> "0110010";
            case "D+A" -> "0000010";
            case "D-A" -> "0010011";
            case "A-D" -> "0000111";
            case "D&A" -> "0000000";
            case "D|A" -> "0010101";
            case "M" -> "1110000";
            case "!M" -> "1110001";
            case "-M" -> "1110011";
            case "M+1" -> "1110111";
            case "M-1" -> "1110010";
            case "D+M" -> "1000010";
            case "D-M" -> "1010011";
            case "M-D" -> "1000111";
            case "D&M" -> "1000000";
            case "D|M" -> "1010101";
            default -> "0000000"; // Should not happen
        };
    }

    private String jumpToBinary(String jump) {
        if (jump == null || jump.isEmpty()) return "000";

        return switch (jump) {
            case "JGT" -> "001";
            case "JEQ" -> "010";
            case "JGE" -> "011";
            case "JLT" -> "100";
            case "JNE" -> "101";
            case "JLE" -> "110";
            case "JMP" -> "111";
            default -> "000";
        };
    }
}
