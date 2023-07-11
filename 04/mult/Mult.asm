// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)
//
// This program only needs to handle arguments that satisfy
// R0 >= 0, R1 >= 0, and R0*R1 < 32768.


	@R1
	D=M     // D=R1
	@R0
	D=D-M   // D=R1-R0
	@INIT
	D;JGE   // if (R1-R0) >= 0, goto INIT

(SWAP)
	@R0
	D=M     // D=R0
	@temp
	M=D     // temp=R0
	@R1
	D=M     // D=R1
	@R0
	M=D     // R0=D
	@temp
	D=M     // D=temp
	@R1
	M=D     // R1=D
(INIT)
	@temp
	M=0     // temp=0
	@R2
	M=0     // R2=0
	@i
	M=0     // i=0
(LOOP)
	@i
	D=M     // D=i
	@R0
	D=D-M   // D=i-R0
	@END
	D;JGE   // If (i-R0)>=0 goto END

	@R1
	D=M     // D=R1
	@R2
	M=D+M   // R2=R1+R2
	@i
	M=M+1   // i=i+1
	@LOOP
	0;JMP   // Goto LOOP
(END)
	@END
	0;JMP
