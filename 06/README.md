
# Nand2Tetris Part 6 - Assembler

This project is an implementation of the Assembler for Part 6 of the [Nand2Tetris](https://www.nand2tetris.org/) course, written in Java.

The Assembler takes `.asm` files as input and produces corresponding `.hack` files as output.

## Project Structure

```
06/
├── src/
│   └── assembler/
│       ├── Assembler.java      // Main class
│       ├── Parser.java         // Parses assembly commands
│       ├── Code.java           // Translates assembly mnemonics into binary
│       └── SymbolTable.java    // Manages symbol resolution
├── asm/                        // Test .asm files
├── out/                        // Output .hack files
└── README.md
```

## Build and Run Instructions

### 1. Compile

From the project root directory, run:

```bash
javac -d out src/assembler/*.java
```

The compiled `.class` files will be placed under the `out/assembler/` directory.

### 2. Execute

```bash
java -cp out assembler.Assembler [input_file.asm]
```

Example:

```bash
java -cp out assembler.Assembler asm/Add.asm
```

The output `.hack` file will be created in the same directory as the input file, with a `.hack` extension.
