package scheduling;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

public class SJFPreemptive {
	
	public static List<Process> schedule(List<Process> processes) {
        int currentTime = 0;
        List<Process> readyQueue = new ArrayList<>();
        List<Process> completedProcesses = new ArrayList<>();

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            if (!processes.isEmpty() && processes.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.remove(0));
                readyQueue.sort(Comparator.comparingInt(p -> p.getRemainingTime()));
            }

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.get(0);
                readyQueue.remove(0);

                currentProcess.setStartTime(currentTime);

                currentTime++;

                currentProcess.setRemainingTime(currentProcess.getRemainingTime() - 1);
                
                if (currentProcess.getRemainingTime() == 0) {
                    currentProcess.setFinishTime(currentTime);
                    completedProcesses.add(currentProcess);
                } else {
                    readyQueue.add(currentProcess);
                    readyQueue.sort(Comparator.comparingInt(p -> p.getRemainingTime()));
                }
            } else {
                currentTime++;
            }
        }

        return completedProcesses;
    }

}
