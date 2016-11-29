ARR         .BYT   '1' ;0
            .BYT   '1'
            .BYT   '1'
            .BYT   '1'
            .BYT   '1'
            .BYT   '1'
            .BYT   '1' ;6
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
                ADI	    R4  36	; Compute Return Address (always a fixed amount)
                STR	    R4  FP  ; Return Address to the Beginning of the Frame
                JMP	    GETDATA	; Call Function


1M_WHILE        LDA     R0  ARR

                LDB     R0  R0
                LDB     R1  AT
                CMP     R0  R1

                BRZ     R0  1M_END_WHILE
                LDB     R4  ARR
                LDB     R5  PLUS
                CMP     R4  R5
                BRZ     R4  1M_IF
                LDB     R4  ARR
                LDB     R5  MINUS
                CMP     R4  R5
                BRZ     R4  1M_IF
                JMP     1M_ELSE
;GETDATA //Get most significant byte
    ; Test for overflow (SP <  SL)
1M_IF           MOV    	R5  SP  ; TODO test with plus or minus
                ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
                CMP 	R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function GETDATA()
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
                JMP     1M_END_IF

1M_ELSE         LDA     R1  ARR
                SUB     R2  R2
                ADI     R2  1
                ADD     R1  R2      ; R1 = ARR[1]
                LDA     R4  ARR     ; R4 = ARR[0]
                LDB     R4  R4
                STB     R4  R1
                LDB     R1  PLUS    ; R1 = '+'
                LDA     R4  ARR     ; R4 = ARR[0]
                STB     R1  R4
                LDR     R4  CNT
                ADI     R4  1
                STR     R4  CNT
1M_END_IF       JMP     2M_WHILE ;TODO SHOULD BE 2M WHILE
1M_END_WHILE    TRP   0
2M_WHILE        LDR     R0  DATA
                SUB     R1  R1
                CMP     R0  R1
                BGT     R0  1M_WHILE



;GETDATA DECLARATION ; TODO CHECK FUNCTION (NUMBER TOO BIG IS BROKEN)
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