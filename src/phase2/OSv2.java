package phase2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class OSv2 {
	
		// registers of the virtual machine
		private char[] IR;
		private char[] R;
		private int SI;
		private int IC;
		private boolean C;
		private int PTR;
		private int TI;
		private int PI;
		private PCB pcb;
		private boolean[] isAllocated;
		// Memory of the Virtual machine
		private char[][] M;
		// reader and writer for I/O operations
		BufferedReader reader;
		PrintWriter writer;
		// Buffer between memory and IO
		private char[] buffer;
		// Auxiliary variables
		private int pageCounter;
		private boolean isTerminated;
		
		// Constructor of the OS
		public OSv2() throws IOException {
			// initializing the registers
			IR = new char[4];
			R = new char[4];
			IC = 0;
			SI = 0;
			TI = 0;
			PI = 0;
			C = false;
			// Map to keep track of allocated pages
			isAllocated = new boolean[30];
			// initialize memory
			M = new char[300][4];
			
			// initialize the IO workers
			buffer = new char[40];
			reader = new BufferedReader(new FileReader("D:\\Vinay Hajare\\Eclipse Project\\OS Course Project\\src\\phase2\\input.txt"));
		    File file = new File("D:\\Vinay Hajare\\Eclipse Project\\OS Course Project\\src\\phase2\\output.txt");
		    writer = new PrintWriter(file);
		}
		
		// Function to initialize the registers and memory
		public void initialize(int pageNumber) throws IOException {
			for (int i = 0; i < 300; i++) {
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
	        TI = 0;
	        PI = 0;
	        C = false;
	        
	        pageCounter = -1;
	        isTerminated = false;
	        
	        isAllocated[pageNumber] = true;
	        
	        for (int j = 0; j < 40; j++) {
	            buffer[j] = ' ';
	        }
		}
		
		// Function to allocate a random page from available pages
		private int allocatePage() {
			Random random = new Random();
			int pageNumber = random.nextInt(30);
			while(isAllocated[pageNumber]) {
				pageNumber = random.nextInt(30);
			}
			isAllocated[pageNumber] = true;
			return pageNumber;
		}
		
		// Function to map a virtual address to corresponding  physical address
		int mapAddress(int virtualAddress) {
			if (virtualAddress < 0 || virtualAddress > 99) {
				this.SI = 0;
	            this.PI = 2;
	            this.TI = 0;
				return -2;
	        }
			
			int pageTableEntry = this.PTR * 10 + (virtualAddress/10);
			int addressOfPTE = Character.getNumericValue(M[pageTableEntry][2]) * 10 + Character.getNumericValue(M[pageTableEntry][3]);
			int physicalAddress = addressOfPTE * 10 + (virtualAddress % 10);
			
			if(physicalAddress < 0 || physicalAddress > 299 ) {
				this.SI = 0;
				this.PI = 3;
				this.TI = 0;
				return -1;
			}
			
			return physicalAddress;
		}
		
		// Function to load the program and data cards
		public void load() throws IOException {
			String line = null;
			
			while((line = reader.readLine()) != null) {
				for (int j = 0; j < 40; j++) {
		            buffer[j] = ' ';
		        }
				
				for(int i = 0; i < line.length(); i++) {
					buffer[i] = line.charAt(i);
				}
				
				if(buffer[0] == '$' && buffer[1] == 'A' && buffer[2] == 'M' && buffer[3] == 'J') {
					int pageNumber = allocatePage();
					pageCounter++;
					int jobId = Character.getNumericValue(buffer[4]) * 1000 + Character.getNumericValue(buffer[3]) * 100 + Character.getNumericValue(buffer[2]) * 10 + Character.getNumericValue(buffer[1]);
					int TTL = Character.getNumericValue(buffer[4]) * 1000 + Character.getNumericValue(buffer[3]) * 100 + Character.getNumericValue(buffer[2]) * 10 + Character.getNumericValue(buffer[1]);
					int TLL = Character.getNumericValue(buffer[4]) * 1000 + Character.getNumericValue(buffer[3]) * 100 + Character.getNumericValue(buffer[2]) * 10 + Character.getNumericValue(buffer[1]);
					pcb = new PCB(jobId, TTL, TLL);
					this.PTR = pageNumber;
					initialize(pageNumber);
					for(int i = 0; i < 10; i++) {
						for(int j = 0; j < 4; j++) {
							M[PTR + i][j] = '*';
						}
					}
				}else if(buffer[0] == '$' && buffer[1] == 'D' && buffer[2] == 'T' && buffer[3] == 'A') {
					startExecution();
				}else if(buffer[0] == '$' && buffer[1] == 'E' && buffer[2] == 'N' && buffer[3] == 'D') {
					continue;
				}else {
					int page = allocatePage();
					M[PTR+pageCounter][2] = Character.forDigit(page / 10, 10);
					M[PTR+pageCounter][3] = Character.forDigit(page % 10, 10);
					page *= 10;
					pageCounter++;
					
					int k = 0; 
					
					for(int i = 0; i < 10; i++) {
						
						if (k == 40 || buffer[k] == '\0' || buffer[k] == '\n') {
	                        isTerminated = true;
							break;
	                    }
						
						for(int j = 0; j<4; j++) {
							if (k == 40 || buffer[k] == '\0' || buffer[k] == '\n') {
								isTerminated = true;
								break;
		                    }
							
							if(buffer[k] == 'H') {
								M[page + i][j] = buffer[k++];
								break;
							}
							
							M[page + i][j] = buffer[k++];
						}
						
						if (k == 40 || buffer[k] == '\0' || buffer[k] == '\n') {
							isTerminated = true;
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
			isTerminated = false;
			while(!isTerminated) {
				int physicalAddress = mapAddress(IC);
				
				if(physicalAddress == -2) {
					System.err.println("Operand Error :: Invalid virtual address!!");
					TI = 0;
					PI = 2;
					SI = 0;
					MOS(physicalAddress);
					continue;
				}
				
				if(physicalAddress == -1) {
					System.err.println("Page Fault");
					TI = 0;
					PI = 3;
					SI = 0;
					MOS(physicalAddress);
					continue;
				}
				
				IR = M[physicalAddress];
				IC++;
				System.err.println(Arrays.toString(IR));
				int operand = Character.getNumericValue(IR[2])*10 + Character.getNumericValue(IR[3]);
				int realOperand = mapAddress(operand);
				
				if(IR[0] == 'G' && IR[1] == 'D') {
					if(realOperand == -1) {
						System.err.println("Page Fault");
						TI = 0;
						PI = 3;
						SI = 0;
						MOS(realOperand);
					}
					SI = 1;
					TI = 0;
					PI = 0;
					MOS(realOperand);
				}else if(IR[0] == 'P' && IR[1] == 'D') {
					SI = 2;
					TI = 0;
					PI = 0;
					MOS(realOperand);
				}else if(IR[0] == 'H') {
					SI = 3;
					TI = 0;
					PI = 0;
					MOS(realOperand);
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
				
				pcb.TTC++;
				if(pcb.TTC >= pcb.TTL) {
					SI = 1;
					TI = 2;
					MOS(realOperand);
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
			
			if((line = reader.readLine()) != null) {
				if(line.contains("$END")) {
					terminate(1);
				}else {
					for(int i=0; i<line.length(); i++) {
						buffer[i] = line.charAt(i);
					}
					
					int virtualAddress = Character.getNumericValue(IR[2])*10 + Character.getNumericValue(IR[3]);
					int physicalAddress = mapAddress(virtualAddress);
					
					if(physicalAddress == -1) {
						TI = 0;
						PI = 3;
						SI = 0;
						MOS(physicalAddress);
					}
					
					int newPhysicalAddress = mapAddress(this.IC);
					
					for(int i = 0; i<10; i++) {
						for(int j = 0; j<4; j++) {
							if(k < 40) {
								M[newPhysicalAddress + i][j] = buffer[k];
								k++;
							}
						}
					}
				}
			}else {
				System.err.println("End of the input file reached !!!");
			}
		}
		
		// function to handle the write interrupt
		private void write() {
			pcb.LLC++;
			if(pcb.LLC > pcb.TLL) {
				terminate(2);
			}else {
				// Clear the buffer
				for (int i = 0; i < 40; i++) {
					buffer[i] = ' ';
				}
				int k = 0;
				int virtualAddress = Character.getNumericValue(IR[2])*10 + Character.getNumericValue(IR[3]);
				int physicalAddress = mapAddress(virtualAddress);
			
				for(int i = 0; i<10; i++) {
					for(int j = 0; j<4; j++) {
						if(k < 40) {
							buffer[k] = M[physicalAddress + i][j];
							k++;
						}
					}
				}
			
				writer.println(new String(buffer));
				writer.flush();
			}
		}
		
		// function to handle the terminate interrupt
		private void terminate(int... EM) {
			writer.println();
		    writer.println();
		    
		    writer.println("Job Id\t:\t"+pcb.jobId);
		    for(int i : EM) {
		    	String message = "";
		    	if(EM[i] == 0) {
		    		message = "NO ERROR";
		    	}else if(EM[i] == 1) {
		    		message = "OUT OF DATA";
		    	}else if(EM[i] == 2) {
		    		message = "LINE LIMIT EXCEEDED";
		    	}else if(EM[i] == 3) {
		    		message = "TIME LIMIT EXCEEDED";
		    	}else if(EM[i] == 4) {
		    		message = "OPERATION CODE ERROR";
		    	}else if(EM[i] == 5) {
		    		message = "OPERAND ERROR";
		    	}else if(EM[i] == 6) {
		    		message = "INVALID PAGE FAULT";
		    	}
		    	writer.println(message);
		    }
		    writer.println("IC\t:\t"+this.IC);
		    writer.println("IR\t:\t"+Arrays.toString(this.IR));
		    writer.println("TTC\t:\t"+this.pcb.TTC);
		    writer.println("LLC\t:\t"+this.pcb.LLC);
		    writer.flush();
		    writer.close();
		}
		
		// Store the register into memory
		private void storeRegister() throws IOException{
			int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
			int realOperand = mapAddress(operand);
			
			if(realOperand == -2) {
				System.err.println("Operand Error :: Invalid virtual address!!");
				TI = 0;
				PI = 2;
				SI = 0;
				MOS(realOperand);
			}
			
			if(realOperand == -1) {
				System.err.println("Page Fault");
				TI = 0;
				PI = 3;
				SI = 0;
				MOS(realOperand);
			}
			
			for(int i = 0; i<4; i++) {
				M[realOperand][i] = R[i];
			}
			
		}
		
		// Load the memory 
		private void loadRegister() {
			int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
			int realOperand = mapAddress(operand);
			
			for(int i = 0; i<4; i++) {
				 R[i] = M[realOperand][i];
			}
		}
		
		// Compare the register with the memory
		private void compareRegister() {
			int operand = Character.getNumericValue(IR[2]) * 10 + Character.getNumericValue(IR[3]);
			int realOperand = mapAddress(operand);
			char temp[] = new char[4];
			
			for(int i = 0; i<4; i++) {
				temp[i] = M[realOperand][i];
			}
			
			C = Arrays.equals(temp, R);
		}
		
		// Branch to give instruction
		private void branchTo(){
			if(C) {
				int newIC = Character.getNumericValue(IR[2])*10 + Character.getNumericValue(IR[3]);
				IC = newIC;
			}
		}
		
		// Master Operating System
		private void MOS(int physicalAddress) throws IOException {
			if(TI == 0 && SI == 1) {
				read();
			}else if(TI == 0 && SI == 2) {
				write();
			}else if(TI == 0 && SI == 3) {
				terminate(0);
			}else if(TI == 2 && SI == 1){
				terminate(3);
			}else if(TI == 2 && SI == 2){
				terminate(3);
			}else if(TI == 3 && SI == 3){
				terminate(0);
			}
			
			if(TI == 0 && PI == 1) {
				terminate(4);
			}else if(TI == 0 && PI == 2) {
				terminate(5);
			}else if(TI == 0 && PI == 3) {
				if(R[0] == 'S' && R[1] == 'R' || R[0] == 'G' && R[1] == 'D') {
					System.err.println("Valid page fault occurred!!!");
					int pageNumber = allocatePage();
					M[PTR + pageCounter][2] = (char) ((pageNumber / 10) + '0');
					M[PTR + pageCounter][3] = (char) ((pageNumber % 10) + '0');
					pageCounter++;
					IC--;
				}else {
					System.err.println("Invalid page fault occurred!!!");
					terminate(6);
				}
			}else if(TI == 2 && PI == 1) {
				terminate(3, 4);
			}else if(TI == 2 && PI == 2) {
				terminate(3, 5);
			}else if(TI == 3 && PI == 3) {
				terminate(3);
			}
		}
		
		public void displayMemory() {
			
			for (int block = 0; block < 300; block++) {
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
		    System.out.println("PTR : "+PTR);
		    System.out.println(Arrays.toString(this.isAllocated));
		}
		
		public static void main(String[] args) throws IOException{
			OSv2 os = new OSv2();
			os.load();
			os.displayMemory();
		}

}

class PCB{
	int jobId;
	int TTL;
	int TLL;
	int LLC;
	int TTC;
	
	public PCB(int jobId, int TTL, int TLL) {
		this.jobId = jobId;
		this.TTL = TTL;
		this.TLL = TLL;
		LLC = 0;
		TTC = 0;
	}
}
