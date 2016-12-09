ARR         .BYT   '0' ;0
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
F           .BYT    'F'
a           .BYT    'a'
c           .BYT    'c'
f           .BYT    'f'
i           .BYT    'i'
l           .BYT    'l'
o           .BYT    'o'
r           .BYT    'r'
s           .BYT    's'
t           .BYT    't'
SPACE       .BYT    'space'
NL          .BYT    '\n'
PROMPT      .BYT    '>'

; Using R0 for manipulating Stack Registers
; Using R3 for TRP
; Using R4 for Stack Value
; Using R5 - R7 to Store Values
; Using R8 as PC
; Using R9 as SL
; Using R10 as SP
; Using R11 as FP
; Using R12 as SB
START       LDB     R3  PROMPT  ;Put a prompt on the screen
            TRP     3
            LDB     R3  SPACE
            TRP     3
            TRP     2           ;Get integer input from console
            BRZ     R3  END     ;If zero is entered end program
            MOV     R7  R3
;FACTORIAL
    ; Test for overflow (SP <  SL)
            MOV    	R0  SP
            ADI	    R0  -12	; Adjust for space needed (Rtn Address & PFP & 1 int)
            CMP     R0  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
            BLT	    R0  OVERFLOW

    ; Create Activation Record and invoke function factorial(int n)
            MOV 	R0  FP	; Save FP in R0, this will be the PFP
            MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
            ADI	    SP  -4	; Adjust Stack Pointer for Return Address
            STR	    R0  SP	; PFP to SP 			(PFP = FP)
            ADI	    SP  -4	; Increment SP

    ; Passed Parameters onto the Stack (Pass by Value)
            SUB     R4  R4  ; Set R4 to 0
            STR     R4  R7
            STR	    R4  SP  ; Place param on the Stack
            ADI	    SP  -4

    ; Set return address
            MOV 	R0  PC	; PC incremented by 1 instruction
            ADI	    R0  36	; Compute Return Address (always a fixed amount)
            STR     R0  FP  ; Return Address to the Beginning of the Frame; Call function
            JMP     FACTO	; Call Function

    ; Display "Factorial of X is Y"
            LDB     R3  F
            TRP     3
            LDB     R3  a
            TRP     3
            LDB     R3  c
            TRP     3
            LDB     R3  t
            TRP     3
            LDB     R3  o
            TRP     3
            LDB     R3  r
            TRP     3
            LDB     R3  i
            TRP     3
            LDB     R3  a
            TRP     3
            LDB     R3  l
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDB     R3  o
            TRP     3
            LDB     R3  f
            TRP     3
            LDB     R3  SPACE
            TRP     3
            MOV     R3  R7
            TRP     1
            LDB     R3  SPACE
            TRP     3
            LDB     R3  i
            TRP     3
            LDB     R3  s
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDR     R3  SP
            TRP     1
            LDB     R3  NL
            TRP     3
    ; Loop to factorial function until zero is pressed
            JMP     START
;FACT DECLARATION
    ; Test for overflow if adding local variables (SP <  SL)
    ; Put local variable on the stack
FACT


    ; Test for Underflow (SP > SB)
            MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
            MOV 	R5  SP
            CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
            BGT	    R5  UNDERFLOW

    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            MOV     R0  FP
            ADI     R0  -4
            LDR     FP  R0   ; Point at Previous Activation Record 	(FP = PFP)
            JMR	    R5	       ; Jump to Return Address in Register R5


FACT            MOV R0 FP
                ADI R0 -8
                LDR R4 R0 ; load param n into register
                LDR R0 ONE ; return 1 since n = 0
                BRZ R4 FACT_DA
                MOV R0 SP
                ADI R0 -12 ; required space for FACT activation record
                CMP R0 SL
                BLT R0 OVERFLOW
                MOV R0 FP ; PFP = FP
                MOV R3 FP
                ADI R3 -8
                LDR R4 R3
                MOV FP SP ; FP = SP
                ADI SP -4 ; increase stack height by 1 integer
                STR R0 SP ; store pfp on stack
                ADI SP -4 ; increase stack height by 1 integer
                ADI R4 -1 ; n-1 Parameter 1
                STR R4 SP
                ADI SP -4
                MOV R1 R8 ; Compute return address
                ADI R1 44 ; adi the amount of instructions.
                STR R1 FP ; store return address
                JMP FACT
                MOV R0 FP
                ADI R0 -8
                LDR R3 R0 ; load param n into register
                LDR R0 SP
                MUL R0 R3
FACT_DA         MOV SP FP ; deallocate FACT function
                MOV R5 SP
                CMP R5 SB
                BGT R5 UNDERFLOW
                LDR R5 FP
                MOV R4 FP
                ADI R4 -4
                LDR FP R4
                STR R0 SP
FACT_END        JMR R5
END             TRP 0
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