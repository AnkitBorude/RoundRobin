package example;

import java.util.LinkedList;

abstract class Systemab {
	static protected int countertime = 0;
	static protected LinkedList<Process> readyQueue = new LinkedList<Process>();
	static protected LinkedList<Process> jobQueue = new LinkedList<Process>();
	protected LinkedList<Process> executedList = new LinkedList<Process>();

	static class Process {
		String name;
		int burst_time = 0;
		int remaining_burst_time = 0;
		int arrival_time = 0;
		int waiting_time = 0;
		int turnaround_time = 0;
		int last_execution = 0;

		Process(String name, int bt) {
			remaining_burst_time = burst_time = bt;
			this.name = name;
		}

		Process(String name, int at, int bt) {
			remaining_burst_time = burst_time = bt;
			arrival_time = at;
			this.name = name;
		}

		public String toString() {

			return name + " \t " + arrival_time + " \t " + burst_time + " \t " + waiting_time + " \t " + turnaround_time
					+ "\n";
		}

		public String getFullprocessInfo() {
			return "Name of process:- " + name + " arrival time " + arrival_time + " burst time " + burst_time
					+ " remaining burst time " + remaining_burst_time + " last execution " + last_execution
					+ " waiting time " + waiting_time + " turnaround time " + turnaround_time + "\n";
		}
	}

	protected void create_process(String name, int arrival_time, int burst_time) {
		Systemab.Process p = new Systemab.Process(name, arrival_time, burst_time);
		Systemab.jobQueue.add(p);
	}

	protected void execute_process(Process process, int quanta) {
		process.remaining_burst_time = process.remaining_burst_time - quanta;
		if (process.last_execution == 0) {
			process.waiting_time = countertime - process.arrival_time;
		} else {
			process.waiting_time = (countertime - process.last_execution) + process.waiting_time;
		}
		process.last_execution = quanta + countertime;
		process.turnaround_time = process.waiting_time + process.burst_time;
	}

	protected void dispatchnewProcess() {
		while (!jobQueue.isEmpty()) {
			if (countertime >= jobQueue.peek().arrival_time) {// for avoiding null pointer return
				readyQueue.add(jobQueue.poll());
			} else {
				break;
			}
		}
	}

	protected void getAllprocessInfo() {
		System.out.println(" Name \t at \t bt \t wt \t tt ");
		System.out.println(executedList);
	}

	abstract void schedule();
}

class Scheduler extends Systemab {
	int quantum = 0;

	Scheduler(int q) {
		quantum = q;
	}

	void setQuantum(int q) {
		quantum = q;
	}

	void schedule() {
		dispatchnewProcess();
		while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
			if (!jobQueue.isEmpty() && readyQueue.isEmpty()) {// for handling idle time
				countertime++;
				dispatchnewProcess();
			} else {
				if (readyQueue.peek().arrival_time <= countertime) {
					Process tmp = readyQueue.poll();
					if (tmp.remaining_burst_time > quantum) {
						execute_process(tmp, quantum);
						countertime = countertime + quantum;
						dispatchnewProcess();
						readyQueue.add(tmp);
					} else if (tmp.remaining_burst_time == quantum) {
						execute_process(tmp, quantum);
						executedList.add(tmp);
						countertime += quantum;
						dispatchnewProcess();
					} else {
						int rbt = tmp.remaining_burst_time;
						execute_process(tmp, tmp.remaining_burst_time);
						executedList.add(tmp);
						countertime = countertime + rbt;
						dispatchnewProcess();
					}
				} else {
					countertime++;
				}
			}
		}
	}
}

public class StaticAccess {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Systemab system = new Scheduler(2);
		system.create_process("P1", 0, 2);// p2
		system.create_process("P2", 3, 5);
		system.create_process("P3", 4, 2);
		system.create_process("P4", 5, 3);
		system.schedule();
		system.getAllprocessInfo();

	}

}
