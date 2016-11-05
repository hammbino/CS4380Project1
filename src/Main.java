import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

public class Main {

    private final static int MEM_SIZE = 10000;
    private final static int BYTE_SIZE = 1;
    private final static int INT_SIZE = 4;
    private final static int TRAP_SIZE = 8;
    private final static int INSTRUCT_SIZE = 12;
    private static int MEM_LOCAL = 0;
    private final static List<String> INSTRUCTIONS = new ArrayList<>(Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP"));
    private final static String INT_STRING = ".INT";
    private final static String BYTE_STRING = ".BYT";
    private static int [] REG= new int[9];
    private final static List<String> REGISTERS = new ArrayList<>(Arrays.asList("RO", "R1", "R2", "R3", "R4", "R5", "R6", "R7", "R8"));
    private static Map<String, Integer> SYMBOL_TABLE = new HashMap<>();
    private static byte[] DATA = new byte[MEM_SIZE];
    private static ByteBuffer BB = ByteBuffer.wrap(DATA);
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
                        } else if (isInstruction(tokens[1])) {
                            //Method to add instructions
                            addInstructToMem(tokens, 1);
                        }
                    } else if (isInstruction(tokens[0])) {
                        //Call method to add instructions
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
        int endProgram = BB.position();
        while(REG[8] < endProgram) {
            int readInstruction = BB.getInt(REG[8]);
            REG[8] += INT_SIZE;
            int instruct1 = BB.getInt(REG[8]);
            REG[8] += INT_SIZE;
            int instruct2;
            String instructRun = INSTRUCTIONS.get(readInstruction);
            switch (instructRun) {
                case "JMR":
                    break;
                case "BNZ":
                    break;
                case "BGT":
                    break;
                case "BLT":
                    break;
                case "BRZ":
                    break;
                case "MOV":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct2];
                    break;
                case "LDA":
                    break;
                case "STR":
                    break;
                case "LDR":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    int loadReg = BB.getInt(instruct2);
                    REG[instruct1] = loadReg;
                    break;
                case "STB":
                    break;
                case "LDB":
                    instruct2 = (char) BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = (int) DATA[instruct2];
                    break;
                case "ADD":
                    instruct2 = BB.getInt(REG[8]);
                    REG[8] += INT_SIZE;
                    REG[instruct1] = REG[instruct1] + REG[instruct2];
                    break;
                case "ADI":
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
                case "AND":
                    break;
                case "OR":
                    break;
                case "CMP":
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
                default:
                    System.out.println("Instruction does not exist: " + readInstruction);
            }
        }
    }
    //Method to add instruction to memory
    private static void addInstructToMem (String[] instruction, int offset) {
        switch (instruction[offset]) {
            case "JMR":
                break;
            case "BNZ":
                break;
            case "BGT":
                break;
            case "BLT":
                break;
            case "BRZ":
                break;
            case "MOV":
                BB.putInt(INSTRUCTIONS.indexOf("MOV"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case "LDA":
                break;
            case "STR":
                break;
            case "LDR":
                int valForReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(INSTRUCTIONS.indexOf("LDR"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(valForReg);
                break;
            case "STB":
                break;
            case "LDB":
                int bytForReg = SYMBOL_TABLE.get(instruction[2 + offset]);
                BB.putInt(INSTRUCTIONS.indexOf("LDB"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(bytForReg);
                break;
            case "ADD":
                BB.putInt(INSTRUCTIONS.indexOf("ADD"));
                BB.putInt(REGISTERS.indexOf(instruction[1 + offset]));
                BB.putInt(REGISTERS.indexOf(instruction[2 + offset]));
                break;
            case "ADI":
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
            case "AND":
                break;
            case "OR":
                break;
            case "CMP":
                break;
            case "TRP":
                int trpValue = Integer.parseInt(instruction[1 + offset]);
                BB.putInt(INSTRUCTIONS.indexOf("TRP"));
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
        return (SYMBOL_TABLE.containsKey(valueToCheck.toUpperCase()));
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
    //check if directive is a byte
    private static boolean isByte (String directive) {
        return directive.equals(BYTE_STRING);
    }
    //check if directive is an int
    private static boolean isInt (String directive) {
        return directive.equals(INT_STRING);
    }
}