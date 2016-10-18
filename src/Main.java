import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {

    private final static int MEM_SIZE = 100000;
    private final static int BYTE_SIZE = 1;
    private final static int INT_SIZE = 4;
    private final static int TRAP_SIZE = 8;
    private final static int INSTRUCT_SIZE = 12;
    private static int MEM_LOCAL = 0;
    private static int PC_SET = 0;
    private final static List<String> INSTRUCTIONS = new ArrayList<>(Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP"));
    private final static String intString = ".INT";
    private final static String bytString = ".BYT";
    private static int R0 = 0, R1 = 0, R2 = 0, R3 = 0, R4 = 0, R5 = 0, R6 = 0, R7 = 0, PC = 0;
    private static Map<String, Integer> SYMBOL_TABLE = new HashMap<>();
    private static byte[] DATA = new byte[MEM_SIZE];
    private static ByteBuffer BB = ByteBuffer.wrap(DATA);

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
                //read each line from the file
                while (fileReader.hasNextLine()) {
                    String[] fileInput = fileReader.nextLine().trim().split("\\s+");
                    if (!isInstruction(fileInput[0])) {
                        addToSymbolTable(fileInput);
                    }
                }
            //--------------------------------
            //check if it is the second pass
            //--------------------------------
            } else if (numberOfFilePasses == 1) { //Todo Remove else and make this an if
                while (fileReader.hasNextLine()) {
                    String[] fileInput = fileReader.nextLine().replaceAll(";.*", " ").trim().split("\\s+");
                    if (isInstruction(fileInput[0]) || isInstruction(fileInput[1])) {
                        for (String word : fileInput) {
                            System.out.print(word + " ");
                        }
                        System.out.println();
                    }
                }


                //check if first word in line is an instruction
                System.out.println("You made it to the second pass");
                //check if it is the third or more pass.
            } else { //Todo remove else
                System.out.println("You made it to a third pass in error");
                System.exit(1);
            }
            fileReader.close();
            numberOfFilePasses++;
        }

        SYMBOL_TABLE.forEach((k, v) -> System.out.println("Item : " + k + " Count : " + v));
        System.out.println(PC);
//TODO delete code block
//        for (Map.Entry<String, Integer> entry : SYMBOL_TABLE.entrySet()) {
//            System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
//        }
    }

    private static boolean isInstruction (String valueToCheck) {
        for (String instruction : INSTRUCTIONS) {
            if (valueToCheck.toUpperCase().equals(instruction)) {
                return true;
            }
        }
        return false;
    }

    private static void addToMemory(String[] lineToAdd){
        //check second item if directive or instruction

        //if directive add byte or char to memory

        //if instruction and not a Trp add instruction and data in register and/or data from hash map (should take up 12 bytes)

        //if trap add instruction and single register to memory a 4 bytes each (should take up 8 bytes)
    }

    private static void addToSymbolTable(String[] lineToCheck) {
        String label = lineToCheck[0];
        String directive = lineToCheck[1].toUpperCase();
        if(isChar(directive)) {
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            MEM_LOCAL += BYTE_SIZE;

        } else if (isInt(directive)) {
            SYMBOL_TABLE.put(label, MEM_LOCAL);
            MEM_LOCAL += INT_SIZE;
        } else {
            if (PC == 0) {
                PC = MEM_LOCAL;
            }

            SYMBOL_TABLE.put(label, MEM_LOCAL);

            if (lineToCheck.length == 2) {
                MEM_LOCAL += TRAP_SIZE;
            } else {
                MEM_LOCAL += INSTRUCT_SIZE;
            }
        }
    }

    private static void addCharOrBytToMem (String[] lineToCheck) {
        String directive = lineToCheck[1].toUpperCase();
        if(isChar(directive)) {

        } else if (isInt(directive)) {

        }

    }

    private static boolean isChar (String directive) {
        return directive.equals(bytString);
    }

    private static boolean isInt (String directive) {
        return directive.equals(intString);
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
