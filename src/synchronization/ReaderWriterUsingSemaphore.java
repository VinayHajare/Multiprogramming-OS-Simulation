package synchronization;

import java.util.concurrent.Semaphore;

class ReaderWriter {
    private Semaphore rwLock;
    private Semaphore mutex;
    private int readers;
    private int sharedVariable;

    public ReaderWriter() {
        mutex = new Semaphore(1);
        rwLock = new Semaphore(1);
        readers = 0;
        sharedVariable = 0; // Initializing shared variable
    }

    public void startReading() throws InterruptedException {
        mutex.acquire();
        readers++;
        if (readers == 1) {
            rwLock.acquire();
        }
        mutex.release();

        // Read shared variable
        System.out.println("Reader is reading: " + sharedVariable);

        mutex.acquire();
        readers--;
        if (readers == 0) {
            rwLock.release();
        }
        mutex.release();
    }

    public void startWriting() throws InterruptedException {
        rwLock.acquire();

        // Write to shared variable
        sharedVariable++; // Incrementing shared variable

        System.out.println("Writer is writing: " + sharedVariable);

        rwLock.release();
    }
}

class Reader extends Thread {
    private ReaderWriter readerWriter;

    public Reader(ReaderWriter readerWriter) {
        this.readerWriter = readerWriter;
    }

    public void run() {
        try {
            while (true) {
                readerWriter.startReading();
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Writer extends Thread {
    private ReaderWriter readerWriter;

    public Writer(ReaderWriter readerWriter) {
        this.readerWriter = readerWriter;
    }

    public void run() {
        try {
            while (true) {
                readerWriter.startWriting();
                Thread.sleep(2000);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ReaderWriterUsingSemaphore {

    public static void main(String[] args) {
        ReaderWriter readerWriter = new ReaderWriter();
        new Writer(readerWriter).start();
        for (int i = 0; i < 2; i++) {
            new Reader(readerWriter).start();
        }
    }
}
