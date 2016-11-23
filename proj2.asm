SIZE     .INT     10
ARR      .INT     10
         .INT     2
         .INT     3
         .INT     4
         .INT     15
         .INT     -6
         .INT     7
         .INT     8
         .INT     9
         .INT     10
I        .INT     0
SUM      .INT     0
TEMP     .INT     0
RESULT   .INT     0
u        .BYT     'u'
m        .BYT     'm'
i        .BYT     'i'
s        .BYT     's'
e        .BYT     'e'
v        .BYT     'v'
n        .BYT     'n'
o        .BYT     'o'
d        .BYT     'd'
D        .BYT     'D'
A        .BYT     'A'
G        .BYT     'G'
S        .BYT     'S'
NL       .BYT     '\n'
SPACE    .BYT     'space'
WHILE     LDR     R0  I     ;Get the value of I
          LDR     R7  SIZE
          MOV     R3  R0    ;R3 = I
          CMP     R3  R7    ;I == SIZE
          BRZ     R3  ENDWHILE  ;If equal go to end of while
          SUB     R3  R3    ;Sets R3 to 0
          ADI     R3  4     ;Adds Integer value (4) to R3
          MUL     R3  R0    ;i in ARR[i]
          LDA     R4  ARR   ;Loads the address of the start of the ARR
          ADD     R4  R3    ;ARR[i]
          LDR     R1  R4    ;R1 = ARR[I]
          LDR     R7  SUM   ;R7 = SUM
          ADD     R7  R1    ;SUM (R7) += ARR[I]
          LDA     R2  SUM   ;R2 = SUM&
          STR     R2  R7    ;Sum& = R7
CHECK     SUB     R2  R2    ;R2 = 0
          ADI     R2  2     ;R2 = 2
          MOV     R3  R1
          DIV     R3  R2
          MUL     R3  R2
          SUB     R3  R1
CHKEVN    BRZ     R3  EVEN
          BNZ     R3  ODD
EVEN      MOV     R3  R1
          TRP     1
          LDB     R3  SPACE
          TRP     3
          LDB     R3  i
          TRP     3
          LDB     R3  s
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDB     R3  e
          TRP     3
          LDB     R3  v
          TRP     3
          LDB     R3  e
          TRP     3
          LDB     R3  n
          TRP     3
          LDB     R3  NL
          TRP     3
          JMP     INCI
ODD       MOV     R3  R1
          TRP     1
          LDB     R3  SPACE
          TRP     3
          LDB     R3  i
          TRP     3
          LDB     R3  s
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDB     R3  o
          TRP     3
          LDB     R3  d
          TRP     3
          TRP     3
          LDB     R3  NL
          TRP     3
          JMP     INCI
INCI      ADI     R0  1
          LDA     R1  I
          STR     R1  R0
          JMP     WHILE
ENDWHILE  LDB     R3  S
          TRP     3
          LDB     R3  u
          TRP     3
          LDB     R3  m
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDB     R3  i
          TRP     3
          LDB     R3  s
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDR     R3  SUM
          TRP     1
          LDB     R3  NL
          TRP     3
DAGS      LDB     R3  D
          TRP     3
          LDB     R3  A
          TRP     3
          LDB     R3  G
          TRP     3
          LDB     R3  S
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDR     R6  D
          MOV     R3  R6
          TRP     1
          LDB     R3  SPACE
          TRP     3
          LDB     R1  D
          LDB     R2  G
          LDA     R4  D
          STB     R2  R4
          LDA     R4  G
          STB     R1  R4
          LDB     R3  D
          TRP     3
          LDB     R3  A
          TRP     3
          LDB     R3  G
          TRP     3
          LDB     R3  S
          TRP     3
          LDB     R3  SPACE
          TRP     3
          LDR     R7  D
          MOV     R3  R7
          TRP     1
          LDB     R3  SPACE
          TRP     3
          SUB     R6  R7
          MOV     R3  R6
          TRP     1
          TRP     0