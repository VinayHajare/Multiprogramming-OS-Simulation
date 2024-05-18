package memory.management;

import java.util.*;

public class DiskSchedulingAlgorithms {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Enter number of cylinders: ");
        int numCylinders = scanner.nextInt();
        
        System.out.print("Enter current head position: ");
        int headPosition = scanner.nextInt();
        
        System.out.print("Enter number of requests: ");
        int numRequests = scanner.nextInt();
        
        int[] requests = new int[numRequests];
        System.out.print("Enter the request queue: ");
        for (int i = 0; i < numRequests; i++) {
            requests[i] = scanner.nextInt();
        }
        
        // FCFS Algorithm
        FCFS(headPosition, requests);
        
        // SCAN Algorithm
        System.out.print("Enter direction (0 for left, 1 for right): ");
        int direction = scanner.nextInt();
        SCAN(headPosition, requests, numCylinders, direction);
        
        // C-SCAN Algorithm
        CSCAN(headPosition, requests, numCylinders, direction);
        
        // SSTF Algorithm
        SSTF(headPosition, requests);
        
        scanner.close();
    }
    
    // FCFS Algorithm
    public static void FCFS(int headPosition, int[] requests) {
        int totalSeekTime = 0;
        System.out.println("\nFCFS Algorithm:");
        for (int request : requests) {
            int seekTime = Math.abs(headPosition - request);
            System.out.println("Move from " + headPosition + " to " + request + " with seek time: " + seekTime);
            totalSeekTime += seekTime;
            headPosition = request;
        }
        System.out.println("Total seek time: " + totalSeekTime);
    }
    
    // SCAN Algorithm
    public static void SCAN(int headPosition, int[] requests, int numCylinders, int direction) {
        int totalSeekTime = 0;
        System.out.println("\nSCAN Algorithm:");
        List<Integer> leftRequests = new ArrayList<>();
        List<Integer> rightRequests = new ArrayList<>();
        for (int request : requests) {
            if (request < headPosition)
                leftRequests.add(request);
            else
                rightRequests.add(request);
        }
        Collections.sort(leftRequests);
        Collections.sort(rightRequests);
        
        if (direction == 0) {
            for (int i = leftRequests.size() - 1; i >= 0; i--) {
                int seekTime = Math.abs(headPosition - leftRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + leftRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = leftRequests.get(i);
            }
            totalSeekTime += headPosition;
            System.out.println("Move from " + headPosition + " to 0 with seek time: " + headPosition);
            headPosition = 0;
            for (int i = 0; i < rightRequests.size(); i++) {
                int seekTime = Math.abs(headPosition - rightRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + rightRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = rightRequests.get(i);
            }
        } else {
            for (int i = 0; i < rightRequests.size(); i++) {
                int seekTime = Math.abs(headPosition - rightRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + rightRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = rightRequests.get(i);
            }
            totalSeekTime += numCylinders - headPosition - 1;
            System.out.println("Move from " + headPosition + " to " + (numCylinders - 1) + " with seek time: " + (numCylinders - headPosition - 1));
            headPosition = numCylinders - 1;
            for (int i = leftRequests.size() - 1; i >= 0; i--) {
                int seekTime = Math.abs(headPosition - leftRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + leftRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = leftRequests.get(i);
            }
        }
        System.out.println("Total seek time: " + totalSeekTime);
    }
    
    // C-SCAN Algorithm
    public static void CSCAN(int headPosition, int[] requests, int numCylinders, int direction) {
        int totalSeekTime = 0;
        System.out.println("\nC-SCAN Algorithm:");
        List<Integer> leftRequests = new ArrayList<>();
        List<Integer> rightRequests = new ArrayList<>();
        for (int request : requests) {
            if (request < headPosition)
                leftRequests.add(request);
            else
                rightRequests.add(request);
        }
        Collections.sort(leftRequests);
        Collections.sort(rightRequests);
        
        if (direction == 0) {
            for (int i = leftRequests.size() - 1; i >= 0; i--) {
                int seekTime = Math.abs(headPosition - leftRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + leftRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = leftRequests.get(i);
            }
            totalSeekTime += headPosition;
            System.out.println("Move from " + headPosition + " to 0 with seek time: " + headPosition);
            headPosition = 0;
            totalSeekTime += numCylinders - 1;
            System.out.println("Move from " + 0 + " to " + (numCylinders - 1) + " with seek time: " + (numCylinders - 1));
            headPosition = numCylinders - 1;
            for (int i = 0; i < rightRequests.size(); i++) {
                int seekTime = Math.abs(headPosition - rightRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + rightRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = rightRequests.get(i);
            }
        } else {
            for (int i = 0; i < rightRequests.size(); i++) {
                int seekTime = Math.abs(headPosition - rightRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + rightRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = rightRequests.get(i);
            }
            totalSeekTime += numCylinders - headPosition - 1;
            System.out.println("Move from " + headPosition + " to " + (numCylinders - 1) + " with seek time: " + (numCylinders - headPosition - 1));
            headPosition = numCylinders - 1;
            totalSeekTime += headPosition;
            System.out.println("Move from " + (numCylinders - 1) + " to 0 with seek time: " + headPosition);
            headPosition = 0;
            for (int i = leftRequests.size() - 1; i >= 0; i--) {
                int seekTime = Math.abs(headPosition - leftRequests.get(i));
                System.out.println("Move from " + headPosition + " to " + leftRequests.get(i) + " with seek time: " + seekTime);
                totalSeekTime += seekTime;
                headPosition = leftRequests.get(i);
            }
        }
        System.out.println("Total seek time: " + totalSeekTime);
    }
    
    // SSTF Algorithm
    public static void SSTF(int headPosition, int[] requests) {
        int totalSeekTime = 0;
        System.out.println("\nSSTF Algorithm:");
        List<Integer> remainingRequests = new ArrayList<>();
        for (int request : requests) {
            remainingRequests.add(request);
        }
        while (!remainingRequests.isEmpty()) {
            int minDistance = Integer.MAX_VALUE;
            int nextRequest = 0;
            for (int i = 0; i < remainingRequests.size(); i++) {
                int distance = Math.abs(headPosition - remainingRequests.get(i));
                if (distance < minDistance) {
                    minDistance = distance;
                    nextRequest = i;
                }
            }
            int seekTime = Math.abs(headPosition - remainingRequests.get(nextRequest));
            System.out.println("Move from " + headPosition + " to " + remainingRequests.get(nextRequest) + " with seek time: " + seekTime);
            totalSeekTime += seekTime;
            headPosition = remainingRequests.get(nextRequest);
            remainingRequests.remove(nextRequest);
        }
        System.out.println("Total seek time: " + totalSeekTime);
    }
}

