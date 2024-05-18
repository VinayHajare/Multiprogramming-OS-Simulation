import java.io.*;
import java.util.*;

enum TASK {
    IS, OS, LD, RT, WT
}

enum STATUS {
    Loading, Loaded, Ready, Execute, Terminate, Read, Write
}

class PCB {
    public int ID;
    public int TTL; //total time limit
    public int TLL; //total line limit 
    public int TTC; //total time counter
    public int LLC; //line limit counter
    public int TSC; // Time slice counter
    public char F;
    public int P_COUNT, D_COUNT;
    public int D, P, O, END;
    public int flag = 0;
    public int PTR;
    public short IC;
    public String terminateMsg;
    public STATUS status;

    public PCB(PCB pcb) {
        ID = pcb.ID;
        TTL = pcb.TTL;
        TLL = pcb.TLL;
        TTC = pcb.TTC;
        LLC = pcb.LLC;
        TSC = pcb.TSC;
        F = pcb.F;
        D = pcb.D;
        P_COUNT = pcb.P_COUNT;
        D_COUNT = pcb.D_COUNT;
        P = pcb.P;
        END = pcb.END;
        O = pcb.O;
        status = pcb.status;
        PTR = pcb.PTR;
        IC = pcb.IC;
        terminateMsg = pcb.terminateMsg;
    }

    public PCB() {
        TTL = TLL = TTC = LLC = TSC = D = P_COUNT = D_COUNT = P = END = IC = O = ID = 0;
        F = 'P'; // UPCOMING LINE IS PROGRAM CARD
        PTR = -1;
        terminateMsg = " ";
        status = STATUS.Loading;
    }
}

class CH {
    public int flag;
    public int value;
    public int time;
    public int total_time;

    public CH() {
        flag = 0;
        value = 0;
        time = 0;
        total_time = 0;
    }
}

class Buffer {
    public int status; // 0 - empty , 1 - ibq , 2-obq
    public char[] value;

    public Buffer() {
        value = new char[40];
        initialize();
    }

    public void initialize() { // ZERO SET THE BUFFER
        Arrays.fill(value, ' ');
    }
}

class MOS {
    private static final int SIZE = 300;
    private static final int K = 4;
    private char[][] MainMemory = new char[SIZE][K];
    private char[][] DrumMemory = new char[SIZE + 200][K];
    private char[][] SS = new char[SIZE + 1000][K];
    private char[] IR = new char[K];
    private char[] R = new char[K];
    private int drum = 0, memory = 0;
    private short RA;
    private boolean C; // TOGGLE REGISTER
    private int SI = 0, PI = 0, TI = 0, IR1 = 0, IR2 = 0, IR3 = 0, IOI = 0;
    private CH[] ch = {new CH(), new CH(), new CH()};
    private int count = 0;
    private BufferedReader fi;
    private BufferedWriter fo;
    private boolean[] mark = new boolean[30];
    private int eof = 0;
    private TASK task = TASK.IS;
    private int flag = 0;
    private Queue<Buffer> emptyQ = new LinkedList<>();
    private Queue<Buffer> inputfulBufferQ = new LinkedList<>();
    private Queue<Buffer> outputfulBufferQ = new LinkedList<>();
    private Queue<PCB> readyQ = new LinkedList<>();
    private Queue<PCB> loadQ = new LinkedList<>();
    private Queue<PCB> inputOutputQ = new LinkedList<>();
    private Queue<PCB> terminateQ = new LinkedList<>();
    private Queue<PCB> pendingQ = new LinkedList<>();

    public MOS() {
        try {
            fi = new BufferedReader(new FileReader("input.txt"));
            fo = new BufferedWriter(new FileWriter("output.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void begin() {
        int tmr = 0; // GLOBAL TIMER
        System.out.println("#############  Operating System IS STARTED ############# \n\n");
        initialization();
        IOI = 1; // ch1 start
        do {
            System.out.println("⏱ GLOBAL TIMER => [" + tmr + "] ..............");
            execute();
            simulation();
            masterMode();
            round_robin();
            tmr++;
        } while (eof != 1 || !inputfulBufferQ.isEmpty() || !outputfulBufferQ.isEmpty() || !loadQ.isEmpty() || !readyQ.isEmpty() || !inputOutputQ.isEmpty() || !terminateQ.isEmpty());
    }

    public void initialization() {
        memory = 0;
        // initialize MainMemory
        for (int i = 0; i < SIZE; i++)
            Arrays.fill(MainMemory[i], ' ');

        Arrays.fill(mark, false); // BOOLEAN ARRAY TO TELL IS PAGE IS AVAILABLE
        Arrays.fill(R, ' ');

        // INITIALIZE EMPTY BUFFER QUEUE
        for (int i = 0; i < 10; i++) {
            Buffer temp = new Buffer();
            temp.status = 0; // 0 - empty , 1 - ibq , 2-obq
            emptyQ.add(temp);
        }

        // INITIALIZE DRUM MEMORY
        for (int i = 0; i < 50; i++)
            Arrays.fill(DrumMemory[i], ' ');

        // SET CHANNEL VALUES AND TOTAL TIME TAKEN BY THEM
        ch[0].value = 1;
        ch[1].value = 2;
        ch[2].value = 4;
        ch[0].total_time = 5;
        ch[1].total_time = 5;
        ch[2].total_time = 2;
    }

    // ALLOCATE PAGES AND MARK THAT IN mark BOOLEAN ARRAY
    public int Allocate() {
        int random = new Random().nextInt(30);
        while (mark[random]) {
            random = new Random().nextInt(30);
        }
        mark[random] = true;
        return random;
    }

    // OPERAND ERROR IS CHECKED
    public boolean check_operand(char a, char b) {
        if (!(((a <= '9' && a >= '0') || a == ' ') && ((b <= '9' && b >= '0') || b == ' '))) {
            PI = 2;
            return false;
        }
        return true;
    }

    // ADDRESS TRANS FUNCTION
    public void Map(PCB temp, int VA) {
        int i = 0, pte_i;
        int pte = temp.PTR + VA / 10;
        StringBuilder content = new StringBuilder();
        for (i = 0; i < K; i++) {
            content.append(MainMemory[pte][i]);
        }
        if (content.toString().trim().equals("")) {
            PI = 3; // Page Fault
            System.out.println("Page fault=> Setting PI=3 : ");
            return;
        }
        pte_i = Integer.parseInt(content.toString().trim());
        RA = (short) (pte_i * 10 + VA % 10);
    }

    public int cardReader() {
        int i = 0;
        char c;
        if (eof == 1 || emptyQ.isEmpty()) {
            return 1;
        }

        // TAKE ONE FROM 10 EMPTY Buffers
        Buffer temp = emptyQ.poll();
        temp.status = 1; // INPUTFULL BQ
        if (inputfulBufferQ.size() == 10) {
            System.out.println("Input Buffer queue is full ! cant read");
            return 1;
        }

        try {
            int count = 0;
            c = (char) fi.read();
            if (c == '\n')
                return 0;
            temp.value[count++] = c;
            if (c == 'H' && pendingQ.peek().F == 'P') {
                temp.value[count++] = ' ';
                temp.value[count++] = ' ';
                temp.value[count++] = ' ';
            }
            while (fi.ready()) {
                c = (char) fi.read();
                if (count == 40 || c == '\n') {
                    if (temp.value[0] == '$') // CONTROL CARD
                    {
                        if (temp.value[1] == 'A') // AMJ LINE IS ENCOUNTERED
                        {
                            PCB temp_pcb = new PCB();
                            temp_pcb.ID = Integer.parseInt(new String(temp.value, 4, 4));
                            temp_pcb.TTL = Integer.parseInt(new String(temp.value, 8, 4));
                            temp_pcb.TLL = Integer.parseInt(new String(temp.value, 12, 4));
                            temp_pcb.TTC = 0;
                            temp_pcb.LLC = 0;
                            temp_pcb.F = 'P';
                            System.out.println("$AMJ PCB created for job id : " + temp_pcb.ID + " " + "Next upcoming Card -> " + temp_pcb.F);
                            System.out.println("PCB=> TTL:" + temp_pcb.TTL + " TLL:" + temp_pcb.TLL + " LLC:" + temp_pcb.LLC + " TTC:" + temp_pcb.TTC + " F:" + temp_pcb.F + "\n\n");
                            pendingQ.add(temp_pcb); // ADD NEWLY CREATED PCB TO PENDING QUEUE
                            inputfulBufferQ.add(temp);
                            if (ch[0].flag == 0) {
                                ch[0].time = 0;
                            }
                        } else if (temp.value[1] == 'D') // END OF JOB LINE
                        {
                            PCB temp_pcb = pendingQ.peek();
                            if (temp_pcb != null) {
                                temp_pcb.END = drum;
                                System.out.println("$DTA Data Card from [" + temp_pcb.ID + "] loaded in Drum Memory range : [" + temp_pcb.D + " - " + temp_pcb.END + "]");
                                temp_pcb.F = 'D';
                                loadQ.add(temp_pcb);
                                pendingQ.poll();
                            }
                        } else if (temp.value[1] == 'E') // END OF JOB LINE
                        {
                            pendingQ.peek().O = drum;
                            System.out.println("$END JOB OF ID " + pendingQ.peek().ID + " ENDS \n");
                        }
                    } else {
                        for (int j = count; j < 40; j++) {
                            temp.value[j] = ' ';
                        }
                    }
                    inputfulBufferQ.add(temp);
                    if (ch[0].flag == 0) {
                        ch[0].time = 0;
                    }
                    return 0;
                }
                temp.value[count++] = c;
            }
            eof = 1;
            System.out.println("Reading done.\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void terminate(PCB pcb) {
        StringBuilder tempp = new StringBuilder("Job ID : [" + pcb.ID + "]  ");
        try {
            fo.write("Job ID : [" + pcb.ID + "]  ");
            fo.write(pcb.terminateMsg);
            fo.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fo.write(tempp.toString());
            fo.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        terminateQ.poll();
        if (!terminateQ.isEmpty())
            terminateQ.peek().status = STATUS.Terminate;
    }

    public void simulateIOI() {
        if (IOI != 0) {
            // Check for IOI bits
            if (IOI % 2 == 1) { //ch1
                ch1();
            }
            if (IOI / 2 % 2 == 1) { //ch2
                ch2();
            }
            if (IOI / 4 % 2 == 1) { //ch3
                ch3();
            }
            IOI = 0;
        }
    }

    public void ch1() {
        System.out.println("🖥 ----CHANNEL 1 ACTIVATED [IOI]--- \n");
        System.out.println("CHANNEL 1 : Input Reader ");
        // Assume the ch1 read the input completely
        if (ch[0].flag == 0) {
            ch[0].flag = 1; // CHANNEL IS BUSY
            cardReader();
        }
    }

    public void ch2() {
        System.out.println("CHANNEL 2 ACTIVATED [IOI]");
        System.out.println("CHANNEL 2 : Loader");
        if (ch[1].flag == 0) {
            ch[1].flag = 1; // CHANNEL IS BUSY
            loader();
        }
    }

    public void ch3() {
        System.out.println("CHANNEL 3 ACTIVATED [IOI]");
        System.out.println("CHANNEL 3 : Output Spooler ");
        if (ch[2].flag == 0) {
            ch[2].flag = 1; // CHANNEL IS BUSY
            outputSpooler();
        }
    }

    public void execute() {
        if (!readyQ.isEmpty()) {
            PCB temp = readyQ.poll();
            Map(temp, temp.IC);
            for (int i = 0; i < 4; i++) {
                IR[i] = MainMemory[RA][i];
            }
            temp.IC++;
            if (temp.IC > SIZE || temp.IC < 0) {
                temp.terminateMsg = "2 => OPERATION CODE ERROR";
                System.out.println("⛔ OPERATION CODE ERROR : " + temp.ID);
                terminateQ.add(temp);
                temp.status = STATUS.Terminate;
                return;
            }
            IR1 = IR[0];
            IR2 = IR[1];
            IR3 = IR[2] - '0';
            System.out.println("=> INSTRUCTION EXECUTED IS : ");
            System.out.println("IR[0]:" + IR[0] + " IR[1]:" + IR[1] + " IR[2]:" + IR[2] + " IR[3]:" + IR[3]);
            if (IR[0] == 'G' && IR[1] == 'D') {
                if (!check_operand(IR[2], IR[3])) {
                    terminateQ.add(temp);
                    temp.status = STATUS.Terminate;
                    return;
                }
                SI = 1; // SET SI to 1
                Map(temp, IR3);
            } else if (IR[0] == 'P' && IR[1] == 'D') {
                if (!check_operand(IR[2], IR[3])) {
                    terminateQ.add(temp);
                    temp.status = STATUS.Terminate;
                    return;
                }
                SI = 2; // SET SI to 2
                Map(temp, IR3);
            } else if (IR[0] == 'L' && IR[1] == 'R') {
                if (!check_operand(IR[2], IR[3])) {
                    terminateQ.add(temp);
                    temp.status = STATUS.Terminate;
                    return;
                }
                Map(temp, IR3);
                for (int i = 0; i < K; i++) {
                    R[i] = MainMemory[RA][i];
                }
            } else if (IR[0] == 'S' && IR[1] == 'R') {
                if (!check_operand(IR[2], IR[3])) {
                    terminateQ.add(temp);
                    temp.status = STATUS.Terminate;
                    return;
                }
                Map(temp, IR3);
                for (int i = 0; i < K; i++) {
                    MainMemory[RA][i] = R[i];
                }
            } else if (IR[0] == 'C' && IR[1] == 'R') {
                if (!check_operand(IR[2], IR[3])) {
                    terminateQ.add(temp);
                    temp.status = STATUS.Terminate;
                    return;
                }
                Map(temp, IR3);
                for (int i = 0; i < K; i++) {
                    if (MainMemory[RA][i] == R[i])
                        C = true;
                    else {
                        C = false;
                        break;
                    }
                }
            } else if (IR[0] == 'B' && IR[1] == 'T') {
                if (!check_operand(IR[2], IR[3])) {
                    terminateQ.add(temp);
                    temp.status = STATUS.Terminate;
                    return;
                }
                if (C == true) {
                    Map(temp, IR3);
                    temp.IC = RA;
                }
            } else if (IR[0] == 'H') {
                SI = 3;
            } else {
                PI = 1;
            }
            if (SI != 0 || PI != 0) {
                readyQ.add(temp);
                task = TASK.RT;
                return;
            }
            temp.TTC++;
            temp.TSC++;
            if (temp.TTC > temp.TTL) {
                TI = 2;
                terminateQ.add(temp);
                temp.status = STATUS.Terminate;
                return;
            }
            if (temp.TSC == 10) {
                temp.TSC = 0;
                readyQ.add(temp);
            } else {
                readyQ.add(temp);
            }
        }
    }

    public void loader() {
        PCB temp = loadQ.poll();
        int counter = temp.PTR = memory;
        int pte, end;
        memory++;
        if (temp.F == 'P') {
            end = temp.P_COUNT;
        } else {
            end = temp.D_COUNT;
        }
        System.out.println("⏱ PCB[" + temp.ID + "] (" + temp.PTR + ") ADDING PAGES FROM " + drum + " TO " + (drum + end));
        for (int i = 0; i < end; i++) {
            pte = Allocate(); // page frame no is created
            System.out.println("  --> Mapping Page to ->" + pte);
            for (int j = 0; j < 4; j++) {
                MainMemory[counter][j] = (char) ('0' + pte);
            }
            counter++;
            for (int k = 0; k < 10; k++) {
                for (int j = 0; j < 4; j++) {
                    MainMemory[pte * 10 + k][j] = DrumMemory[drum][j];
                }
                drum++;
            }
        }
        if (temp.F == 'P') {
            temp.P = drum;
            temp.F = 'D';
            System.out.println("Adding PCB[" + temp.ID + "] to Input Queue");
            inputQ.add(temp);
        } else if (temp.F == 'D') {
            temp.D = drum;
            temp.F = 'O';
            System.out.println("Adding PCB[" + temp.ID + "] to Ready Queue");
            readyQ.add(temp);
        }
    }

    public void outputSpooler() {
        PCB temp = outputQ.poll();
        temp.outputBuffer.removeAllElements();
        System.out.println("Writing Output for PCB[" + temp.ID + "] to output file.");
        for (int i = 0; i < 10; i++) {
            String line = "OUT : ";
            for (int j = 0; j < 4; j++) {
                line += MainMemory[Map(temp, temp.O) + i][j];
            }
            System.out.println(line);
            temp.outputBuffer.add(line);
        }
        try {
            fo.write("Output for PCB ID : " + temp.ID + " is \n");
            for (int i = 0; i < temp.outputBuffer.size(); i++) {
                fo.write(temp.outputBuffer.get(i));
                fo.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        temp.terminateMsg = "OK";
        terminateQ.add(temp);
        temp.status = STATUS.Terminate;
    }

    public void task() {
        task = TASK.NONE;
        System.out.println("⏳TASK: ");
        switch (task) {
            case Start:
                System.out.println("Start TASK:");
                if (ch[0].flag == 1 && !inputfulBufferQ.isEmpty()) {
                    pendingQ.add(readyQ.poll());
                    ch[0].flag = 0;
                }
                if (ch[1].flag == 1 && !loadQ.isEmpty()) {
                    PCB temp = loadQ.poll();
                    drum++;
                    System.out.println("PCB[" + temp.ID + "] LOADING...");
                    ch[1].flag = 0;
                    loadQ.add(temp);
                }
                if (ch[2].flag == 1 && !terminateQ.isEmpty()) {
                    terminate(terminateQ.poll());
                    ch[2].flag = 0;
                }
                task = TASK.NONE;
                break;
            case Execute:
                System.out.println("Execute TASK:");
                execute();
                task = TASK.NONE;
                break;
            case RT:
                task = TASK.Start;
                break;
            case Output:
                System.out.println("Output TASK:");
                task = TASK.NONE;
                break;
        }
    }

    public int Allocate() {
        return memory++;
    }

    public boolean check_operand(char c1, char c2) {
        if (c1 >= '0' && c1 <= '9' && c2 >= '0' && c2 <= '9')
            return true;
        return false;
    }

    public int Map(PCB temp, int IC) {
        if (IC / 10 == temp.PTR / 10)
            return IC % 10;
        if (IC / 10 >= temp.PTR + 10)
            return IC;
        int RA = 0;
        for (int i = 0; i < 4; i++) {
            RA = RA * 10 + MainMemory[temp.PTR / 10][i] - '0';
        }
        return RA + IC % 10;
    }
}
