// Jasmine Chi 
// This class represents a Huffman Code that can either create a huffman
// code from a file, compress a file, decompress a file, or compress and then
// decompress a file. Compressing a file means to encode it using its
// ASCII values and its respective binary representation. Decompressing a
// file means to use its ASCII values and its respective binary representation
// and to output the respective characters represented into an output file.
// The files to be compress into and decompress from are in standard format where the 
// first line of the pair is the ASCII value of the character and the second line 
// of the pair is the respective binary representation for the character.

import java.util.*;
import java.io.*;

public class HuffmanCode {
	private HuffmanNode huffmanTree; // our huffman tree

	// Constructs a HuffmanCode using the ASCII character values and
	// given frequencies of the characters. It sorts the ASCII characters values
	// and their frequencies in ascending order with respect to frequency
	// of the ASCII character values.
	// Parameters:
	// int[] frequencies - frequencies that keep track of the count of the
	// ASCII character values
	public HuffmanCode(int[] frequencies) {
		Queue<HuffmanNode> tree = new PriorityQueue<>();
		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] != 0) {
				tree.add(new HuffmanNode(i, frequencies[i]));
			}
		}
		while (tree.size() > 1) {
			HuffmanNode currLeft = tree.remove();
			HuffmanNode currRight = tree.remove();
			HuffmanNode newNode = new HuffmanNode(currLeft, currRight, 0, currLeft.freq + currRight.freq);
			tree.add(newNode);
		}
		HuffmanNode eachNode = tree.remove();
		huffmanTree = eachNode;
	}

	// Constructs a HuffmanCode using the ASCII value of the character and
	// the binary representation for that respective character. It is assumed
	// that the input file being read is always in legal, valud standard format.
	// Parameters:
	// Scanner input - reads the input file that is used. The file is in standard format. 
	// Standard format is where the first line of the pair is the ASCII value of 
	// the character and the second line of the pair is the respective binary 
	// representation for the character. The input file is never null.
	public HuffmanCode(Scanner input) {
		while (input.hasNext()) {
			int currentAsciiValue = Integer.parseInt(input.nextLine());
			String ourCode = input.nextLine();
			huffmanTree = huffmanCodeHelper(ourCode, huffmanTree, currentAsciiValue);
		}
	}

	// Constructs a HuffmanCode using the ASCII value of the character and
	// the binary representation for that respective character. It is assumed
	// that the input file being read is always in legal, valud standard format.
	// Returns the HuffmanNode that is used to get the ASCII value of the
	// character and the binary representation for that respective character.
	// Parameters:
	// String integers - binary representation of the character
	// HuffmanNode currentBinary - current binary choice of "0" or "1"
	// int ascii - ASCII value of the character
	private HuffmanNode huffmanCodeHelper(String integers, HuffmanNode currentBinary,
										int ascii) {
		if (currentBinary == null) {
			currentBinary = new HuffmanNode(0, 0);
		}
		if (integers.length() == 0) {
			HuffmanNode createNode = new HuffmanNode(ascii, 0);
			return createNode;
		} else {
			char newIntegers = integers.charAt(0);
			String nextInteger = integers.substring(1);
			if (newIntegers == '1') {
				currentBinary.right = huffmanCodeHelper(nextInteger, currentBinary.right, ascii);
			} else {
				currentBinary.left = huffmanCodeHelper(nextInteger, currentBinary.left, ascii);
			}
			return currentBinary;
		}
	}

	// Outputs the ASCII values and respective binary representation of the
	// characters to the given output file in standard format. Standard format is where
	// the first line of the pair is the ASCII value of the character
	// and the second line of the pair is the respective binary representation
	// for the character.
	// Parameters:
	// PrintStream output - the output file to print output to.
	public void save(PrintStream output) {
		saveHelper(output, huffmanTree, "");
	}

	// Outputs the ASCII values and respective binary representation of the
	// characters to the given output file in standard format. Standard format is where
	// the first line of the pair is the ASCII value of the character
	// and the second line of the pair is the respective binary representation
	// for the character.
	// Parameters:
	// PrintStream output - the output file to print output to.
	// HuffmanNode currentBinary - current binary choice of "0" or "1".
	// String currentCode - binary representation of the character.
	private void saveHelper(PrintStream output, HuffmanNode currentBinary, 
							String currentCode) {
		if (currentBinary.left == null && currentBinary.right == null) {
			output.println(currentBinary.character);
			output.println(currentCode);
		} else {
			saveHelper(output, currentBinary.left, currentCode + "0");
			saveHelper(output, currentBinary.right, currentCode + "1");
		}
	}

	// Reads the bits from a given compressed input file in standard format
	// and then based on the bits information, it uses these ASCII values and
	// binary representation of characters in order to find the respective letter
	// characters and outputs the characters to a given file. It continues reading
	// until the input file that it is reading from is empty. Standard format is
	// where the first line of the pair is the ASCII value of the character
	// and the second line of the pair is the respective binary representation
	// for the character. It is assumed that the input file can be used to find
	// the letter characters based on the ASCII values and binary representation of 
	// characters in the file.
	// Parameters:
	// BitInputStream input - reads the bit information from the compressed file.
	// PrintStream output - the output file to print output to.
	public void translate(BitInputStream input, PrintStream output) {
		while (input.hasNextBit()) {
			translateHelper(input, output, huffmanTree);
		}
	}
	
	// Reads the bits from a given compressed input file in standard format
	// and then based on the bits information, it uses these ASCII values and
	// binary representation of characters in order to find the respective letter
	// characters and outputs the characters to a given file. It continues reading
	// until the input file that it is reading from is empty. Standard format is
	// where the first line of the pair is the ASCII value of the character
	// and the second line of the pair is the respective binary representation
	// for the character. It is assumed that the input file can be used to find
	// the letter characters based on the ASCII values and binary representation of 
	// characters in the file.
	// Parameters:
	// BitInputStream input - reads the bit information from the compressed file
	// PrintStream output - the output file to print output to.
	// HuffmanNode currentBinary - current binary choice of "0" or "1"
	private void translateHelper(BitInputStream input, 
							PrintStream output, HuffmanNode currentBinary) {
		if (currentBinary.left == null && currentBinary.right == null) {
			output.write(currentBinary.character);
		} else {
			if (input.nextBit() == 1) {
				translateHelper(input, output, currentBinary.right);
			} else {
				translateHelper(input, output, currentBinary.left);
			}
		}
	}

	// A class representing a HuffmanNode object that is used to for huffman codes.
	private static class HuffmanNode implements Comparable<HuffmanNode> {
		public HuffmanNode left;
		public HuffmanNode right;
		public int freq;
		public int character;

		// Constructs a HuffmanNode with the given character and frequency and null
		// subtrees.
		public HuffmanNode(int character, int freq) {
			this(null, null, character, freq);
		}

		// Constructs a HuffmanNode with the left(0) and right(1) subtrees and
		// the given character and frequency.
		public HuffmanNode(HuffmanNode left, HuffmanNode right, int character, int freq) {
			this.left = left;
			this.right = right;
			this.character = character;
			this.freq = freq;
		}

		// Compares our given frequencies with each other.
		// Returns a number < 0 if this frequency comes before other.
		// Returns a number > 0 is this frequency comes after other.
		// Returns 0 if this frequency and other are the same.
		public int compareTo(HuffmanNode other) {
			return this.freq - other.freq;
		}
	}
}
