package deadlock.detection;

import java.util.Arrays;

public class DeadlockDetector {

    private int[][] allocation;
    private int[][] request;
    private int[] available;
    private boolean[] finished;
    private int numOfProcesses;
    private int numOfResources;

    public DeadlockDetector(int[][] allocation, int[][] request, int[] available) {
        this.allocation = allocation;
        this.request = request;
        this.available = available;
        this.numOfProcesses = allocation.length;
        this.numOfResources = available.length;
        this.finished = new boolean[numOfProcesses];

        // Initialize finished array
        for (int i = 0; i < numOfProcesses; i++) {
            boolean isFinished = true;
            for (int j = 0; j < numOfResources; j++) {
                if (allocation[i][j] != 0) {
                    isFinished = false;
                    break;
                }
            }
            finished[i] = isFinished;
        }
    }

    public void detectDeadlock() {
        int[] work = Arrays.copyOf(available, numOfResources);

        boolean noDeadlock = true;
        System.out.println("__________Initial_____________");
        System.out.println("Work: " + Arrays.toString(work));
        System.out.println("Available: " + Arrays.toString(available));
        System.out.println("Finish Status: " + Arrays.toString(finished));
        System.out.println("______________________________");
        
        while (noDeadlock) {
            noDeadlock = false;
            for (int i = 0; i < numOfProcesses; i++) {
                if (!finished[i] && canAllocate(i, work)) {
                    noDeadlock = true;
                    finished[i] = true;
                    allocateResources(i, work);

                    // Print debugging information
                    printDebugInfo(work, i);
                }
            }
        }

        for (int i = 0; i < numOfProcesses; i++) {
            if (!finished[i]) {
                System.err.println("Deadlock detected. Process P" + i + " is deadlocked.");
                return;
            }
        }

        System.out.println("No deadlock detected.");
    }

    private boolean canAllocate(int process, int[] work) {
        for (int i = 0; i < numOfResources; i++) {
            if (request[process][i] > work[i]) {
                return false;
            }
        }
        
        return true;
    }


    private void allocateResources(int process, int[] work) {
        for (int i = 0; i < numOfResources; i++) {
            work[i] += allocation[process][i];
            available[i] = work[i];
        }
    }

    private void printDebugInfo(int[] work, int process) {
        System.out.println("Allocating resources to Process P" + process);
        System.out.println("Work: " + Arrays.toString(work));
        System.out.println("Available: " + Arrays.toString(available));
        System.out.println("Request for Process P" + process + ": " + Arrays.toString(request[process]));
        System.out.println("Allocation for Process P" + process + ": " + Arrays.toString(allocation[process]));
        System.out.println("Finish Status: " + Arrays.toString(finished));
        System.out.println("--------------------------------------------------");
    }

    public static void main(String[] args) {
        int[][] allocation = {
                {0, 1, 0},
                {2, 0, 0},
                {3, 0, 3},
                {3, 1, 1},
                {0, 0, 2}
        };

        int[][] request = {
                {0, 0, 0},
                {2, 0, 2},
                {0, 0, 0},
                {1, 0, 0},
                {0, 0, 2}
        };

        int[] available = {0, 0, 0};

        DeadlockDetector deadlockDetector = new DeadlockDetector(allocation, request, available);
        deadlockDetector.detectDeadlock();
    }
}

