public class Fbox {
    
    //attributes
    public static int[] sboxdata1;
    public static int[] sboxdata2;
    public static int[] sboxdata3;
    public static int[] sboxdata4;
    public static int[] sboxdata5;
    public static int[] sboxdata6;
    public static int[] sboxdata7;
    public static int[] sboxdata8;

    //constructor
    public Fbox(){
        int[] sboxdata1 = new int[]{6,12,3,8,14,5,11,1,2,4,13,7,0,10,15,9};
        int[] sboxdata2 = new int[]{10,14,15,11,6,8,3,13,7,9,2,12,1,0,4,5};
        int[] sboxdata3 = new int[]{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8};
        int[] sboxdata4 = new int[]{15,9,7,0,10,13,2,4,3,6,12,5,1,8,14,11};
        int[] sboxdata5 = new int[]{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9};
        int[] sboxdata6 = new int[]{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11};
        int[] sboxdata7 = new int[]{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1};
        int[] sboxdata8 = new int[]{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7};
    }
    

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
            
            //find sbox1
            result[0] = Integer.toString(sboxdata1[Ddt.hexToInt(input[0])]);



            //find sbox2
            result[1] = Integer.toString(sboxdata2[Ddt.hexToInt(input[1])]);
            //sbox3
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

            String[] deltaZ = XOR(sboxOutput, CL);

            //cal

            return deltaZ;
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


            String[] input1 = {"1", "1", "0", "1", "0", "0", "0", "0", "0", "0", "0", "0","0", "0", "0", "0"};
            String[] input2 = {"7", "3", "0", "7", "0", "0", "0", "0", "0", "0", "0", "0","0", "0", "0", "0"};

            String[] result = XOR(input1, input2);
            for(String str: result){
                System.out.print(str + " ");
            }
        }
}
