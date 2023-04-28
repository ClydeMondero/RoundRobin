package roundrobin;

import java.text.DecimalFormat;
import java.util.*;

class Process{
    int pid, arrivalTime, burstTime, remainingTime, completionTime, turnaroundTime, waitingTime;
    boolean isComplete, inQueue;

    public Process(int pid, int arrivalTime, int burstTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.completionTime = 0;
        this.turnaroundTime = 0;
        this.waitingTime = 0;
        this.isComplete = false;
        this.inQueue = false;
    }

    public int getPid() {
        return pid;
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

    public boolean isComplete() {
        return isComplete;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public void setCompletionTime(int completionTime) {
        this.completionTime = completionTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }   
    
        
}

public class RoundRobin {

    //Initialize Variables and Objects
    
    //Numbers of Processes, Time Quantum, Arrival Times, Burst Times
    static int n, timeQuantum, arrivalTime, burstTime;    
    
    //Scanner
    static Scanner sc = new Scanner(System.in);
    
    //Initialize ArrayList of Objects
    static ArrayList<Process> processes = new ArrayList<>();
    
    //Current Time and Number of Processes Executed
    static int currentTime, processesExecuted;
    
    //Ready Queue
    static Queue<Integer> readyQueue = new LinkedList<>();
    
    //String Builders for the Gantt Chart
    static StringBuilder topLine = new StringBuilder(); 
    static StringBuilder processIdGanttChart = new StringBuilder(); 
    static StringBuilder bottomLine = new StringBuilder(); 
    static StringBuilder timeGanttChart = new StringBuilder();
    
    //Average Turnaround Time and Average Waiting Time
    static double avgTurnaroundTime, avgWaitingTime;
    
    //Current Process Executing, Start and End Time
    static int currentProcess, startTime, endTime;           
    
    //Total Burst Time
    static double totalBurstTime, totalTurnaroundTime, totalWaitingTime;
    
    //CPU Utilization
    static double cpuUtilization;        
       
    public static void input(){      
        //Clear
        if(!processes.isEmpty()){
            processes.clear();
            readyQueue.clear();
            topLine.setLength(0);
            processIdGanttChart.setLength(0);
            bottomLine.setLength(0);
            timeGanttChart.setLength(0);            
        }
        
        //Title
        System.out.println("----------------------------Round Robin CPU Scheduling Algorithm----------------------------");
        
        //Reads the Number of Processes
        System.out.print("\nEnter the number of processes: ");
        n = sc.nextInt();
        
        //Tests if the Number of Processes is valid
        while(n <= 0 || n < 3 || n > 6){
            System.out.println("\nInvalid number of processes!");            
            if(n <= 0){
                System.out.println("\nNumber of processes cannot be less than or equal to 0!");
            }else if(n < 3)         {
                System.out.println("\nMinimun number of processes is 3!");
            }else if(n > 6){
                System.out.println("\nMaximum number of processes is 6!");
            }
            System.out.print("\nEnter the number of processes: ");
            n = sc.nextInt();
        }
                

        //Reads Arrival Time and Burst Time for each Processes
        for (int i = 0; i < n; i++) {
            System.out.print("\nEnter Arrival Time and Burst Time for Process #" + (i + 1) + ": ");
            arrivalTime = sc.nextInt();
            burstTime = sc.nextInt();
            processes.add(new Process((i + 1), arrivalTime, burstTime));
        }
        
        //Reads the Time Quantum
        System.out.print("\nEnter Time Quantum: ");
        timeQuantum = sc.nextInt();
        
        //Tests if the Time Quantum is valid
        while(timeQuantum <= 0){
            System.out.println("\nInvalid Time Quantum!");
            System.out.println("\nTime Quantum cannot be less than or equal 0!");
            System.out.print("\nEnter Time Quantum: ");
            timeQuantum = sc.nextInt();
        }
        
        //Sorts Processes by its Arrival Time
        Collections.sort(processes, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return Integer.compare(p1.getArrivalTime(), p2.getArrivalTime());
            }
        });        
    }
         
    public static void roundRobin(){                
        //Initalizes Current Time and the Number of Procceses Executed
        currentTime = 0;                
        processesExecuted = 0;   
        
        //Initializes Start Time, and End Time
        startTime = 0;
        endTime = 0;  

        //Sets Current Process to Null or Empty     
        currentProcess = -1;                                                                               

        if(processes.get(0).getArrivalTime() != 0){           
            //Gantt Chart             
            topLine.append("---------");

            processIdGanttChart.append(" |  ").append("--").append("   ");
            timeGanttChart.append("    ").append(currentTime).append("    ");

            bottomLine.append("---------");
            
            currentTime = processes.get(0).getArrivalTime();
        }
        
        //Initialize Queue 
        readyQueue.add(0);        
        
        //Sets the 1st Processes InQueue to True
        processes.get(0).setInQueue(true);
        
        // Update Ready Queue until it is Empty
        while (processesExecuted != n) {
            if(!readyQueue.isEmpty()){
                int i = readyQueue.remove();
            
                //Tests if the Process's Remaining Time is less than or equal than the Time Quantum

                //If True
                if (processes.get(i).getRemainingTime() <= timeQuantum) {
                    //Saves Current Process
                    currentProcess = i;

                    //Updates Process's Start Time
                    startTime = currentTime;

                    //Update Process's End Time
                    endTime = currentTime + processes.get(i).getRemainingTime();

                    //Sets Process's isComplete to True
                    processes.get(i).setIsComplete(true);

                    //Updates Process's Current Time
                    currentTime = endTime;

                    //Updates  Process's Remaining Time to 0
                    processes.get(i).setRemainingTime(0);

                    //Saves Process's Completion Time, Turnaround Time, and Waiting Time
                    processes.get(i).setCompletionTime(currentTime);
                    processes.get(i).setTurnaroundTime(processes.get(i).getCompletionTime() - processes.get(i).getArrivalTime());
                    processes.get(i).setWaitingTime(processes.get(i).getTurnaroundTime() - processes.get(i).getBurstTime());

                    //Checks for New Processes
                    updateReadyQueue();

                    //Increments the Numbers of Processes that Executed
                    processesExecuted++;

                    //If False
                } else {
                    //Saves Current Process
                    currentProcess = i;

                    //Updates Process's Start Time
                    startTime = currentTime;

                    //Updates Process's End Time
                    endTime = currentTime + timeQuantum;

                    //Updates Process's Remaining Time                
                    processes.get(i).setRemainingTime(processes.get(i).getRemainingTime() - timeQuantum);

                    //Updates Process's Current Time
                    currentTime = endTime;

                    //Checks for New Processes
                    updateReadyQueue();

                    readyQueue.add(i);
                }

                //Gantt Chart    
                topLine.append("---------");

                processIdGanttChart.append("|   ").append("P").append(processes.get(currentProcess).getPid()).append("   ");
                if (processesExecuted != n) {
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
            }else{
                //Gantt Chart             
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
                    if(p.getArrivalTime() > currentTime){
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
        
        //Sums Every Process's Turnaround Time, Waiting Time, and Burst Time
        for (Process p : processes) {
            totalTurnaroundTime += p.getTurnaroundTime();
            totalWaitingTime += p.getWaitingTime();
            totalBurstTime += p.getBurstTime();
        }

        //Saves the Average Turnaround Time and Average Waiting Time
        avgTurnaroundTime = totalTurnaroundTime / n;
        avgWaitingTime = totalWaitingTime / n;
                
        //Saves the CPU Utilization
        cpuUtilization = (totalBurstTime/(double)endTime) * 100 ;
    }
        
    public static void updateReadyQueue() {
        //Tests if all of the Processes is Executed
        if (processesExecuted != n) {
            //Checks for New Processes
            for (int j = 0; j < n; j++) {
                //Tests if the Process's Arrival Time is less than Current Time and It is not in queue and is not completed
                if (processes.get(j).getArrivalTime() <= currentTime && !processes.get(j).isInQueue() && !processes.get(j).isComplete()) {
                    //Sets Process's is inQueue to True
                    processes.get(j).setInQueue(true);

                    //Adds the Process to the Queue
                    readyQueue.add(j);
                }
            }
        }
    }
         
    public static void output(){
        //Sorts Processes by its Arrival Time
       Collections.sort(processes, new Comparator<Process>() {
            @Override
            public int compare(Process p1, Process p2) {
                return Integer.compare(p1.getPid(), p2.getPid());
            }
        });                         
        
        System.out.println("\nInput:");
        System.out.print("------------------------------------------");

        //Prints Process's ID, Arrival Time, Burst Time, Time Quantum
        System.out.println("\nProcess \tArrival Time\tBurst Time");
        for (Process p : processes) {
            System.out.println(p.getPid() + "\t\t" + p.getArrivalTime() + "\t\t" + p.getBurstTime());
        }
        System.out.print("Time Quantum: " + timeQuantum);

        System.out.print("\n------------------------------------------\n");
        
        //Prints Gantt Chart        
        System.out.println("\nGantt Chart:");
        System.out.println(" " + topLine.toString());
        System.out.println(processIdGanttChart.toString());
        System.out.println(" " + bottomLine.toString());
        System.out.println(timeGanttChart.toString());
        
        System.out.println("\nOutput:");
        System.out.print("--------------------------------------------------------------------------------------------");

        //Prints Process's ID, Arrival Time, Burst Time, Completion Time, Turnaround Time, and Waiting Time
        System.out.println("\nProcess \tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.println(p.getPid() + "\t\t" + p.getArrivalTime() + "\t\t" + p.getBurstTime() + "\t\t"
                    + p.getCompletionTime() + "\t\t" + p.getTurnaroundTime() + "\t\t" + p.getWaitingTime());
        }

        System.out.println("--------------------------------------------------------------------------------------------");                                          
        
        System.out.println("\nTotal Turnaround Time: " + (int) totalTurnaroundTime);
        
        System.out.println("\nTotal Waiting Time: " + (int) totalWaitingTime);
        
        System.out.println("\nNumber of Processes: " + n);  
        
        //Prints Average Turnaround Time and Waiting Time into 2 Decimal Places
        DecimalFormat df = new DecimalFormat("#.##");        
        System.out.println("\nAverage Turnaround Time: " + (int) totalTurnaroundTime + " / " + n + " = " + df.format(avgTurnaroundTime));
        System.out.println("\nAverage Waiting Time: " + (int) totalWaitingTime + " / " + n + " = " + df.format(avgWaitingTime));
        
        System.out.println("\nTotal Burst Time: " + (int) totalBurstTime);
        
        System.out.println("\nLast Completion Time: " + endTime);   
        
        //Prints CPU Utilization        
        System.out.println("\nCPU Utilization: (" + (int)totalBurstTime + " / " + endTime + ") * 100 = " +  df.format(cpuUtilization) + "%");                              
    }

    public static void main(String[] args) {
        //Loops until User chooses to stop
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

