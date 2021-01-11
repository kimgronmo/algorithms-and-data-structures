import java.util.*; // scanner
import java.io.*;   // exceptions

class Oblig3{
    public static void main(String[] args){
	if (args.length != 2){
	    System.out.println("Use command: java Oblig3 config.txt data.txt");
	}
	else{
	    String config = args[0];
	    String data = args[1];
	    Network theNet = new Network(config,data);
	    theNet.printMenu();
	    theNet.run();
	}
    }
}

class Network{
    LinkedList<Node> theNodes = new LinkedList<Node>();
    Node temp1;
    Node temp2;
    Node addData; // adds data to this node.
    Node listInfo; // prints info for this node/machine
    Node requestsData,sendsData;

    // Dijkstra data
    Node v,lowest;
    LinkedList<Integer> dataNotFound = new LinkedList<Integer>();;
    LinkedList<Node> candidates = new LinkedList<Node>();
    String[] dataListe;
    int totalTime,totalCost;
    boolean funnet=false;
    int lowestCost,lowestTime;
    int allTime,allCost; // total time/cost for entire transfer
    LinkedList<Node> both=new LinkedList<Node>();
    LinkedList<Integer> bothTime=new LinkedList<Integer>();
    int hvilkentid;

    Network(String makeMe,String information){
	System.out.println("\n"+"The system is configuring the Network....");
	try{
	    Scanner scanner1 = new Scanner(new File(makeMe));
	    while (scanner1.hasNextLine()){
		String line1 = scanner1.nextLine();
		String[] info1 = line1.split("\t");
		//System.out.println(line1);
		if (theNodes.size()==0){
		    //System.out.println("I'm making the first node");
		    Node new1 = new Node(Integer.parseInt(info1[0]));
		    Node new2 = new Node(Integer.parseInt(info1[1]));
		    int time = Integer.parseInt(info1[2]);
		    int cost = Integer.parseInt(info1[3]);
		    new1.addEdge(new2,time,cost);
		    new2.addEdge(new1,time,cost);
		    theNodes.add(new1);
		    theNodes.add(new2);
		    //System.out.println(theNodes.size()); // sjekk om jeg har 40 noder til slutt
		}
		else{
		    int nodeId1=Integer.parseInt(info1[0]);
		    int nodeId2=Integer.parseInt(info1[1]);
		    int time = Integer.parseInt(info1[2]);
		    int cost = Integer.parseInt(info1[3]);
		    int størrelse=theNodes.size();
		    temp1=null;
		    temp2=null; // checks to see if the nodes already exists
		    for (int i=0;i<størrelse;i++){
			if (theNodes.get(i).identity==nodeId1){
			    temp1=theNodes.get(i);
			}
			if (theNodes.get(i).identity==nodeId2){
			    temp2=theNodes.get(i);
			}
		    }
		    if (temp1==null){
			temp1=new Node(nodeId1);
			theNodes.add(temp1);
		    }
		    if (temp2==null){
			temp2=new Node(nodeId2);
			theNodes.add(temp2);
		    }
		    temp1.addEdge(temp2,time,cost);
		    temp2.addEdge(temp1,time,cost);
		}
	    }

	} catch (FileNotFoundException e){
	    System.out.println("The config file does not exist");
	    System.exit(0);
	}
	System.out.println("The number of Nodes created is: "+theNodes.size());
	System.out.println("The network has been created successfully");
	try{
	    System.out.println("\n"+"Trying to add data to the network nodes....");
	    Scanner scanner2 = new Scanner(new File(information));
	    while (scanner2.hasNextLine()){
		String line2 = scanner2.nextLine();
		String[] info2 = line2.split("\t");
		int nodeId=Integer.parseInt(info2[0]);
		int cap=Integer.parseInt(info2[1]);
		int numberOfDataElements=info2.length-2;
		//System.out.println(numberOfDataElements);
		// usikkert om capacity er antall originale+local eller bare local
		// knows/assumes the node already exists..
		addData=null;
		for (int j=0;j<theNodes.size();j++){
		    if (theNodes.get(j).identity==nodeId){
			addData=theNodes.get(j);
		    }
		}
		addData.updateInfo(cap,info2);
	    }
	} catch (FileNotFoundException e){
	    System.out.println("The data file does not exist");
	    System.exit(0);
	}
	System.out.println("The data has been added successfully");
	System.out.println("The network is now ready for use"+"\n");
    }
    void printMenu(){
	System.out.println("\n"+"Available commands:");
	System.out.println("list <Machine id>        - lists data info on a given machine");
	System.out.println("machine_id:search_pref:data_ownership_pref:data data ...");
	System.out.println("help                     - prints this help");
	System.out.println("exit                     - exit the program"+"\n");
    }
    // loops a command line interface
    void run(){
	String[] kommandoer;
	int command = -2;
	Scanner scan = new Scanner(System.in);
	while (command != 0){
	    System.out.print("Shell>>");
	    String commandLine = scan.nextLine();
	    kommandoer= commandLine.split(" ");
	    int antallKommandoer=kommandoer.length;
	    if (kommandoer[0].equals("")){
		System.out.println("You have given no commands. Use help for information");
		continue;
	    }
	    if (kommandoer[0].equals("help")){
		if (antallKommandoer > 1){
		    System.out.println("You have given illegal arguments. Use help for information");
		    continue;
		}
		else{
		    printMenu();
		    continue;
		}
	    }
	    if (kommandoer[0].equals("exit")){
		if (antallKommandoer > 1){
		    System.out.println("You have given illegal arguments. Use help for information");
		    continue;
		}
		else{
		    command=0;
		    continue;
		}
	    }
	    if (kommandoer[0].equals("list")){
		if (antallKommandoer != 2){
		    System.out.println("You have given illegal arguments. Use help for information");
		    continue;
		}
		else{
		    int nodeId=Integer.parseInt(kommandoer[1]);
		    boolean found=false;
		    listInfo=null;
		    for (int k=0;k<theNodes.size();k++){
			if (theNodes.get(k).identity==nodeId){
			    listInfo=theNodes.get(k);
			}
		    }
		    if (listInfo==null){
			System.out.println("You have requested a machine not found on the network");
			continue;
		    }
		    else{
			listInfo.printInfo();
			continue;
		    }
		}
	    }
	    else{
		// Assumes the requests given have correct syntax..
		String[] request = commandLine.split(":");
		//System.out.println("antall request commandoer er: "+request.length);
		// splitt og hersk...
		//System.out.println("commands: "+kommandoer[0]);
		int nodeId=Integer.parseInt(request[0]);
		//System.out.println("Noden jeg ber om er: "+nodeId);
		boolean found=false;
		requestsData=null;
		for (int k=0;k<theNodes.size();k++){
		    if (theNodes.get(k).identity==nodeId){
			requestsData=theNodes.get(k);
			//System.out.println("Har funnet noden: "+requestsData.identity);
		    }
		}
		if (requestsData==null){
		    System.out.println("You have requested a machine not found on the network");
		    continue;
		}
		else{ // working on this method...
		    // Finds the shortest paths to the given node
		    if (request[1].equals("C") || request[1].equals("B")){
			dijkstraCost(requestsData);
		    }
		    if (request[1].equals("T")){
			dijkstraTime(requestsData);
		    }
		    //if (request[2].equals("O")){
			// tar en databit og leter gjennom alle nodene til den finner noder som har
			// den biten.
		    dataListe=request[3].split("\\s+");
		    dataNotFound.clear(); // removes the data not found on last search
		    for (int p=0;p<dataListe.length;p++){
			candidates.clear();
			int myData=Integer.parseInt(dataListe[p]);
			// går gjennom alle nodene og ser etter original data
			//			if (request[2].equals("O")){
			for (int aa=0;aa<theNodes.size();aa++){
			    Node test=theNodes.get(aa);
			    //funnet=false;
			    if (request[2].equals("O")){
				funnet=false;
				for (int ab=0;ab<test.originalData.size();ab++){
				    if (myData==test.originalData.get(ab).data){
					funnet=true;
				    }
				}
				if (funnet==true){
				    candidates.add(test);
				}
			    }
			    if (request[2].equals("A")){
				funnet=false;
				for (int ba=0;ba<test.originalData.size();ba++){
				    if (myData==test.originalData.get(ba).data){
					funnet=true;
				    }
				}
				for (int bb=0;bb<test.localCopy.size();bb++){
				    if (myData==test.localCopy.get(bb).data){
					//System.out.println("Funnet i localcopy");
					funnet=true;
				    }
				}
				if (funnet==true){
				    candidates.add(test);
				}
			    }
			}
			if (candidates.size()==0){ // hvis data ikke ble funnet.
			    dataNotFound.add(myData);
			    //System.out.println("Data is not found");
			}

			// antall kandidater funnet.
			//System.out.println(candidates.size()+" Candidates have been found");
			Integer[] totalcosts=new Integer[candidates.size()];
			Integer[] totaltime=new Integer[candidates.size()];
			for (int ac=0;ac<candidates.size();ac++){
			    Node candidat=candidates.get(ac);
			    totalCost=0;
			    totalTime=0;
			    //Node path1;
			    //System.out.println("");
			    while(candidat!=null){
				//System.out.print(candidat.identity);
				Node tmp=candidat;
				candidat=candidat.path;
				Edge kant;
				if (candidat!=null){
				    for (int ad=0;ad<candidat.outGoing.size();ad++){
					kant=candidat.outGoing.get(ad);
					if (kant.w==tmp){
					    totalTime=totalTime+kant.time;
					    totalCost=totalCost+kant.cost;
					}
				    }
				}
			    }
			    //System.out.println("Total cost is: "+totalCost);
			    //System.out.println("Total time is: "+totalTime);
			    totalcosts[ac]=totalCost;
			    totaltime[ac]=totalTime;
			}
			// Må velge ut hvilken kandidat som er best..
			// og printe denne..C T B. oppdater local data
			if (request[1].equals("C")){
			    //Node lowest=null;
			    //int lowestCost;
			    //int lowestTime;
			    for (int ca=0;ca<candidates.size();ca++){
				if (ca==0){
				    lowest=candidates.get(ca);
				    lowestCost=totalcosts[ca];
				    lowestTime=totaltime[ca];
				}
				else{
				    if (totalcosts[ca]<lowestCost){
					lowest=candidates.get(ca);
					lowestCost=totalcosts[ca];
					lowestTime=totaltime[ca];
				    }
				}
			    }
			    // for each completed transfer
			    //System.out.println("Lowest cost is: "+lowestCost);
			    //System.out.println("Lowest time is: "+lowestTime);
			}
			if (request[1].equals("T")){
			    for (int cc=0;cc<candidates.size();cc++){
				if (cc==0){
				    lowest=candidates.get(cc);
				    lowestCost=totalcosts[cc];
				    lowestTime=totaltime[cc];
				}
				else{
				    if (totaltime[cc]<lowestTime){
					lowest=candidates.get(cc);
					lowestCost=totalcosts[cc];
					lowestTime=totaltime[cc];
				    }
				}
			    }
			}
			if (request[1].equals("B")){
			    // liste med lowest cost verdier og finn den med lavest tid..
			    for (int ca=0;ca<candidates.size();ca++){
				if (ca==0){
				    lowest=candidates.get(ca);
				    lowestCost=totalcosts[ca];
				    lowestTime=totaltime[ca];
				    // 
				    both.add(lowest);
				    bothTime.add(lowestTime);
				}
				else{
				    if (totalcosts[ca]==lowestCost){
					lowest=candidates.get(ca);
					lowestCost=totalcosts[ca];
					lowestTime=totaltime[ca];
					//
					both.add(lowest);
					bothTime.add(lowestTime);
				    }
				    if	(totalcosts[ca]<lowestCost){
					lowest=candidates.get(ca);
					lowestCost=totalcosts[ca];
					lowestTime=totaltime[ca];
					both.clear();
					bothTime.clear();
					both.add(lowest);
					bothTime.add(lowestTime);
				    }
				}
			    }
			    // finds the candidate with lowest time among those with
			    // lowest cost.
			    System.out.println("Antall med samme cost: "+both.size());
			    for (int ad=0;ad<bothTime.size();ad++){
				if (ad==0){
				    hvilkentid=ad;
				}
				else{
				    if (bothTime.get(ad)<bothTime.get(hvilkentid)){
					hvilkentid=ad;
				    }
				}
			    }
			    //System.out.println("the node nr is: "+hvilkentid);
			    lowest=both.get(hvilkentid);
			    lowestTime=bothTime.get(hvilkentid);

			}
			// add funnet data til localcopy og print path... husk å fjerne hvis full...
			if (candidates.size()!=0){
			    // ellers lagres data som ikke ble funnet...
			    requestsData.addData(myData);
			}
			if (p==0){
			    allTime=lowestTime;
			    allCost=lowestCost;
			}
			else{
			    if (lowestTime>allTime){
				allTime=lowestTime;
			    }
			    allCost=allCost+lowestCost;
			}
			// No printing if data not found...
			if (candidates.size()!=0){
			Node print=lowest;
			System.out.print(myData+": ");
			while (print!=null){
			    System.out.print(print.identity);
			    if (print.path!=null){
				System.out.print("->");
			    }
			    print=print.path;
			}
			System.out.println(" (t="+lowestTime+", c="+lowestCost+")");

			}
			// oppdater totaltime og totalcosts.
		    }	
		    System.out.println("Total time: "+allTime+", total cost = "+allCost);
		    // If any data is not found the data is printed to screen.
		    if (dataNotFound.size()>0){
			System.out.print("\n"+"These data have not been found: ");
			// prints the data that was not found in the Network
			for (int bc=0;bc<dataNotFound.size();bc++){
			    System.out.print(" "+dataNotFound.get(bc));
			}
		    }
		    System.out.println("\n");
		    continue;
		}
	    }
	}
    }
    void dijkstraCost(Node s){
	for (int o=0;o<theNodes.size();o++){
	    Node v=theNodes.get(o);
	    v.distance=999999;
	    v.known=false;
	}
	s.path=null; // trengs denne??
	s.distance=0;
	// går gjennom alle noder og finner neste node som skal behandles.
	for (int k=0;k<theNodes.size();k++){

	    v=theNodes.get(0);
	    for (int j=0;j<theNodes.size();j++){
		Node smallest=theNodes.get(j);
		if (smallest.known==false && smallest.distance <= v.distance){
		    v=smallest;
		}
	    }
	    // v inneholder ukjent node med minste avstand
	    if (v.known==true){
		// do nothing.. no more nodes to be found 
	    }
	    else{
		v.known=true;
		for (int l=0;l<v.outGoing.size();l++){
		    Edge kantTilNode=v.outGoing.get(l);
		    if (!kantTilNode.w.known){// hvis ukjent
			if (v.distance+kantTilNode.cost<kantTilNode.w.distance){
			    // updates w
			    kantTilNode.w.distance=v.distance+kantTilNode.cost;
			    kantTilNode.w.path=v;
			}
		    }
		}
	    }
	}
    }
    void dijkstraTime(Node s){
	for (int o=0;o<theNodes.size();o++){
	    Node v=theNodes.get(o);
	    v.distance=999999;
	    v.known=false;
	}
	s.path=null; // trengs denne??
	s.distance=0;
	// går gjennom alle noder og finner neste node som skal behandles.
	for (int k=0;k<theNodes.size();k++){
	    v=theNodes.get(0);
	    for (int j=0;j<theNodes.size();j++){
		Node smallest=theNodes.get(j);
		if (smallest.known==false && smallest.distance <= v.distance){
		    v=smallest;
		}
	    }
	    // v inneholder ukjent node med minste avstand
	    if (v.known==true){
		// do nothing.. no more nodes to be found 
	    }
	    else{
		v.known=true;
		for (int l=0;l<v.outGoing.size();l++){
		    Edge kantTilNode=v.outGoing.get(l);
		    if (!kantTilNode.w.known){// hvis ukjent
			if (v.distance+kantTilNode.time<kantTilNode.w.distance){
			    // updates w
			    kantTilNode.w.distance=v.distance+kantTilNode.time;
			    kantTilNode.w.path=v;
			}
		    }
		}
	    }
	}
    }
}

class Node{
    LinkedList<Edge> outGoing = new LinkedList<Edge>();
    LinkedList<Data> originalData=new LinkedList<Data>();
    LinkedList<Data> localCopy=new LinkedList<Data>();
    int identity;
    int capacity;
    Data dataNode;

    // Dijkstra data
    boolean known=false;
    int distance;
    Node path;

    Node(int id){
	identity=id;
    }
    void addEdge(Node To,int tm,int co){
	Edge makeEdge=new Edge(this,To,tm,co);
	outGoing.add(makeEdge);
    }
    void updateInfo(int cap,String[] info){
	capacity=cap;
	for (int k=2;k<(info.length);k++){
	    int dataInfo=Integer.parseInt(info[k]);
	    dataNode = new Data(dataInfo);
	    originalData.add(dataNode);
	}
	int n=originalData.size();
	capacity=capacity-n; // capacity er summen av local og original data
	//System.out.println("The capacity is: "+capacity);
	//System.out.println("The number of original data added is: "+n);
    }
    void printInfo(){
	System.out.println("Machine: "+identity);
	System.out.print("Original data: ");
	for (int l=0;l<originalData.size();l++){
	    System.out.print(originalData.get(l).data+" ");
	}
	System.out.print("\n"+"Local copies: ");
	for (int m=0;m<localCopy.size();m++){
	    System.out.print(localCopy.get(m).data+" ");
	}
	System.out.println("\n");    
    }
    void addData(int dt){
	Data tmp2=new Data(dt);
	if (localCopy.size()<capacity){
	    localCopy.add(0,tmp2);
	}
	else{
	    localCopy.removeLast();
	    localCopy.add(0,tmp2);
	}
    }
}
class Edge{
    Node u; // from
    Node w; // to
    int time,cost;
    Edge(Node uu,Node vv,int t,int c){
	u=uu;
	w=vv;
	time=t;
	cost=c;
    }
}
class Data{
    int data;
    Data(int d){
	data=d;
    }
}