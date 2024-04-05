package scheduling;

import java.util.ArrayList;
import java.util.List;

public class FCFS {
	
	public static List<Process> schedule(List<Process> processes){
		int currentTime = 0;
		List<Process> completedProcesses = new ArrayList<>();
		
		for(Process process : processes) {
			// If the current time is less than the arrival time of the process,
            // move the current time to the arrival time of the process
			if(currentTime < process.getArrivalTime()) {
				currentTime = process.getArrivalTime();
			}
			// Set start time of the process
			process.setStartTime(currentTime);
			// Update current time by adding burst time of the process
			currentTime += process.getBrustTime();
			// Set finish time of the process
			process.setFinishTime(currentTime);
			// Add the process to completed processes
			completedProcesses.add(process);
		}
		
		return completedProcesses;
	}
}
