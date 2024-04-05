package deadlock.avoidance;

import java.util.Arrays;

public class BankersAlgorithm {
	
	private int[][] max;
    private int[][] allocation;
    private int[] available;
    private int[][] need;
    private int numOfProcesses;
    private int numOfResources;

    public BankersAlgorithm(int[][] max, int[][] allocation, int[] available) {
        this.max = max;
        this.allocation = allocation;
        this.available = available;
        this.numOfProcesses = max.length;
        this.numOfResources = available.length;
        this.need = new int[numOfProcesses][numOfResources];

        // Calculate need matrix
        for (int i = 0; i < numOfProcesses; i++) {
            for (int j = 0; j < numOfResources; j++) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
    }

    public void runBankersAlgorithm() {
        boolean[] finished = new boolean[numOfProcesses];
        int[] safeSequence = new int[numOfProcesses];
        Arrays.fill(safeSequence, -1);
        int count = 0;

        while (count < numOfProcesses) {
            boolean found = false;

            // Try to find a process which can be allocated
            for (int i = 0; i < numOfProcesses; i++) {
                if (!finished[i] && check(i)) {
                    // Allocate resources to process i
                    for (int j = 0; j < numOfResources; j++) {
                        available[j] += allocation[i][j];
                    }
                    safeSequence[count++] = i;
                    finished[i] = true;
                    found = true;

                    // Print debugging information
                    printDebugInfo(i, finished, safeSequence);

                    break;
                }
            }

            if (!found) {
                System.err.println("System is in unsafe state. Unable to allocate more resources.");
                return;
            }
        }

        // Print safe sequence
        System.out.println("System is in safe state.");
        System.out.println("Safe sequence: " + Arrays.toString(safeSequence));
    }

    private boolean check(int process) {
        // Check if need of process can be satisfied with available resources
        for (int i = 0; i < numOfResources; i++) {
            if (need[process][i] > available[i]) {
                return false;
            }
        }
        return true;
    }

    private void printDebugInfo(int process, boolean[] finished, int[] safeSequence) {
        System.out.println("Checking Process " + process);
        System.out.println("Need: " + Arrays.toString(need[process]));
        System.out.println("Allocation: " + Arrays.toString(allocation[process]));
        System.out.println("Max: " + Arrays.toString(max[process]));
        System.out.println("Available resources: " + Arrays.toString(available));
        
        StringBuilder finishStatus = new StringBuilder("Finish Status: ");
        for (boolean isFinished : finished) {
            finishStatus.append(isFinished ? "F " : "NF ");
        }
        System.out.println(finishStatus);
        System.out.println("Current Safe sequence: " + Arrays.toString(safeSequence));
        System.out.println("-*-*-*-*-*-*-*--*-*-*-*-*-*-*--*-*-*-*-*-*-*-");
        System.out.println();
    }

	
	public static void main(String[] args) {
        int[][] max = {
        		{7, 5, 3}, 
        		{3, 2, 2}, 
        		{9, 0, 2}, 
        		{2, 2, 2}, 
        		{4, 3, 3}
        	};
        
        int[][] allocation = {
        		{0, 1, 0}, 
        		{2, 0, 0}, 
        		{3, 0, 2}, 
        		{2, 1, 1}, 
        		{0, 0, 2}
        	};
        
        int[] available = {3, 3, 2};

        BankersAlgorithm banker = new BankersAlgorithm(max, allocation, available);
        banker.runBankersAlgorithm();
    }
}