import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {
    
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
