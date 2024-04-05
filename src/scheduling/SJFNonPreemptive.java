package scheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SJFNonPreemptive {
	
	public static List<Process> schedule(List<Process> processess){
		List<Process> readyQueue = new ArrayList<>();
		List<Process> completedProcesses = new ArrayList<>();
		int currentTime = 0;
		
		while(!processess.isEmpty() || !readyQueue.isEmpty()) {
			while(!processess.isEmpty() && processess.get(0).getArrivalTime() <= currentTime) {
				readyQueue.add(processess.remove(0));
			}
			
			readyQueue.sort(Comparator.comparingInt(p -> p.getRemainingTime()));
			
			if(!readyQueue.isEmpty()) {
				Process currentProcess = readyQueue.remove(0);
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
