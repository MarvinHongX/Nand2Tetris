# Nand2Tetris Part 8 - VM Translator 2

This project implements the VM Translator for Part 8 of the [Nand2Tetris](https://www.nand2tetris.org/) course.  
It translates `.vm` files—written in the virtual machine language—into Hack assembly `.asm` files.  
This version supports advanced features such as function calls, returns, and program initialization.

## Project Structure

```
08/
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
java -cp out vmtranslator.VMTranslator [input_file.vm | directory]
```

Example:

```bash
java -cp out vmtranslator.VMTranslator vm/BasicLoop
```

The generated `.asm` file will be saved alongside the input `.vm` file.

## Features

- Supports all arithmetic and logical commands
- Handles memory access commands (`push`/`pop`) for all segments
- Implements function calling and returning
- Includes program initialization (bootstrap code)

## Notes

- The output `.asm` file contains comments with the original VM commands to improve readability and debugging.
