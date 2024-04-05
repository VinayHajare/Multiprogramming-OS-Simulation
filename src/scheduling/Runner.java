package scheduling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Runner {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		List<Process> processes = new ArrayList<>();
		
		System.out.println("Enter the toatal number of processes : ");
		int numProcesses = scanner.nextInt();
		
		for(int i = 0; i<numProcesses; i++) {
			System.out.println("Enter the arrival time of process "+(i+1)+" : ");
			int arrivalTime = scanner.nextInt();
			System.out.println("Enter the brust time of Process "+(i+1)+" : ");
			int brustTime = scanner.nextInt();
			System.out.println("Enter the priority of Process "+(i+1)+" : ");
			int priority = scanner.nextInt();
			processes.add(new Process((i+1), arrivalTime, brustTime, priority));
		}
	
		
		//System.out.println("----First Come First Serve : ----");
		//List<Process> completedProcesses = FCFS.schedule(processes);
		//Process.printStatistics(completedProcesses);
		
		//System.out.println("----Shortest Job First (Non-Preemptive) : ----");
		//List<Process> completedProcesses = SJFNonPreemptive.schedule(processes);
		//Process.printStatistics(completedProcesses);
		
		//System.out.println("----Shortest Job First (Preemptive): ----");
		//List<Process> completedProcesses = SJFPreemptive.schedule(processes);
		//Process.printStatistics(completedProcesses);
		
		//System.out.println("----Priority Scheduling (Non-Preemptive): ----");
		//List<Process> completedProcesses = PriorityNonPreemptive.schedule(processes);
		//Process.printStatistics(completedProcesses);
		
		//System.out.println("----Priority Scheduling (Preemptive): ----");
		//List<Process> completedProcesses = PriorityPreemptive.schedule(processes);
		//Process.printStatistics(completedProcesses);
		
		System.out.println("----Round Robin : ----");
		List<Process> completedProcesses = RoundRobin.schedule(processes, 2);
		Process.printStatistics(completedProcesses);
		
		scanner.close();
	}

}
