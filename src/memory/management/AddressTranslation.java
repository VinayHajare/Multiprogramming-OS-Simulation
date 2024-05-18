package memory.management;

import java.util.HashMap;
import java.util.Map;

// Segment Table Entry
class SegmentTableEntry {
    int segmentNumber;
    int baseAddress;
    int limit;

    SegmentTableEntry(int segmentNumber, int baseAddress, int limit) {
        this.segmentNumber = segmentNumber;
        this.baseAddress = baseAddress;
        this.limit = limit;
    }
}

// Page Table Entry
class PageTableEntry {
    int pageNumber;
    int frameNumber;

    PageTableEntry(int pageNumber, int frameNumber) {
        this.pageNumber = pageNumber;
        this.frameNumber = frameNumber;
    }
}

public class AddressTranslation {
	// Segment and Page size
	static private final int SEGMENT_SIZE = 1024;
	static private final int PAGE_SIZE = 256;
	
    // Segment Table
    static Map<Integer, SegmentTableEntry> segmentTable;

    // Page Table
    static Map<Integer, PageTableEntry> pageTable;

    static {
        // Initialize Segment Table
        segmentTable = new HashMap<>();
        segmentTable.put(0, new SegmentTableEntry(0, 0, 1023));   // Segment 0
        segmentTable.put(1, new SegmentTableEntry(1, 1024, 511)); // Segment 1
        segmentTable.put(2, new SegmentTableEntry(2, 1536, 255)); // Segment 2
        segmentTable.put(3, new SegmentTableEntry(3, 1792, 511)); // Segment 3

        // Initialize Page Table
        pageTable = new HashMap<>();
        pageTable.put(0, new PageTableEntry(0, 2)); // Page 0
        pageTable.put(1, new PageTableEntry(1, 3)); // Page 1
        pageTable.put(2, new PageTableEntry(2, 1)); // Page 2
        pageTable.put(3, new PageTableEntry(3, 0)); // Page 3
    }

    // Function to perform address translation using segmentation
    static int translateAddressSegmentation(int logicalAddress) {
        int segmentNumber = logicalAddress / SEGMENT_SIZE; // Calculate segment number
        int offset = logicalAddress % SEGMENT_SIZE;       // Calculate offset

        SegmentTableEntry segmentTableEntry = segmentTable.get(segmentNumber);
        if (segmentTableEntry != null) {
            if (offset < segmentTableEntry.limit) {
                int physicalAddress = segmentTableEntry.baseAddress + offset;
                return physicalAddress;
            } else {
                System.out.println("Segmentation Fault: Offset exceeds segment limit");
                return -1;
            }
        } else {
            System.out.println("Segmentation Fault: Segment number not found in segment table");
            return -1;
        }
    }

    // Function to perform address translation using paging
    static int translateAddressPaging(int logicalAddress) {
        int pageNumber = logicalAddress / PAGE_SIZE; // Calculate page number
        int offset = logicalAddress % PAGE_SIZE;    // Calculate offset

        PageTableEntry pageTableEntry = pageTable.get(pageNumber);
        if (pageTableEntry != null) {
            int physicalAddress = (pageTableEntry.frameNumber * 256) + offset;
            return physicalAddress;
        } else {
            System.out.println("Page Fault: Page number not found in page table");
            return -1;
        }
    }

    public static void main(String[] args) {
        // Logical address to be translated
        int logicalAddress = 1079;

        // Translate logical address to physical address using segmentation
        int physicalAddressSegmentation = translateAddressSegmentation(logicalAddress);

        if (physicalAddressSegmentation != -1) {
            System.out.println("Physical Address (Segmentation): " + physicalAddressSegmentation);
        }

        // Translate logical address to physical address using paging
        int physicalAddressPaging = translateAddressPaging(logicalAddress);

        if (physicalAddressPaging != -1) {
            System.out.println("Physical Address (Paging): " + physicalAddressPaging);
        }
    }
}
