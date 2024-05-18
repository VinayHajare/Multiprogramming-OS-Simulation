package memory.management;

import java.util.*;

public class PageReplacementAlgorithms {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of frames: ");
        int numFrames = scanner.nextInt();

        System.out.print("Enter number of pages: ");
        int numPages = scanner.nextInt();

        int[] pageReferenceString = new int[numPages];
        System.out.print("Enter page reference string: ");
        for (int i = 0; i < numPages; i++) {
            pageReferenceString[i] = scanner.nextInt();
        }

        FIFO(pageReferenceString, numFrames);
        LRU(pageReferenceString, numFrames);
        Optimal(pageReferenceString, numFrames);

        scanner.close();
    }

    // FIFO Algorithm
    public static void FIFO(int[] pageReferenceString, int numFrames) {
        int totalPageFaults = 0;
        Queue<Integer> frameQueue = new LinkedList<>();
        Set<Integer> frameSet = new HashSet<>();

        System.out.println("\nFIFO Algorithm:");

        for (int page : pageReferenceString) {
            if (!frameSet.contains(page)) {
                totalPageFaults++;
                if (frameQueue.size() == numFrames) {
                    int removedPage = frameQueue.poll();
                    frameSet.remove(removedPage);
                }
                frameQueue.offer(page);
                frameSet.add(page);
            }
            System.out.println("Frame: " + frameQueue);
        }

        System.out.println("Total page faults: " + totalPageFaults);
    }

    // LRU Algorithm
    public static void LRU(int[] pageReferenceString, int numFrames) {
        int totalPageFaults = 0;
        int[] frames = new int[numFrames];
        int[] lastUsed = new int[numFrames];
        Arrays.fill(frames, -1);
        Arrays.fill(lastUsed, Integer.MAX_VALUE);

        System.out.println("\nLRU Algorithm:");

        for (int page : pageReferenceString) {
            boolean pageFault = true;
            for (int i = 0; i < numFrames; i++) {
                if (frames[i] == page) {
                    pageFault = false;
                    lastUsed[i] = totalPageFaults;
                    break;
                }
            }

            if (pageFault) {
                int indexToReplace = 0;
                int oldest = Integer.MAX_VALUE;
                for (int i = 0; i < numFrames; i++) {
                    if (lastUsed[i] < oldest) {
                        oldest = lastUsed[i];
                        indexToReplace = i;
                    }
                }
                frames[indexToReplace] = page;
                lastUsed[indexToReplace] = totalPageFaults;
                totalPageFaults++;
            }

            System.out.println("Frame: " + Arrays.toString(frames));
        }

        System.out.println("Total page faults: " + totalPageFaults);
    }

    // Optimal Algorithm
    public static void Optimal(int[] pageReferenceString, int numFrames) {
        int totalPageFaults = 0;
        int[] frames = new int[numFrames];
        int[] nextUse = new int[numFrames];
        boolean[] pageInFrame = new boolean[numFrames];

        Arrays.fill(frames, -1);
        Arrays.fill(nextUse, Integer.MAX_VALUE);

        System.out.println("\nOptimal Algorithm:");

        for (int i = 0; i < pageReferenceString.length; i++) {
            int currentPage = pageReferenceString[i];
            boolean pageFault = true;

            for (int j = 0; j < numFrames; j++) {
                if (frames[j] == currentPage) {
                    pageInFrame[j] = true;
                    pageFault = false;
                    break;
                }
            }

            if (pageFault) {
                int indexToReplace = -1;
                for (int j = 0; j < numFrames; j++) {
                    if (!pageInFrame[j]) {
                        indexToReplace = j;
                        break;
                    }
                    if (nextUse[j] == Integer.MAX_VALUE) {
                        indexToReplace = j;
                        break;
                    }
                    if (nextUse[j] > nextUse[indexToReplace]) {
                        indexToReplace = j;
                    }
                }

                frames[indexToReplace] = currentPage;
                nextUse[indexToReplace] = nextUsage(pageReferenceString, i, currentPage);
                totalPageFaults++;
            }

            pageInFrame = new boolean[numFrames];
            System.out.println("Frame " + (i + 1) + ": " + Arrays.toString(frames));
        }
        System.out.println("Total page faults: " + totalPageFaults);
    }

    // Helper method to find the next usage of a page in the page reference string
    public static int nextUsage(int[] pageReferenceString, int currentIndex, int page) {
        for (int i = currentIndex + 1; i < pageReferenceString.length; i++) {
            if (pageReferenceString[i] == page) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }
}
