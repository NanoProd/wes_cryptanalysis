/**   wesAttack.c
 * 
 * Date:    2022-12-12
 * Author:  Joshua-James Nantel-Ouimet
 * version: 0.1
 *
 * Course:  SOEN 321
 *
 * Description: known plaintext attack on Weak Encryption Scheme (WES) cipher. Reference implementation.
 *              Designed for Concordia SOEN 321 course. WES is a Feistel cipher
 *              intentionally made vulnerable to differential cryptanalysis. 
 *              
 * 
 * Compile: run gcc -02 wesAttack.c -o wesAttack`
 * Note: compiler optimization used
 * Usage:   ./wesAttack
 *
 * Files: 		
 * 				- Ddt.java  : File used to analyze sboxes to construct character differentials.
 *
 *
 * */



#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h> /* for `false` symbol */
#include <stdint.h> 
#include <time.h>
#include <math.h>


//inputs x1 and x2 which result in input difference 0x62060000 and their ciphertext pairs C1 + C2
//CL and CR represent the left amd right halves of the ciphertext output respectively

//uint32_t CL1[15] = {0x43CAB9B8, 0xAB047740, 0x008DA0D1, 0x125D724E, 0x8F129F91, 0x81925419, 0x016AC042, 0x7EAEF954, 0x2EEE9B46, 0xB41D9579, 0x3AA57246, 0xE229084A, 0xFCC60E67, 0xF054B535, 0xD5D1EF51};
//uint32_t CR1[15] = {0xECDEF9FE, 0xF9DA641B, 0xDF6FD457, 0x40F24C37, 0x6646FD7F, 0x55D9ACFC, 0xF2D3A07C, 0x770381BB, 0xDE0780BB, 0xCA5689FE, 0xDA8981BF, 0xFC1A27BA, 0xF89B8438, 0xCBC48DFE, 0xC951ADFA};

//uint32_t CL2[15] = {0x4882A845, 0x7874469D, 0xFAE9D8B4, 0x67A8C4D0, 0xC062FF78, 0xACD05401, 0xF430347B, 0x66CCDA4E, 0x74847E57, 0x305CC575, 0x22C5527A, 0x8323F06E, 0x2DEABFC7, 0x6435D571, 0xC9B24F47};
//uint32_t CR2[15] = {0x88CB745F, 0x9FCFE8BB, 0xBBFA5BF6, 0x06E7C117, 0x02D372DE, 0x394A23FF, 0xBD402FFE, 0x3B918CBA, 0x93958D3A, 0xA4C584FE, 0xB61A8CBE, 0xB088A8B9, 0xB5090B3B, 0xA55780FE, 0x85C3A0FB};

#ifdef DEBUG
#define DEBUG_PRINT(...) do{ fprintf( stderr, __VA_ARGS__ ); } while( false )
#else
#define DEBUG_PRINT(...) do{ } while ( false )
#endif
#ifndef MASTERKEY
#define MASTERKEY 0x7D64C7A0A1CFBD1D
#endif

#ifndef MAXPAIRS
#define MAXPAIRS 1000
#endif


int numPairs = 45;
uint64_t plaintext1[MAXPAIRS];
uint64_t plaintext2[MAXPAIRS];
uint64_t ciphertext1[MAXPAIRS];
uint64_t ciphertext2[MAXPAIRS];


// Key scheduling tables 
int k1p[32] = {1,3,5,7,9,11,13,15,17,19,21,23,25,27,29,31,33,35,37,39,41,43,45,47,49,51,53,55,57,59,61,63};      // Odd bits
int k2p[32] = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32};          // Left half
int k3p[32] = {2,4,6,8,10,12,14,16,18,20,22,24,26,28,30,32,34,36,38,40,42,44,46,48,50,52,54,56,58,60,62,64};     // Even bits
int k4p[32] = {33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64}; // Right half


// S-box Table
int sbox[8][16] = {
	/* 1 */
	{ 6, 12, 3, 8, 14, 5, 11, 1, 2, 4, 13, 7, 0, 10, 15, 9}, 
	/* 2 */
	{ 10, 14, 15, 11, 6, 8, 3, 13, 7, 9, 2, 12, 1, 0, 4, 5},
	/* 3 */
	{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8}, 
	/* 4 */
	{ 15, 9, 7, 0, 10, 13, 2, 4, 3, 6, 12, 5, 1, 8, 14, 11},
	/* 5 */
	{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9},
	/* 6 */
	{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11},
	/* 7 */
	{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1},
	/* 8 */
	{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7}
};


// Permutation Table
int per[32]
	= { 16,  7, 20, 21, 
		29, 12, 28, 17,
		 1, 15, 23, 26,
		 5, 18, 31, 10, 
		 2,  8, 24, 14, 
		32, 27,  3,  9, 
		19, 13, 30,  6, 
		22, 11,  4, 25 };


uint32_t mask[32]; /* permutation mask to speed up the permutation transform */


uint32_t sbox_layer(uint32_t x)
{
	uint32_t res = 0;
	res = res | (sbox[0][(x>>28)&0xf] << 28);
	res = res | (sbox[1][(x>>24)&0xf] << 24);
	res = res | (sbox[2][(x>>20)&0xf] << 20);
	res = res | (sbox[3][(x>>16)&0xf] << 16);
	res = res | (sbox[4][(x>>12)&0xf] << 12);
	res = res | (sbox[5][(x>>8)&0xf] << 8);
	res = res | (sbox[6][(x>>4)&0xf] << 4);
	res = res | (sbox[7][x&0xf]);
	return res;
}

uint32_t permute(uint32_t x)
{
	uint32_t res = 0;
	for(int i = 0;i<32;i++)
		res |= ((x & mask[i]) << (per[i]-1)) >> i;
	return res;
}

/** 
 *  WES round function: 
 *
 *          1) xor with the round key
 *          2) Pass through f-box:
 *             -- sboxes
 *             -- permutaion
 *
 *
 *                         +------------- K (in)
 *                         |
 *           +------+      v
 *    out <--|  f   |<--- xor <--- x (in)
 *           +------+ 
 *
 *                 
 *  f(x) : out <-- PERMUTATION_BOX <-- SBOX's <-- x
 *
 * */
uint32_t round_func(uint32_t x, uint32_t rkey)
{
	x = x ^ rkey;
	x = sbox_layer(x);
	x = permute(x);
	return x;
}

/* Optimization: mask is used to extract bits at certain position.
 * Since we can do it once before the encryption, it will save us
 * some operations during the encryption */
int precompute_wes_permutation_mask()
{
	for(int i = 0; i<32; i++)
		mask[i] = 1 << (32-per[i]);
	return 0;
}

/* 
 * Key schedule function
 *
 * Generate 4 round keys based on master key. Each round key is a subset of 
 * master key's bits:
 *
 * 	  K1: odd bits
 * 	  K2: left 32bit-half
 * 	  K3: even bits
 * 	  K4: right 32bit-half
 *
 * @param      master_key  Master key (64-bits)
 * @param[out] rkeys       Array of 4 round keys (to be generated by this
 *                         function and returned to the caller)
 */
void key_schedule(uint64_t master_key, uint32_t rkeys[])
{
	uint32_t bit1, bit2, bit3, bit4;
	uint64_t s = master_key;

	memset(rkeys, 0, 4*sizeof(uint32_t));
	for(int i = 0; i<32; i++)
	{
		/* Extract specfic bits from the master key according to k1p, k2p, k3p,
		 * and k4p permutations */
		bit1 = ((s >> (64-k1p[i])) & 0x1);
		bit2 = ((s >> (64-k2p[i])) & 0x1);
		bit3 = ((s >> (64-k3p[i])) & 0x1);
		bit4 = ((s >> (64-k4p[i])) & 0x1);

		rkeys[0] |= bit1 << (31-i);
		rkeys[1] |= bit2 << (31-i);
		rkeys[2] |= bit3 << (31-i);
		rkeys[3] |= bit4 << (31-i);
	}
	return;
}

uint64_t wes_encrypt(uint64_t pt, uint64_t master_key)
{
	uint32_t tmp;
	uint32_t l = pt >> 32;
	uint32_t r = pt & 0xffffffff;
	uint32_t rkeys[4] = {0}; /* Round keys */
	
	key_schedule(master_key, rkeys);   /* Generate round keys */   
	precompute_wes_permutation_mask(); /* Just an optimization: makes permutation step a bit faster */

	/* Do 4 rounds of encryption. */
	DEBUG_PRINT("** Plaintext: %016lX    Master key: %016lX\n", pt, master_key);
	for(int i = 0; i<4; i++)
	{
		DEBUG_PRINT("   input to r%d:  %08X  %08X     rkey = %08X\n", i+1, l, r, rkeys[i]);
		l = l ^ round_func(r, rkeys[i]);
		if(i != 3) /* if not the last round */
			{tmp = l; l = r; r = tmp;} /* swap left and rigth */
	}
	
	DEBUG_PRINT("  outp from r4:  %08X  %08X --\n", l, r);
	/* Recombine 64bits ciphertext from 32bits-left and 32bits-right */
	uint64_t ct = ((uint64_t )l << 32) | r;
	return ct;
}

uint64_t wes_decrypt(uint64_t ct, uint64_t master_key)
{
	uint32_t tmp;
	uint32_t l = ct >> 32;
	uint32_t r = ct & 0xffffffff;
	uint32_t rkeys[4] = {0}; /* Round keys */
	
	key_schedule(master_key, rkeys);   /* Generate round keys */   
	precompute_wes_permutation_mask(); /* Just an optimization: makes permutation step a bit faster */

	/* Do 4 rounds of encryption. */
	DEBUG_PRINT("** Ciphertext: %016lX    Master key: %016lX\n", ct, master_key);
	for(int i = 0; i<4; i++)
	{
		DEBUG_PRINT("   input to r%d:  %08X  %08X     rkey = %08X\n", i+1, l, r, rkeys[3-i]);
		l = l ^ round_func(r, rkeys[3-i]);
		if(i != 3) /* if not the last round */
			{tmp = l; l = r; r = tmp;} /* swap left and rigth */
	}
	
	DEBUG_PRINT("  outp from r4:  %08X  %08X --\n", l, r);
	/* Recombine 64bits ciphertext from 32bits-left and 32bits-right */
	uint64_t pt = ((uint64_t )l << 32) | r;
	return pt;
}


/*generate plaintext ciphertext pairs*/

void generatePlainCipherPairs(uint64_t inputDiff){
    
    //initialize random number generator
    time_t t;
    srand((unsigned) time(&t));
    uint64_t master_key = MASTERKEY;
    int i;
    for(i = 0; i < numPairs; i++)
    {
        plaintext1[i] = (rand() & 0xFFFFLL) << 48LL;
        plaintext1[i] += (rand() & 0xFFFFLL) << 32LL;
        plaintext1[i] += (rand() & 0xFFFFLL) << 16LL;
        plaintext1[i] += (rand() & 0xFFFFLL);

        ciphertext1[i] = wes_encrypt(plaintext1[i], master_key);
        plaintext2[i] = plaintext1[i] ^ inputDiff;
        ciphertext2[i] = wes_encrypt(plaintext2[i], master_key); 
    }

}



uint32_t crackLastRound(){
    uint32_t DELTAZ = 0x8080D052;
    printf("Using Z differential of 0x8080D052\n");
    printf("Cracking last round...\n");

    uint32_t targetKey;

    for(targetKey = 0x00000000; targetKey < 0xFFFFFFFF; targetKey++){
        int score = 0;
        //printf("testing target key: 0x%08X\n", targetKey);

        int c;
        for(c = 0; c < numPairs; c++)
        {
            //evaluate ciphertext pairs c1 and c2
            //grab keys
            uint32_t CL1 = (ciphertext1[c] >> 32);
            uint32_t CR1 = (ciphertext1[c] & 0xFFFFFFFF);

            uint32_t CL2 = (ciphertext2[c] >> 32);
            uint32_t CR2 = (ciphertext2[c] & 0xFFFFFFFF);


            //calculate output from fbox
            uint32_t outputFbox1 = round_func(CR1, targetKey);
            uint32_t outputFbox2 = round_func(CR2, targetKey);

            //find delta z by XOR output with CL
            uint32_t z1 = CL1 ^ outputFbox1;
            uint32_t z2 = CL2 ^ outputFbox2;
            uint32_t deltaZ = z1 ^ z2;

            if(deltaZ == DELTAZ){
                score++;
            } else {
                break;
            }
        }

        if(score == numPairs)
        {
            printf("found subkey: 0x%08X\n", targetKey);
            return targetKey;
        }
    }

}

/*Driver function*/
int main()
{
    clock_t start,end;
    double cpu_time;

    precompute_wes_permutation_mask(); //optimization: makes permutation step faster


    printf("Cracking 4-round WES cipher...\n");
    printf("Using input differential: 0x62060000\n");
    printf("Using delta z: 0x8080D052\n");
    start = clock();

    /*generate plaintext ciphertext pairs*/
    printf("Generating %i pairs of plaintext/ciphertext\n", numPairs);
    uint64_t inputdiff = 0x6206000000000000;
    generatePlainCipherPairs(inputdiff);
    printf("Finished generating pairs...\n");

    uint32_t r4key = crackLastRound();

    end = clock();


    cpu_time = ((double)end-start)/CLOCKS_PER_SEC/60; //in minutes
    printf("Total time to crack last round subkey: %f minutes\n", cpu_time);

    return 0;
}