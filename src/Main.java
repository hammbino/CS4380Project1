import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import static java.util.regex.Pattern.matches;

public class Main {
    private final static int MEM_SIZE = 10000;
    private final static int BYTE_SIZE = 1;
    private final static int INT_SIZE = 4;
//    private final static int TRAP_SIZE = 8;
//    private final static int JUMP_SIZE = 8;
    private final static int INSTRUCT_SIZE = 12;
    private final static int NUM_REGISTERS = 13;
    private final static int NUM_THREADS = 5;
    private final static List<String> INSTRUCTIONS = Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP", "RUN", "END", "BLK", "LCK", "ULK");
    private final static String INT_STRING = ".INT";
    private final static String BYTE_STRING = ".BYT";
    private final static List<String> REGISTERS = Arrays.asList("R0", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "PC", "SL", "SP", "FP", "SB");
    private final static int [] REG= new int[NUM_REGISTERS];
    private final static Map<String, Integer> SYMBOL_TABLE = new HashMap<>();
    private final static byte[] DATA = new byte[MEM_SIZE];
    private final static ByteBuffer BB = ByteBuffer.wrap(DATA).order(ByteOrder.LITTLE_ENDIAN);
    private static int MEM_LOCAL = 0;
    private static int numberOfFilePasses = 0;
    private static Stack<Character> inputStack = new Stack<>();
    private static int mutex = -1;
    private final static int [] THREADS= new int[] {0, 0, 0, 0, 0};
    private static int threadNum = 0;
    private static int THREAD_STACK_SIZE;
    private static int currentThreadID = 0;
    private static int endProgram;
    private static Queue<Integer> queue = new LinkedList<>();
    private static boolean endFlag = true;
    private static int SB;

    public static void main(String[] args) throws Exception {
        Scanner fileReader = null;
        Scanner input = new Scanner(System.in);
        //check to see if the program was run with the command line argument
        if (args.length < 1) {
            System.out.println("Error: No file was provided.");
            System.exit(9);     // TERMINATE THE PROGRAM
        }

        SB = (MEM_SIZE - INT_SIZE);
        REG[REGISTERS.indexOf("SB")] = (MEM_SIZE - INT_SIZE); //To account for BB starting a 0
        REG[REGISTERS.indexOf("SP")] = (MEM_SIZE - INT_SIZE);
        REG[REGISTERS.indexOf("FP")] = (MEM_SIZE - INT_SIZE);

        while (numberOfFilePasses < 2) {
            //check to see if a scanner can be created using the file that was input
            try {
                fileReader = new Scanner(new FileInputStream(args[0])).useDelimiter("(\\n|\\r|;)");
            } catch (FileNotFoundException x) {
                System.out.println("ERROR: Unable to open file " + args[0]);
                x.printStackTrace();
                System.exit(7);   // TERMINATE THE PROGRAM
            }
            firstPass(fileReader);

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
                            //Call method to add instructions
                            addInstructToMem(tokens, 1);
                        }
                    } else if (isInstruction(tokens[0])) {
                        //Call method to add instructions
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
                if (REG[REGISTERS.indexOf("PC")] == 0) {
                    System.out.println("ERROR: No instructions were given");
                    System.exit(0);   // TERMINATE THE PROGRAM
                }
                REG[REGISTERS.indexOf("SL")] = (MEM_SIZE - BB.remaining());
                THREAD_STACK_SIZE = (MEM_SIZE - BB.remaining())/NUM_THREADS;
                endProgram = BB.position();
                createThreadStack(currentThreadID, REG[REGISTERS.indexOf("PC")]);
                getRegisters();
            }
            fileReader.close();
            numberOfFilePasses++;
        }
        //--------------------------------
        //Virtual Machine
        //--------------------------------

        while(REG[REGISTERS.indexOf("PC")] < endProgram) {
            int opCode = BB.getInt(REG[REGISTERS.indexOf("PC")]);
            REG[REGISTERS.indexOf("PC")] += INT_SIZE;
            int instruct1 = BB.getInt(REG[REGISTERS.indexOf("PC")]);
            REG[REGISTERS.indexOf("PC")] += INT_SIZE;
            int instruct2 = BB.getInt(REG[REGISTERS.indexOf("PC")]);
            REG[REGISTERS.indexOf("PC")] += INT_SIZE;
            switch (opCode) {
                //Branch to Label
                case 1: //JMP
                    REG[REGISTERS.indexOf("PC")] = instruct1;
                    break;
                //Branch to address in source register
                case 2: //JMR
                    REG[REGISTERS.indexOf("PC")] = REG[instruct1];
                    break;
                //Branch to Label if source register is not zero
                case 3: //BNZ
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] != 0) {
                        REG[REGISTERS.indexOf("PC")] = instruct2;
                    }
                    break;
                //Branch to Label if source register is greater than zero
                case 4: //BGT
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] > 0) {
                        REG[REGISTERS.indexOf("PC")] = instruct2;
                    }
                    break;
                //Branch to Label if source register is greater than zero
                case 5: //BLT
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] < 0) {
                        REG[REGISTERS.indexOf("PC")] = instruct2;
                    }
                    break;
                case 6: //BRZ
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] == 0) {
                        REG[REGISTERS.indexOf("PC")] = instruct2;
                    }
                    break;
                //Move data from source register to destination register
                case 7: //MOV
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (instruct1 != REGISTERS.indexOf("PC")) {
                        REG[instruct1] = REG[instruct2];
                    } else {
                        System.out.println("Can not change value of the Program Counter with MOV instruction");
                    }
                    break;
                //Load the Address of the label into the RD register.
                case 8: //LDA
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = instruct2;
                    break;
                case 9: //STR
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    BB.putInt(instruct2, REG[instruct1]);
                    break;
                //Load destination register with data from Mem
                case 10: //LDR
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = BB.getInt(instruct2);
                    break;
                case 11: //STB
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    BB.put(instruct2, (byte) REG[instruct1]);
                    break;
                case 12: //LDB
//                    instruct2 = (byte) BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = (int) DATA[instruct2];
                    break;
                case 13: //ADD
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] + REG[instruct2];
                    break;
                case 14: //ADI
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] + instruct2;
                    break;
                case 15: //SUB
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] - REG[instruct2];
                    break;
                case 16: //MUL
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] * REG[instruct2];
                    break;
                case 17: //DIV
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = REG[instruct1] / REG[instruct2];
                    break;
                case 18: //AND
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] > 0 && REG[instruct2] > 0) {
                        REG[instruct1] = 1;
                    } else {
                        REG[instruct1] = 1;
                    }
                    break;
                case 19: //OR
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] > 0 || REG[instruct2] > 0) {
                        REG[instruct1] = 1;
                    } else {
                        REG[instruct1] = 1;
                    }
                    break;
                case 20: //CMP
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    if (REG[instruct1] == REG[instruct2])
                        REG[instruct1] = 0;
                    else if (REG[instruct1] > REG[instruct2])
                        REG[instruct1] = 1;
                    else if (REG[instruct1] < REG[instruct2])
                        REG[instruct1] = -1;
                    break;
                case 21: //TRP
                    switch (instruct1) {
                        case 0:
//                            System.out.println("\n\nClosing Program! Trap 0 encountered");
                            System.exit(0);
                            break;
                        case 1:
                            //write integer to standard out
                            int intOutput = REG[3];
                            System.out.print(intOutput);
                            break;
                        case 2:
                            //read an integer from standard in
                            int num = input.nextInt();
                            REG[3] = num;
                            break;
                        case 3:
                            char charOutput = (char) REG[3];
                            System.out.print(charOutput);
                            break;
                        case 4:
                            trap4();
                            break;
                        case 99:
                            System.out.println("TRP 99 " + instruct2);
                            break;
                        default:
                            System.out.println("Incorrect value for trap command given: " + instruct1);
                            break;
                    }
                    break;
                //Store data at register location from source register RS, RG
                case 22: //STR
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    BB.putInt(REG[instruct2], REG[instruct1]);//putInt(int index, int value)
                    break;
                //Load destination register with data at register location RD, RG
                case 23: //LDR
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    int indValue = BB.getInt(REG[instruct2]);
                    REG[instruct1] = indValue;
                    break;
                case 24: //STB
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    BB.put(REG[instruct2], (byte) REG[instruct1]);
                    break;
                case 25: //LDB
//                    instruct2 = BB.getInt(PC);
//                    PC += INT_SIZE;
                    REG[instruct1] = BB.get(REG[instruct2]);
                    break;
                case 26: //RUN
                    threadNum++;
                    //putting this in to possibly recreate threads once thread has ended
                    if (THREADS[threadNum] == 0 && threadNum < NUM_THREADS) {
                        THREADS[threadNum] = threadNum;
                    }
                    if (threadNum >= NUM_THREADS) {
                        System.out.println("To many threads created");
                        throw new Exception("To many threads created");
                    }
                    saveRegisters(currentThreadID);
                    REG[instruct1] = threadNum;
                    createThreadStack(threadNum, instruct2);
                    getRegisters();
                    break;
                case 27: //END
                    if (currentThreadID != 0) {
                        endFlag = false;
                    }
                    break;
                case 28: //BLK
                    if (currentThreadID == 0 && queue.size() > 0) {
                        REG[REGISTERS.indexOf("PC")] =  REG[REGISTERS.indexOf("PC")] - 12;
                    }
                    if (queue.size() < 0) {
                        System.out.println("problem with Queue.");
                    }
                    break;
                case 29: //LCK
                    if(mutex == -1) {
                        mutex = currentThreadID;
                        BB.putInt(instruct1, currentThreadID);//putInt(int index, int value)

                    }
                    else if (mutex != currentThreadID){
                        REG[REGISTERS.indexOf("PC")] =  REG[REGISTERS.indexOf("PC")] - 12;
                    }
                    break;
                case 30: //ULK
                    if(mutex == currentThreadID) {
                        mutex = -1;
                        BB.putInt(instruct1, -1);//putInt(int index, int value)
                    }
                    break;
                default:
                    System.out.println("Instruction does not exist: " + INSTRUCTIONS.get(opCode + 1));
                    break;
            }
            if (endFlag)
                saveRegisters(currentThreadID);
            getRegisters();
        }
    }

    //--------------------------------
    //Assembler
    //--------------------------------
    private static void addInstructToMem (String[] instruction, int offset) {
        String indirectReg = "^[rR][0-8]$|^[S][L]$|^[S][P]$|^[F][P]$|^[S][B]$";
        int instructOpCode = (INSTRUCTIONS.indexOf(instruction[offset]) + 1);
        switch (instructOpCode) {
            //Branch to Label
            case 1: //JMP
                int jmpLocal = SYMBOL_TABLE.get(instruction[1 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(jmpLocal);
                BB.putInt(0);
                break;
            case 2: //JMR
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(0);
                break;
            case 3: //BNZ
                int bnzLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bnzLocal);
                break;
            case 4: //BGT
                int bgtLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bgtLocal);
                break;
            case 5: //BLT
                int bltLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bltLocal);
                break;
            case 6: //BRZ
                int brzLocal = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(brzLocal);
                break;
            case 7: //MOV
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
            case 13: //ADD
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 14: //ADI
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(Integer.parseInt(instruction[2 + offset])); //parse the immediate string value
                break;
            case 15: //SUB
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 16: //MUL
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 17: //DIV
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 18: //AND
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 19: //OR
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 20: //CMP
                BB.putInt(instructOpCode);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case 21: //TRP
                int trpValue = Integer.parseInt(instruction[1 + offset]);
                BB.putInt(instructOpCode);
                BB.putInt(trpValue);
                if (instruction.length > 2 && !inSymTable(instruction[0])) {
                    BB.putInt(Integer.parseInt(instruction[2 + offset]));
                } else {
                    BB.putInt(0);
                }
                break;
            case 22: //RUN
                //RUN REG, LABEL
                BB.putInt(instructOpCode + 4);
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(SYMBOL_TABLE.get(instruction[2 + offset]));
                break;
            case 23: //END
                //END
                instructOpCode += 4;
                BB.putInt(instructOpCode);
                BB.putInt(0);
                BB.putInt(0);
                break;
            case 24: //BLK
                //BLK
                instructOpCode += 4;
                BB.putInt(instructOpCode);
                BB.putInt(0);
                BB.putInt(0);
                break;
            case 25: //LCK
                //LCK LABEL\
                instructOpCode += 4;
                BB.putInt(instructOpCode);
                BB.putInt(SYMBOL_TABLE.get(instruction[1 + offset]));
                BB.putInt(0);
                break;
            case 26: //ULK
                //ULK LABEL
                instructOpCode += 4;
                BB.putInt(instructOpCode);
                BB.putInt(SYMBOL_TABLE.get(instruction[1 + offset]));
                BB.putInt(0);
                break;
            default:
                System.out.println("Instruction does not exist: " + instruction[offset]);
                break;
        }

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
            SYMBOL_TABLE.put(label, MEM_LOCAL);
//            if (lineToCheck[1].equals("TRP") || lineToCheck[1].equals("JMP") || lineToCheck[1].equals("JMR") && lineToCheck.length <= 3) {
//                MEM_LOCAL += TRAP_SIZE;
//            } else {
            MEM_LOCAL += INSTRUCT_SIZE;
//            }
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

    //check if directive is a byte
    private static boolean isByte (String directive) {
        return directive.equals(BYTE_STRING);
    }

    //check if directive is an int
    private static boolean isInt (String directive) {
        return directive.equals(INT_STRING);
    }

    //First Pass
    private static void firstPass(Scanner fileReader) {
        //------------------------------
        //check if it is the first pass
        //------------------------------
        if (numberOfFilePasses == 0) {
            //read each line from the file
            while (fileReader.hasNextLine()) {
                String[] fileInput = fileReader.nextLine().replaceAll(";.*", " ").trim().split("\\s+");
                if (!fileInput[0].isEmpty() && (isInstruction(fileInput[0]) || isInstruction(fileInput[1])) && REG[8] == 0) {
                    REG[REGISTERS.indexOf("PC")] = MEM_LOCAL;
                }
                if (!isInstruction(fileInput[0]) && !isInt(fileInput[0]) && !isByte(fileInput[0]) && !fileInput[0].isEmpty()) {
                    addToSymbolTable(fileInput);
                } else if (isInt(fileInput[0])) {
                    MEM_LOCAL += INT_SIZE;
                } else if (isByte(fileInput[0])) {
                    MEM_LOCAL += BYTE_SIZE;
                } else if (isInstruction(fileInput[0])) {
//                        if (fileInput[0].equals("TRP") || fileInput[0].equals("JMP") || fileInput[0].equals("JMR") && fileInput.length <= 3) {
//                            MEM_LOCAL += TRAP_SIZE;
//                        } else {
                    MEM_LOCAL += INSTRUCT_SIZE;
//                        }
                }
            }
        }
    }

    //Trap 4 works like getChar()
    private static void trap4() {
        if (inputStack.empty()) {
            inputStack.push('\n');
            Scanner inputScanner = new Scanner(System.in);
            char[] dataCharArray = inputScanner.nextLine().toCharArray();

            for(int counter = (dataCharArray.length -1); counter >= 0; counter--){
                inputStack.push(dataCharArray[counter]);
            }
        }
        try {
            REG[3] = inputStack.pop();
        } catch (EmptyStackException e) {
            System.out.println("empty stack");
        }
    }

    private static void createThreadStack(int threadID, int lblLocal) {
        int tsInitializer = (SB - (THREAD_STACK_SIZE * threadID));
        int curThreadSL = (SB - (THREAD_STACK_SIZE * threadID) - THREAD_STACK_SIZE);
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R0")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R1")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R2")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R3")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R4")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R5")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R6")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R7")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, lblLocal);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, curThreadSL);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, (tsInitializer - 12));
        tsInitializer -= 4;
        BB.putInt(tsInitializer, (tsInitializer - 8));
        tsInitializer -= 4;
        BB.putInt(tsInitializer, (tsInitializer - 4));
        queue.add(threadID);
    }

    private static void saveRegisters(int threadID) {
        int tsInitializer = (SB - (THREAD_STACK_SIZE * threadID));
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R0")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R1")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R2")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R3")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R4")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R5")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R6")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("R7")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("PC")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("SL")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("SP")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("FP")]);
        tsInitializer -= 4;
        BB.putInt(tsInitializer, REG[REGISTERS.indexOf("SB")]);
        queue.add(threadID);
    }

    private static void getRegisters() {
        currentThreadID = queue.remove();
        endFlag = true;
        int tsGetter = (SB - (THREAD_STACK_SIZE * currentThreadID));
        REG[REGISTERS.indexOf("R0")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R1")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R2")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R3")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R4")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R5")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R6")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("R7")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("PC")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("SL")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("SP")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("FP")] = BB.getInt(tsGetter);
        tsGetter -= 4;
        REG[REGISTERS.indexOf("SB")] = BB.getInt(tsGetter);
    }
}