// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
// The algorithm is based on repetitive addition.

// Pseudo Code
// int sum = 0;
//
// if (a = 0 || b = 0) {
//     return;	
// } 
//
// int a = RAM[0];
// int b = RAM[1];
//
// int i = 0;
// int negative = 0;
//
// if (a < 0) {
//     a = -a;
//     negative = !negative;
// }
//
// if (b < 0) {
//     b = -b;
//     negative = !negative;
// }
//
// while (i < b) {
//     sum += a;
//     i++;
// }
//
// if (negative) {
//     sum = -sum;
// }
//
// RAM[2] = sum;


@R2
M=0  // sum = 0

// if (a == 0 || b == 0): jump to LOOP_END
@R0
D=M
@LOOP_END
D;JEQ

@R1
D=M
@LOOP_END
D;JEQ


// init variable
@I
M=0
@NEGATIVE
M=0


// if (a >= 0): jump to CHECK_B
@R0
D=M
@CHECK_B
D;JGE


// if (a < 0): a = -a; negative = !negative;
@R0
M=-M
@NEGATIVE
!M


(CHECK_B)
// if (b >= 0): jump to LOOP
@R1
D=M
@LOOP
D;JGE


// if (b < 0): b = -b; negative = !negative;
@R1
M=-M
@NEGATIVE
!M


(LOOP)
	// sum += a
	@R0
	D=M
	@R2
	M=D+M

	// i++
	@I
	M=M+1 
	D=M

	// if (i - b < 0): continue loop
	@R1
	D=D-M
	@LOOP
	D;JLT

(LOOP_END)
// if (negative == 0): EXIT
@NEGATIVE
D=M
@EXIT
D;JEQ

@R2
M=-M

(EXIT)
@EXIT
0;JMP
