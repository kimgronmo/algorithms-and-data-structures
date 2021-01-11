import java.util.*; // scanner
import java.lang.*;
import java.io.*; // exceptions

class Oblig1{
    public static void main(String[] args){
	ContactTree bst = new ContactTree();
	if (args.length != 1) {
	    System.out.println("Bruk kommando: java Oblig1 contact.txt");
	} else {
	    String infile = args[0];
	    System.out.println("<------ Programmet leser inn kontaktdata ------>");

	    /* innlesing av fil */
	    try {
		Scanner scanner = new Scanner(new File(infile));
		int antalllinjer=0;
		while (scanner.hasNextLine()) {
		    String line = scanner.nextLine();

		    // splits the line using Tab.
		    String[] words = line.split("\t");

		    // If the line read is empty or too few words go to next line.
		    if (words.length < 4){
			continue;
		    }
		    bst.add(words[0],words[1],words[2],words[3]);
		   
		    // Tester for å se om filen er lest korrekt.
		    //antalllinjer++;
		    //System.out.println("Har lest inn "+antalllinjer);
		    //System.out.println(words[0]+" "+words[1]+" "+words[2]+" "+words[3]);
		}
		scanner.close();
		}catch (FileNotFoundException e) {
		    System.out.println("Fil som skal leses inn finnes ikke");
		    System.exit(0);
		} 
	    }
	    Shell shell= new Shell(bst);
	    shell.createFreqTree2();
	    shell.printMenu();
	    shell.loop();
	    System.out.println("<------ Programmet blir nå avsluttet     ------>");
    }
}

class ContactTree{
    Node roten;
    int størrelse;
    String[] lastNames;
    int teller;
    Node[] nodeArray;
    int nodeCounter;

    // Definerer nodene i treet.
    class Node{
	Node denne;
	Node leftChild;
	Node rightChild;
	String lastName;
	String firstName;
	String countryCode;
	String phoneNumber;

	Node(String etternavn,String fornavn,String landskode,String tlf){
	    denne=this;
	    lastName=etternavn;
	    firstName=fornavn;
	    countryCode=landskode;
	    phoneNumber=tlf;
	}
	// Creates an array of the lastnames.
	void addToArray(){
	    if (leftChild != null){
		leftChild.addToArray();
	    }
	    lastNames[teller]=denne.lastName;
	    teller++;
	    if (rightChild != null){
		rightChild.addToArray();
	    }
	}
	void toNodeArray(){
	    if (leftChild != null){
		leftChild.toNodeArray();
	    }
	    nodeArray[nodeCounter]=denne;
	    // fjern denne
	    //System.out.println("Node counter"+nodeCounter);
	    nodeCounter++;
	    if (rightChild != null){
		rightChild.toNodeArray();
	    }
	}    
	void updateCountryCode(String code){
	    countryCode=code;
	    System.out.println("Country code has been updated");
	}
	void skrivUt(){
	    if (leftChild != null){
		leftChild.skrivUt();
	    }
	    denne.printInfo();
	    if (rightChild != null){
		rightChild.skrivUt();
	    }
	}
	void printInfo(){
	    System.out.println(lastName+"   \t"+firstName+"     \t"+countryCode+"\t"+phoneNumber);
	}
	void printPhone(){
	    if (leftChild!=null){
		leftChild.printPhone();
	    }
	    denne.printInfo2();
	    if (rightChild != null){
		rightChild.printPhone();
	    }
	}
	void printInfo2(){
	    System.out.println(lastName+"   \t"+firstName+"\t"+phoneNumber);
	}
	boolean add(Node n){
	    int sammenligne = denne.lastName.compareTo(n.lastName);
	    if(sammenligne == 0) 
		if (denne.firstName.compareTo(n.firstName)==0)
		    if (denne.countryCode.compareTo(n.countryCode)==0)
			if (denne.phoneNumber.compareTo(n.phoneNumber)==0)
			    return false;
	    if(sammenligne <= 0) {
		if(rightChild == null) {
		    rightChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse=rightChild.add(n); 
		    return truefalse;
		}
	    } else if(sammenligne > 0) {
		if(leftChild == null) {
		    leftChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse = leftChild.add(n);
		    return truefalse;
		}
	    }
	    return false;
	}
	boolean add2(Node n){
	    int sammenligne = denne.phoneNumber.compareTo(n.phoneNumber);
	    if(sammenligne == 0) 
		if (denne.firstName.compareTo(n.firstName)==0)
		    if (denne.countryCode.compareTo(n.countryCode)==0)
			if (denne.lastName.compareTo(n.lastName)==0)
			    return false;
	    if(sammenligne <= 0) {
		if(rightChild == null) {
		    rightChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse=rightChild.add2(n); 
		    return truefalse;
		}
	    } else if(sammenligne > 0) {
		if(leftChild == null) {
		    leftChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse = leftChild.add2(n);
		    return truefalse;
		}
	    }
	    return false;
	}	
	Node removeMin(Node m){
	    if (m==null)
		return null;
	    else if (m.leftChild != null){
		m.leftChild = removeMin(m.leftChild);
		return m;
	    } else
		return m.rightChild;
	}
	Node remove2(Node a,Node b){
	    if (b==null)
		return null;
	    if (a.phoneNumber.compareTo(b.phoneNumber) <0){
		b.leftChild = remove2(a,b.leftChild);
	    } else if (a.phoneNumber.compareTo(b.phoneNumber)>0){
		b.rightChild = remove2(a,b.rightChild);
	    }
	    else{
		if (b.leftChild == null){
		    b = b.rightChild;
		} else if (b.rightChild == null){
		    b = b.leftChild;
		} else { // to barn.
		    Node d = findMin(a);
		    //System.out.println(d.firstName);
		    a.lastName=d.lastName;
		    a.firstName=d.firstName;
		    a.countryCode=d.countryCode;
		    a.phoneNumber=d.phoneNumber;
		    // b.element data skrives til minste 
		    // data
		    // b.rightChild = remove(b,b.rightChild)
		    // ordne en metode node findMin()
		    b.rightChild=removeMin(b.rightChild);
		}    
	    }
	    return b; // husk å lage ny node med ny data.		
	}
	Node remove(Node a,Node b){
	    if (b == null)
		return null;
	    if (a.lastName.compareTo(b.lastName) < 0){
		b.leftChild = remove(a,b.leftChild);
	    } else if (a.lastName.compareTo(b.lastName) > 0){
		b.rightChild = remove(a,b.rightChild);
	    }
	    else{
		// sjekker om vi har korrekt node.
		if (a.firstName.equals(b.firstName)){
		    if (a.countryCode.equals(b.countryCode)){
			if (a.phoneNumber.equals(b.phoneNumber)){
			    if (b.leftChild == null){
				b = b.rightChild;
			    } else if (b.rightChild == null){
				b = b.leftChild;
			    } else { // to barn.
				Node d = findMin(a);
				//System.out.println(d.firstName);
				a.lastName=d.lastName;
				a.firstName=d.firstName;
				a.countryCode=d.countryCode;
				a.phoneNumber=d.phoneNumber;
				// b.element data skrives til minste 
				// data
				// b.rightChild = remove(b,b.rightChild)
				// ordne en metode node findMin()
				b.rightChild=removeMin(b.rightChild);
			    }
			}
		    }				    
		}
		else{ // går til høyre.
		    b.rightChild = remove(a,b.rightChild);
		}
	    }
	    return b; // husk å lage ny node med ny data.
	}
    }

    // legger til et element i treet. Leter gjennom treet til den finner 
    // rett plassering.
    // Sender true/false oppover i treet til roten om den lykkes/feiler.
    boolean add(String last,String first,String countryC,String phoneN){
	Node insert = new Node(last,first,countryC,phoneN);
	//(e == null) {
	//  throw new NullPointerException();
	//
	if(roten == null) {
	    roten = insert;
	    størrelse++;
	    return true;
	} else {
	    boolean truefalse = roten.add(insert);
	    return truefalse;
	}
    }
    // Adds a phone number to the phone number tree
    boolean add2(String last,String first,String countryC,String phoneN){
	Node insert = new Node(last,first,countryC,phoneN);
	if(roten == null) {
	    roten = insert;
	    størrelse++;
	    return true;
	} else {
	    boolean truefalse = roten.add2(insert);
	    return truefalse;
	}
    }
    int size(){
	return størrelse;
    }
    void printInfo(){
	roten.skrivUt();
    }
    void printPhone(){
	if (roten==null)
	    roten=null;
	else
	    roten.printPhone();
    }
    // Creates an array containing the trees lastnames.
    String[] toArray(){
	lastNames = new String[size()];
	roten.addToArray();
	teller=0; // resetter telleren
	return lastNames;
    }
    // Finds the node with the lowest value.
    Node findMin(Node n){
	if (n == null)
	    return null;
	else if (n.leftChild == null)
	    return n;
	return findMin(n.leftChild);
    }
    // Creates an array containing the nodes of the tree.
    void toNodeArray(){
	nodeArray = new Node[size()];
	//System.out.println(" array size "+size());
	roten.toNodeArray();
	nodeCounter=0; // resetter telleren
    }
    // removes a node from the tree
    void remove(Node m){
	roten=roten.remove(m,roten);
	størrelse--;
    }
    // removes a node from phone number tree
    void remove2(Node m){
	roten=roten.remove2(m,roten);
	størrelse--;
    }
    // removes the smalles node from the tree.
    void removeMin(){
	roten.removeMin(roten);
    }
}

class Shell{
    ContactTree BST;
    FrequencyTree FQT;
    FrequencyTree FQT2; // freqtre som brukes i spørsmål 2.
    String[] data;
    Shell(ContactTree tree){
	BST = tree;
    }
    void createFreqTree2(){
	FQT2 = new FrequencyTree();
	BST.toNodeArray();
	for (int i=0;i<BST.size();i++){
	    String fn=BST.nodeArray[i].firstName;
	    String ln=BST.nodeArray[i].lastName;
	    String Cc=BST.nodeArray[i].countryCode;
	    String pn=BST.nodeArray[i].phoneNumber;
	    FQT2.add3(ln,fn,Cc,pn);
	}
    }
    void printMenu(){
	// Tilgjengelige kommandoer som angitt i oppgaveteksten.
	System.out.println("\n");
	System.out.println("Available commands:");
	System.out.println("listall                  - list all registered information");
	System.out.println("numUniqueLast            - list the number of unique lastnames ");
	System.out.println("mostCommonName           - list the most common lastname(s) and firstnames");
	System.out.println("prefix <Lastna...>       - Searches all last names for specified prefix.");
	System.out.println("                      Note that the last name first letter must be a Capital.");
	System.out.println("changeName <Lastname> <firstname> <Newlastname> ");
	System.out.println("nonUniqueDiffC           - lists names where country codes differ");
	System.out.println("printPhone               - prints the Country Code tree and Phone number Tree");
	System.out.println("independence             - must give countrycode and first digit of city.");
	System.out.println("                           Example 43 2 . New countrycode 432");
	System.out.println("help                     - print this help");
	System.out.println("exit                     - exit program "+"\n");
    }
    void loop(){
	String[] kommandoer;
	int command = -2;
	Scanner scan = new Scanner(System.in);
	while (command != 0){
	    System.out.print("Shell>>");
	    String commandLine = scan.nextLine();
	    kommandoer = commandLine.split(" ");
	    int antallKommandoer = kommandoer.length;
	    // Checks to see whether the user has given any commands at all.
	    if (kommandoer[0].equals("")){
		System.out.print("You have given no commands. Use help for information or");
		System.out.println(" exit to quit the program.");
		continue;
	    }
	    // Gives an overview of which commands are available.
	    if (kommandoer[0].equals("help")){
		if (antallKommandoer > 1){
		    System.out.print("You have given illegal arguments. Use help for information");
		    System.out.println(" or just exit to quit the program");
		    continue;
		}
		else{
		    printMenu();
		    continue;
		}
	    }
	    // Removes a person from the list of registered persons.
	    if (kommandoer[0].equals("listall")){
		if (antallKommandoer < 1 || antallKommandoer > 1){
		    System.out.println("You have given too few or too many arguments.");
		    continue;
		}
		if (antallKommandoer == 1){
		    BST.printInfo();
		    System.out.println("Antall personer registrert er: "+BST.size()+"\n");
		    continue;
		}
	    }
	    if (kommandoer[0].equals("numUniqueLast")){
		if (antallKommandoer > 1){
		    System.out.println("You have given too many arguments. ");
		    continue;
		}
		if (antallKommandoer == 1){
		    FQT = new FrequencyTree();
		    String[] unikeNavn = BST.toArray();
		    for (int i=0;i<unikeNavn.length;i++)
			FQT.add(unikeNavn[i]);
		    // Prints the last name and its frequency.
		    //FQT.printInfo();
		    System.out.println("The number of unique last names is :"+FQT.size());
		    continue;
		}
	    }
	    if (kommandoer[0].equals("printPhone")){
		if (antallKommandoer > 1){
		    System.out.println("You have given too many arguments. ");
		    continue;
		}
		if (antallKommandoer == 1){
		    // Prints the last name and its frequency.
		    FQT2.printPhones();
		    //String aa="360";String bb="21";
		    //int as = aa.compareTo(bb);
		    //System.out.println(as);
		    System.out.println("The number of unique Country Codes are :"+FQT2.size());
		    continue;
		}
	    }
	    if (kommandoer[0].equals("independence")){
		if (antallKommandoer > 1){
		    System.out.println("You have given too many arguments. ");
		    continue;
		}
		if (antallKommandoer == 1){
		    int cmd =-1;
		    while (cmd!=0){
			System.out.print("Provide the country code and first digit for the city: ");
			String cmdline=scan.nextLine();
			//String[] data;
			data=cmdline.split(" ");
			cmd=0;
		    }
		    String newCountryCode=data[0]+data[1];
		    System.out.println("The new CountryCode is :"+newCountryCode);
		    FQT2.toNodeArray();
		    int as=FQT2.size();

		    // Searches through the country code treefor the correct country code. If the phonenumber
		    // in the country starts with the given number, the persons contactdata is recorded and
		    // the person is deleted from this country. A new country code node is created if it doesn't
		    // exist and the person is then added to its list of phonenumbers.

		    // The main contact tree person is then updated with the new country code.

		    for (int i=0;i<as;i++){
			if (FQT2.nodeArray[i].countryCode.equals(data[0])){
			    FQT2.nodeArray[i].phoneNumberTree.toNodeArray();
			    for (int j=0;j<FQT2.nodeArray[i].phoneNumberTree.størrelse;j++){
				if (FQT2.nodeArray[i].phoneNumberTree.nodeArray[j].phoneNumber.startsWith(data[1])){
				    System.out.println("Har funnet 1");
				    String ettern=FQT2.nodeArray[i].phoneNumberTree.nodeArray[j].lastName;
				    String forn=FQT2.nodeArray[i].phoneNumberTree.nodeArray[j].firstName;
				    String countryC=FQT2.nodeArray[i].phoneNumberTree.nodeArray[j].countryCode;
				    String phone=FQT2.nodeArray[i].phoneNumberTree.nodeArray[j].phoneNumber;
				    ContactTree tt = FQT2.nodeArray[i].phoneNumberTree;
				    tt.remove2(tt.nodeArray[j]);
				    FQT2.add3(ettern,forn,newCountryCode,phone);
  
				    // leter gjennom BST og oppdaterer personen med ny countrycode
				    for (int a=0;a<BST.size();a++){
					if (BST.nodeArray[a].lastName.equals(ettern)){
					    if (BST.nodeArray[a].firstName.equals(forn)){
						if (BST.nodeArray[a].countryCode.equals(countryC)){
						    System.out.println("Har funnet personen som skal få ny landskode");
						    BST.nodeArray[a].updateCountryCode(newCountryCode);
						}
					    }
					}
				    }

				}
			    }
			}
		    }
		    System.out.println("The number of unique Country Codes are :"+FQT2.size());
		    continue;
		}
	    }
	    // Searches the tree after lastNames starting with the string given. Note that the first letter
	    // must be a captital (i.e Egeland). problem: if the name is changed to EGeland, then prefix E 
	    // won't list it. Needs to use prefix EG.
	    if (kommandoer[0].equals("prefix")){
		if (antallKommandoer > 2 || antallKommandoer < 2){
		    System.out.print("You have given too few or too many arguments.");
		    System.out.println(" Use help or just exit to quit the program");
		    continue;
		}
		else{
		    String prefix = kommandoer[1];
		    BST.toNodeArray();
		    for (int i=0;i<BST.nodeArray.length;i++){
			if (BST.nodeArray[i].lastName.startsWith(prefix))
			    BST.nodeArray[i].printInfo();
		    }
		    continue;
		}
	    }
	    // Gives a list of person with same lastName and firstName living in different countries.
	    if (kommandoer[0].equals("nonUniqueDiffC")){
		if (antallKommandoer > 1 || antallKommandoer < 1){
		    System.out.print("You have given too few or too many arguments.");
		    System.out.println(" Use help or just exit to quit the program");
		    continue;
		}
		else{
		    BST.toNodeArray();
		    FQT = new FrequencyTree();
		    for (int i=0;i<BST.nodeArray.length;i++){
			String forn=BST.nodeArray[i].firstName;
			String ettern=BST.nodeArray[i].lastName;
			FQT.add2(ettern,forn);
		    }
		    FQT.toNodeArray();
		    //System.out.println("Tester nodes "+FQT.nodeArray.length);
		    // traverses frequency tree and checks if a name has a frequency of 
		    // more or equal to 2. If so a list of different country codes is
		    // printed if names are in different countries.
		    for(int j=0;j<FQT.size();j++){
			int frekvensen=FQT.nodeArray[j].getFreq();
			if (frekvensen >= 2){
			    //System.out.println("Funnet person med 2+");
			    String forn=FQT.nodeArray[j].firstName;
			    String ettern=FQT.nodeArray[j].lastName;
			    String[] landskoder=new String[frekvensen];
			    String[] ulikelk=new String[frekvensen];
			    int teller=0;
			    for (int k=0;k<BST.size();k++){
				String fn=BST.nodeArray[k].firstName;
				String en=BST.nodeArray[k].lastName;
				if (fn.equals(forn) && en.equals(ettern)){
				    landskoder[teller]=BST.nodeArray[k].countryCode;
				    teller++;
				}
			    }
			    //System.out.println(landskoder.length);
			    for (int l=0;l<landskoder.length;l++){
				if (ulikelk[0]==null){
				    ulikelk[0]=landskoder[l];
				    continue;
				}
				else{
				    boolean truefalse2=false;
				    for (int m=0;m<landskoder.length;m++){
					if (ulikelk[m]==null)
					    continue;
					if (ulikelk[m].equals(landskoder[l])){
					    truefalse2=true;
					    continue;
					}
				    }
				    if (truefalse2==false)
					ulikelk[l]=landskoder[l];
				}
			    }
			    int teller2=0;
			    //System.out.println("Størrelsen på ulike lk er :"+ulikelk.length);
			    for (int n=0;n<ulikelk.length;n++){
				//System.out.println(ulikelk[n]);
				if (ulikelk[n]!=null)
				    teller2++;
			    }
			    //System.out.println("teller2 er "+teller2);
			    if (teller2>=2){
				for (int o=0;o<ulikelk.length;o++){
				    if (ulikelk[o]!=null){
					System.out.println(ettern+" "+forn+" "+ulikelk[o]);
				    }
				}
			    }
			}
		    }
		    //System.out.println("Antall unike for+etternavn er: "+FQT.size());
		    continue;
		}
	    }
	    if (kommandoer[0].equals("changeName")){
		if (antallKommandoer > 4 || antallKommandoer < 4){
		    System.out.print("You have given too few or too many arguments.");
		    System.out.println(" Use help or just exit to quit the program");
		    continue;
		}
		else{
		    //System.out.println("er her");
		    int freq=0;
		    BST.toNodeArray();
		    for (int i=0;i<BST.nodeArray.length;i++){
			if (BST.nodeArray[i].lastName.equals(kommandoer[1])){
			    if (BST.nodeArray[i].firstName.equals(kommandoer[2])){
			    freq++;
			    }
			}
		    }
		    System.out.println("Antall personer funnet er: "+freq);
		    if (freq==0){
			System.out.println("Navn er ikke skrevet korrekt");
			continue;
		    }
		    //int cntr=0; // counter keeps track of which name has been found.
		    for (int j=0;j<BST.nodeArray.length;j++){
			if (BST.nodeArray[j].lastName.equals(kommandoer[1])){
			    if (BST.nodeArray[j].firstName.equals(kommandoer[2])){
				BST.nodeArray[j].printInfo();
			    }
			}
		    }
		    int cmd =-1;
		    while (cmd!=0){
			System.out.print("Angi landskode og tlfnummer på personen som skal endres: ");
			String cmdline=scan.nextLine();
			data=cmdline.split(" ");
			cmd=0;
		    }
		    boolean truefalse=false;
    		    for (int j=0;j<BST.nodeArray.length;j++){
			if (BST.nodeArray[j].lastName.equals(kommandoer[1])){
			    if (BST.nodeArray[j].firstName.equals(kommandoer[2])){
				if (BST.nodeArray[j].countryCode.equals(data[0]) && 
				    BST.nodeArray[j].phoneNumber.equals(data[1])){
				    System.out.println("Tester remove metoden");
				    BST.remove(BST.nodeArray[j]);
				    BST.add(kommandoer[3],kommandoer[2],data[0],data[1]);
				    truefalse=true;
				}
			    }
			}
		    }
		    if (truefalse)
			System.out.println("Etternavn har blitt endret og ny person lagt inn");
		    else
			System.out.println("Du skrev ikke inn rett landskode og tlf");
		    continue;
		}
	    }    
	    if (kommandoer[0].equals("mostCommonName")){
		if (antallKommandoer > 1){
		    System.out.println("You have given too many arguments. ");
		    continue;
		}
		if (antallKommandoer == 1){
		    // Remakes the frequency tree in case some personal info has changed.
		    FQT = new FrequencyTree();
		    String[] unikeNavn2 = BST.toArray();
		    for (int i=0;i<unikeNavn2.length;i++)
			FQT.add(unikeNavn2[i]);
		    FQT.toNodeArray();
		    BST.toNodeArray(); // legger alle nodene i en array.
		    String[] vanligsteNavn = FQT.mostCommonNames();
		    //int noder=FQT.nodeArray.length;
		    // Prints the last name and its frequency.
		    //FQT.printInfo();
		    System.out.println("The most common last name(s) is(are) :"+"\n");
		    for (int a=0;a<vanligsteNavn.length;a++){
			System.out.println(vanligsteNavn[a]);
			for (int b=0;b<BST.nodeArray.length;b++){
			    //String etternavn = BST.nodeArray[b].lastName;
			    if (BST.nodeArray[b].lastName.equals(vanligsteNavn[a])){
				//System.out.println(BST.nodeArray.length);
				//System.out.println(b);
				System.out.println("   "+BST.nodeArray[b].firstName);
			    }
			}
		    }
		    continue;
		}
	    }
	    // Exits the program and closes the shell.
	    if (kommandoer[0].equals("exit")){
		if (antallKommandoer > 1){
		    System.out.print("You have given illegal arguments. Use help for information");
		    System.out.println(" or just exit to quit the program");
		}
		else
		    command = 0;
	    }
	    else{
		System.out.print("You have given illegal commands or arguments. Use help for information");
		System.out.println(" or just exit to quit the program");
	    }		
	}
    }
}

class FrequencyTree{
    Node roten;
    int størrelse;
    Node[] nodeArray;
    int nodeCounter;
    //String[] noder;
    //ContactTree kontaktTre;
    // skal jeg starte med roten på annet tre eller bare bruke en annen verdi for node rot.
    class Node{
	Node denne;
	Node leftChild;
	Node rightChild;
	String lastName;
	String firstName;
	String phoneN;
	String countryCode;
	int frekvens;
	ContactTree phoneNumberTree;

	Node(String etternavn,String fornavn,String cc,String pn,String test){
	    denne=this;
	    lastName=etternavn;
	    firstName=fornavn;
	    countryCode=cc;
	    phoneN=pn;
	    phoneNumberTree=new ContactTree();
	    frekvens=1;
	}
	Node(String etternavn){
	    denne=this;
	    lastName=etternavn;
	    frekvens=1;
	}
	// Node for komplett navn
	Node(String etternavn,String fornavn){
	    denne=this;
	    lastName=etternavn;
	    firstName=fornavn;
	    frekvens=1;
	}
   	void skrivUt(){
	    if (leftChild != null){
		leftChild.skrivUt();
	    }
	    denne.printInfo();
	    if (rightChild != null){
		rightChild.skrivUt();
	    }
	}
	void printInfo(){
	    System.out.println("Antall personer med etternavnet "+lastName+" er :"+frekvens);
	}
	// Preorder print of codes.
	void printCountryCodes(){
	    denne.printCountryCode();
	    denne.phoneNumberTree.printPhone();
	    if (leftChild != null){
		leftChild.printCountryCodes();
	    }

	    if (rightChild != null){
		rightChild.printCountryCodes();
	    }
	}
	void printCountryCode(){
	    System.out.println("\n"+"Country code: "+countryCode);
	}
	void updateFrequency(){
	    frekvens++;
	}
	int getFreq(){
	    return frekvens;
	}
	// Legger til komplett navn noder.
	boolean add2(Node n){
	    int sammenligne = denne.lastName.compareTo(n.lastName);
	    int sammenligne2 = denne.firstName.compareTo(n.firstName);
	    if (sammenligne == 0){
		if (sammenligne2 == 0)
		    denne.updateFrequency();
		else{
		    if (sammenligne2<0){
			if(rightChild == null){
			    rightChild = n;
			    størrelse++;
			    return true;
			} else {
			    boolean truefalse=rightChild.add2(n);
			    return truefalse;
			}
		    } else if(sammenligne2>0){
			if(leftChild==null){
			    leftChild=n;
			    størrelse++;
			    return true;
			} else {
			    boolean truefalse = leftChild.add2(n);
			    return truefalse;
			}
		    }
		}
	    }
	    if(sammenligne < 0) {
		if(rightChild == null) {
		    rightChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse=rightChild.add2(n); 
		    return truefalse;
		}
	    } else if(sammenligne > 0) {
		if(leftChild == null) {
		    leftChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse = leftChild.add2(n);
		    return truefalse;
		}
	    }
	    return false;
	}
	boolean add(Node n){
	    int sammenligne = denne.lastName.compareTo(n.lastName);
	    if(sammenligne == 0) 
		denne.updateFrequency();
	    if(sammenligne < 0) {
		if(rightChild == null) {
		    rightChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse=rightChild.add(n); 
		    return truefalse;
		}
	    } else if(sammenligne > 0) {
		if(leftChild == null) {
		    leftChild = n;
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse = leftChild.add(n);
		    return truefalse;
		}
	    }
	    return false;
	}
	// adds a countrycode node and a node to the phone N tree.
	boolean add3(Node n){
	    int sammenligne = denne.countryCode.compareTo(n.countryCode);
	    int kode1=Integer.parseInt(denne.countryCode);
	    int kode2=Integer.parseInt(n.countryCode);

	    // samm==0
	    if(kode1 == kode2){ 
		denne.updateFrequency();
		denne.phoneNumberTree.add2(n.lastName,n.firstName,n.countryCode,n.phoneN);
	    }
	    // samm < 0
	    if(kode1 < kode2) {
		if(rightChild == null) {
		    rightChild = n;
		    n.phoneNumberTree.add2(n.lastName,n.firstName,n.countryCode,n.phoneN);
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse=rightChild.add3(n); 
		    return truefalse;
		}
		// samm > 0
	    } else if(kode1 > kode2) {
		if(leftChild == null) {
		    leftChild = n;
		     n.phoneNumberTree.add2(n.lastName,n.firstName,n.countryCode,n.phoneN);
		    størrelse++;
		    return true;
		} else {
		    boolean truefalse = leftChild.add3(n);
		    return truefalse;
		}
	    }
	    return false;
	}
	void toNodeArray(){
	    if (leftChild != null){
		leftChild.toNodeArray();
	    }
	    nodeArray[nodeCounter]=denne;
	    nodeCounter++;
	    if (rightChild != null){
		rightChild.toNodeArray();
	    }
	}    
    }
    // legger til et element i treet. Leter gjennom treet til den finner 
    // rett plassering.
    // Sender true/false oppover i treet til roten om den lykkes/feiler.
    boolean add(String last){
	Node insert = new Node(last);
	//(e == null) {
	//  throw new NullPointerException();
	//
	if(roten == null) {
	    roten = insert;
	    størrelse++;
	    return true;
	} else {
	    boolean truefalse = roten.add(insert);
	    return truefalse;
	}
    }
    // Adds nodes with lastname and firstname to tree.
    boolean add2(String last,String first){
	Node insert = new Node(last,first);
	if (roten == null){
	    roten=insert;
	    størrelse++;
	    return true;
	} else{
	    boolean truefalse = roten.add2(insert);
	    return truefalse;
	}
    } // add nodes with country codes to tree.
    boolean add3(String last,String first,String countryC,String phoneN){
	String test="t"; // brukes bare for å lage ny node konstruktør.
	Node insert = new Node(last,first,countryC,phoneN,test);
	if (roten==null){
	    roten=insert;
	    insert.phoneNumberTree.add2(last,first,countryC,phoneN);
	    størrelse++;
	    return true;
	} else{
	    boolean truefalse = roten.add3(insert);
	    return truefalse;
	}
    }
    int size(){
	return størrelse;
    }
    void printInfo(){
	roten.skrivUt();
    }
    void printPhones(){
	if (roten==null)
	    roten=null;
	else
	    roten.printCountryCodes();
    }
    void toNodeArray(){
	//if (roten == null)
	//    System.out.println("Rot er null");
	nodeArray = new Node[size()];
	roten.toNodeArray();
	nodeCounter=0;
	//System.out.println("Størrelse: "+nodeArray.length);
	//return nodeArray;
    }
    String[] mostCommonNames(){
	// Vet vi allerede hva største verdi er fordi siste element i frekvenstreet er
	// sortert på frekvenser?? (nodeArray[nodeArray.length-1].frekvens)
	int maxValue = nodeArray[0].frekvens;  
	for(int i=1;i < nodeArray.length;i++){  
	    if(nodeArray[i].frekvens > maxValue){  
		maxValue = nodeArray[i].frekvens;  
	    }  
	}
	int hvorofte=0;
	for (int j=0;j<nodeArray.length;j++){
	    if (nodeArray[j].frekvens == maxValue)
		hvorofte++;
	}
	String[] vanligsteEtternavn = new String[hvorofte];
	
	// jobber med denne
	// send string array tilbake til funksjonen.
	int n =0;
	for (int k=0;k<nodeArray.length;k++){
	    if (nodeArray[k].frekvens == maxValue){
		String navn = nodeArray[k].lastName;
		//System.out.println(navn);
		int l = 0;
		for (int m=0;m<vanligsteEtternavn.length;m++){
		    //if (vanligsteEtternavn[m]==null)
		    //System.out.println("Dette er bare tull null");
		    if (vanligsteEtternavn[m]!=null){
			if (vanligsteEtternavn[m].equals(navn))
			    l=1;
		    }
		}
		if (l ==0){
		    vanligsteEtternavn[n]=navn;
		    n++;
		}
	    }
	}    
	System.out.println("Antall vanligste etternavn er : "+vanligsteEtternavn.length);
	return vanligsteEtternavn;
    }
}