package scheduling;

import java.util.ArrayList;
import java.util.List;

public class RoundRobin {

    public static List<Process> schedule(List<Process> processes, int quantum) {

        List<Process> readyQueue = new ArrayList<>();
        List<Process> completedProcesses = new ArrayList<>();
        int currentTime = 0;

        while (!processes.isEmpty() || !readyQueue.isEmpty()) {
            while (!processes.isEmpty() && processes.get(0).getArrivalTime() <= currentTime) {
                readyQueue.add(processes.remove(0));
            }

            if (!readyQueue.isEmpty()) {
                Process currentProcess = readyQueue.remove(0);
                currentProcess.setStartTime(currentTime);
                int remainingBurstTime = currentProcess.getRemainingTime();

                if (remainingBurstTime <= quantum) {
                    // Process completes within the time quantum
                    currentTime += remainingBurstTime;
                    currentProcess.setFinishTime(currentTime);
                    currentProcess.setRemainingTime(0);
                    completedProcesses.add(currentProcess);
                } else {
                    // Process needs more quantum time, but we have to yield to the next process after quantum
                    currentTime += quantum;
                    currentProcess.setRemainingTime(currentProcess.getRemainingTime() - quantum);
                    readyQueue.add(currentProcess); // Adding back to the end of the queue
                }
            } else {
                currentTime++;
            }
        }

        return completedProcesses;
    }
}
