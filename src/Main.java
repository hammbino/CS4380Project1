import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class Main {

    private final static int MEM_SIZE = 100000;
    private final static int CHAR_SIZE = 1;
    private final static int BYTE_SIZE = 4;
    final static int INSTRUCT_SIZE = 12;
    private final static List<String> INSTRUCTIONS = new ArrayList<>(Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP"));
//    private final static String [] DIRECTIVES = new String [] {".INT", ".BYT"};//Todo delete line
    private final static String intString = ".INT";
    private final static String bytString = ".BYT";
    private static int R0 = 0, R1 = 0, R2 = 0, R3 = 0, R4 = 0, R5 = 0, R6 = 0, R7 = 0, PC = 0;
    private static Map<String, Integer> SYMBOL_TABLE = new HashMap<>();

    public static void main(String[] args) throws IOException {
        Scanner fileReader = null;
        //Declare memory array
        byte[] data = new byte[MEM_SIZE];
        ByteBuffer bb = ByteBuffer.wrap(data);
        bb.order(ByteOrder.nativeOrder());

        //check to see if the program was run with the command line argument
        if (args.length < 1) {
            System.out.println("Error: No file was provided.");
            System.exit(0);     // TERMINATE THE PROGRAM
        }

        //flag for number of passes
        int numberOfFilePasses = 0;

        while(numberOfFilePasses < 2) {
            //check to see if a scanner can be created using the file that was input
            try {
                fileReader = new Scanner(new FileInputStream(args[0])).useDelimiter("\\n|;");
            } catch (FileNotFoundException x) {
                System.out.println("ERROR: Unable to open file " + args[0]);
                x.printStackTrace();
                System.exit(0);   // TERMINATE THE PROGRAM
            }
            //read each line from the file
            while (fileReader.hasNextLine()) {
                String[] fileInput = fileReader.nextLine().split("\\s+");
                //check if it is the first pass
                if(numberOfFilePasses == 0) {
                    if (!isInstruction(fileInput[0])) {
                        addToSymbolTable(fileInput);
                    }
                //check if it is the second pass
                } else if(numberOfFilePasses == 1) { //Todo Remove else and make this an if
//                    System.out.println("You made it to the second pass."); //Todo delete line
                    //check if first word in line is an instruction
                    if (isInstruction(fileInput[1])) {
                        for(String word : fileInput) {
                            System.out.print(word + " ");
                        }
                        System.out.println();
                    }
                //check if it is the third or more pass.
                } else { //Todo remove else
                    System.out.println("You made it to a third pass in error");
                    System.exit(1);
                }
            }
            fileReader.close();
            numberOfFilePasses++;
        }

        SYMBOL_TABLE.forEach((k,v)->System.out.println("Item : " + k + " Count : " + v));
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
            SYMBOL_TABLE.put(label, PC);
            PC += CHAR_SIZE;

        } else if (isInt(directive)) {
            SYMBOL_TABLE.put(label, PC);
            PC += BYTE_SIZE;
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
