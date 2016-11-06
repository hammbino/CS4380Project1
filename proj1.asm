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
SP    .BYT  'space'
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
      LDB R3 SP
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
      LDB R3 Y
      TRP 3
      LDB R3 NL
      TRP 3
      TRP 3
ADD1  LDR R1 B1    ;ADD all the elements of list B togehter Part 3
      LDR R2 B2
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 B3
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 B4
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 B5
      ADD R1 R2
      MOV R3 R1
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 B6
      ADD R1 R2
      MOV R3 R1  ;Final ADD result is in R1 for Part 3
      TRP 1
      LDB R3 NL  ;Print a blank line Part 4
      TRP 3
      TRP 3
      LDR R4 A1  ;MUL all the elements of list A together Part 5
      LDR R2 A2
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 A3
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 A4
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 A5
      MUL R4 R2
      MOV R3 R4
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      LDR R2 A6
      MUL R4 R2
      MOV R3 R4   ;Final MUL result in R4 for Part 5
      TRP 1
      LDB R3 NL   ;Print a blank line Part 6
      TRP 3
      TRP 3
      MOV R3 R1   ;DIV the final result from Part 3 by each element in list B Part 7
      LDR R2 B1
      DIV R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 B2
      DIV R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 B3
      DIV R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 B4
      DIV R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 B5
      DIV R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R1
      LDR R2 B6
      DIV R3 R2
      TRP 1
      LDB R3 NL   ;Print a blank line Part 8
      TRP 3
      TRP 3
      MOV R3 R4   ;SUB from the final result of Part 5 each element of list C Part 9
      LDR R2 C1
      SUB R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R4
      LDR R2 C2
      SUB R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R4
      LDR R2 C3
      SUB R3 R2
      TRP 1
      LDB R3 SP
      TRP 3
      TRP 3
      MOV R3 R4
      LDR R2 C4
      SUB R3 R2
      TRP 1
      LDB R3 NL
      TRP 3