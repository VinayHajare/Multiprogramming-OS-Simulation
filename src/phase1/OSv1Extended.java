package phase1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

public class OSv1Extended {
	// registers of the virtual machine
	private char[] IR;
	private char[] R;
	private int SI;
	private int IC;
	private boolean C;
	// Memory of the Virtual machine
	private char[][] M;
	// reader and writer for I/O operations
	BufferedReader reader;
	PrintWriter writer;
	// Buffer between memory and IO
	private char[] buffer;
	
	// Constructor of the OS
	public OSv1Extended() throws IOException {
		// initializing the registers
		IR = new char[4];
		R = new char[4];
		IC = 0;
		SI = 0;
		C = false;
		
		// initialize memory
		M = new char[100][4];
		
		// initialize the IO workers
		buffer = new char[40];
		reader = new BufferedReader(new FileReader("D:\\Vinay Hajare\\Eclipse Project\\OS Course Project\\src\\input.txt"));
	    File file = new File("D:\\Vinay Hajare\\Eclipse Project\\OS Course Project\\src\\output.txt");
	    writer = new PrintWriter(file);
	}
	
	// Function to initialize the registers and memory
	public void initialize() throws IOException {
		for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 4; j++) {
                M[i][j] = ' ';
            }
        }
		
        for(int i = 0; i<4; i++) {
        	IR[i] = ' ';
        	R[i] = ' ';
        }
        
        IC = 0;
        SI = 0;
        
        C = false;
        
        for (int j = 0; j < 40; j++) {
            buffer[j] = ' ';
        }
	}
	
	// Function to load the program and data cards
	public void load() throws IOException {
		String line = null;
		int block = 0;
		
		while((line = reader.readLine()) != null) {
			for (int j = 0; j < 40; j++) {
	            buffer[j] = ' ';
	        }
			
			for(int i = 0; i < line.length(); i++) {
				buffer[i] = line.charAt(i);
			}
			
			if(buffer[0] == '$' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'J') {
				initialize();
			}else if(buffer[0] == '$' && buffer[1] == 'D' && buffer[2] == 'T' && buffer[3] == 'A') {
				startExecution();
			}else if(buffer[0] == '$' && buffer[1] == 'E' && buffer[2] == 'N' && buffer[3] == 'D') {
				break;
			}else {
				int k = 0; 
				if(block == 100) {
					  // Memory exceeded
		              System.out.println("Memory exceeded. Aborting.");
		              break;
				}
				
				for(; block < 100; block++) {
					
					if (k == 40 || buffer[k] == '\0' || buffer[k] == '\n') {
                        break;
                    }
					
					for(int j = 0; j<4; j++) {
						if (k == 40 || buffer[k] == '\0' || buffer[k] == '\n') {
	                        break;
	                    }
						if(buffer[k] == 'H') {
							M[block][j] = buffer[k++];
							break;
						}
						
						M[block][j] = buffer[k++];
					}
					
					if (k == 40 || buffer[k] == '\0' || buffer[k] == '\n') {
                        break;
                    }
				}
			}
		}
	}
	
	// Start the execution of the program 
	private void startExecution() throws IOException {
		IC = 00;
		executeUserProgram();
	}
	
	// execute the user program
	private void executeUserProgram() throws IOException {
		while(true) {
			IR = M[IC++];
			System.err.println(Arrays.toString(IR));
			if(IR[0] == 'G' && IR[1] == 'D') {
				SI = 1;
				MOS();
			}else if(IR[0] == 'P' && IR[1] == 'D') {
				SI = 2;
				MOS();
			}else if(IR[0] == 'H') {
				SI = 3;
				MOS();
				break;
			}else if(IR[0] == 'L' && IR[1] == 'R') {
				loadRegister();
			}else if(IR[0] == 'S' && IR[1] == 'R') {
				storeRegister();
			}else if(IR[0] == 'C' && IR[1] == 'R') {
				compareRegister();
			}else if(IR[0] == 'B' && IR[1] == 'T') {
				branchTo();
			}else {
				System.err.print("Invalid Opcode : "+ Character.toString(IR[0]) + Character.toString(IR[1]));
				break;
			}
		}
	}
	
	// function to handle the read interrupt
	private void read() throws IOException {
		// Clear the buffer
		for (int i = 0; i < 40; i++) {
            buffer[i] = ' ';
        }
		
		int k = 0;
		String line;
		
		int block = Character.getNumericValue(IR[2])*10;
		
		if((line = reader.readLine()) != null) {
			if(line.contains("$END")) {
				System.err.println("OUT OF DATA !!!");
			}else {
				for(int i=0; i<line.length(); i++) {
					buffer[i] = line.charAt(i);
				}
				
				for(int i = 0; i<10; i++) {
					for(int j = 0; j<4; j++) {
						if(k < 40) {
							M[block][j] = buffer[k];
							k++;
						}
					}
					block++;
				}
			}
		}else {
			System.err.println("End of the input file reached !!!");
		}
	}
	
	// function to handle the write interrupt
	private void write() {
		// Clear the buffer
		for (int i = 0; i < 40; i++) {
		     buffer[i] = ' ';
		}
		int k = 0;
		int block = Character.getNumericValue(IR[2]);
		block *= 10;
		
		for(int i = 0; i<10; i++) {
			for(int j = 0; j<4; j++) {
				if(k < 40) {
					buffer[k] = M[block][j];
					k++;
				}
			}
			block++;
		}
		
		writer.print(new String(buffer));
		writer.println();
		writer.flush();
	}
	
	// function to handle the terminate interrupt
	private void terminate() {
		writer.println();
	    writer.println();
	    writer.flush();
	    writer.close();
	}
	
	// Store the register into memory
	private void storeRegister() {
		int block = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
		
		for(int i = 0; i<4; i++) {
			M[block][i] = R[i];
		}
		
	}
	
	// Load the memory 
	private void loadRegister() {
		int block = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
		for(int i = 0; i<4; i++) {
			 R[i] = M[block][i];
		}
	}
	
	// Compare the register with the memory
	private void compareRegister() {
		int block = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
		char temp[] = new char[4];
		
		for(int i = 0; i<4; i++) {
			temp[i] = M[block][i];
		}
		
		C = Arrays.equals(temp, R);
	}
	
	// Branch to give instruction
	private void branchTo(){
		System.err.println("Reach here!");
		if(C) {
			int block = Character.getNumericValue(IR[2])*10 + Character.getNumericValue(IR[3]);
			IC = block;
			System.err.println(block);
		}
	}
	
	// Master Operating System
	private void MOS() throws IOException {
		if(SI == 1) {
			read();
		}else if(SI == 2) {
			write();
		}else if(SI == 3) {
			terminate();
		}else {
			System.err.println("Inavlid Interrupt !!!");
		}
	}
	
	public void displayMemory() {
		
		for (int block = 0; block < 100; block++) {
	        System.out.print("Block " + block + ": ");
	        for (int word = 0; word < 4; word++) {
	            char asciiChar = M[block][word];
	            String asciiString = Character.toString(asciiChar);
	            System.out.print(asciiString + " ");
	        }
	        System.out.println();
	    }
		
	    System.out.print("R : ");
	    for(int i = 0; i<4; i++) {
	    	char asciiChar = R[i];
	    	String asciiString = Character.toString(asciiChar);
	    	System.out.print(asciiString+" ");
	    }
	    
	    System.out.println();
	    System.out.println("C : "+C);
	}
	
	public static void main(String[] args) throws IOException {
		OSv1Extended os = new OSv1Extended();
		os.load();
		os.displayMemory();
	}

}
