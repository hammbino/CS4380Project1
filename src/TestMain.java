import sun.nio.ch.sctp.SctpNet;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by jeffreyhammond on 11/23/16.
 */
public class TestMain {

    public static void main(String[] args) throws IOException {
        TestMain tester = new TestMain();
        tester.testFirstPass();
        tester.test2FirstPass();
        tester.testTrap4();
    }

    private void testFirstPass() {
        //Setup
        Scanner testScanner = new Scanner("A1    .INT  1");
        //When
        Main.firstPass(testScanner);
        //Then
        Set keys = Main.SYMBOL_TABLE.keySet();
        if (keys.size() == 1 && keys.contains("A1")) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }

    }
    private void test2FirstPass() {
        //Setup
        Scanner testScanner = new Scanner("A5    .INT  5\n" + "A6   .INT  6\n" + "A7   .INT  7\n");
        //When
        Main.firstPass(testScanner);
        //Then
        Set keys = Main.SYMBOL_TABLE.keySet();
        if (keys.size() == 4 && keys.contains("A5") && keys.contains("A6") && keys.contains("A7")) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }

    }  private void testTrap4() {
        //Setup
        //Enter 1 and then type enter
        //When
        Main.inputStack.clear();
        Main.trap4();
        //Then
        if(Main.inputStack.size() == 1) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        if(Main.REG[3] == '1') {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        Main.trap4();
        if(Main.REG[3] == '\n') {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        if(Main.inputStack.empty() ) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        Main.trap4();
        //Enter in 1234
        if(Main.REG[3] == '1') {
            printReg3(Main.REG[3]);
            System.out.println("Passed");
        } else {
            printReg3(Main.REG[3]);
            System.out.println("Failed");
        }
        if(Main.inputStack.size() == 4) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        Main.trap4();
        if(Main.REG[3] == '2') {
            printReg3(Main.REG[3]);
            System.out.println("Passed");
        } else {
            printReg3(Main.REG[3]);
            System.out.println("Failed");
        }
        if(Main.inputStack.size() == 3) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        Main.trap4();
        if(Main.REG[3] == '3') {
            printReg3(Main.REG[3]);
            System.out.println("Passed");
        } else {
            printReg3(Main.REG[3]);
            System.out.println("Failed");
        }
        if(Main.inputStack.size() == 2) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        Main.trap4();
        if(Main.REG[3] == '4') {
            printReg3(Main.REG[3]);
            System.out.println("Passed");
        } else {
            printReg3(Main.REG[3]);
            System.out.println("Failed");
        }
        if(Main.inputStack.size() == 1) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
        Main.trap4();
        if(Main.REG[3] == '\n') {
            printReg3(Main.REG[3]);
            System.out.println("Passed");
        } else {
            printReg3(Main.REG[3]);
            System.out.println("Failed");
        }
        if(Main.inputStack.empty() ) {
            System.out.println("Passed");
        } else {
            System.out.println("Failed");
        }
    }

    private void printReg3 (int reg3) {
        System.out.println((char) Main.REG[3]);
    }
}
