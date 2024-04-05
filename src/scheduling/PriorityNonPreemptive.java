package scheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityNonPreemptive {
	public static List<Process> schedule(List<Process> processes){
		List<Process> completedProcesses = new ArrayList<>();
		int currentTime = 0;
		
		while(!processes.isEmpty()) {
			List<Process> arrivedProcesses = new ArrayList<>();
			for(Process process : processes) {
				if(process.getArrivalTime() <= currentTime) {
					arrivedProcesses.add(process);
				}
			}
			
			if(!arrivedProcesses.isEmpty()) {
				arrivedProcesses.sort(Comparator.comparingInt(p -> p.getPriority()));
				
				Process currentProcess = arrivedProcesses.get(0);
				processes.remove(currentProcess);
				
				currentProcess.setStartTime(currentTime);
				currentTime += currentProcess.getRemainingTime();
				
				currentProcess.setFinishTime(currentTime);
				completedProcesses.add(currentProcess);
			}else {
				currentTime++;
			}
		}
		return completedProcesses;
	}
}
