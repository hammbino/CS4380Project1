import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

import static java.util.regex.Pattern.matches;

//TODO validate that command has valid number of instructions

public class Main {
    private final static int MEM_SIZE = 10000;
    private final static int BYTE_SIZE = 1;
    private final static int INT_SIZE = 4;
    private final static int TRAP_SIZE = 8;
    private final static int INSTRUCT_SIZE = 12;
    private static int MEM_LOCAL = 0;
    private final static List<String> INSTRUCTIONS = Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP");
    private final static String INT_STRING = ".INT";
    private final static String BYTE_STRING = ".BYT";
    private static int [] REG= new int[8];
    private static int PC = 0;
    private final static List<String> REGISTERS = new ArrayList<>(Arrays.asList("R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8"));
    private static Map<String, Integer> SYMBOL_TABLE = new HashMap<>();
    private static byte[] DATA = new byte[MEM_SIZE];
    private static ByteBuffer BB = ByteBuffer.wrap(DATA).order(ByteOrder.LITTLE_ENDIAN);
    private static int numberOfFilePasses = 0;

    public static void main(String[] args) throws IOException {
        Scanner fileReader = null;

        //check to see if the program was run with the command line argument
        if (args.length < 1) {
            System.out.println("Error: No file was provided.");
            System.exit(0);     // TERMINATE THE PROGRAM
        }

        while (numberOfFilePasses < 2) {
            //check to see if a scanner can be created using the file that was input
            try {
                fileReader = new Scanner(new FileInputStream(args[0])).useDelimiter("\\n|\\r|;");
            } catch (FileNotFoundException x) {
                System.out.println("ERROR: Unable to open file " + args[0]);
                x.printStackTrace();
                System.exit(0);   // TERMINATE THE PROGRAM
            }
            //------------------------------
            //check if it is the first pass
            //------------------------------
            if (numberOfFilePasses == 0) {
                //read each line from the file
                while (fileReader.hasNextLine()) {
                    String[] fileInput = fileReader.nextLine().replaceAll(";.*", " ").trim().split("\\s+");
                    if (!isInstruction(fileInput[0]) && !isInt(fileInput[0]) && !isByte(fileInput[0])) {
                        addToSymbolTable(fileInput);
                    } else if (isInt(fileInput[0])) {
                        MEM_LOCAL += INT_SIZE;
                    } else if (isByte(fileInput[0])) {
                        MEM_LOCAL += BYTE_SIZE;
                    } else if (isInstruction(fileInput[0])) {
                        if (fileInput[0].equals("TRP") || fileInput[0].equals("JMP") || fileInput[0].equals("JMR") && fileInput.length <= 3) {
                            MEM_LOCAL += TRAP_SIZE;
                        } else {
                            MEM_LOCAL += INSTRUCT_SIZE;
                        }
                    }
                }
            }
            //--------------------------------
            //check if it is the second pass
            //--------------------------------
            if (numberOfFilePasses == 1) {
                while (fileReader.hasNextLine()) {
                    String[] tokens = fileReader.nextLine().replaceAll(";.*", " ").trim().split("\\s+");
                    if (SYMBOL_TABLE.containsKey(tokens[0])) {
                        if (isInt(tokens[1])) {
                            int toAdd = Integer.parseInt(tokens[2]);
                            BB.putInt(toAdd);
                        } else if (isByte(tokens[1])) {
                            switch (tokens[2]) {
                                case "'\\n'":
                                    char NL = (char) 10;
                                    BB.put((byte) NL);
                                    break;
                                case "'space'":
                                    char SP = (char) 32;
                                    BB.put((byte) SP);
                                    break;
                                default:
                                    BB.put((byte) tokens[2].charAt(1));
                            }
                        } else if (isInstruction(tokens[1])) {
                            //Method to add instructions
//                            if (PC == 0) {
//                                PC = MEM_LOCAL;
//                                //TODO find a better way to set PC start
//                            }
                            addInstructToMem(tokens, 1);
                        }
                    } else if (isInstruction(tokens[0])) {
                        //Call method to add instructions
//                        if (PC == 0) {
//                            PC = MEM_LOCAL;
//                            //TODO find a better way to set PC start
//                        }
                        addInstructToMem(tokens, 0);
                    } else if (isInt(tokens[0])) {
                        int toAdd = Integer.parseInt(tokens[1]);
                        BB.putInt(toAdd);
                    } else if (isByte(tokens[0])) {
                        switch (tokens[1]) {
                            case "'\\n'":
                                char NL = (char) 10;
                                BB.put((byte) NL);
                                break;
                            case "'space'":
                                char SP = (char) 32;
                                BB.put((byte) SP);
                                break;
                            default:
                                BB.put((byte) tokens[1].charAt(1));
                        }
                    }
                }
                if (PC == 0) {
                    System.out.println("ERROR: No instructions were given");
                    System.exit(0);   // TERMINATE THE PROGRAM
                }
            }

            fileReader.close();
            numberOfFilePasses++;
        }
        //--------------------------------
        //Virtual Machine
        //--------------------------------
        int endProgram = BB.position();
        while(PC < endProgram) {
            int opCode = BB.getInt(PC);
            PC += INT_SIZE;
            int instruct1 = BB.getInt(PC);
            PC += INT_SIZE;
            int instruct2;
//            String instructRun = INSTRUCTIONS.get(readInstruction);
            switch (opCode) {
                //Branch to Label
                case 1: //JMP
                    PC = instruct1;
                    break;
                //Branch to address in source register
                case 2: //JMR
                    PC = REG[instruct1];
                    break;
                //Branch to Label if source register is not zero
                case 3: //BNZ
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
//                    int bnzLocal = BB.getInt(instruct2);
                    if (REG[instruct1] != 0) {
                        PC = instruct2;
                    }
                    break;
                //Branch to Label if source register is greater than zero
                case 4: //BGT
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
//                    int bgtLocal = BB.getInt(instruct2);
                    if (REG[instruct1] > 0) {
                        PC = instruct2; //bgtLocal;
                    }
                    break;
                //Branch to Label if source register is greater than zero
                case 5: //BLT
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
//                    int bltLocal = BB.getInt(instruct2);
                    if (REG[instruct1] < 0) {
                        PC = instruct2; //bltLocal;
                    }
                    break;
                case 6: //BRZ
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    if (REG[instruct1] == 0) {
                        PC = instruct2;  //Set to instruct2
                    }
                    break;
                //Move data from source register to destination register
                case 7: //MOV
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    if (instruct1 != 8 && instruct2 != 8) { //TODO remove this. The PC counter is not a REG.
                        REG[instruct1] = REG[instruct2];
                    } else {
                        System.out.println("Can not change value of the Program Counter with MOV instruction");
                    }
                    break;
                //Load the Address of the label into the RD register.
                case 8: //LDA
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = instruct2;
                    //SYMBOL_TABLE.get(instruction[2 + offset])
                    break;
                case 9:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    BB.putInt(instruct2, REG[instruct1]);
                    break;
                case 10: //LDR
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = BB.getInt(instruct2);
                    break;
                case 11:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    BB.put(instruct2, (byte) REG[instruct1]);
                    break;
                case 12: //LDB
                    instruct2 = (byte) BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = (int) DATA[instruct2];
                    break;
                case 13: //ADD
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] + REG[instruct2];
                    break;
                case 14: //ADI
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] + instruct2;
                    break;
                case 15: //SUB
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] - REG[instruct2];
                    break;
                case 16: //MUL
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] * REG[instruct2];
                    break;
                case 17:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] / REG[instruct2];
                    break;
                case 18:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    if (REG[instruct1] > 0 && REG[instruct2] > 0) {
                        REG[instruct1] = 1;
                    } else {
                        REG[instruct1] = 1;
                    }
                    break;
                case 19:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    if (REG[instruct1] > 0 || REG[instruct2] > 0) {
                        REG[instruct1] = 1;
                    } else {
                        REG[instruct1] = 1;
                    }
                    break;
                case 20: //CMP
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    if (REG[instruct1] == REG[instruct2])
                        REG[instruct1] = 0;
                    else if (REG[instruct1] > REG[instruct2])
                        REG[instruct1] = 1;
                    else if (REG[instruct1] < REG[instruct2])
                        REG[instruct1] = -1;
                    break;
                case 21:
                    switch (instruct1) {
                        case 0:
                            System.out.println("Closing Program! Trap 0 encountered");
                            System.exit(0);
                            break;
                        case 1:
                            //write integer to standard out
                            int intOutput = REG[3];
                            System.out.print(intOutput);
                            break;
                        case 2:
                            //read an integer from standard in
                            break;
                        case 3:
                            char charOutput = (char) REG[3];
                            System.out.print(charOutput);
                            break;
                        case 4:
                            //read character from standard in
                            break;
                        default:
                            System.out.println("Incorrect value for trap command given: " + instruct1);
                            break;
                    }
                    break;
                case 22: //STR
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    BB.putInt(REG[instruct1], REG[instruct2]);
                    break;
                case 23:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    int indValue = BB.getInt(REG[instruct2]);
                    REG[instruct1] = indValue;
//                    REG[instruct1] = BB.getInt(REG[instruct2]);
                    break;
                case 24:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    BB.put(REG[instruct2], (byte) REG[instruct1]);
                    break;
                case 25:
                    instruct2 = BB.getInt(PC);
                    PC += INT_SIZE;
                    REG[instruct1] = BB.get(REG[instruct2]);
                    break;
                default:
                    System.out.println("Instruction does not exist: " + INSTRUCTIONS.get(opCode + 1));
            }
        }
    }
    //--------------------------------
    //Assembler
    //--------------------------------
    private static void addInstructToMem (String[] instruction, int offset) {
        String indirectReg = "[rR][0-8]"; //"\\([rR][0-8]\\)"
        int instructOpCode = (INSTRUCTIONS.indexOf(instruction[offset]) + 1);
        switch (instructOpCode) {
            //Branch to Label
            case 1: //JMP
                int jmpLocal = SYMBOL_TABLE.get(instruction[1 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(jmpLocal);
                break;
            case 2:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                break;
            case 3: //BNZ
                int bnzLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bnzLocal);
                break;
            case 4:
                int bgtLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bgtLocal);
                break;
            case 5:
                int bltLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bltLocal);
                break;
            case 6:
                int brzLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(brzLocal);
                break;
            case 7:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 8: //LDA
                int addressOfLbl = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(addressOfLbl);
                break;
            case 9: //STR
                if(matches(indirectReg, instruction[2 + offset])) {
//                    int memLocal = REG[instruction[1 + offset]];
                    BB.putInt(instructOpCode + 13);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                } else if(inSymTable(instruction[2 + offset])) {
                    int lblToStrReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                    BB.putInt(instructOpCode);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(lblToStrReg);
                } else {
                    System.out.println("Invalid second value given with STR command");
                }
                break;
            case 10: //LDR
                if(matches(indirectReg, instruction[2 + offset])) {
                    BB.putInt(instructOpCode + 13);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                } else if(inSymTable(instruction[2 + offset])) {
                    int valForReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                    BB.putInt(instructOpCode);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(valForReg);
                } else {
                System.out.println("Invalid second value given with LDR command");
                }
                break;
            case 11: //STB
                if(matches(indirectReg, instruction[2 + offset])) {
                    BB.putInt(instructOpCode + 13);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                } else if(inSymTable(instruction[2 + offset])) {
                    int lblToStrByt = SYMBOL_TABLE.get(instruction[2 + offset]);
                    BB.putInt(instructOpCode);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(lblToStrByt);
                } else {
                    System.out.println("Invalid second value given with STB command");
                }
                break;
            case 12: //LDB
                if(matches(indirectReg, instruction[2 + offset])) {
                    BB.putInt(instructOpCode + 13);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                } else if(inSymTable(instruction[2 + offset])) {
                    int bytForReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                    BB.putInt(instructOpCode);
                    BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                    BB.putInt(bytForReg);
                } else {
                    System.out.println("Invalid second value given with LDB command");
                }
                break;
            case 13:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 14:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(Integer.parseInt(instruction[2 + offset])); //parse the immediate string value
                break;
            case 15: //Sub
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 16:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 17:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 18:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 19:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 20:
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 21:
                int trpValue = Integer.parseInt(instruction[1 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(trpValue);
                break;
            default:
                System.out.println("Instruction does not exist: " + instruction[offset]);
                break;
        }

    }
    //Checks if string is an instruction, if not an instruction then a label
    private static boolean isInstruction (String valueToCheck) {
        for (String instruction : INSTRUCTIONS) {
            if (valueToCheck.toUpperCase().equals(instruction)) {
                return true;
            }
        }
        return false;
    }
    //Checks if the label already exists
    private static boolean inSymTable (String valueToCheck) {
        return (SYMBOL_TABLE.containsKey(valueToCheck));
    }
    //Add label and location to symbol table
    private static void addToSymbolTable(String[] lineToCheck) {
        String label = lineToCheck[0];
        String directive = lineToCheck[1].toUpperCase();
        if (inSymTable(label) && numberOfFilePasses == 0) {
            System.out.println("ERROR: label has already been used: " + label);
            System.exit(0);   // TERMINATE THE PROGRAM
        }
        if(isByte(directive)) {
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            MEM_LOCAL += BYTE_SIZE;

        } else if (isInt(directive)) {
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            MEM_LOCAL += INT_SIZE;
        } else {
            if (PC == 0) {
                PC = MEM_LOCAL;
                //TODO find a better way to set PC start
            }
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            if (lineToCheck[1].equals("TRP") || lineToCheck[1].equals("JMP") || lineToCheck[1].equals("JMR") && lineToCheck.length <= 3) {
                MEM_LOCAL += TRAP_SIZE;
            } else {
                MEM_LOCAL += INSTRUCT_SIZE;
            }
        }
    }
    //check if directive is a byte
    private static boolean isByte (String directive) {
        return directive.equals(BYTE_STRING);
    }
    //check if directive is an int
    private static boolean isInt (String directive) {
        return directive.equals(INT_STRING);
    }
}