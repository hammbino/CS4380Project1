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
HUND        .INT    100
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
;FLUSH
    ; Test for overflow (SP <  SL)
          MOV    	R5  SP
          ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
          CMP 	    R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
          BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function FLUSH()
          MOV 	R4  FP	; Save FP in R4, this will be the PFP
          MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
          ADI	SP  -4	; Adjust Stack Pointer for Return Address
          STR	R4  SP	; PFP to Top of Stack 			(PFP = FP)
          ADI	SP  -4	; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
    ; Set return address
          MOV 	R4  PC	; PC incremented by 1 instruction
          ADI	R4  36	; Compute Return Address (always a fixed amount)
          STR	R4  FP  ; Return Address to the Beginning of the Frame
          JMP	FLUSH	; Call Function
          TRP   0

    ; Test for Underflow (SP > SB)
GD_ENDIF    MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
            MOV 	R5  SP
            CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
            BGT	    R5  UNDERFLOW

    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            MOV     R0  FP
            ADI     R0  -4
            LDR     R0  FP     ; Point at Previous Activation Record 	(FP = PFP)
            JMR	    R5	       ; Jump to Return Address in Register R5

;FLUSH DECLARATION ;TODO FUNCTION CHECKED
    ; Test for overflow of local variables(SP <  SL)
    ; Put local variable on the stack
FLUSH       SUB     R4  R4
            STR     R4  DATA
            LDA     R0  ARR
            TRP     4            ;TRAP 4
            STB     R3  ARR

FLUSH_WHILE LDB     R0  ARR
            LDB     R1  NL
            CMP     R0  R1

            BRZ     R0  END_FLUSH_WHILE
            TRP     4
            STB     R3  ARR
            JMP     FLUSH_WHILE
    ; Test for Underflow (SP > SB)
END_FLUSH_WHILE MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
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