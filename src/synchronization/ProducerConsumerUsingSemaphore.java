package synchronization;
import java.util.concurrent.Semaphore;

class Buffer{
	private int[] queue;
	private int capacity;
	private int front, rear, count;
	
	private Semaphore semEmpty, semFull;
	
	Buffer(int size){
		queue = new int[size];
		capacity = size;
		front = rear = count  = 0;
		semEmpty = new Semaphore(size, true);
		semFull = new Semaphore(0, true);
	}
	
	public void insert(int item) {
		try {
			semEmpty.acquire();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		queue[rear] = item;
		rear = (rear + 1) % capacity;
		count++;
		semFull.release();
	}
	
	public int remove() {
		int item;
		
		try {
			semFull.acquire();
		}catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		item = queue[front];
		front = (front + 1) % capacity;
		count--;
		semEmpty.release();
		
		return item;
	}
}

class Producer implements Runnable{
	private Buffer buffer;
	
	Producer(Buffer buffer){
		this.buffer = buffer;
	}
	
	@Override
	public void run() {
		for(int i = 0; i<10; i++) {
			buffer.insert(i);
			System.out.println("Produced : "+i);
			try {
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
		}
	}
	
}

class Consumer implements Runnable{

private Buffer buffer;
	
	Consumer(Buffer buffer){
		this.buffer = buffer;
	}
	
	@Override
	public void run() {
		for(int i = 0; i<10; i++) {
			int item = buffer.remove();
			System.out.println("Consumed : "+item);
			try {
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e) {
                System.out.println("InterruptedException caught");
            }
		}
	}
}

public class ProducerConsumerUsingSemaphore {

	public static void main(String[] args) {
		Buffer buffer = new Buffer(5);
        Producer p = new Producer(buffer);
        Consumer c = new Consumer(buffer);

        Thread producerThread = new Thread(p);
        Thread consumerThread = new Thread(c);

        producerThread.start();
        consumerThread.start();
	}
	
}
