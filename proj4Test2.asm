ZERO            .INT 0
ONE             .INT 1
INT_SIZE        .INT 4
CNT             .INT 0
ARR             .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
                .INT 0
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
START           LDB R3 PROMPT
                TRP 3
                LDB R3 SPACE
                TRP 3
                TRP 2
                BRZ R3 END
                MOV R7 R3
                MOV R0 SP
                ADI R0 -12 ; required space for FACT activation record
                CMP R0 SL
                BLT R0 OVERFLOW
                MOV R0 FP ; PFP = FP
                MOV FP SP ; FP = SP
                ADI SP -4 ; increase stack height by 1 integer
                STR R0 SP ; store pfp on stack
                ADI SP -4 ; increase stack height by 1 integer
                STR R7 SP ; Parameter 1
                ADI SP -4
                MOV R1 PC ; Compute return address
                ADI R1 36 ; adi the amount of instructions.
                STR R1 FP ; store return address
                JMP FACT
                LDB R3 F
                TRP 3
                LDB R3 a
                TRP 3
                LDB R3 c
                TRP 3
                LDB R3 t
                TRP 3
                LDB R3 o
                TRP 3
                LDB R3 r
                TRP 3
                LDB R3 i
                TRP 3
                LDB R3 a
                TRP 3
                LDB R3 l
                TRP 3
                LDB R3 SPACE
                TRP 3
                LDB R3 o
                TRP 3
                LDB R3 f
                TRP 3
                LDB R3 SPACE
                TRP 3
                MOV R3 R7
                TRP 1
                LDB R3 SPACE
                TRP 3
                LDB R3 i
                TRP 3
                LDB R3 s
                TRP 3
                LDB R3 SPACE
                TRP 3
                LDR R3 SP
                TRP 1
                LDA R0 ARR
                LDR R1 CNT
                ADD R0 R1
                STR R3 R0
                LDR R4 R0
                ADI R1 4
                STR R1 CNT
                LDB R3 NL
                TRP 3
                JMP START
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
                MOV R1 PC ; Compute return address
                ADI R1 36 ; adi the amount of instructions.
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
UNDERFLOW       JMP END
OVERFLOW        JMP END
END             TRP 0