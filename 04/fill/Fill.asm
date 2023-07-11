// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

(INIT)
    @32
    D=A
    @col
    M=D       // col=32 (16bit x 32 = 512)
    @256
    D=A
    @row
    M=D       // row=256

    @SCREEN
    D=A
    @addr
    M=D           // addr=16384
    @i
    M=0           // i=0

(LOOP)
    @j
    M=0           // j=0
    @i
    D=M           // D=i
    @row
    D=D-M         // D=i-row
    @INIT
    D;JGE         // If (i-row)>=0 goto (LOOP)
(SUB_LOOP)
    @j
    D=M           // D=j
    @col
    D=D-M         // D=j-col
    @SUB_END
    D;JGE         // If (j-col)>=0 goto SUBEND
    
    @density
    M=0           // density=0 (0000000000000000)
    @KBD
    D=M
    @ONOFF
    D;JEQ         // If (KBD value)=0 goto ONOFF 
    @density
    M=-1          // density=-1 (1111111111111111)

(ONOFF)
    @density
    D=M           // D=density
    @addr
    A=M
    M=D           // RAM[addr]=density
    
    @j
    M=M+1         // j=j+1
    @addr
    M=M+1         // addr=addr+1

    @SUB_LOOP
    0;JMP         // goto SUBLOOP
(SUB_END)
    @i
    M=M+1         // i=i+1
    @LOOP
    0;JMP         // goto LOOP