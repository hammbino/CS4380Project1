ARR         .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
            .INT    0
CNT         .INT    0
MUTEX       .INT    -1
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
COMMA       .BYT    ','

; Using R0 for manipulating Stack Registers
; Using R3 for TRP
; Using R4 for Stack Value
; Using R5 - R7 to Store Values
; Using R8 as PC
; Using R9 as SL
; Using R10 as SP
; Using R11 as FP
; Using R12 as SB

            LDB     R3  PROMPT  ;Put a prompt on the screen
            TRP     3
            LDB     R3  SPACE
            TRP     3
            TRP     2           ;Get integer input from console
            BRZ     R3  PART3
            JMP     ALLOCARR
START       LDB     R3  PROMPT  ;Put a prompt on the screen
            TRP     3
            LDB     R3  SPACE
            TRP     3
            TRP     2           ;Get integer input from console
            BRZ     R3  PARR    ;If zero is entered print the array
ALLOCARR    LCK
            MOV     R7  R3      ;Store the input value into R7
            LDA     R5  ARR     ;Load the Array R5
            LDR     R6  CNT     ;Load the current array position into R6
            ADD     R5  R6      ;Move to the correct position in the array
            STR     R7  R5
            ADI     R6  4
            STR     R6  CNT
            ULK
;FACTORIAL
    ; Test for overflow (SP <  SL)
FACTALLOC   MOV    	R0  SP
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
            STR     R7  SP  ; Store value that was input at the SP
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
            LCK
            LDA     R5  ARR     ;Load the Array R5
            LDR     R6  CNT     ;Load the current array position into R6
            ADD     R5  R6      ;Move to the correct position in the array
            STR     R3  R5      ;Store the Y vale into the open position in array
            ADI     R6  4
            STR     R6  CNT
            ULK
            LDB     R3  NL
            TRP     3
            END
    ; Loop to factorial function until zero is pressed
            JMP     START //TODO difference here
PARR        SUB     R5  R5      ;Front of the array
            LDR     R6  CNT     ;Back of the array
            BRZ     R6  PART3END
            ADI     R6  -4
WHILEARR    LDA     R1  ARR
            ADD     R1  R5
            LDR     R3  R1
            TRP     1
            LDB     R3  COMMA
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDA     R1  ARR
            ADD     R1  R6
            LDR     R3  R1
            TRP     1
            LDB     R3  COMMA
            TRP     3
            LDB     R3  SPACE
            TRP     3
            ADI     R5  4
            ADI     R6  -4
            MOV     R1  R5
            CMP     R1  R6
            BLT     R1  WHILEARR
PART3       SUB     R5  R5          ;JMP FINISH
            STR     R5  CNT
            LDB R3 NL
            TRP 3
            TRP 3
            LCK
PART3WHILE  LDB     R3  PROMPT  ;Put a prompt on the screen
            TRP     3
            LDB     R3  SPACE
            TRP     3
            TRP     2           ;Get integer input from console
            BRZ     R3  PART3END;TODO Different
            RUN     R7  FACTALLOC
            JMP     PART3WHILE
PART3END    ULK
            END
            BLK
FINISH      TRP     0

;FACTO DECLARATION
    ; Test for overflow if adding local variables (SP <  SL)
    ; Put local variable on the stack
FACTO       MOV     R0  FP
            ADI     R0  -8
            LDR     R5  R0    ; load param n into register 5
            SUB     R7  R7
            ADI     R7  1
            BRZ     R5  NZERO
;FACTORIAL
    ; Test for overflow (SP <  SL)
            MOV    	R0  SP
            ADI	    R0  -12	; Adjust for space needed (Rtn Address & PFP & 1 int)
            CMP     R0  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
            BLT	    R0  OVERFLOW
    ; Create Activation Record and invoke function factorial(int n)
            MOV 	R0  FP	; Save FP in R0, this will be the PFP
            MOV     R4  FP  ; Get pass value
            ADI     R4  -8  ; FP (Base) + Offet (-8, up to spots)
            LDR     R4  R4  ; Stores value from Activation Record into R4
            MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
            ADI	    SP  -4	; Adjust Stack Pointer for Return Address
            STR	    R0  SP	; PFP to SP 			(PFP = FP)
            ADI	    SP  -4	; Increment SP
    ; Passed Parameters onto the Stack (Pass by Value)
            ADI     R4  -1  ; Subtract 1 from value passed previously
            STR	    R4  SP  ; Place param AT SP
            ADI	    SP  -4
    ; Set return address
            MOV 	R0  PC	; PC incremented by 1 instruction
            ADI	    R0  36	; Compute Return Address (always a fixed amount)
            STR     R0  FP  ; Return Address to the Beginning of the Frame
            JMP     FACTO	; Call Function
    ; n * fact(n – 1)
            MOV     R7  FP
            ADI     R7  -8
            LDR     R7  R7
            LDR     R0  SP
            MUL     R7  R0
    ; Test for Underflow (SP > SB)
NZERO       MOV  	SP  FP	    ; De-allocate Current Activation Record (SP = FP)
            MOV 	R0  SP
            CMP 	R0  SB	    ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
            BGT	    R0  UNDERFLOW
    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            MOV     R0  FP
            ADI     R0  -4
            LDR     FP  R0   ; Point at Previous Activation Record 	(FP = PFP)
            STR     R7  SP
            JMR	    R5	       ; Jump to Return Address in Register R5

;OVERFLOW DECLARATION
OVERFLOW    SUB R3 R3
            ADI R3 9
            TRP 1
            TRP 0

; UNDERFLOW DECLARATION
UNDERFLOW   SUB R3 R3
            ADI R3 8
           