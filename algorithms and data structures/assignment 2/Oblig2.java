import java.util.*; // scanner
import java.io.*;   // exceptions

class Oblig2{
    public static void main(String[] args){
	if (args.length != 2){
	    System.out.println("Use command: java Oblig2 projectName.txt manpower");
	} else {
	    String infile = args[0];
	    System.out.println("<------ Program is reading the task data ------>" +"\n");

	    // reads the file and constructs the data structure
	    try {
		Scanner scanner = new Scanner(new File(infile));
		int numberOfLinesRead = 0;
		int numberOfTasks = Integer.parseInt(scanner.nextLine());
		String empty = scanner.nextLine();
		Task[] tasks=new Task[numberOfTasks];
		while (scanner.hasNextLine()){
		    String line = scanner.nextLine();

		    // splits the line using multiple space marks
		    String[] words = line.split("\\s+");
		    if (words.length<4){
			//line = scanner.nextLine();
			continue;
		    }
		    // pass på linjer kortere enn 4...
		    int identity = Integer.parseInt(words[0]);
		    String nameOfTask = words[1];
		    int timeEstimate = Integer.parseInt(words[2]);
		    int manPower = Integer.parseInt(words[3]);

		    // sjekker om objektet allerede er laget..
		    if (tasks[identity-1]==null){
			Task temp = new Task(identity,nameOfTask,timeEstimate,manPower);
			tasks[identity-1]=temp;
		    }
		    else {
			tasks[identity-1].update(nameOfTask,timeEstimate,manPower);
		    }
		    // Reads whichs tasks must be completed before this one can start.
		    // If the task does not exist a new task object is made and later updated.
		    for (int a=4;a<words.length;a++){
			int readNumber=Integer.parseInt(words[a]); 
			if (readNumber==0)
			    continue;
			else{

			if (tasks[readNumber-1]==null){
			    // lager nytt tomt task objekt
			    tasks[readNumber-1]=new Task(readNumber);
			    // må oppdatere disse kantene...
			    // nye skal peke på denne...
			    tasks[readNumber-1].makeEdge(tasks[readNumber-1],tasks[identity-1]);
			    tasks[identity-1].inComingTasks.add(tasks[readNumber-1]);// incomm node.
			    tasks[identity-1].increasePredecessors();
			    continue;
			}
			else {
			    tasks[readNumber-1].makeEdge(tasks[readNumber-1],tasks[identity-1]);
			    tasks[identity-1].inComingTasks.add(tasks[readNumber-1]);// incomm node.
			    tasks[identity-1].increasePredecessors();
			    continue;
			}
			} 
		    }
		}
		Graph graf = new Graph(tasks);
		graf.topologicalSort();
		graf.putInTopOrder();
		graf.computeSlack();
		graf.runProject();
		graf.printInfo();

		scanner.close();
	    } catch (FileNotFoundException e) {
		System.out.println("The file to be read does not exist");
		System.exit(0);
	    }
	}
    }
}

class Graph{
    Task[] toDo;
    Task[] topologicalOrder;
    Graph(Task[] tsk){
	toDo=tsk;
    }
    // Ensure that the order of the nodes indicates
    // that all previous tasks which it depends upon have been completed.
    void putInTopOrder(){
	topologicalOrder = new Task[toDo.length];
	for (int i=0;i<toDo.length;i++){
	    int place = toDo[i].topOrder;
	    topologicalOrder[place]=toDo[i];
	}
    }
    // Traverses the nodes using topological sorts and computes necessary data.
    void topologicalSort(){
	LinkedList<Task> queue = new LinkedList<Task>();
	Task v;
	LinkedList<Task> myLoop = new LinkedList<Task>();

	int teller=0;
	for (int i=0;i<toDo.length;i++){
	    if (toDo[i].cntPredecessors==0){
		queue.add(toDo[i]);
	    }
	}
	while (queue.size()>0){
	    v=queue.getFirst();

	    // computes earliest start times starting with the first node.
	    if (v.inComingTasks.size()==0){
		v.earliestStart=0;
		v.earliestCompleted=v.time;
	    }
	    else{
		v.computeEarliest();
	    }

	    // removes the node from loop candidates
	    myLoop.remove(v);

	    queue.removeFirst();

	    v.updateTopologicalOrder(teller);
	    teller++;
	    for (int j=0;j<v.outEdges.size();j++){
		Task node = v.outEdges.get(j).v;
		node.deCreasePred();
		
		// finds nodes for the loop candidates
		if (node.pointed==false)
		    myLoop.add(node); // this node is yet to be completed
		node.pointed=true; // we have pointed to this node.
		if (node.cntPredecessors==0)
		    queue.add(node);
	    }
	}
	// If we cannot access all nodes we have found a loop
	if (teller<toDo.length){
	    System.out.println("One or more cycles has been found. The program has aborted");
	    boolean found=false;
	    while (!found){
		for (int a=0;a<myLoop.size();a++){
		    Task findLoop=myLoop.get(a);
		    System.out.println("Prøver node: "+findLoop.id);
		    findLoop.findPath(findLoop);
		}
		found=true;
	    }
	}
    }
    void printInfo(){
	System.out.println("List of project tasks: ");
	for (int i=0;i<toDo.length;i++){
	    toDo[i].printInfo();
	    System.out.print("\n");
	}
    }
    void printInfoTop(){
	for (int i=0;i<topologicalOrder.length;i++)
	    topologicalOrder[i].printInfo();
    }
    void computeSlack(){
	int antallNoder=toDo.length;
	int maksVerdi=0;
	for (int a=0;a<toDo.length;a++){
	    if (toDo[a].earliestCompleted>=maksVerdi){
		maksVerdi=toDo[a].earliestCompleted;
	    }
	}
	//System.out.println("Maksverdi er "+maksVerdi);
	for (int i=antallNoder;i>0;i--){
	    Task tmp2=topologicalOrder[i-1];
	    
	    if (tmp2.outEdges.size()==0){
		//System.out.println("Jeg har kant null "+tmp2.id);
		tmp2.latestStart=maksVerdi-tmp2.time;
		//System.out.println(" latest start er: "+tmp2.latestStart);
		tmp2.slack=tmp2.latestStart-tmp2.earliestStart;
		//tmp2.latestStart=tmp2.earliestStart;
		//tmp2.slack=0;
	    }
	    else{
		tmp2.computeSlack();
	    }
	}
    }
    void runProject(){
	int totalTime=topologicalOrder[toDo.length-1].earliestCompleted;
	LinkedList<Task> running = new LinkedList<Task>();
	int manPowerInUse=0;

	System.out.println("Starting the project:");
	for (int i=0;i<totalTime+1;i++){
	    System.out.println("\n"+"Time: "+i);
	    for (int c=0;c<toDo.length;c++){
		Task thisTask=topologicalOrder[c];
		if (topologicalOrder[c].earliestStart==i){
		    running.add(thisTask);
		    System.out.println("Starting task: "+thisTask.id+" "+thisTask.name);
		    manPowerInUse=manPowerInUse+thisTask.staff;
		}
	    }
	    for (int b=0;b<running.size();b++){
		Task toStop=running.get(b);
		if (toStop.earliestCompleted==i){
		    running.remove(toStop);
		    b=b-1;
		    System.out.println("Completed task: "+toStop.id+" "+toStop.name);
		    manPowerInUse=manPowerInUse-toStop.staff;
		}
	    }
	    System.out.println("Current working staff: "+manPowerInUse);
	}
	System.out.println("\n"+"**** Shortest possible project execution is "+totalTime+" ****\n");
    }
}

class Task{
    int id, time, staff;
    String name;
    int earliestStart, latestStart, earliestCompleted, slack;
    LinkedList<Edge> outEdges = new LinkedList<Edge>();
    LinkedList<Task> inComingTasks = new LinkedList<Task>();
    int cntPredecessors;
    int topOrder; // the order in which the task is to be started
    boolean visited=false;
    boolean pointed=false;
    // Creates empty task if not read from file yet
    Task(int n){
	id = n;
    }
    Task(int n,String nm,int tm,int mp){
	id = n;
	name = nm;
	time = tm;
	staff = mp;
    }
    // if the object is already made with empty info.
    void computeEarliest(){
	int tmp=0;
	for (int i=0;i<inComingTasks.size();i++){
	    Task test = inComingTasks.get(i);
	    if (test.earliestCompleted>tmp){
		tmp=test.earliestCompleted;
	    }
	}
	earliestStart=tmp;
	earliestCompleted=earliestStart+time;
    }
    void update(String na,int ti,int st){
	name=na;
	time=ti;
	staff=st;
    }
    void increasePredecessors(){
	cntPredecessors++;
    }
    void deCreasePred(){
	cntPredecessors--;
    }
    void makeEdge(Task from,Task to){
	Edge addNew = new Edge(from,to);
	outEdges.add(addNew);
    }
    void updateTopologicalOrder(int t){
	topOrder=t;
    }
    void printInfo(){
	System.out.println("\n"+id+" "+name); 
	System.out.println("Time needed to finish task: "+time);
	System.out.println("Manpower required to complete task: "+staff);
	System.out.println("Slack er: "+slack);
	System.out.println("Earliest start is: "+earliestStart);
	System.out.println("latestStart is: "+latestStart);
	System.out.print("Tasks which depend on this task: ");
	for (int i=0;i<outEdges.size();i++){
	    Edge get=outEdges.get(i);
	    Task dependant=get.v;
	    System.out.print(dependant.id+" ");
	}
    }
    // Tries to find a path back to itself using recursion.
    void findPath(Task findMe){
	if (visited==false){
	    System.out.println("Noden er: "+id);
	}
	for (int b=0;b<outEdges.size();b++){
	    //System.out.println("Trying edge");
	    Edge tryEdge=outEdges.get(b);
	    Task tryNode=tryEdge.v;
	    //System.out.println("Trying "+tryNode.id);
	    if (tryNode==findMe){
		System.out.println("Cycle is completed. Next node is: "+tryNode.id);
		visited=true;
		System.exit(0);
	    }
	    else{
		tryNode.findPath(findMe);
	    }
	}
    }
    void computeSlack(){
	int temp3=0;
	for (int i=0;i<outEdges.size();i++){
	    Edge testEdge = outEdges.get(i);
	    Task test2 = testEdge.v;
	    if (temp3==0){
		temp3=test2.earliestStart;
	    }
	    else{
		if (test2.earliestStart<temp3){
		    temp3=test2.earliestStart;
		}
	    }
	    latestStart=temp3-time;
	    slack=latestStart-earliestStart;
	}
    }
}

class Edge{
    Task u; // from
    Task v; // to
    Edge(Task uu,Task vv){
	u=uu;
	v=vv;
    }
}