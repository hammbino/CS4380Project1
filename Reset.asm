ARR         .BYT   '0' ;0
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0' ;6
SIZE        .INT    7  ;7
CNT         .INT    0  ;11
TENTH       .INT    0  ;15
DATA        .INT    0  ;19
FLAG        .INT    0  ;23
OPDV        .INT    0  ;27
ZERO        .INT    0
ONE         .INT    1
C0          .INT    0
C1          .INT    1
C2          .INT    2
C3          .INT    3
C4          .INT    4
C5          .INT    5
C6          .INT    6
C7          .INT    7
C8          .INT    8
C9          .INT    9
N           .BYT   'N'
u           .BYT   'u'
m           .BYT   'm'
b           .BYT   'b'
e           .BYT   'e'
r           .BYT   'r'
i           .BYT   'i'
s           .BYT   's'
t           .BYT   't'
o           .BYT   'o'
g           .BYT   'g'
O           .BYT   'O'
p           .BYT   'p'
a           .BYT   'a'
n           .BYT   'n'
d           .BYT   'd'
AT          .BYT   '@'
PLUS        .BYT   '+'
MINUS       .BYT   '-'
SPACE       .BYT   'space'
NL          .BYT   '\n'
;RESET
    ; Test for overflow (SP <  SL)
                MOV    	R5  SP
                ADI	    R5  -24	; Adjust for space needed (Rtn Address & PFP)
                CMP     R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function reset(int w, int x, int y, int z)
                MOV 	R4  FP	; Save FP in R4, this will be the PFP
                MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
                ADI	    SP  -4	; Adjust Stack Pointer for Return Address
                STR	    R4  SP	; PFP to Top of Stack 			(PFP = FP)
                ADI	    SP  -4	; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
                SUB     R4  R4  ; Set R4 to 0
                ADI     R4  1
                STR	    R4  SP  ; Place 1 on the Stack
                ADI	    SP  -4
                SUB     R4  R4  ; Set R4 to 0
                STR	    R4  SP	; Place 0 on the Stack
                ADI	    SP  -4
                STR 	R4  SP	; Place 0 on the Stack
                ADI 	SP  -4
                STR	    R4  SP	; Place 0 on the Stack
                ADI	    SP  -4
    ; Set return address
                MOV 	R4  PC	; PC incremented by 1 instruction
                ADI	    R4  36	; Compute Return Address (always a fixed amount)
                STR     R4  FP  ; Return Address to the Beginning of the Frame
    ; Call function
                JMP     RESET	; Call Function
                TRP     0

;RESET DECLARATION TODO FUNCTION CHECKED
    ; Test for overflow (SP <  SL)
RESET       MOV     R5  SP  ; check for stack overflow for local variable k
            ADI     R5  -4
            CMP 	R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
            BLT	    R5  OVERFLOW
    ; Put local variable on the stack
            SUB     R5  R5
            STR	    R5  SP	; Place K initialized to 0 on the Stack
            ADI	    SP  -4

    ; Initialize ARR
            LDA     R0  ARR
            LDR     R1  SIZE
            MOV     R2  FP
            ADI     R2  -24 ; local variable k address
            LDR     R4   R2 ; local var k value
    ; For loop
 R_FOR      CMP     R1   R4
            BRZ     R1   R_FOR_END
            SUB     R5   R5
            STB     R5   R0 ; set ARR[k] = 0
            ADI     R4   1  ; increment k
            ADI     R0   1  ; increment ARR pointer
            STR     R4   R2
            JMP     R_FOR
 R_FOR_END  MOV     R0   FP
            ADI     R0   -8
            LDR     R1   R0     ; R1 = w (1)
            STR     R1   DATA

            MOV     R0   FP
            ADI     R0   -12
            LDR     R1   R0     ; R1 = x (0)
            STR     R1   OPDV

            MOV     R0   FP
            ADI     R0   -16
            LDR     R1   R0     ; R1 = y (0)
            STR     R1   CNT

            MOV     R0   FP
            ADI     R0   -20
            LDR     R1   R0     ; R1 = z (0)
            STR     R1   FLAG
    ; Test for Underflow (SP > SB)
            MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
            MOV 	R5  SP
            CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
            BGT	    R5  UNDERFLOW

    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            MOV     R0  FP
            ADI     R0  -4
            LDR     R0  FP     ; Point at Previous Activation Record 	(FP = PFP)
            JMR	    R5	       ; Jump to Return Address in Register R5

    ;OVERFLOW DECLARATION
    OVERFLOW    SUB R3 R3
                ADI R3 9
                TRP 1
                TRP 0

    ; UNDERFLOW DECLARATION
    UNDERFLOW   SUB R3 R3
                ADI R3 8
                TRP 1
                TRP 0