# Nand2Tetris Part 7 - VM Translator

This project is an implementation of the VM Translator for Part 7 of the [Nand2Tetris](https://www.nand2tetris.org/) course, written in Java.

The VM Translator takes `.vm` files as input and produces corresponding `.asm` files as output, written in Hack assembly language.

## Project Structure

```
07/
├── src/
│   └── vmtranslator/
│       ├── VMTranslator.java   // Main class
│       ├── Parser.java         // Parses VM commands
│       ├── CodeWriter.java     // Translates VM commands to Hack assembly
│       └── CommandType.java    // Enum for different VM command types
├── vm/                         // Test .vm files
├── out/                        // Output .asm files
└── README.md
```

## Build and Run Instructions

### 1. Compile

From the project root directory, run:

```bash
javac -d out src/vmtranslator/*.java
```

The compiled `.class` files will be placed under the `out/vmtranslator/` directory.

### 2. Execute

```bash
java -cp out vmtranslator.VMTranslator [input_file.vm]
```

Example:

```bash
java -cp out vmtranslator.VMTranslator vm/BasicTest/BasicTest.vm
```

The output `.asm` file will be created in the same directory as the input file, with a `.asm` extension.

---

> 💡 Note: The VM commands in the input file will be preserved as comments (`// command`) in the output `.asm` file to enhance readability and traceability.
