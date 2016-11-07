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
TEMP     .INT     100
RESULT   .INT     0
NL       .BYT     '\n'
SP       .BYT     'space'
WHILE     LDR     R1  I
          SUB     R3  R3    ;Sets R3 to 0
          ADI     R3  4     ;Adds 4 to R3
          MUL     R3  R1    ;Gets the correct location in the ARR
          LDR     R7  SIZE
          MOV     R2  R1
          CMP     R2   R7
          BRZ     R2  ENDWHILE
          LDA     R4  ARR   ;Loads the address of the start of the ARR
          ADD     R4  R3    ;Increments the position in the ARR by I
          LDR     R2  R4    ;() indirect load. Value in Memory at location in R4
          LDR     R6  SUM
          ADD     R2  R6   ;Add ARR + 1 value to SUM
          LDA     R6  SUM
          STR     R6  R2   ;Store new sum in Sum location
          ADI     R1  1
          LDA     R5  I
          STR     R5  R1
          LDR     R3  SUM
          TRP     1
          LDB     R3  NL
          TRP     3
          TRP     3
          JMP     WHILE
ENDWHILE  LDR     R3  SUM  ;TODO END WHILE IS NOT BEING SAVED AT THE CORRECT LOCAL IN SYMBOL TABLE
          TRP     1