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
    private final static List<String> INSTRUCTIONS = new ArrayList<String>(Arrays.asList("JMP", "JMR", "BNZ", "BGT", "BLT", "BRZ", "MOV", "LDA", "STR", "LDR", "STB", "LDB", "ADD", "ADI", "SUB", "MUL", "DIV", "AND", "OR", "CMP", "TRP"));
    private final static String [] DIRECTIVES = new String [] {".INT", ".BYT"};
    static int R0 = 0, R1 = 0, R2 = 0, R3 = 0, R4 = 0, R5 = 0, R6 = 0, R7 = 0, PC = 0;
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

        //check to see if a scanner can be created using the file that was input
        try {
            fileReader = new Scanner(new FileInputStream(args[0])).useDelimiter("\\n|;");
        } catch (FileNotFoundException x) {
            System.out.println("ERROR: Unable to open file " + args[0]);
            x.printStackTrace();
            System.exit(0);   // TERMINATE THE PROGRAM
        }

        while (fileReader.hasNextLine()) {
            String[] result = fileReader.nextLine().split("\\s+");
            if(isLabel(result[0])) {
                addToSymbolTable(result);
            }
        }
        fileReader.close();

//        SYMBOL_TABLE.forEach((k,v)->System.out.println("Item : " + k + " Count : " + v));

        for (Map.Entry<String, Integer> entry : SYMBOL_TABLE.entrySet()) {
            System.out.println("Item : " + entry.getKey() + " Count : " + entry.getValue());
        }

    }


    private static boolean isLabel (String valueToCheck) {
        for (String instruction : INSTRUCTIONS) {
            if (valueToCheck.toUpperCase().equals(instruction)) {
                return false;
            }
        }
        return true;
    }

    private static void addToSymbolTable(String[] label) {
        String directive = label[1].toUpperCase();
        if(isChar(directive)) {
            SYMBOL_TABLE.put(label[0], PC);
            PC += CHAR_SIZE;

        } else if (isInt(directive)) {
            SYMBOL_TABLE.put(label[0], PC);
            PC += BYTE_SIZE;
        }
    }

    private static boolean isChar (String directive) {
        return directive.equals(DIRECTIVES[1]);
    }

    private static boolean isInt (String directive) {
        return directive.equals(DIRECTIVES[0]);
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
