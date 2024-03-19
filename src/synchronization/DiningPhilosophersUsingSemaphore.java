package synchronization;

import java.util.concurrent.Semaphore;

class DiningPhilosophers{
	private Semaphore[] forks;
	private Semaphore mutex;
	private int numPhilosophers;
	private int maxAllowed;
	private int countEating;
	
	public DiningPhilosophers(int numPhilosophers){
		this.numPhilosophers = numPhilosophers;
		this.maxAllowed = numPhilosophers - 1;
		this.countEating = 0;
		
		mutex = new Semaphore(1);
		forks = new Semaphore[numPhilosophers];
		
		for(int  i = 0; i < numPhilosophers; i++) {
			forks[i] = new Semaphore(1);
		}
	}
	
	public void dine(int philosopherId) throws InterruptedException{
		while(true) {
			think(philosopherId);
			
			mutex.acquire();
			if(countEating < maxAllowed) {
				countEating++;
				mutex.release();
			}else {
				mutex.release();
				continue;
			}
			
			forks[philosopherId].acquire();
			forks[(philosopherId + 1) % numPhilosophers].acquire();
			
			eat(philosopherId);
			
			forks[philosopherId].release();
			forks[(philosopherId + 1) % numPhilosophers].release();
			
			mutex.acquire();
			countEating--;
			mutex.release();
		}
	}
	
	private void think(int philosopherId) throws InterruptedException{
		System.out.println("Philosopher "+ (philosopherId + 1) +" is thinking...");
		Thread.sleep(1000);
	}
	
	private void eat(int philosopherId) throws InterruptedException{
		System.out.println("Philosopher "+ (philosopherId + 1) +" is eating...");
		Thread.sleep(2000);
	}
}

class Philosopher extends Thread{
	private DiningPhilosophers diningPhilosophers;
	private int philosopherId;
	
	public Philosopher(DiningPhilosophers diningPhilosophers, int philosopherId) {
		this.diningPhilosophers = diningPhilosophers;
		this.philosopherId = philosopherId;
	}
	
	public void run() {
		try {
			diningPhilosophers.dine(philosopherId);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
}

public class DiningPhilosophersUsingSemaphore {
	
	public static void main(String[] args) {
		int numPhilosophers = 5;
		DiningPhilosophers dinigPhilosophers = new DiningPhilosophers(numPhilosophers);
		for(int i = 0; i < numPhilosophers; i++) {
			new Philosopher(dinigPhilosophers, i).start();
		}
	}

}
