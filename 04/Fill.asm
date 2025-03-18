// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, 
// the screen should be cleared.


// Pseudo Code
// int status = 0;
// int newStatus = 0;
// int screenAddress = 0;
// int i = 0;
//
// while (true) { 
//    if (KBD == 0) {
//        newStatus = 0;
//    } else {
//        newStatus = -1;
//    }
//    
//    if (newStatus != status) {
//        status = newStatus;
//
//        screenAddress = SCREEN_START_ADDRESS + SCREEN_SIZE;
//
//        for (i = screenAddress; i >= SCREEN_START_ADDRESS; i--) {
//            SCREEN[i] = status;
//        }
//    }
// }


@status
M=0
D=M
@CHECK_STATUS
0;JMP

(LOOP)
    // if (key isn't pressed): jump to CHECK_STATUS with ARG=0
    @KBD
    D=M
    @CHECK_STATUS
    D;JEQ
    // if (key pressed): jump to CHECK_STATUS with ARG=-1
    D=-1
    
(CHECK_STATUS)
    // if (status not changed): continue loop
    @ARG
    M=D
    @status
    D=D-M
    @LOOP
    D;JEQ
    
    // if (status changed): set i = last screen
    @ARG
    D=M
    @status
    M=D
    
    @SCREEN
    D=A
    @8192
    D=D+A
    @i
    M=D
    
(SET_SCREEN)
    // i--
    @i
    D=M-1
    M=D

    // if (i < @SCREEN): goto LOOP
    @i
    D=M
    @SCREEN
    D=D-A
    @LOOP
    D;JLT
    
    // if (i >= 0): SCREEN[i] = status
    @status
    D=M         
    @i
    A=M         
    M=D

    // continue loop 
    @SET_SCREEN
    0;JMP
