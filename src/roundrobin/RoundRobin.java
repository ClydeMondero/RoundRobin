package roundrobin;

import java.text.DecimalFormat;
import java.util.*;

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
    static Queue<Integer> queue = new LinkedList<>();
    
    //String Builders for the Gantt Chart
    static StringBuilder topLine = new StringBuilder(); 
    static StringBuilder ganttChart = new StringBuilder(); 
    static StringBuilder bottomLine = new StringBuilder(); 
    static StringBuilder ganttChart2 = new StringBuilder();
    
    //Average Turnaround Time and Average Waiting Time
    static double avgTurnaroundTime, avgWaitingTime;
    
    //Current Process Executing, Start and End Time
    static int currentProcess, startTime, endTime;           
    
    //Total Burst Time
    static double totalBurstTime;
    
    //CPU Utilization
    static double cpuUtilization;        
       
    public static void input(){      
        //Clear
        if(!processes.isEmpty()){
            processes.clear();
            queue.clear();
            topLine.setLength(0);
            ganttChart.setLength(0);
            bottomLine.setLength(0);
            ganttChart2.setLength(0);
            
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
        Comparator<Process> arrivalTimeComparator = new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return Double.compare(p1.getArrivalTime(), p2.getArrivalTime());
            }
        };
                
        Collections.sort(processes, arrivalTimeComparator);
    }
         
    public static void roundRobin(){
        //Initialize Queue 
        queue.add(0);        
        
        //Sets the 1st Processes InQueue to True
        processes.get(0).setInQueue(true);
        
        //Initalizes Current Time and the Number of Procceses Executed
        currentTime = 0;                
        processesExecuted = 0;                

        //Sets Current Process to Null or Empty     
        currentProcess = -1;                
        
        //Initializes Start Time, and End Time
        startTime = 0;
        endTime = 0;                 
                        
        if(currentTime != processes.get(0).getArrivalTime()){
            //Gantt Chart             
            topLine.append("---------");                                                           
            
            ganttChart.append(" |  ").append("--").append("  |");
            ganttChart2.append("    ").append(currentTime).append("    ");            
            
            bottomLine.append("---------");
        }
        
        //Increments Current Time until the First Process Arives
        while(currentTime != processes.get(0).getArrivalTime()){
            currentTime++;
        }

        // Update Ready Queue until it is Empty
        while (!queue.isEmpty()) {            
            int i = queue.remove();                
            
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
                checkForNewProcesses();

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
                checkForNewProcesses();

                queue.add(i);
           }                                                                                               
            
            //Gantt Chart    
            topLine.append("---------");                                                        
            
            ganttChart.append(" |  ").append("P").append(processes.get(currentProcess).getPid()).append("  |");
            ganttChart2.append("    ").append(startTime);
            if(startTime >= 10){
                ganttChart2.append("   ");
            }else {
                ganttChart2.append("    ");
            }
            
            bottomLine.append("---------");                       
        }
        
        //Lines and End Time
        topLine.append("------");
        bottomLine.append("------");
        ganttChart2.append("    ").append(endTime);
        
        avgTurnaroundTime = 0;
        avgWaitingTime = 0;
        totalBurstTime = 0;
        
        //Sums Every Process's Turnaround Time, Waiting Time, and Burst Time
        for (Process p : processes) {
            avgTurnaroundTime += p.getTurnaroundTime();
            avgWaitingTime += p.getWaitingTime();
            totalBurstTime += p.getBurstTime();
        }

        //Saves the Average Turnaround Time and Average Waiting Time
        avgTurnaroundTime /= n;
        avgWaitingTime /= n;
                
        //Saves the CPU Utilization
        cpuUtilization = (totalBurstTime/(double)endTime) * 100 ;
    }
        
    public static void checkForNewProcesses() {
        //Tests if all of the Processes is Executed
        if (processesExecuted != n) {
            //Checks for New Processes
            for (int j = 0; j < n; j++) {
                //Tests if the Process's Arrival Time is less than Current Time and It is not in queue and is not completed
                if (processes.get(j).getArrivalTime() <= currentTime && !processes.get(j).isInQueue() && !processes.get(j).isComplete()) {
                    //Sets Process's is inQueue to True
                    processes.get(j).setInQueue(true);

                    //Adds the Process to the Queue
                    queue.add(j);

                }
            }
        }
    }
         
    public static void output(){
        //Sorts Processes by its Arrival Time
        Comparator<Process> processIdComparator = new Comparator<Process>() {
            public int compare(Process p1, Process p2) {
                return Double.compare(p1.getPid(), p2.getPid());
            }
        };
                
        Collections.sort(processes, processIdComparator);
        
        System.out.print("\n------------------------------------------");

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
        System.out.println(ganttChart.toString());
        System.out.println(" " + bottomLine.toString());
        System.out.println(ganttChart2.toString());
        
        System.out.print("\n--------------------------------------------------------------------------------------------");

        //Prints Process's ID, Arrival Time, Burst Time, Completion Time, Turnaround Time, and Waiting Time
        System.out.println("\nProcess \tArrival Time\tBurst Time\tCompletion Time\tTurnaround Time\tWaiting Time");
        for (Process p : processes) {
            System.out.println(p.getPid() + "\t\t" + p.getArrivalTime() + "\t\t" + p.getBurstTime() + "\t\t"
                    + p.getCompletionTime() + "\t\t" + p.getTurnaroundTime() + "\t\t" + p.getWaitingTime());
        }

        System.out.println("--------------------------------------------------------------------------------------------");
        
        //Prints Average Turnaround Time and Waiting Time into 2 Decimal Places
        DecimalFormat df = new DecimalFormat("#.##");        
        System.out.println("\nAverage Turnaround Time: " + df.format(avgTurnaroundTime));
        System.out.println("\nAverage Waiting Time: " + df.format(avgWaitingTime));
        
        //Prints CPU Utilization        
        System.out.println("\nCPU Utilization: " +  df.format(cpuUtilization) + "%");                              
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

