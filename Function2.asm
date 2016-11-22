SIZE        .INT    7
CNT         .INT    0
TENTH       .INT    0
ARR         .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0'
DATA        .INT    0
FLAG        .INT    0
OPDV        .INT    0
ZERO        .INT    0
ONE         .INT    1
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
                ADI	R4  36	; Compute Return Address (always a fixed amount)
                STR	R4  FP  ; Return Address to the Beginning of the Frame
    ; Call function
                JMP	RESET	; Call Function

;GETDATA
    ; Test for overflow (SP <  SL)
                MOV    	R5  SP
                ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
                CMP 	    R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function GETDAT()
                MOV 	R4  FP	; Save FP in R4, this will be the PFP
                MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
                ADI	    SP  -4	; Adjust Stack Pointer for Return Address
                STR	    R4  SP	; PFP to Top of Stack 			(PFP = FP)
                ADI	    SP  -4	; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
    ; Set return address
                MOV 	R4  PC	; PC incremented by 1 instruction
                ADI	R4  36	; Compute Return Address (always a fixed amount)
                STR	R4  FP  ; Return Address to the Beginning of the Frame
                JMP	GETDATA	; Call Function

1M_WHILE        LDR     R0  ARR
                LDR     R1  AT
                CMP     R0  R1
                BRZ     R0  1M_END_WHILE
                LDR     R4  ARR
                LDR     R5  PLUS
                CMP     R4  R5
                BRZ     R4  1M_IF
                LDR     R4  ARR
                LDR     R5  MINUS
                CMP     R4  R5
                BRZ     R4  1M_IF
                JMP     1M_ELSE
;GETDATA //Get most significant byte
    ; Test for overflow (SP <  SL)
1M_IF           MOV    	R5  SP
                ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
                CMP 	    R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function GETDAT()
                MOV 	R4  FP	; Save FP in R4, this will be the PFP
                MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
                ADI	    SP  -4	; Adjust Stack Pointer for Return Address
                STR	    R4  SP	; PFP to Top of Stack 			(PFP = FP)
                ADI	    SP  -4	; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
    ; Set return address
                MOV 	R4  PC	; PC incremented by 1 instruction
                ADI	    R4  36	; Compute Return Address (always a fixed amount)
                STR 	R4  FP  ; Return Address to the Beginning of the Frame
                JMP	    GETDATA	; Call Function

1M_ELSE         LDA     R1  ARR
                SUB     R2  R2
                ADI     R2  1
                ADD     R1  R2
                LDA     R4  ARR
                MOV     R3  R4  ;
                TRP     1        ;
                STR     R4  R1 ;TODO THIS IS BROKEN
                MOV     R3  R4  ;
                TRP     1        ;
                MOV     R3  R4  ;
                TRP     1        ;
                LDR     R3  R1 ;
                TRP     1
                LDR     R1  PLUS
                STR     R1  R4
                LDR     R4  CNT
                ADI     R4  1
                STR     R4  CNT

2M_WHILE        LDR     R1  DATA
                BGT     R1  2M_END_WHILE
2M_IF           LDR     R4  CNT
                ADI     R4  -1
                LDA     R5  ARR
                ADD     R5  R4
                LDR     R5  R5
                LDR     R6  NL
                CMP     R5  R6
                BNZ     R5  2M_ELSE
                SUB     R1  R1
                STR     R1  DATA
                ADI     R1  1
                STR     R1  TENTH
                LDR     R1  CNT
                ADI     R1  -2
                STR     R1  CNT
;3M_WHILE
;3M_IF

;GETDATA
    ; Test for overflow (SP <  SL)
2M_ELSE         MOV    	R5  SP
                ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
                CMP 	    R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function GETDAT()
                MOV 	R4  FP	; Save FP in R4, this will be the PFP
                MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
                ADI	    SP  -4	; Adjust Stack Pointer for Return Address
                STR	    R4  SP	; PFP to Top of Stack 			(PFP = FP)
                ADI	    SP  -4	; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
    ; Set return address
                MOV 	R4  PC	; PC incremented by 1 instruction
                ADI	R4  36	; Compute Return Address (always a fixed amount)
                STR	R4  FP  ; Return Address to the Beginning of the Frame
                JMP	GETDATA	; Call Function

;RESET
    ; Test for overflow (SP <  SL)
2M_END_WHILE    MOV    	R5  SP
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
                ADI	R4  36	; Compute Return Address (always a fixed amount)
                STR	R4  FP  ; Return Address to the Beginning of the Frame
    ; Call function
                JMP	RESET	; Call Function
;GETDATA
    ; Test for overflow (SP <  SL)
                MOV    	R5  SP
                ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
                CMP 	    R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function GETDAT()
                MOV 	R4  FP	; Save FP in R4, this will be the PFP
                MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
                ADI	    SP  -4	; Adjust Stack Pointer for Return Address
                STR	    R4  SP	; PFP to Top of Stack 			(PFP = FP)
                ADI	    SP  -4	; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
    ; Set return address
                MOV 	R4  PC	; PC incremented by 1 instruction
                ADI	R4  36	; Compute Return Address (always a fixed amount)
                STR	R4  FP  ; Return Address to the Beginning of the Frame
                JMP	GETDATA	; Call Function

                JMP     1M_WHILE
1M_END_WHILE    TRP   0

;GETDATA DECLARATION
    ; Test for overflow (SP <  SL)
    ; Put local variable on the stack
GETDATA   LDR   R4  CNT
          LDR   R5  SIZE
          CMP   R4  R5
          BLT   R4  GD_IF
          JMP   GD_ELSE
GD_IF     LDA   R0  ARR
          LDR   R1  CNT
          ADD   R0  R1
          TRP   4
          STB   R3  R0
          ADI   R1  1
          STR   R1  CNT
          JMP   GD_ENDIF
GD_ELSE   LDB     R3  N
          TRP     3
          LDB     R3  u
          TRP     3
          LDB     R3  m
          TRP     3
          LDB     R3  b
          TRP     3
          LDB     R3  e
          TRP     3
          LDB     R3  r
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDB     R3  i
          TRP     3
          LDB     R3  s
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDB     R3  t
          TRP     3
          LDB     R3  o
          TRP     3
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDB     R3  b
          TRP     3
          LDB     R3  i
          TRP     3
          LDB     R3  g
          TRP     3
          LDB     R3  NL
          TRP     3

;GETDATA
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
    ; Test for Underflow (SP > SB)
GD_ENDIF    MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
            MOV 	R5  SP
            CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
            BGT	    R5  UNDERFLOW

    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            LDR	    FP  FP     ; Point at Previous Activation Record 	(FP = PFP)
            JMR	    R5	       ; Jump to Return Address in Register R5

;FLUSH DECLARATION
    ; Test for overflow (SP <  SL)
    ; Put local variable on the stack
FLUSH       SUB     R4  R4
            STR     R4  DATA
            LDA     R0  ARR
            TRP     4
            STB     R3  R0
FLUSH_WHILE LDR     R0  ARR
            LDB     R1  NL
            CMP     R0  R1
            BRZ     R0  END_FLUSH_WHILE
            LDA     R0  ARR
            TRP     4
            STB     R3  R0
            JMP     FLUSH_WHILE
    ; Test for Underflow (SP > SB)
END_FLUSH_WHILE MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
                MOV 	R5  SP
                CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
                BGT	    R5  UNDERFLOW

    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            LDR	    FP  FP     ; Point at Previous Activation Record 	(FP = PFP)
            JMR	    R5	       ; Jump to Return Address in Register R5

;RESET DECLARATION
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
 R_FOR_END  MOV    R0   FP
            ADI     R0   -8
            LDR     R1   R0
            STR     R1   DATA
            MOV     R0   FP
            ADI     R0   -12
            LDR     R1   R0
            STR     R1   OPDV
            MOV     R0   FP
            ADI     R0   -16
            LDR     R1   R0
            STR     R1   CNT
            MOV     R0   FP
            ADI     R0   -20
            LDR     R1   R0
            STR     R1   FLAG

    ; Test for Underflow (SP > SB)
            MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
            MOV 	R5  SP
            CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
            BGT	    R5  UNDERFLOW

    ; Set Previous Frame to Current Frame and Return
            LDR 	R5  FP	   ; Return Address Pointed to by FP
            LDR	    FP  FP     ; Point at Previous Activation Record 	(FP = PFP)
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