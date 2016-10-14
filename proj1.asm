A1    .INT  1
A2    .INT  2
A3    .INT  3
A4    .INT  4
A5    .INT  5
A6    .INT  6
B1    .INT  300
B2    .INT  150
B3    .INT  50
B4    .INT  20
B5    .INT  10
B6    .INT  5
C1    .INT  500
C2    .INT  2
C3    .INT  5
C4    .INT  10
J     .BYT  'J'
E     .BYT  'E'
F     .BYT  'F'
R     .BYT  'R'
Y     .BYT  'Y'
COM   .BYT  ','
H     .BYT  'H'
A     .BYT  'A'
M     .BYT  'M'
O     .BYT  'O'
N     .BYT  'N'
D     .BYT  'D'
NL    .BYT  '\n'
SP    .BYT  '\s'
START LDB R3 H
      TRP 3
      LDB R3 A
      TRP 3
      LDB R3 M
      TRP 3
      TRP 3
      LDB R3 O
      TRP 3
      LDB R3 N
      TRP 3
      LDB R3 D
      TRP 3
      LDB R3 COM
      TRP 3
      LDB R3 J
      TRP 3
      LDB R3 E
      TRP 3
      LDB R3 F
      TRP 3
      TRP 3
      LDB R3 R
      TRP 3
      LDB R3 E
      TRP 3
      LDR R3 Y
      TRP 3
      LDR R3 NL
      TRP 3
      TRP 3
      LDR R1 B1    ;ADD Step 3 R1
      LDR R2 B2
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 B3
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 B4
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 B5
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 B6
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDR R3 NL
      TRP 3
      TRP 3
      LDR R4 A1  ;MUL Step 5 R4
      LDR R2 A2
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 A3
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 A4
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 A5
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      LDR R2 A6
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDR R3 NL
      TRP 3
      TRP 3
      MOV R3 R1    ;DIV
      LDR R2 C1
      DIV R3 R2
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 C2
      DIV R3 R2
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 C3
      DIV R3 R2
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 C4
      DIV R3 R2
      TRP 1
      LDR R3 NL
      TRP 3
      TRP 3
      MOV R3 R4    ;SUB
      LDR R2 C1
      SUB R3 R2
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      MOV R3 R4
      LDR R2 C2
      SUB R3 R2
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      MOV R3 R4
      LDR R2 C3
      SUB R3 R2
      TRP 1
      LDR R3 SP
      TRP 3
      TRP 3
      MOV R3 R4
      LDR R2 C4
      SUB R3 R2
      TRP 1