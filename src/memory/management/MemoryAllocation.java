package memory.management;

import java.util.ArrayList;
import java.util.List;

class MemoryBlock {
    int id;
    int size;
    boolean allocated;

    MemoryBlock(int id, int size) {
        this.id = id;
        this.size = size;
        this.allocated = false;
    }
}

class Memory {
    List<MemoryBlock> memoryBlocks;

    Memory() {
        memoryBlocks = new ArrayList<>();
    }

    void addBlock(int id, int size) {
        memoryBlocks.add(new MemoryBlock(id, size));
    }

    void bestFit(int processId, int size) {
        int bestFitIndex = -1;
        int minFragmentation = Integer.MAX_VALUE;

        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size >= size && block.size - size < minFragmentation) {
                bestFitIndex = i;
                minFragmentation = block.size - size;
            }
        }

        if (bestFitIndex != -1) {
            MemoryBlock block = memoryBlocks.get(bestFitIndex);
            block.allocated = true;
            System.out.println("Process " + processId + " allocated to block " + block.id);
        } else {
            System.out.println("Process " + processId + " cannot be allocated");
        }
    }

    void firstFit(int processId, int size) {
        for (MemoryBlock block : memoryBlocks) {
            if (!block.allocated && block.size >= size) {
                block.allocated = true;
                System.out.println("Process " + processId + " allocated to block " + block.id);
                return;
            }
        }
        System.out.println("Process " + processId + " cannot be allocated");
    }

    void nextFit(int processId, int size, int lastAllocatedIndex) {
        for (int i = lastAllocatedIndex; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size >= size) {
                block.allocated = true;
                System.out.println("Process " + processId + " allocated to block " + block.id);
                return;
            }
        }
        for (int i = 0; i < lastAllocatedIndex; i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size >= size) {
                block.allocated = true;
                System.out.println("Process " + processId + " allocated to block " + block.id);
                return;
            }
        }
        System.out.println("Process " + processId + " cannot be allocated");
    }

    void worstFit(int processId, int size) {
        int worstFitIndex = -1;
        int maxFragmentation = Integer.MIN_VALUE;

        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (!block.allocated && block.size >= size && block.size - size > maxFragmentation) {
                worstFitIndex = i;
                maxFragmentation = block.size - size;
            }
        }

        if (worstFitIndex != -1) {
            MemoryBlock block = memoryBlocks.get(worstFitIndex);
            block.allocated = true;
            System.out.println("Process " + processId + " allocated to block " + block.id);
        } else {
            System.out.println("Process " + processId + " cannot be allocated");
        }
    }
}

public class MemoryAllocation {
    public static void main(String[] args) {
        Memory memory = new Memory();

        // Adding memory blocks
        memory.addBlock(1, 100);
        memory.addBlock(2, 50);
        memory.addBlock(3, 200);
        memory.addBlock(4, 150);

        // Best Fit
        System.out.println("Best Fit Allocation:");
        memory.bestFit(1, 70);
        memory.bestFit(2, 100);
        memory.bestFit(3, 80);

        // Reset memory allocation
        resetMemory(memory);

        // First Fit
        System.out.println("\nFirst Fit Allocation:");
        memory.firstFit(1, 70);
        memory.firstFit(2, 100);
        memory.firstFit(3, 80);

        // Reset memory allocation
        resetMemory(memory);

        // Next Fit
        System.out.println("\nNext Fit Allocation:");
        int lastAllocatedIndex = 0;
        lastAllocatedIndex = nextFitAllocation(memory, 1, 70, lastAllocatedIndex);
        lastAllocatedIndex = nextFitAllocation(memory, 2, 100, lastAllocatedIndex);
        lastAllocatedIndex = nextFitAllocation(memory, 3, 80, lastAllocatedIndex);

        // Reset memory allocation
        resetMemory(memory);

        // Worst Fit
        System.out.println("\nWorst Fit Allocation:");
        memory.worstFit(1, 70);
        memory.worstFit(2, 100);
        memory.worstFit(3, 80);
    }

    static int nextFitAllocation(Memory memory, int processId, int size, int lastAllocatedIndex) {
        memory.nextFit(processId, size, lastAllocatedIndex);
        for (int i = lastAllocatedIndex; i < memory.memoryBlocks.size(); i++) {
            if (memory.memoryBlocks.get(i).allocated) {
                return i + 1;
            }
        }
        return 0;
    }

    static void resetMemory(Memory memory) {
        for (MemoryBlock block : memory.memoryBlocks) {
            block.allocated = false;
        }
    }
}

