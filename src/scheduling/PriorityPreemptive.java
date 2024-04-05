package scheduling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PriorityPreemptive {
	public static List<Process> schedule(List<Process> processess){
		int currentTime = 0;
		List<Process> readyQueue = new ArrayList<>();
		List<Process> completedProcessess = new ArrayList<>();
		
		while(!processess.isEmpty() || !readyQueue.isEmpty()) {
			while(!processess.isEmpty() && processess.get(0).getArrivalTime() <= currentTime) {
				readyQueue.add(processess.remove(0));
			}
			
			readyQueue.sort(Comparator.comparingInt(p -> p.getPriority()));
			
			if(!readyQueue.isEmpty()) {
				Process currentProcess = readyQueue.remove(0);
				
				currentProcess.setStartTime(currentTime);
				currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
				currentTime++;
				
				if(currentProcess.getRemainingTime() == 0){
					currentProcess.setFinishTime(currentTime);
					completedProcessess.add(currentProcess);
				}else {
					readyQueue.add(currentProcess);
				}
			}else {
				currentTime++;
			}
		}
		return completedProcessess;
	}
}
