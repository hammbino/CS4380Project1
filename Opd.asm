ARR         .BYT   '-'
            .BYT   '1'
            .BYT   '2'
            .BYT   '3'
            .BYT   '0'
            .BYT   '0'
            .BYT   '0' ;6
SIZE        .INT    7  ;7
CNT         .INT    1  ;11
TENTH       .INT    0  ;15
DATA        .INT    0  ;19
FLAG        .INT    0  ;23
OPDV        .INT    0  ;27
ZERO        .INT    0  ;31
ONE         .INT    1  ;35
C0          .INT    0  ;39
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

;OPD
    ; Test for overflow (SP <  SL)
                MOV    	R5  SP
                ADI	    R5  -20	; Adjust for space needed (Rtn Address & PFP)
                CMP     R5  SL	    ; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
                BLT	    R5  OVERFLOW

    ; Create Activation Record and invoke function reset(int w, int x, int y, int z)
                MOV 	R4  FP	    ; Save FP in R4, this will be the PFP
                MOV 	FP  SP	    ; Point at Current Activation Record 	(FP = SP)
                ADI	    SP  -4	    ; Adjust Stack Pointer for Return Address
                STR	    R4  SP	    ; PFP to Top of Stack 			(PFP = FP)
                ADI	    SP  -4	    ; Adjust Stack Pointer for PFP

    ; Passed Parameters onto the Stack (Pass by Value)
                LDB     R4  ARR     ; Load ARR[0] to R4
                STR	    R4  SP      ; Place ARR[0] on the Stack
                ADI	    SP  -4      ; Move SP
                LDR     R4  TENTH   ; Set R4 to TENTH
                STR	    R4  SP	    ; Place TENTH on the Stack
                ADI	    SP  -4      ; Move SP
                LDA     R4  ARR     ; Load ARR[0] to R4
                LDR     R5  CNT     ; Load CNT to R5
                ADD     R4  R5      ; Load ARR[CNT] into R4
                LDB     R4  R4
                STR	    R4  SP	    ; Place ARR[CNT] on the Stack
MOV  R3 R4
TRP  1
                ADI	    SP  -4      ; Move SP
    ; Set return address
                MOV 	R4  PC	    ; PC incremented by 1 instruction
                ADI	    R4  36	    ; Compute Return Address (always a fixed amount)
                STR	    R4  FP      ; Return Address to the Beginning of the Frame
    ; Call function
                JMP	    OPD	        ; Call Function ; TODO Correct to here
LDR  R3  OPDV
TRP  1
                TRP 0


;OPD DECLARATION
    ; Test for overflow (SP <  SL)
OPD         MOV     R5  SP  ; check for stack overflow for local variable k
            ADI     R5  -4
            CMP 	R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
            BLT	    R5  OVERFLOW
    ; Put local variable on the stack
            SUB     R5  R5
            STR	    R5  SP	; Place T initialized to 0 on the Stack
            ADI	    SP  -4
    ;if/else statements
            SUB     R7  R7     ;Integer value to store  ; TODO Correct to here
            MOV     R0  FP
            ADI     R0  -16
IF0_OPD     LDB     R2  R0 ;
MOV  R3 R2
TRP  1
            LDR     R1  C0
            CMP     R2  R1
            BNZ     R2  IF1_OPD
            MOV     R6  FP
            ADI     R6  -20
MOV  R3 R6
TRP  1
            STR     R7  R6
            JMP     OPD_LAST_IF
IF1_OPD     LDB     R2  R0
            LDR     R1  C1
            CMP     R2  R1
            BNZ     R2  IF2_OPD
            ADI     R7  1
            MOV     R6  FP
            ADI     R6  -20
MOV  R3 R6
TRP  1
            STR     R7  R6
            JMP     OPD_LAST_IF
IF2_OPD     LDB     R2  R0
            LDR     R1  C2
            CMP     R2  R1
            BNZ     R2  IF3_OPD
            ADI     R7  2
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF3_OPD     LDB     R2  R0
            LDR     R1  C3
            CMP     R2  R1
            BNZ     R2  IF3_OPD
            ADI     R7  3
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF4_OPD     LDB     R2  R0
            LDR     R1  C4
            CMP     R2  R1
            BNZ     R2  IF4_OPD
            ADI     R7  4
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF5_OPD     LDB     R2  R0
            LDR     R1  C5
            CMP     R2  R1
            BNZ     R2  IF6_OPD
            ADI     R7  5
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF6_OPD     LDB     R2  R0
            LDR     R1  C6
            CMP     R2  R1
            BNZ     R2  IF7_OPD
            ADI     R7  6
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF7_OPD     LDB     R2  R0
            LDR     R1  C7
            CMP     R2  R1
            BNZ     R2  IF8_OPD
            ADI     R7  7
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF8_OPD     LDB     R2  R0
            LDR     R1  C8
            CMP     R2  R1
            BNZ     R2  IF9_OPD
            ADI     R7  8
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
IF9_OPD     LDB     R2  R0
            LDR     R1  C9
            CMP     R2  R1
            BNZ     R2  OPD_NOT_NUM
            ADI     R7  9
            MOV     R6  FP
            ADI     R6  -20
            STR     R7  R6
            JMP     OPD_LAST_IF
OPD_NOT_NUM LDB     R3  R0
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDB     R3  i
            TRP     3
            LDB     R3  s
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDB     R3  n
            TRP     3
            LDB     R3  o
            TRP     3
            LDB     R3  t
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDB     R3  a
            TRP     3
            LDB     R3  SPACE
            TRP     3
            LDB     R3  n
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
            LDB     R3  NL
            TRP     3
            ADI     R7  1
            STR     R7  FLAG
            JMP     OPD_RETURN
    ;OPD_LAST_IF statement
OPD_LAST_IF LDR     R7  FLAG
            BNZ     R7  OPD_RETURN
            MOV     R0  FP
            ADI     R0  -8     ; R0 = s
            LDR     R0  R0
            MOV     R1  FP
            ADI     R1  -12     ; R1 = k
            LDR     R1  R1
            MOV     R2  FP
            ADI     R2  -20     ; R2 = t
            LDR     R2  R2
            LDB     R4  PLUS
            CMP     R0  R4
            BNZ     R0  OPDFIN_ELSE
            MUL     R2  R1      ; t *= k
            JMP     ADD_OPDV
OPDFIN_ELSE SUB     R6  R6
            ADI     R6  -1
            MUL     R1  R6      ; k *= -1
            MUL     R2  R1      ; t *= k
ADD_OPDV    LDR     R4  OPDV    ; R4 = OPDV
            ADD     R4  R2      ; OPDV += t
            STR     R4  OPDV
    ; Test for Underflow (SP > SB)
OPD_RETURN  MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
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