public class Ddt {
    //attributes
    String[] input;
    String[] output;
    int[][] table;

    //constructor
    public Ddt(String[] input, String[] output){
        this.input = input;
        this.output = output;
        for(int i = 0; i < 16; i++){
            for(int j = 0; j < 16; j++){
                table[i][j] = 0;
            }
        }
    }


    public static void main(String[] args) {
        String[] sboxinput = new String[]{"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"};
        String[] sbox8output = new String[]{"13", "2", "8", "4", "6", "15", "11", "1", "10", "9", "3", "14", "5", "0", "12", "7"};

        Ddt sbox8 = new Ddt()
    }
}
