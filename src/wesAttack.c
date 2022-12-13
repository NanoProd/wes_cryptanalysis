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

#ifndef DELTAZ
#define DELTAZ 0x8080D052
#endif

//inputs x1 and x2 which result in input difference 0x62060000 and their ciphertext pairs C1 + C2
//CL and CR represent the left amd right halves of the ciphertext output respectively

uint32_t CL1[10] = {0x43CAB9B8, 0xAB047740, 0x008DA0D1, 0x125D724E, 0x8F129F91, 0x81925419, 0x016AC042, 0x7EAEF954, 0x2EEE9B46, 0xB41D9579 };
uint32_t CR1[10] = {0xECDEF9FE, 0xF9DA641B, 0xDF6FD457, 0x40F24C37, 0x6646FD7F, 0x55D9ACFC, 0xF2D3A07C, 0x770381BB, 0xDE0780BB, 0xCA5689FE };

uint32_t CL2[10] = {0x4882A845, 0x7874469D, 0xFAE9D8B4, 0x67A8C4D0, 0xC062FF78, 0xACD05401, 0xF430347B, 0x66CCDA4E, 0x74847E57, 0x305CC575 };
uint32_t CR2[10] = {0x88CB745F, 0x9FCFE8BB, 0xBBFA5BF6, 0x06E7C117, 0x02D372DE, 0x394A23FF, 0xBD402FFE, 0x3B918CBA, 0x93958D3A, 0xA4C584FE };

int numPairs = 10;

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

uint32_t crackLastRound(){
    printf(" Using Z differential of 0x8080D052");
    printf(" Cracking last round...");

    uint32_t targetKey;

    for(targetKey = 0x00000000; targetKey < 0xFFFFFFFF; targetKey++){
        int score = 0;
        //printf("testing target key: 0x%08X\n", targetKey);

        int c;
        for(c = 0; c < numPairs; c++)
        {
            //evaluate ciphertext pairs c1 and c2
            //xor CR with target key
            uint32_t inputFbox1 = CR1[c] ^ targetKey;
            uint32_t inputFbox2 = CR2[c] ^ targetKey;

            //calculate output from fbox
            uint32_t outputFbox1 = round_func(inputFbox1, targetKey);
            uint32_t outputFbox2 = round_func(inputFbox2, targetKey);

            //find delta z by XOR output with CL
            uint32_t z1 = CL1[c] ^ outputFbox1;
            uint32_t z2 = CL2[c] ^ outputFbox2;
            uint32_t deltaZ = z1 ^ z2;

            if(deltaZ == DELTAZ){
                score++;
            } else {
                break;
            }
        }

        if(score == numPairs)
        {
            printf("found subkey: 0x%08X", targetKey);
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


    printf("Cracking 4-round WES cipher...");
    printf("Using input differential: 0x62060000");
    printf("Using delta z: 0x8080D052");
    start = clock();

    uint32_t subkey = crackLastRound();
    end = clock();

    cpu_time = ((double)end-start)/CLOCKS_PER_SEC/60; //in minutes

    printf(" Total time to crack last round subkey: %f minutes\n", cpu_time);

    return 0;
}