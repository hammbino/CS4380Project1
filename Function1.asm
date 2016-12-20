A     .INT   6
B     .INT   4
T     .BYT  'T'
J     .BYT  'J'
H     .BYT  'H'

START LDB R3 J
      TRP 3

; Fixed Length 2 byte Instructions
; Classical 5 Stage Pipeline: Address Modes (Reg Indirect), (Reg Indirect + Index)
; Address Size (Integer) 4 Bytes

  ; Test for overflow (SP <  SL)	; Must compute space needed for Frame
      MOV    	R5  SP
      ADI	    R5  -8	; Adjust for space needed (Rtn Address & PFP)
      CMP 	    R5  SL	; 0 (SP=SL), Pos (SP > SL), Neg (SP < SL)
      BLT	    R5  OVERFLOW

; Create Activation Record and invoke function F(x, y)
      MOV 	R4  FP	; Save FP in R4, this will be the PFP
      MOV 	FP  SP	; Point at Current Activation Record 	(FP = SP)
      ADI	SP  -4	; Adjust Stack Pointer for Return Address
      STR	R4  SP	; PFP to Top of Stack 			(PFP = FP)
      ADI	SP  -4	; Adjust Stack Pointer for PFP
; Passed Parameters onto the Stack (Pass by Value)
      LDR   R5  A
      STR	R5  SP  ; Place A on the Stack
      ADI	SP  -4
      LDR	R5  B
      STR	R5  SP	; Place B on the Stack
      ADI	SP  -4
      MOV 	R1  PC	; PC incremented by 1 instruction
      ADI	R1  36	; Compute Return Address (always a fixed amount)
      STR	R1  FP  ; Return Address to the Beginning of the Frame
      JMP	F	    ; Call Function F

      LDB R3 H
      TRP 3
      TRP 0

; Instruction where we return
; Fixed Length 2 byte Instructions
; Classical 5 Stage Pipeline
; Address Size (Integer) 4 Bytes

F   MOV     R6  FP
    ADI     R6  -12
    LDR     R6  R6
    MOV     R7  FP
    ADI     R7  -8
    LDR     R7  R7
    ADD     R6  R7
    MOV     R3  R6
    TRP     1
  ; Test for Underflow (SP > SB)
    MOV  	SP  FP	  ; De-allocate Current Activation Record 	(SP = FP)
    MOV 	R5  SP
    CMP 	R5  SB	  ; 0 (SP=SB), Pos (SP > SB), Neg (SP < SB)
    BGT	    R5  UNDERFLOW
  ; Set Previous Frame to Current Frame and Return
    LDR 	R5  FP	   ; Return Address Pointed to by FP
    LDR	    FP  FP     ; Point at Previous Activation Record 	(FP = PFP)
    JMR	    R5	       ; Jump to Return Address in Register R5

 OVERFLOW SUB R3 R3
         ADI R3 9
         TRP 1
         TRP 0

UNDERFLOW SUB R3 R3
         ADI R3 8
         TRP 1
         TRP 0
