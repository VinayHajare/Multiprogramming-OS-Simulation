package scheduling;

import java.util.List;

public class Process {
	private int processID;
	private int arrivalTime;
	private int brustTime;
	private int remainingTime;
	private int finishTime;
	private int trunAroundTime;
	private int waitingTime;
	private int startTime;
	private int priority;
	

	Process(int processID, int arrivalTime, int brustTime, int priority){
		this.processID = processID;
		this.arrivalTime = arrivalTime;
		this.brustTime = brustTime;
		this.remainingTime = brustTime;
		this.priority = priority;
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}

	public int getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(int arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public int getBrustTime() {
		return brustTime;
	}

	public void setBrustTime(int brustTime) {
		this.brustTime = brustTime;
	}

	public int getRemainingTime() {
		return remainingTime;
	}


	public void setRemainingTime(int remainingTime) {
		this.remainingTime = remainingTime;
	}

	public int getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(int finishTime) {
		this.finishTime = finishTime;
	}

	public int getTrunAroundTime() {
		return trunAroundTime;
	}

	public void setTrunAroundTime(int trunAroundTime) {
		this.trunAroundTime = trunAroundTime;
	}

	public int getWaitingTime() {
		return waitingTime;
	}

	public void setWaitingTime(int waitingTime) {
		this.waitingTime = waitingTime;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	
	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public static void printStatistics(List<Process> completedProcesses) {
        double totalTurnaroundTime = 0;
        double totalWaitingTime = 0;
        
        System.out.println("\nProcess \tPriority \tArriavl Time \tBrust Time \tFinish Time \tTurnaround Time\tWaiting Time");
        
        for (Process p : completedProcesses) {
            int turnaroundTime = p.getFinishTime() - p.getArrivalTime();
            int waitingTime = turnaroundTime - p.getBrustTime();
            totalTurnaroundTime += turnaroundTime;
            totalWaitingTime += waitingTime;
            System.out.println(p.getProcessID() + "\t\t" + p.getPriority()+ "\t\t" + p.getArrivalTime()+ "\t\t" + p.getBrustTime()+ "\t\t" + p.getFinishTime() + "\t\t" +turnaroundTime + "\t\t" + waitingTime);
        }

        double averageTurnaroundTime = totalTurnaroundTime / completedProcesses.size();
        double averageWaitingTime = totalWaitingTime / completedProcesses.size();
        System.out.println("\nAverage Turnaround Time: " + averageTurnaroundTime);
        System.out.println("Average Waiting Time: " + averageWaitingTime);
    }
	
}
