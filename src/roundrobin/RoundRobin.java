package roundrobin;

import java.text.DecimalFormat;
import java.util.*;

class Process {

    private int id, arrivalTime, burstTime, remainingTime, completionTime, turnaroundTime, waitingTime; 
    boolean inQueue, isComplete;

    public Process(int pid, int arrivalTime, int burstTime) {
        this.id = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.completionTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;      
        this.inQueue = false;
        this.isComplete = false;
    }

    public int getId() {
        return id;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getCompletionTime() {
        return completionTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }
   
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public void setTurnaroundTime(int completionTime, int arrivalTime) {
        this.turnaroundTime = completionTime - arrivalTime;
    }

    public void setWaitingTime(int turnaroundTime, int burstTime) {
        this.waitingTime = turnaroundTime - burstTime;
    } 

    public boolean inQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
    
    
}

public class RoundRobin {
    
    static int n, timeQuantum, arrivalTime, burstTime;
   
    static Scanner sc = new Scanner(System.in);
    
    static ArrayList<Process> processes = new ArrayList<>();                 

    public static void input() {
        if (!processes.isEmpty()) {
            processes.clear();
            readyQueue.clear();
            topLine.setLength(0);
            processIdGanttChart.setLength(0);
            bottomLine.setLength(0);
            timeGanttChart.setLength(0);
        }

        System.out.println("----------------------------Round Robin CPU Scheduling Algorithm----------------------------");

        System.out.print("\nEnter the number of processes: ");
        n = sc.nextInt();

        while (n <= 0 || n < 3 || n > 6) {
            System.out.println("\nInvalid number of processes!");
            if (n <= 0) {
                System.out.println("\nNumber of processes cannot be less than or equal to 0!");
            } else if (n < 3) {
                System.out.println("\nMinimun number of processes is 3!");
            } else if (n > 6) {
                System.out.println("\nMaximum number of processes is 6!");
            }
            System.out.print("\nEnter the number of processes: ");
            n = sc.nextInt();
        }

        
        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter Arrival Time and Burst Time for Process #" + (i + 1) + ": ");
            arrivalTime = sc.nextInt();
            burstTime = sc.nextInt();
            processes.add(new Process((i + 1), arrivalTime, burstTime));
        }

        
        System.out.print("\nEnter Time Quantum: ");
        timeQuantum = sc.nextInt();

       
        while (timeQuantum <= 0) {
            System.out.println("\nInvalid Time Quantum!");
            System.out.println("\nTime Quantum cannot be less than or equal 0!");
            System.out.print("\nEnter Time Quantum: ");
            timeQuantum = sc.nextInt();
        }

        processes.sort(Comparator.comparing(Process::getArrivalTime));
    }
    
    static ArrayList<Process> readyQueue = new ArrayList<>();

    static int currentTime, completedProcess;
    static int startTime, endTime;
    
    static Process currentProcess;
    
    static int i;     
    
    static double avgTurnaroundTime, avgWaitingTime;   

    static double totalBurstTime, totalTurnaroundTime, totalWaitingTime;

    static double cpuUtilization;
    
    static StringBuilder topLine = new StringBuilder();
    static StringBuilder processIdGanttChart = new StringBuilder();
    static StringBuilder bottomLine = new StringBuilder();
    static StringBuilder timeGanttChart = new StringBuilder();

    public static void roundRobin() {
        currentTime = 0;
        completedProcess = 0;

        startTime = 0;
        endTime = 0;

        currentProcess = null;
        
        i = 0;
        
        if (processes.get(0).getArrivalTime() != 0) {                      
            topLine.append("---------");

            processIdGanttChart.append(" |  ").append("--").append("   ");
            timeGanttChart.append("    ").append(currentTime).append("    ");

            bottomLine.append("---------");

            currentTime = processes.get(0).getArrivalTime();
        }
        
        readyQueue.add(processes.get(0));
        
        processes.get(0).setInQueue(true);

        while (completedProcess != n) {            
            if (!readyQueue.isEmpty()) {
                currentProcess = readyQueue.get(0);
                readyQueue.remove(0);
                i = processes.indexOf(currentProcess);                
                
                if (currentProcess.getRemainingTime() <= timeQuantum) {                                        
                    startTime = currentTime;
                    
                    endTime = currentTime + currentProcess.getRemainingTime();
                                       
                    currentTime = endTime;
                    
                    processes.get(i).setIsComplete(true);
                    
                    processes.get(i).setRemainingTime(0);
                    
                    processes.get(i).setCompletionTime(currentTime);
                    processes.get(i).setTurnaroundTime(processes.get(i).getCompletionTime(), processes.get(i).getArrivalTime());
                    processes.get(i).setWaitingTime(processes.get(i).getTurnaroundTime(), processes.get(i).getBurstTime());   
                    
                    updateReadyQueue();
                    
                    completedProcess++;                 
                } else {                                        
                    startTime = currentTime;
                    
                    endTime = currentTime + timeQuantum;
                                   
                    processes.get(i).setRemainingTime(processes.get(i).getRemainingTime() - timeQuantum);
                    
                    currentTime = endTime;  
                    
                    updateReadyQueue();

                    readyQueue.add(currentProcess);
                }
                   
                topLine.append("---------");

                processIdGanttChart.append("|   ").append("P").append(currentProcess.getId()).append("   ");
                if (completedProcess != n) {
                    timeGanttChart.append("    ").append(startTime);
                } else {
                    processIdGanttChart.append("|");
                    timeGanttChart.append("     ").append(startTime).append("       ").append(endTime);
                }

                if (startTime >= 10) {
                    timeGanttChart.append("   ");
                } else {
                    timeGanttChart.append("    ");
                }

                bottomLine.append("---------");
            } else {                            
                topLine.append("---------");

                processIdGanttChart.append("|   ").append("--").append("   ");
                timeGanttChart.append("    ").append(currentTime);

                if (currentTime >= 10) {
                    timeGanttChart.append("  ");
                } else {
                    timeGanttChart.append("    ");
                }

                bottomLine.append("---------");

                for (Process p : processes) {
                    if (p.getArrivalTime() > currentTime) {
                        currentTime = p.getArrivalTime();
                    }
                }
                updateReadyQueue();
            }
        }

        totalBurstTime = 0;
        totalTurnaroundTime = 0;
        totalWaitingTime = 0;

        avgTurnaroundTime = 0;
        avgWaitingTime = 0;
        
        for (Process p : processes) {
            totalTurnaroundTime += p.getTurnaroundTime();
            totalWaitingTime += p.getWaitingTime();
            totalBurstTime += p.getBurstTime();
        }
       
        avgTurnaroundTime = totalTurnaroundTime / n;
        avgWaitingTime = totalWaitingTime / n;
        
        cpuUtilization = (totalBurstTime / (double) endTime) * 100;
    }

    public static void updateReadyQueue() {
        if (completedProcess != n) {
            for (Process p : processes) {
                if (p.getArrivalTime() <= currentTime && !p.inQueue() && !p.isComplete()) {
                    p.setInQueue(true);

                    readyQueue.add(p);
                }
            }
        }       
    }

    public static void output() {
        processes.sort(Comparator.comparing(Process::getId));

        System.out.println("\nInput:");
        System.out.print("------------------------------------------");

        System.out.println("\nProcess \tArrival Time\tBurst Time");
        for (Process p : processes) {
            System.out.println(p.getId() + "\t\t" + p.getArrivalTime() + "\t\t" + p.getBurstTime());
        }
        System.out.print("Time Quantum: " + timeQuantum);

        System.out.print("\n------------------------------------------\n");

        System.out.println("\nGantt Chart:");
        System.out.println(" " + topLine.toString());
        System.out.println(processIdGanttChart.toString());
        System.out.println(" " + bottomLine.toString());
        System.out.println(timeGanttChart.toString());

        System.out.println("\nOutput:");
        System.out.print("--------------------------------------------------------------------------------------------");
        
        System.out.println("\nProcess \tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.println(p.getId() + "\t\t" + p.getArrivalTime() + "\t\t" + p.getBurstTime() + "\t\t"
                    + p.getCompletionTime() + "\t\t" + p.getTurnaroundTime() + "\t\t" + p.getWaitingTime());
        }

        System.out.println("--------------------------------------------------------------------------------------------");

        System.out.println("\nTotal Turnaround Time: " + (int) totalTurnaroundTime);

        System.out.println("\nTotal Waiting Time: " + (int) totalWaitingTime);

        System.out.println("\nNumber of Processes: " + n);

        DecimalFormat df = new DecimalFormat("#.##");
        System.out.println("\nAverage Turnaround Time: " + (int) totalTurnaroundTime + " / " + n + " = " + df.format(avgTurnaroundTime));
        System.out.println("\nAverage Waiting Time: " + (int) totalWaitingTime + " / " + n + " = " + df.format(avgWaitingTime));

        System.out.println("\n\nTotal Burst Time: " + (int) totalBurstTime);

        System.out.println("\nLast Completion Time: " + endTime);

        System.out.println("\nCPU Utilization: (" + (int) totalBurstTime + " / " + endTime + ") * 100 = " + df.format(cpuUtilization) + "%");
    }

    public static void main(String[] args) {
        int choice = 1;
        while (choice == 1) {
            input();

            roundRobin();

            output();

            System.out.print("\nTry Again? [1] Yes [0] No: ");
            choice = sc.nextInt();
        }

    }

}
