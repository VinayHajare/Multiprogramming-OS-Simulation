package phase1;
import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.util.Arrays;
import java.io.FileReader;
import java.io.BufferedReader;

class OS {
  private final int NUM_BLOCKS = 20;
  private final int NUM_WORDS = 10;
  // registers of the virtual machine
  int[] IR;  
  int IC;
  int SI;
  int[] R;
  boolean C;
  boolean isHalted;

  // memory of 100 locations, divided into 10 blocks each with 10 word wide
  int[][] memory;

  // reader and writer for I/O operations
  BufferedReader reader;
  PrintWriter writer;

  // Initialize the registers and I/O worker
  public void initialize() throws IOException {
	memory = new int[NUM_BLOCKS][NUM_WORDS];
    IC = 0;
    SI = 0;
    R = new int[4];
    IR = new int[4];
    C = false;
    isHalted = false;
    reader = new BufferedReader(new FileReader("D:\\Vinay Hajare\\Eclipse Project\\OS Course Project\\src\\phase1\\input.txt"));
    File file = new File("D:\\Vinay Hajare\\Eclipse Project\\OS Course Project\\src\\phase1\\output.txt");
    writer = new PrintWriter(file);
  }

  // Load the job from the file to the memory
  public void load() throws IOException {
    String line;
    int block = 0;

    while ((line = reader.readLine()) != null) {
      if(line.contains("$AMJ")) {
        continue;
      }else if(line.contains("$DTA")) {
        startExecution();
      }else if(line.contains("$END")) {
        break;
      }else{
    	  if (block == NUM_BLOCKS) {
              // Memory exceeded
              System.out.println("Memory exceeded. Aborting.");
              break;
          }
    	  
    	  if(isHalted) {
    		  break;
    	  }

          for (int i = 0; i<NUM_WORDS && i < line.length(); i++) {
              String asciiCode = line.substring(i, i + 1);
              int decimalValue = (int) asciiCode.charAt(0);
              memory[block][i] = decimalValue;
          }
          block++;
      }
    }
  }
  
  // start the execution of the program 
  private void startExecution() throws IOException {
	  IC = 0;
	  executeUserProgram();
  }
  
  // function to read the instructions
  private int[] fetchInstruction() {
	    int[] instruction = new int[4];
	    int block = IC / NUM_WORDS;
	    int word = IC % NUM_WORDS;
	  
	    // Read the first byte of opcode
	    instruction[0] = memory[block][word];
	    word++;
	    if (word == NUM_WORDS) {
	        block++;
	        word = 0;
	    }
	    if (block == NUM_BLOCKS) {
	        System.out.println("End of memory reached. Halting execution.");
	        SI = 3; // Halt
	        return instruction;
	    }

	    // Read the second byte of opcode
	    instruction[1] = memory[block][word];
	    word++;
	    if (word == NUM_WORDS) {
	        block++;
	        word = 0;
	    }
	    if (block == NUM_BLOCKS) {
	        System.out.println("End of memory reached. Halting execution.");
	        SI = 3; // Halt
	        return instruction;
	    }

	    // Read the first byte of operand
	    instruction[2] = memory[block][word];
	    word++;
	    if (word == NUM_WORDS) {
	        block++;
	        word = 0;
	    }
	    if (block == NUM_BLOCKS) {
	        System.out.println("End of memory reached. Halting execution.");
	        SI = 3; // Halt
	        return instruction;
	    }

	    // Read the second byte of operand
	    instruction[3] = memory[block][word];

	    IC += 4; // Move to the next instruction
	    return instruction;
	}

  // function to execute the user program
  private void executeUserProgram() throws IOException {
	    boolean loop = true;
	    while (loop) {
	        // Fetch instruction from memory
	        int[] instruction = fetchInstruction();
	        
	        // Decode instruction
	        char opcode1 = (char) instruction[0];
	        char opcode2 = (char) instruction[1];
	        char operand1 = (char) instruction[2];
	        char operand2 = (char) instruction[3];
	        
	        IR[0] = opcode1;
	        IR[1] = opcode2;
	        IR[2] = operand1;
	        IR[3] = operand2;
	        
	        // Execute instruction based on opcode
	        String opcode = "" + opcode1 + opcode2;
	        System.err.println(opcode+""+operand1+""+operand2);
	        if(Character.compare(opcode1, 'G') == 0 && Character.compare(opcode2, 'D') == 0) {
	        	SI = 1;
                MOS();
	        }else if(Character.compare(opcode1, 'P') == 0 && Character.compare(opcode2, 'D') == 0) {
	        	SI = 2;
                MOS();
	        }else if(Character.compare(opcode1, 'H') == 0 || Character.compare(opcode2, 'H') == 0) {
	        	SI = 3;
                MOS();
                loop = false;
                break;
	        }else if(Character.compare(opcode1, 'S') == 0 && Character.compare(opcode2, 'R') == 0) {
	        	storeRegister();
	        }else if(Character.compare(opcode1, 'L') == 0 && Character.compare(opcode2, 'R') == 0) {
	        	loadRegister();
	        }else if(Character.compare(opcode1, 'C') == 0 && Character.compare(opcode2, 'R') == 0) {
	        	compareRegister();
	        }else if(Character.compare(opcode1, 'B') == 0 && Character.compare(opcode2, 'T') == 0) {
	        	branchTo();
	        }else {
	        	System.err.println("Invalid opcode: " + opcode);
                break;
	        }
	    }
	}

  // function to handle read interrupt
  private void read() throws IOException {
	  	IR[3] = 0;
		int block = Integer.parseInt(String.valueOf((char)IR[2]));
		String line;
		
		if((line = reader.readLine()) != null) {
			if(line.contains("$END")) {
				System.err.println("OUT OF DATA !!!");
			}else {
				for(int i=0; i<NUM_WORDS && i<line.length(); i++) {
					String asciiCode = line.substring(i, i+1);
					int decimalValue = (int) asciiCode.charAt(0);
					memory[block][i] = decimalValue;
				}
			}
		}else {
			System.err.println("End of the input file reached !!!");
		}
	}

  // function to handle write interrupt
  private void write() {
	  	IR[3] = 0;
		int block = Integer.parseInt(String.valueOf((char)IR[2]));
		for(int i=0; i<NUM_WORDS; i++) {
			writer.print((char)memory[block][i]);
		}
		writer.println();
		writer.flush(); 
  }
	
  // function to handle termination interrupt
  private void terminate() throws IOException {
	    // Write 2 blank lines in the output file
	  	this.isHalted = true;
	    writer.println();
	    writer.println();
	    writer.flush();
	    writer.close();
  }
  
  // function to store register into memory
  private void storeRegister() {
	  IR[3] = 0;
	  int block = Character.getNumericValue((char)IR[2]);
	  
	  for(int i=0; i<4; i++) {
		  memory[block][i] = R[i];
	  }
  }
  
  // function to load register from memory
  private void loadRegister() {
	  IR[3] = 0;
	  int block = Character.getNumericValue((char)IR[2]);
	  
	  for(int i=0; i<4; i++) {
		  R[i] = memory[block][i];
	  }
  }

  // function to compare register with memory location
  private void compareRegister() {
	  IR[3] = 0;
	  int block = Character.getNumericValue((char)IR[2]);
	  int temp[] = new int[4];
	  
	  for(int i=0; i<4; i++) {
		  temp[i] = memory[block][i];
	  }
	  C = Arrays.equals(temp, this.R); 
  }
  
  // function to perform branching instruction
  private void branchTo() {
	  if(C) {
		int operand1 = IR[2];
	  	int operand2 = IR[3];
	  	int target = ((Character.getNumericValue((char)operand1) * 10) + Character.getNumericValue((char)operand2))-1;
	  	this.IC = target;
	  }
  }
  
  // function to handle interrupts
  private void MOS() throws IOException {
        if (SI == 1) {
            read();
        } else if (SI == 2) {
            write();
        } else if (SI == 3) {
            terminate();
        }
	}
	
  // function to display memory 
  public void displayMemory() {
	    for (int block = 0; block < NUM_BLOCKS; block++) {
	        System.out.print("Block " + block + ": ");
	        for (int word = 0; word < NUM_WORDS; word++) {
	            int decimalValue = memory[block][word];
	            char asciiChar = (char) decimalValue;
	            String asciiString = Character.toString(asciiChar);
	            System.out.print(asciiString + " ");
	        }
	        System.out.println();
	    }
	    
	    System.out.print("R : ");
	    for(int i = 0; i<4; i++) {
	    	int decimalValue = R[i];
	    	char asciiChar = (char) decimalValue;
	    	String asciiString = Character.toString(asciiChar);
	    	System.out.print(asciiString+" ");
	    }
	    
	    System.out.println();
	    System.out.println("C : "+this.C);
	}

}

public class OSv1 {
  public static void main(String[] args) throws IOException {
    OS os = new OS();
    os.initialize();
    os.load();
    os.displayMemory();
  }
}