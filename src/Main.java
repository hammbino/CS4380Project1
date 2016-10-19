import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Main {

    private final static int MEM_SIZE = 10000;
    private final static int BYTE_SIZE = 1;
    private final static int INT_SIZE = 4;
    private final static int TRAP_SIZE = 8;
    private final static int INSTRUCT_SIZE = 12;
    private static int MEM_LOCAL = 0;
//    private static int PC_SET = 0;
    private final static List<String> INSTRUCTIONS = new ArrayList<>(Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP"));
//    private final static String [] INSTRUCTIONS = new String[] {"JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP"};
    private final static String INT_STRING = ".INT";
    private final static String BYTE_STRING = ".BYT";
    private static int [] REG= new int[9];
    private final static List<String> REGISTERS = new ArrayList<>(Arrays.asList("RO", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8"));
    private static Map<String, Integer> SYMBOL_TABLE = new HashMap<>();
    private static byte[] DATA = new byte[MEM_SIZE];
    private static ByteBuffer BB = ByteBuffer.wrap(DATA);
    private static int END_PROGRAM;

    public static void main(String[] args) throws IOException {
        Scanner fileReader = null;

        //check to see if the program was run with the command line argument
        if (args.length < 1) {
            System.out.println("Error: No file was provided.");
            System.exit(0);     // TERMINATE THE PROGRAM
        }

        //flag for number of passes
        int numberOfFilePasses = 0;

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
                System.out.println("You made it to the FIRST pass");
                //read each line from the file
                while (fileReader.hasNextLine()) {
                    String[] fileInput = fileReader.nextLine().trim().split("\\s+");
                    if (!isInstruction(fileInput[0])) {
                        addToSymbolTable(fileInput);
                    }
                }
            }
            //--------------------------------
            //check if it is the second pass
            //--------------------------------
            if (numberOfFilePasses == 1) {
                System.out.println("You made it to the SECOND pass");
                while (fileReader.hasNextLine()) {
                    String[] tokens = fileReader.nextLine().replaceAll(";.*", " ").trim().split("\\s+");
                    if (SYMBOL_TABLE.containsKey(tokens[0])) {
                        if( isInt(tokens[1])) {
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
//
//                            if(tokens[2].equals("'\\n'")) {
//                                char NL = (char) 10;
//                                BB.put((byte) NL);
//                            } else if(tokens[2] .equals("'space'")) {
//                                char SP = (char) 32;
//                                BB.put((byte) SP);
//                            } else {
//                                BB.put((byte) tokens[2].charAt(1));
//                            }
                        } else if (isInstruction(tokens[1])) {
                            //Method to add instructions
                            addInstructToMem(tokens, 1);
                        }
                    } else if (isInstruction(tokens[0])) {
                        //Call method to add intructions
                        addInstructToMem(tokens, 0);
                    }
                }
            }
            fileReader.close();
            numberOfFilePasses++;
        }
        //--------------------------------
        //Virtual Machine
        //--------------------------------
        END_PROGRAM = BB.position();
        while(REG[8] < END_PROGRAM) {
            int readInstruction = BB.getInt(REG[8]);
            REG[8] += INT_SIZE;
            int instruct1 = BB.getInt(REG[8]);
            REG[8] += INT_SIZE;
            int instruct2;
            String instructRun = INSTRUCTIONS.get(readInstruction);
            switch (instructRun) {
                case "ADD":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct1] + REG[instruct2];
                    break;
                case "SUB":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct1] - REG[instruct2];
                    break;
                case "MUL":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct1] * REG[instruct2];
                    break;
                case "DIV":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct1] / REG[instruct2];
                    break;
                case "LDB":
                    instruct2 = (char) BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = (int) DATA[instruct2];
                    break;
                case "TRP":
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
                case "LDR":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    int loadReg = BB.getInt(instruct2);
                    REG[instruct1] = loadReg;
                    break;
                case "MOV":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct2];
                    break;
                default:
                    System.out.println("Instruction does not exist: " + readInstruction);
            }
        }
    }

    private static void addInstructToMem (String[] instruction, int offset) {
        switch (instruction[offset]) {
            case "ADD":
                BB.putInt(INSTRUCTIONS.indexOf("ADD"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case "SUB":
                BB.putInt(INSTRUCTIONS.indexOf("SUB"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case "MUL":
                BB.putInt(INSTRUCTIONS.indexOf("MUL"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case "DIV":
                BB.putInt(INSTRUCTIONS.indexOf("DIV"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case "LDB":
                int bytForReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(INSTRUCTIONS.indexOf("LDB"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bytForReg);
                break;
            case "TRP":
                int trpValue = Integer.parseInt(instruction[1 + offset]);
                BB.putInt(INSTRUCTIONS.indexOf("TRP"));
                BB.putInt(trpValue);
                break;
            case "LDR":
                int valForReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(INSTRUCTIONS.indexOf("LDR"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(valForReg);
                break;
            case "MOV":
                BB.putInt(INSTRUCTIONS.indexOf("MOV"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            default:
                System.out.println("Instruction does not exist: " + instruction[offset]);
                break;
        }

    }

    private static boolean isInstruction (String valueToCheck) {
        for (String instruction : INSTRUCTIONS) {
            if (valueToCheck.toUpperCase().equals(instruction)) {
                return true;
            }
        }
        return false;
    }

    private static void addToSymbolTable(String[] lineToCheck) {
        String label = lineToCheck[0];
        String directive = lineToCheck[1].toUpperCase();
        if(isByte(directive)) {
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            MEM_LOCAL += BYTE_SIZE;

        } else if (isInt(directive)) {
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            MEM_LOCAL += INT_SIZE;
        } else {
            if (REG[8] == 0) {
                REG[8] = MEM_LOCAL;
            }

            SYMBOL_TABLE.put(label, MEM_LOCAL);

            if (lineToCheck[0].equals("TRP") || lineToCheck[1].equals("TRP") && lineToCheck.length <= 3) {
                MEM_LOCAL += TRAP_SIZE;
            } else {
                MEM_LOCAL += INSTRUCT_SIZE;
            }
        }
    }

//    private static void addCharOrBytToMem (String[] lineToCheck) {
//        String directive = lineToCheck[1].toUpperCase();
//        if(isByte(directive)) {
//
//        } else if (isInt(directive)) {
//
//        }
//
//    }

    private static boolean isByte (String directive) {
        return directive.equals(BYTE_STRING);
    }

    private static boolean isInt (String directive) {
        return directive.equals(INT_STRING);
    }
}


//        char a = 'a';
//        char b = 'b';
//        char c = 'c';
//        char z = 'z';
//
//        int one = 1;
//        int two = 2;
//        int oneHundred = 100;
//        int oneTwentyEight = 128;
//        int twoFiftySix = 256;
//        int twoFiftySeven = 257;
//
//        bb.put((byte) a);
//        bb.put((byte) b);
//        bb.put((byte) c);
//        bb.put((byte) z);
//        bb.putInt(one);
//        bb.putInt(two);
//        bb.putInt(oneHundred);
//        bb.putInt(oneTwentyEight);
//        bb.putInt(twoFiftySix);
//        bb.putInt(twoFiftySeven);
//
//
//        System.out.println((char) bb.get(0));
//        System.out.println((char) bb.get(1));
//        System.out.println((char) bb.get(2));
//        System.out.println((char) bb.get(3));
//        System.out.println(bb.getInt(4));
//        System.out.println(bb.getInt(8));
//        System.out.println(bb.getInt(12));
//        System.out.println(bb.getInt(16));
//        System.out.println(bb.getInt(20));
//        System.out.println(bb.getInt(24));
//    }
//
//    private static byte[] addIntToByteArray (int i) {
//        final ByteBuffer bb = ByteBuffer.allocate(Integer.SIZE / Byte.SIZE);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        bb.putInt(i);
//        return bb.array();
//    }
//
//    public static int getIntFromByteArray (byte[] b) {
//        final ByteBuffer bb = ByteBuffer.wrap(b);
//        bb.order(ByteOrder.LITTLE_ENDIAN);
//        return bb.getInt();
//    }
//}