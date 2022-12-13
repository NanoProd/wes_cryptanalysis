import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Fbox {
    
    //attributes
    public static int[] sboxdata1 = new int[]{6,12,3,8,14,5,11,1,2,4,13,7,0,10,15,9};
    public static int[] sboxdata2 = new int[]{10,14,15,11,6,8,3,13,7,9,2,12,1,0,4,5};
    public static int[] sboxdata3 = new int[]{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8};
    public static int[] sboxdata4 = new int[]{15,9,7,0,10,13,2,4,3,6,12,5,1,8,14,11};
    public static int[] sboxdata5 = new int[]{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9};
    public static int[] sboxdata6 = new int[]{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11};
    public static int[] sboxdata7 = new int[]{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1};
    public static int[] sboxdata8 = new int[]{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7};
    public static int expectedDeltaZ = 0x8080D052;

    

        /*Permutation*/
        public static int[] permutation(int[] input){
            int[] per= { 16,  7, 20, 21, 
            29, 12, 28, 17,
             1, 15, 23, 26,
             5, 18, 31, 10, 
             2,  8, 24, 14, 
            32, 27,  3,  9, 
            19, 13, 30,  6, 
            22, 11,  4, 25 };
    
            int[] result = new int[32];
    
            for(int i = 0; i < 32; i++){
                result[i] = input[per[i] - 1];
            }
            return result;
        }
    
        /*Reverse permutation*/
        public static int[] reverse_permutation(int[] input){
            int[] per= { 16,  7, 20, 21, 
            29, 12, 28, 17,
             1, 15, 23, 26,
             5, 18, 31, 10, 
             2,  8, 24, 14, 
            32, 27,  3,  9, 
            19, 13, 30,  6, 
            22, 11,  4, 25 };
    
            int[] result = new int[32];
            
            for(int i = 0; i < 32; i++){
                result[per[i] - 1] = input[i];
            }
            return result;
        }

        /*Compute sbox input and output using the differential characteristics that we found*/
        public static String[] compute_delta_sbox(String[] input){
            //given sbox1 input  difference 6 -> output difference = D
            //given sbox2 input  difference 2 -> output difference = 5
            //given sbox4 input  difference 6 -> output difference = D
            //others should all be zero 
            String[] result = {"0", "0", "0", "0", "0", "0", "0", "0"};
            
            //check for sbox1
            if(input[0] == "6"){
                result[0] = "D";
            } else if(input[0] != "0"){
                result[0] = "U";
            }

            //check for sbox2
            if(input[1] == "2"){
                result[1] = "5";
            } else if(input[1] != "0"){
                result[1] = "U";
            }

            //check for sbox3
            if(input[2] != "0"){
                result[2] = "U";
            }

            //check for sbox4
            if(input[3] == "6"){
                result[3] = "D";
            } else if(input[3] != "0"){
                result[3] = "U";
            }

            //check for sbox5
            if(input[4] != "0"){
                result[4] = "U";
            }

            //check for sbox6
            if(input[5] != "0"){
                result[5] = "U";
            }

            //check for sbox7
            if(input[6] != "0"){
                result[6] = "U";
            }

            //check for sbox8
            if(input[7] != "0"){
                result[7] = "U";
            }

            return result;
        }
        
        /*Convert string[] to int[] bits */
        public static int[] convert_str_int(String[] input){
            int[] result = new int[32];
            int counter = 0;

            for(int i = 0; i < 8; i++){
                String bit = Ddt.hexToBit(input[i]);
                for(int j = 0; j < 4; j++){
                    result[counter] = Character.getNumericValue(bit.charAt(j));
                    counter++;
                }
            }
            return result;
        }


        /*Convert int[] bits to string[] in hex*/
        public static String[] convert_int_str(int[] input){
            String[] result = new String[8]; 
            int counter = 0;

            for(int i = 0; i < 8; i++){
                String temp = "";
                for(int j = 0; j < 4; j++){
                    temp += Integer.toString(input[counter]);
                    counter++;
                }
                result[i] = Ddt.intToHex(Ddt.bitToInt(temp));
            }
            return result;
        }


        
        /*Compute sbox*/
        public static String[] computeSbox(String[] input){
            String[] result = new String[8];
            
            //find sbox results
            result[0] = Integer.toString(sboxdata1[Ddt.hexToInt(input[0])]);
            result[1] = Integer.toString(sboxdata2[Ddt.hexToInt(input[1])]);
            result[2] = Integer.toString(sboxdata3[Ddt.hexToInt(input[2])]);
            result[3] = Integer.toString(sboxdata4[Ddt.hexToInt(input[3])]);
            result[4] = Integer.toString(sboxdata5[Ddt.hexToInt(input[4])]);
            result[5] = Integer.toString(sboxdata6[Ddt.hexToInt(input[5])]);
            result[6] = Integer.toString(sboxdata7[Ddt.hexToInt(input[6])]);
            result[7] = Integer.toString(sboxdata8[Ddt.hexToInt(input[7])]);

            return result;
        }

        /*Compute fbox for IO differentials used in character differentials METHOD FOR PART3 only*/
        public static String[] computeFbox(String[] input){

            //input goes through sboxes and is converted to int[]
            int[] afterSbox = convert_str_int(compute_delta_sbox(input));

            //compute permutation
            int[] afterPerm = permutation(afterSbox);

            //convert from int[] to string[] for hex
            String[] result = convert_int_str(afterPerm);

            return result;
        }


        /*XOR operation for 2 hexadecimal arrays of size 8 - might be long due to string-> int conversions*/
        public static String[] XOR(String[] input1, String[] input2){
            String[] resultStr = new String[8];
            int[] in1 = convert_str_int(input1);
            int[] in2 = convert_str_int(input2);
            int[] result = new int[32];

            for(int i = 0; i < 32; i++){
                if(in1[i] == in2[i]){
                    result[i] = 0;
                } else {
                    result[i] = 1;
                }
            }
            resultStr = convert_int_str(result);
            return resultStr;
        }

        /*reverse the last round of the cipher of a given ciphertext pair and a key to calculate z*/
        public static String[] calculateZ(String[] CL, String[] CR, String[] targetKey){
            //calculate the input to the fbox
            String[] fboxInput = XOR(CR, targetKey);

            //calculate the output of the fbox
            //sbox and permutation
            String[] sboxOutput =  convert_int_str(permutation(convert_str_int(computeSbox(fboxInput))));

            String[] Z = XOR(sboxOutput, CL);

            //cal

            return Z;
        } 

        /*calculate delta z*/
        public static String calculateDeltaZ(String[] z1, String[] z2){
            String result = "";
            String[] deltaZ = XOR(z1, z2);

            for(String str: deltaZ){
                result+= str;
            }
            return result;
        }

        /*Create an 8 bit hexadecimal key*/
        public static String[] generateKey(int a, int b, int c, int d, int e, int f, int g, int h){
            String[] key = new String[8];

            key[0] = Ddt.intToHex(a);
            key[1] = Ddt.intToHex(b);
            key[2] = Ddt.intToHex(c);
            key[3] = Ddt.intToHex(d);
            key[4] = Ddt.intToHex(e);
            key[5] = Ddt.intToHex(f);
            key[6] = Ddt.intToHex(g);
            key[7] = Ddt.intToHex(h);

            return key;

        }

        /*Brute for to find the key in round 3 - returns key3*/
        public static String[] breakCipher(){
            //get your cipher text pairs by computing ./wes-key-13 file  these are shown in my report

            //these values are obtained using
            //X1 = BC0A000000000000 => C1 = EC1C33D26342E49B
            //X2 = DF0D000000000000 => C2 = BE9DA0C82652EC3B
            //pair one
            String[] CL1 = {"4", "3", "C", "A", "B", "9", "B", "8"};
            String[] CR1 = {"E", "C", "D", "E", "F", "9", "F", "E"};

            String[] CL2 = {"4", "8", "8", "2", "A", "8", "4", "5"};
            String[] CR2 = {"8", "8", "C", "B", "7", "4", "5", "F"};

            //pair two
            String[] CL3 = {"A", "B", "0", "4", "7", "7", "4", "0"};
            String[] CR3 = {"F", "9", "D", "A", "6", "4", "1", "B"};

            String[] CL4 = {"7", "8", "7", "4", "4", "6", "9", "D"};
            String[] CR4 = {"9", "F", "C", "F", "E", "8", "B", "B"};

            //pair three
            String[] CL5 = {"0", "0", "8", "D", "A", "0", "D", "1"};
            String[] CR5 = {"D", "F", "6", "F", "D", "4", "5", "7"};

            String[] CL6 = {"F", "A", "E", "9", "D", "8", "B", "4"};
            String[] CR6 = {"B", "B", "F", "A", "5", "B", "F", "6"};

            //pair four
            String[] CL7 = {"1", "2", "5", "D", "7", "2", "4", "E"};
            String[] CR7 = {"4", "0", "F", "2", "4", "C", "3", "7"};

            String[] CL8 = {"6", "7", "A", "8", "C", "4", "D", "0"};
            String[] CR8 = {"0", "6", "E", "7", "C", "1", "1", "7"};

            //pair 5
            String[] CL9 = {"8", "F", "1", "2", "9", "F", "9", "1"};
            String[] CR9 = {"6", "6", "4", "6", "F", "D", "7", "F"};

            String[] CL10 = {"C", "0", "6", "2", "F", "F", "7", "8"};
            String[] CR10 = {"0", "2", "D", "3", "7", "2", "D", "E"};

            //pair 6
            // String[] CL11 = {"0", "0", "8", "D", "A", "0", "D", "1"};
            // String[] CR11 = {"D", "F", "6", "F", "D", "4", "5", "7"};

            // String[] CL11 = {"F", "A", "E", "9", "D", "8", "B", "4"};
            // String[] CR11 = {"B", "B", "F", "A", "5", "B", "F", "6"};



            //generate all possible values of the target key and compute delta z till you find the correct key!
            //there will be 2^32 possible keys so this may take a long time however, function returns the moment the right delta z is found
            //for(int a = 0; a < 16; a++){
                int a = 10;
            //    for(int b = 0; b < 16; b++){
                int b = 1;
            //        for(int c = 0; c < 16; c++){
                int c = 12;
            //            for(int d = 0; d < 16; d++){
                int d = 15;
            //                for(int e = 0; e < 16; e++){
                int e = 11;
                                for(int f = 0; f < 16; f++){
                                    for(int g = 0; g < 16; g++){
                                        for(int h = 0; h < 16; h++){
                                            String[] key = generateKey(a, b, c, d, e, f, g, h);
                                            System.out.println(Arrays.toString(key));
                                            //calculate z1
                                            String[] z1 = calculateZ(CL1, CR1, key);
                                            String[] z2 = calculateZ(CL2, CR2, key);
                                            String deltaZ = calculateDeltaZ(z1, z2);

                                            //check if you found a potential key
                                            if(deltaZ.equals(expectedDeltaZ)){
                                                //see if this key also works for c3/c4
                                                String[] z3 = calculateZ(CL3, CR3, key);
                                                String[] z4 = calculateZ(CL4, CR4, key);
                                                String deltaZ2 = calculateDeltaZ(z3, z4);
                                                if(deltaZ2.equals(expectedDeltaZ)){
                                                    //check if it also works for c5/c6
                                                    String[] z5 = calculateZ(CL5, CR5, key);
                                                    String[] z6 = calculateZ(CL6, CR6, key);
                                                    String deltaZ3 = calculateDeltaZ(z5, z6);
                                                    if(deltaZ3.equals(expectedDeltaZ)){
                                                        //check if it also works for c7/c8
                                                        String[] z7 = calculateZ(CL7, CR7, key);
                                                        String[] z8 = calculateZ(CL8, CR8, key);
                                                        String deltaZ4 = calculateDeltaZ(z7, z8);
                                                        if (deltaZ4.equals(expectedDeltaZ)){
                                                            //check if it also works for c9/c10
                                                            String[] z9 = calculateZ(CL9, CR9, key);
                                                            String[] z10 = calculateZ(CL10, CR10, key);
                                                            String deltaZ5 = calculateDeltaZ(z9, z10);
                                                            if(deltaZ5.equals(expectedDeltaZ)){
                                                                return key;
                                                            }
                                                        }
                                                        
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                        //    }
                      //  }
                   // }
                //}
           // }
                    
            return CL1;
        }



        public static void main(String[] args){

            /* 
            USED TO FIND CHARACTER DIFFERENTIAL FOR PART 3


            String[] input = {"6", "2", "0", "6", "0", "0", "0", "0"};

            String[] result = computeFbox(input);

            for(String str: result){
                System.out.print(str + " ");
            }


            */

            //Delta Z test
              String[] CL1 = {"4", "3", "C", "A", "B", "9", "B", "8"};
              String[] CR1 = {"E", "C", "D", "E", "F", "9", "F", "E"};

              String[] CL2 = {"4", "8", "8", "2", "A", "8", "4", "5"};
              String[] CR2 = {"8", "8", "C", "B", "7", "4", "5", "F"};

        //      String[] key = {"A", "1", "C", "F", "B", "D", "1", "D"};
                String[] key = {"A", "1", "C", "F", "B", "D", "1", "C"};
              String[] z1 = calculateZ(CL1, CR1, key);
              String[] z2 = calculateZ(CL2, CR2, key);

              String deltaZ = calculateDeltaZ(z1, z2);

              if(deltaZ.equals(expectedDeltaZ)){
                  System.out.println("Found key!");
             }

        //     // //B 3 9 6 C F 1 8
        //     // //B 3 1 6 5 F 0 A

             System.out.println(deltaZ);

        //     //check generate key function
        //      String[] target = generateKey(10, 1, 12, 15, 11, 13, 1, 13);

        //      for(String str: target){
        //          System.out.print(str + " ");
        //     }

        //    System.out.println(Ddt.intToHex(10));
        //    System.out.println(Ddt.intToHex(1));
        //    System.out.println(Ddt.intToHex(12));
        //    System.out.println(Ddt.intToHex(15));
        //    System.out.println(Ddt.intToHex(11));
        //    System.out.println(Ddt.intToHex(13));
        //    System.out.println(Ddt.intToHex(1));
        //    System.out.println(Ddt.intToHex(13));


            //  long startTime = System.nanoTime();
            //  String[] key = breakCipher();
            //  long endTime = System.nanoTime();
            //  long totalTime = endTime - startTime;
            //  System.out.println(totalTime);

            //  System.out.println("The key was successfully found!!");
            //  System.out.println("Key: ");

            //     for(String str: key){
            //         System.out.print(str + " ");
            //     }

             


        }
}
