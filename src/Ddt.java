import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;

public class Ddt {
    //attributes
    int[] output;
    int[][] table;
    String name;

    //constructor
    public Ddt(int[] output, String name){
        this.output = output;
        this.name = name;

        //create empty 16x16 array
        table = new int[16][16];
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                table[i][j] = 0;
            }
        }
    }

    //methods
    /*
    * only done for 4 bit input so 0-15
    * */
    public static String intToBit(int hex){
        String bit = "";
        switch(hex){
            case 0:
                bit = "0000";
                break;
            case 1:
                bit = "0001";
                break;
            case 2:
                bit = "0010";
                break;
            case 3:
                bit = "0011";
                break;
            case 4:
                bit = "0100";
                break;
            case 5:
                bit = "0101";
                break;
            case 6:
                bit = "0110";
                break;
            case 7:
                bit = "0111";
                break;
            case 8:
                bit = "1000";
                break;
            case 9:
                bit = "1001";
                break;
            case 10:
                bit = "1010";
                break;
            case 11:
                bit = "1011";
                break;
            case 12:
                bit = "1100";
                break;
            case 13:
                bit = "1101";
                break;
            case 14:
                bit = "1110";
                break;
            case 15:
                bit = "1111";
                break;
            default:
                System.out.println("Error hex input invalid: " + hex);
                break;
        }
        return bit;
    }

    /*
    * converts bit to int
    * */
    public static int bitToInt(String bit){
        int result = Integer.parseInt(bit, 2);
        return result;
    }

    /*XOR operation for 4 bit inputs*/
    public static String bitXOR(String b1, String b2){
        String result = "";
        for(int i = 0; i < 4; i++){
            if(b1.charAt(i) == b2.charAt(i)){
                result += "0";
            } else {
                result += "1";
            }
        }
        return result;
    }

    public static int delta(int a, int b){
        //convert a and b to bit strings
        String s1 = intToBit(a);
        String s2 = intToBit(b);

        //calculate delta x
        String xor = bitXOR(s1,s2);
        int delta = bitToInt(xor);

        return delta;
    }

    /*calculates the deltaX and deltaY difference and updates difference distribution table*/
    public void ioDifference(int x1, int x2){
        //compute delta X
        int deltaX = delta(x1,x2);

        //fetch y1 and y2
        int y1 = this.output[x1];
        int y2 = this.output[x2];

        //compute delta Y
        int deltaY = delta(y1,y2);

        //update table
        this.table[deltaX][deltaY] += 1;
    }


    /*creates difference distribution table*/
    public void calculateDDT(){
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                this.ioDifference(i,j);
            }
        }
    }

    public static String intToHex(int a){
        if(a < 10){
            return Integer.toString(a);
        } else {
            switch(a){
                case 10:
                    return "A";
                case 11:
                    return "B";
                case 12:
                    return "C";
                case 13:
                    return "D";
                case 14:
                    return "E";
                case 15:
                    return "F";
                default:
                    System.out.println("Invalid hex value passed");
                    return "Error";
            }
        }
    }

    public static String toEqualWhiteSpace(int a){

        if(a < 9){
            return " " + a + " ";
        } else {
            return " " + a;
        }

    }

    /*Prints out difference distribution table*/
    public void printDDT(){
        System.out.println("\n\nDifference Distribution table for " + this.name);
        System.out.println("Output Difference");

        String output = "";
        for(int i = 0; i < 16; i++){
            if(i == 0){
                output += "    0  1  2  3  4  5  6  7  8  9  A  B  C  D  E  F";
                output += "\n__________________________________________________";
            }
            for(int j = 0; j < 16; j++){
                if(j == 0){
                    output+= "\n" + intToHex(i) + " |";
                }
                output+= toEqualWhiteSpace(table[i][j]);
            }
        }

        System.out.println(output);
    }

    public static void main(String[] args) throws FileNotFoundException {

        //generate sbox data
        int[] sbox1Data = new int[]{6,12,3,8,14,5,11,1,2,4,13,7,0,10,15,9};
        int[] sbox2Data = new int[]{10,14,15,11,6,8,3,13,7,9,2,12,1,0,4,5};
        int[] sbox3Data = new int[]{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8};
        int[] sbox4Data = new int[]{15,9,7,0,10,13,2,4,3,6,12,5,1,8,14,11};
        int[] sbox5Data = new int[]{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9};
        int[] sbox6Data = new int[]{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11};
        int[] sbox7Data = new int[]{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1};
        int[] sbox8Data = new int[]{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7};

        //create ddt objects
        Ddt sbox1 = new Ddt(sbox1Data, "S-Box 1");
        Ddt sbox2 = new Ddt(sbox2Data, "S-Box 2");
        Ddt sbox3 = new Ddt(sbox3Data, "S-Box 3");
        Ddt sbox4 = new Ddt(sbox4Data, "S-Box 4");
        Ddt sbox5 = new Ddt(sbox5Data, "S-Box 5");
        Ddt sbox6 = new Ddt(sbox6Data, "S-Box 6");
        Ddt sbox7 = new Ddt(sbox7Data, "S-Box 7");
        Ddt sbox8 = new Ddt(sbox8Data, "S-Box 8");

        //calculate ddt tables
        sbox1.calculateDDT();
        sbox2.calculateDDT();
        sbox3.calculateDDT();
        sbox4.calculateDDT();
        sbox5.calculateDDT();
        sbox6.calculateDDT();
        sbox7.calculateDDT();
        sbox8.calculateDDT();

        //print out results to text file
        // Creating a File object that
        // represents the disk file
        PrintStream o = new PrintStream(new File("DifferenceDistributionTables.txt"));

        // Store current System.out
        // before assigning a new value
        PrintStream console = System.out;

        // Assign o to output stream
        // using setOut() method
        System.setOut(o);



        sbox1.printDDT();
        sbox2.printDDT();
        sbox3.printDDT();
        sbox4.printDDT();
        sbox5.printDDT();
        sbox6.printDDT();
        sbox7.printDDT();
        sbox8.printDDT();
    }
}
