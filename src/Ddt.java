import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /*
    * The row and column sums must be equal to 2^^n -> 2^^4 = 16
    * All entries must be equal
    * */
    public boolean verifyTable(){
        //verify row sum
        int r;
        int c;
        int rowSum;
        int colSum;
        for(r = 0; r < 16; r++){
            rowSum = 0;
            for(c = 0; c < 16; c++){
                rowSum += table[r][c];
            }
            if(rowSum != 16){
                System.out.println("Error in table " + this.name);
                return false;
            }
        }

        //verify column sum
        for(c = 0; c < 16; c++){
            colSum = 0;
            for(r = 0; r < 16; r++){
                colSum+= table[r][c];
            }
            if(colSum != 16){
                System.out.println("Error in table " + this.name);
                return false;
            }
        }

        //verify that all entries are even
        for(r= 0; r < 16; r++){
            for(c = 0; c < 16; c++){
                if(table[r][c] % 2 != 0){
                    System.out.println("Error in table " + this.name);
                    return false;
                }
            }
        }
        return true;
    }


    /*creates difference distribution table then verifies if it is a valid table*/
    public void calculateDDT(){
        System.out.println("Creating table for " + this.name + "...");
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                this.ioDifference(i,j);
            }
        }

        boolean success = verifyTable();

        if(success){
            System.out.println("Table " + this.name + " is validated");
        } else {
            System.out.println("Error in table " + this.name);
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

    /*find candidates for constructing differential characteristics with given alpha out of 16, given n = 5 -> alpha = 5/16 */
    public void findCandidates(int n){
        for(int r = 0; r < 16; r++){
            for(int c = 0; c < 16; c++){
                if(!(r == 0 && c == 0)){
                    if(table[r][c] >= n){
                        System.out.println(this.name + ": DeltaX = " + intToHex(r) + "--> DeltaY = " + intToHex(c) + "  with probability " + table[r][c] + "/16");
                        System.out.println("finding candidate x1 and x2 to get DeltaX = " + intToHex(r) + " : " + intToBit(r));
                        List<List<Integer>> candidates = findXORCandidates(intToBit(r));
                        for(List<Integer> result : candidates){
                            System.out.println("X1: " + intToHex(result.get(0)) + "  X2: " + intToHex(result.get(1)));
                        }
                        System.out.println();
                    }
                }
            }
        }
    }

    /*find number pairs which will result in the XOR difference given*/
    public static List<List<Integer>> findXORCandidates(String xor){
        int[] numbers = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
        List<List<Integer>> result = new ArrayList<>();
        String s1;
        String s2;
        String diff;

        for(int i = 0; i < 16; i++){
            s1 = intToBit(numbers[i]);
            for(int j = 0; j < 16; j++){
                s2 = intToBit(numbers[j]);
                diff = bitXOR(s1,s2);
                if(diff.equals(xor)){
                    //verify that pair doesn't already exist
                    boolean exists = false;
                    for(List<Integer> pair: result){
                        if(pair.get(0) == j && pair.get(1) == i){
                            exists = true;
                            break;
                        }
                    }
                    if(!exists){
                        List<Integer> temp = new ArrayList<>();
                        temp.add(numbers[i]);
                        temp.add(numbers[j]);
                        result.add(temp);
                    }
                }
            }
        }
        return result;
    }
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Calculating sbox difference distribution tables...");
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

        //store ddt objects in container
        Ddt[] objects = new Ddt[8];
        objects[0] = sbox1;
        objects[1] = sbox2;
        objects[2] = sbox3;
        objects[3] = sbox4;
        objects[4] = sbox5;
        objects[5] = sbox6;
        objects[6] = sbox7;
        objects[7] = sbox8;

        //calculate ddt tables
        for(Ddt table: objects){
            table.calculateDDT();
        }

        //set output stream to text file
        PrintStream o = new PrintStream(new File("DifferenceDistributionTables.txt"));
        // Store current System.out
        PrintStream console = System.out;
        // Assign o to output stream
        System.setOut(o);

        //print to file
        for(Ddt table: objects){
            table.printDDT();
        }

        //find candidates for differential characteristics
        System.out.println("\n\n\n\nFinding candidates for differential characteristics with alpha = 1 ...\n");
        for(Ddt table: objects){
            table.findCandidates(16); // alpha = 100%
        }

        System.setOut(console);
        System.out.println("Success!");
    }
}
