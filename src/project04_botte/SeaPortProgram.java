/*
File Name: SeaPortProgram.java
Date: 10-10-2017
Author: Barrett Otte
Purpose: This class will generate the GUI and instantiate a World object.
        It also handles, all button actions and generating a JTree based off
        of the given text file from the user.
*/
package project04_botte;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.* ;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.*;
import javax.swing.tree.DefaultMutableTreeNode;


public class SeaPortProgram extends JFrame {
    
    private World world;
    private Scanner fileScanner;
    private JPanel treePanel;
    private JTextArea textArea;
    private JComboBox <String> searchComboBox;
    private JComboBox <String> sortTypeComboBox;
    private JComboBox <String> sortTargetComboBox;
    private JTextField searchField;
    private JTree tree;
    private Dimension screenSize;
    private HashMap<Integer, Thing> masterMap;
    
    private JobTableModel jobModel;
    private JTable jobTable;
    private JPanel tableMainPanel;
    private JPanel jobButtonPanel;
    public boolean isRunning;
    public boolean isReady;
    
    private JTextArea poolTextArea;
    private HashMap<String, ArrayList<Person>> resourcePool;
    
    
    public static void main(String[] args){
        SeaPortProgram program = new SeaPortProgram();
        program.drawGUI();
        
        program.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                program.isRunning = false;
            }
        }); 
        while(program.isRunning){
            program.update();
        }
    }
    
    
    
    /*This method is responsible for allowing ships to dock and work on their jobs. When
        the ship is done all of their jobs, they leave the dock and the table is updated.
        TODO: Make sure this method is only called once a second, it seems like calling it
            in a while loop with no increment would be very resource expensive.
    */
    public void update() {   
        if(isReady){
            for (SeaPort msp : world.getPorts()) {
                 
                for (Dock md : msp.getDocks()) {
                    boolean shipLeave = true;
                    if(md.getShip() == null){
                        continue;
                    }
                    for (Job mj : md.getShip().getJobs()) {
                        if (md.getShip().getJobs().isEmpty()) {
                            shipLeave = true;
                        }
                        if (!mj.isFinished()) {
                            shipLeave = false;
                        }
                        else if(mj.isFinished()){
                            putWorkersBack(mj.getWorkers(), msp.getName());
                            mj.setWorkers(new ArrayList<>());
                        }
                    }
                    if (shipLeave) {
                        textArea.append(String.format("\n  [SHIP LEAVING]\t %s leaving %s in %s\n", md.getShip().getName(), md.getName(), msp.getName()));
                        for (Job mj : md.getShip().getJobs()) {
                            mj.endJob();
                            jobModel.remove(mj.getName());
                            jobTable.validate();
                        }
                        md.setShip(null);
                        if(msp.getQueue().isEmpty()){
                            return; 
                        }
                        md.setShip(msp.getQueue().remove(0));
                        
                        textArea.append(String.format("\n  [ SHIP ENTERING]\t %s entering %s in %s\n", md.getShip().getName(), md.getName(), msp.getName()));
                        for (Job mj : md.getShip().getJobs()) {
                            mj.showGUI(jobButtonPanel);
                            md.getShip().setParent(md.getIndex());
                            jobModel.add(md.getShip(), masterMap, mj);
                            jobTable.validate();
                            jobResourceAllocation(mj, md.getShip());
                        }
                    }
                    jobButtonPanel.setLayout(new GridLayout(jobModel.getRowCount(), 0, 2, 2));
                    jobButtonPanel.setPreferredSize(new Dimension(400, jobModel.getRowCount() * 25));
                    updateResourcePoolUI();
                }
            } 
        }  
    }
    
    
    /*Called when Read button is pressed. Uses JFileChooser to select a text file.
        Returns the Scanner to be used by World object.
    */
    private Scanner readFile() {
        textArea.append("Read File Button pressed.\n");
        try {
            int option = -1;
            JFileChooser chooser = new JFileChooser(".");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
            chooser.setDialogTitle("SeaPort Simulation");
            
            while (option != JFileChooser.APPROVE_OPTION && option != JFileChooser.CANCEL_OPTION) {
                option = chooser.showOpenDialog(null);
            }
            if (option == JFileChooser.CANCEL_OPTION) {
                return null;
            }
            fileScanner = new Scanner(chooser.getSelectedFile());
            textArea.append("File[" + chooser.getSelectedFile().getName() + "] successfully loaded. Ready to display.\n");
        } 
        catch (IOException e){
            textArea.append("Problem occurred while reading selected file.\n");
        }
        return null;
    }
    
    
     /*Creates world object to be used in GUI*/
    private void loadWorld(Scanner sc){
        world = new World(sc); 
        HashMap<Integer, SeaPort> portMap = new HashMap<>();
        HashMap<Integer, Dock> dockMap = new HashMap<>();
        HashMap<Integer, Ship> shipMap = new HashMap<>();
        masterMap = new HashMap<>();
        resourcePool = new HashMap<>();
        
        while(sc.hasNextLine()){
            String line = sc.nextLine().trim();
            if(line.length() == 0) continue;
            Scanner scan = new Scanner(line);
            if(!scan.hasNext()) return;
            switch(scan.next()){
                case "port":
                    SeaPort msp = new SeaPort(scan);
                    portMap.put(msp.getIndex(), msp);
                    masterMap.put(msp.getIndex(), msp);
                    world.assignPort(msp);
                    break;
                case "dock":
                    Dock md = new Dock(scan);
                    dockMap.put(md.getIndex(), md);
                    masterMap.put(md.getIndex(), md);
                    world.assignDock(md, portMap.get(md.getParent()));
                    break;
                case "pship":
                    PassengerShip pShip = new PassengerShip(scan);
                    shipMap.put(pShip.getIndex(), pShip);
                    masterMap.put(pShip.getIndex(), pShip);
                    SeaPort pPort = portMap.get(pShip.getParent());
                    Dock pDock = dockMap.get(pShip.getParent());
                    pPort = (pPort == null) ? portMap.get(pDock.getParent()) : pPort;
                    world.assignShip(pShip, pPort, pDock);
                    break;
                case "cship":
                    CargoShip cShip = new CargoShip(scan);
                    shipMap.put(cShip.getIndex(), cShip);
                    masterMap.put(cShip.getIndex(), cShip);
                    SeaPort cPort = portMap.get(cShip.getParent());
                    Dock cDock = dockMap.get(cShip.getParent());
                    pPort = (cPort == null) ? portMap.get(cDock.getParent()) : cPort;
                    world.assignShip(cShip, pPort, cDock);
                    break;
                case "person":
                    Person mp = new Person(scan);
                    masterMap.put(mp.getIndex(), mp);
                    world.assignPerson(mp, portMap.get(mp.getParent()));
                    break;
                case "job":
                    Job mj = new Job(scan);
                    masterMap.put(mj.getIndex(), mj);
                    world.assignJob(mj, masterMap.get(mj.getParent()));
                    break;
                default:
                    break;
            }
        }  
        
        //Create Resource pool entries.
        for(SeaPort msp : portMap.values()){
            for(Person mp : msp.getPersons()){
                String key = (msp.getName() + "_" + mp.getSkill());
                if(resourcePool.get(key) == null){
                    resourcePool.put(key, new ArrayList<>());
                    resourcePool.get(key).add(mp);
                }
                else{
                    resourcePool.get(key).add(mp);
                }
            }
        }
        for(Ship ms : shipMap.values()){
            if(!ms.getJobs().isEmpty() && masterMap.get(ms.getParent()) instanceof Dock){
                textArea.append("\n  [ENTERING] " + ms.getName() + " entering dock: " + dockMap.get(ms.getParent()).getName());
                for(Job mj : ms.getJobs()){
                    jobModel.add(ms, masterMap, mj);
                    mj.showGUI(jobButtonPanel);
                    jobResourceAllocation(mj, ms);
                }
            }
        }
        jobButtonPanel.setLayout(new GridLayout(jobModel.getRowCount(),2, 2, 2));
        jobButtonPanel.setBorder(new EmptyBorder(0,2,0,2));
        jobButtonPanel.setPreferredSize(new Dimension(400, jobModel.getRowCount() * 25));
        updateResourcePoolUI();
        isReady = true;
    }
    
    
    /*This allocates resources to jobs and starts/cancels them accordingly.
      If a job cannot find all resources, the resources are released back to the pool.
    */
    public void jobResourceAllocation(Job mj, Ship ms){
        boolean shouldAllocateResources = true;
        for (String req : mj.getRequirements()) {
            SeaPort port = (SeaPort) masterMap.get(masterMap.get(ms.getParent()).getParent());
            mj.setPort(port.getName());
            ArrayList<Person> workerList = resourcePool.get(port.getName() + "_" + req);

            if (workerList != null && !workerList.isEmpty()) {
                Person worker = workerList.remove(0);
                mj.addWorker(worker);
                textArea.append("\n  [ADD WORKER]  " + worker.toString() + " to job " + mj.getName() + " \n");
            } 
            else if (workerList == null) {
                textArea.append("\n  [CANCEL JOB]  Cannot find " + req + " at port " + port.getName() + "\n");
                mj.couldNotFindResources();
                shouldAllocateResources = false;
                break;
            }
        }
        if (shouldAllocateResources && mj.jobCanBeStarted()) {
            textArea.append("\n  [START JOB]  Found all requirements for " + mj.getName() + "\n");
            mj.startJob();
        }
    }
    
    
    /*Releases workers back to pool*/
    public void putWorkersBack(ArrayList<Person> allocatedWorkers, String port){
        for (Person w : allocatedWorkers) {
            resourcePool.get((port + "_" + w.getSkill())).add(w);
            textArea.append("\n  [RELEASE WORKER]  " + w.getSkill() + " back in pool for Port: " + port + "\n");
        }
        updateResourcePoolUI();
    }
    
    
    /*Draws jobs of each ship to a table to provide a visual representation of
        work being done.
    */
    private void drawTable(){
        String[] header = {"Ship Name", "Ship Location", "Job Name"};
        jobModel = new JobTableModel(header);
        jobTable = new JTable(jobModel);
        jobTable.setRowHeight(35);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        jobButtonPanel = new JPanel();
        tablePanel.add(jobTable, BorderLayout.CENTER);
        tablePanel.add(jobButtonPanel, BorderLayout.EAST);
        tablePanel.add(jobTable.getTableHeader(), BorderLayout.NORTH);
        
        JScrollPane tableScroll = new JScrollPane(tablePanel);
        tableMainPanel.add(tableScroll);
        validate();
    }
    
    
    /*Updates text area showing the resource pool*/
    private void updateResourcePoolUI(){
        String poolOutput = "[RESOURCE POOL]\n\n";
        
        for(String key : resourcePool.keySet()){
            poolOutput += ("\n    " + key + "\n");
            for(Person worker : resourcePool.get(key)){
                poolOutput += ("        " + worker.getName() + "\n");
            }
        }
        poolTextArea.setText(poolOutput);
    }
    
    
    /*Called when Display button is pressed. Displays the World object and 
        calls drawTree() to create a JTree of the text file given. If the file
        has not yet been loaded it calls the readFile() method. 
    */
    private void displayWorld(){
        if(fileScanner == null){
            textArea.append("File not loaded. Loading file before display.\n");
            readFile();
        }
        textArea.append("\nDisplay World Button pressed.\n");
        drawTable();
        loadWorld(fileScanner);
        drawTree();
        isReady = true;
    }
    
    
    /*Called when Search button is pressed. Uses the searchComboBox to decide on
        what search will be performed in World object. Outputs the results of
        the search to the textArea.
    */
    public void search (String type, String target) {
        textArea.append("\nSearch button pressed.\n");
        if(fileScanner == null){
            displayWorld();
        }
        if(target.equals("")){
            textArea.append("\nPlease specify target.");
            return;
        }
        textArea.append ("\nType[" + type + "] looking for Target[" + target + "]\n\n");
        
        ArrayList<Thing> results = new ArrayList<>();
        switch (type) {
            case "Index":
                try{
                    int i = Integer.parseInt(target);
                    results.add(masterMap.get(i));
                }
                catch(NumberFormatException e){
                    textArea.append("Invalid index. Unable to search.");
                }
                break;
            case "Type":
                results = world.searchByType(target);
                break;
            case "Name":
                results = world.searchByName(target);
                break;
            case "Skill":
                results = world.searchBySkill(target);
                break;
        }
        if(results == null){
            textArea.append("\nNo results found.");
            return;
        }
        for(Thing t : results){
            if(t != null){
                textArea.append(t + "\n");
            }
        }
    }
    
    
    /*Handles drawing all GUI components, except the tree. Primarily uses 
        a BorderLayout to display things decently clean.
    */
    private void drawGUI(){
        isRunning = true;
        Toolkit t = Toolkit.getDefaultToolkit();
        screenSize = t.getScreenSize();        
        setTitle ("SeaPort Simulation");
        setSize ((screenSize.width / 2) + 350, 900);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        setVisible (true);
        setLayout(new BorderLayout());
        
        textArea = new JTextArea();
        textArea.setEditable(false);
        poolTextArea = new JTextArea();
        poolTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane (textArea);
        scrollPane.setPreferredSize(new Dimension(300, 400));
        JScrollPane poolScrollPane = new JScrollPane(poolTextArea);
        poolScrollPane.setPreferredSize(new Dimension(300, 300));
        JButton readButton = new JButton ("Read");
        JButton displayButton = new JButton ("Display");
        JButton searchButton = new JButton ("Search");
        JButton sortButton = new JButton ("Perform Sort");
        JLabel searchLabel = new JLabel ("Search Target");
        JLabel sortTargetLabel = new JLabel("Sort Target:");
        JLabel sortTypeLabel = new JLabel("Sort Type:");
        
        searchField = new JTextField (10);
        searchComboBox = new JComboBox<>();
        searchComboBox.addItem ("Index");
        searchComboBox.addItem ("Type");
        searchComboBox.addItem ("Name");
        searchComboBox.addItem ("Skill");
        
        sortTargetComboBox = new JComboBox<>();
        sortTargetComboBox.addItem("Ship Queue");
        sortTargetComboBox.addItem("SeaPorts");
        sortTargetComboBox.addItem("Docks");
        sortTargetComboBox.addItem("Ships");
        sortTargetComboBox.addItem("Persons");
        sortTargetComboBox.addItem("Jobs");
        sortTargetComboBox.addItem("All");
        
        sortTypeComboBox = new JComboBox<>();
        updateSortTypeComboBox();
        
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setBorder(new EmptyBorder(10,25,0,25));
        JPanel actionPanel = new JPanel ();
        actionPanel.add (readButton);
        actionPanel.add (displayButton);
        actionPanel.add (searchLabel);
        actionPanel.add (searchField);
        actionPanel.add (searchComboBox);
        actionPanel.add (searchButton);
        actionPanel.add(sortTargetLabel);
        actionPanel.add(sortTargetComboBox);
        actionPanel.add(sortTypeLabel);
        actionPanel.add(sortTypeComboBox);
        actionPanel.add(sortButton);
        northPanel.add(actionPanel, BorderLayout.WEST);
        
        JPanel textAreaPanel = new JPanel(new BorderLayout());
        textAreaPanel.setPreferredSize(new Dimension(300, screenSize.height / 2));
        textAreaPanel.setBorder(new EmptyBorder(10,0,25,25));
        textAreaPanel.add(scrollPane, BorderLayout.NORTH);
        textAreaPanel.add(poolScrollPane, BorderLayout.SOUTH);
        
        treePanel = new JPanel(new BorderLayout());
        treePanel.setPreferredSize(new Dimension(375, screenSize.height / 2));
        treePanel.setBorder(new EmptyBorder(10,0,25,25));
        
        tableMainPanel = new JPanel(new BorderLayout());
        tableMainPanel.setPreferredSize(new Dimension(800, screenSize.height / 2));
        tableMainPanel.setBorder(new EmptyBorder(10, 0, 25, 25));
        
        add (northPanel, BorderLayout.NORTH);
        add (textAreaPanel, BorderLayout.CENTER);
        add (treePanel, BorderLayout.WEST);
        add (tableMainPanel, BorderLayout.EAST);
        validate ();  
        
        readButton.addActionListener (e -> readFile());
        displayButton.addActionListener (e -> displayWorld ());
        searchButton.addActionListener (e -> search ((String)(searchComboBox.getSelectedItem()), searchField.getText()));
        sortButton.addActionListener(e -> performSort());
        sortTargetComboBox.addActionListener(e -> updateSortTypeComboBox());
    }
    
    
    /*Update the sortTypeComboBox items based on the selected sortTarget*/
    private void updateSortTypeComboBox(){
        sortTypeComboBox.removeAllItems();
        if(sortTargetComboBox.getSelectedItem().equals("Ship Queue")){
            sortTypeComboBox.addItem("Weight");
            sortTypeComboBox.addItem("Width");
            sortTypeComboBox.addItem("Length");
            sortTypeComboBox.addItem("Draft");
        }
        sortTypeComboBox.addItem("Name"); 
        validate();
    }
    
    
    /*Helper method to determine what type of sort will occur*/
    private void performSort(){
        textArea.append("\nSort button pressed.\n");
        if(fileScanner == null){
            displayWorld();
        } 
        String sortType = sortTypeComboBox.getSelectedItem().toString();
        String sortTarget = sortTargetComboBox.getSelectedItem().toString();
        if(sortType.equals("Name")){
            sortThingList(sortTarget,sortType);
        }
        else{
            sortQueue(sortType);
        }
    }
    
    
    /*Sort ship queue by specified type using ShipComparator*/
    private void sortQueue(String sortBy){       
        String result = "";
        
        for(SeaPort msp : world.getPorts()){
            Collections.sort(msp.getQueue(), new ShipComparator(sortBy));
            for(Ship ms : msp.getQueue()){
                result += String.format("%s\n", ms.toString());
            }
        }
        textArea.append(String.format("\n\nSorted Ship Queue by %s:\n\n%s", sortBy, result));
    }
    
    
    /*Sorts the specified target list by the sort criteria using ThingComparator*/
    private void sortThingList(String target, String sortBy) {
        ArrayList<?> thingList = new ArrayList<>();
        
        if(target.equals("All")){
            sortThingList("SeaPorts", sortBy);
            sortThingList("Docks", sortBy);
            sortThingList("Ships", sortBy);
            sortThingList("Jobs", sortBy);
            sortThingList("Persons", sortBy);
            return;
        }
        else if(target.equals("SeaPorts")){
            ArrayList<SeaPort> ports = world.getPorts();
            Collections.sort(ports, new ThingComparator(sortBy));
            world.setPorts(ports);
            thingList = world.getPorts();
        }
        else{
            for (SeaPort msp : world.getPorts()) {
                switch (target) {
                    case "Docks":
                        Collections.sort(msp.getDocks(), new ThingComparator(sortBy));
                        thingList = msp.getDocks();
                        break;
                    case "Ships":
                        Collections.sort(msp.getShips(), new ThingComparator(sortBy));
                        thingList = msp.getShips();
                        break;
                    case "Jobs":
                        for (Ship ms : msp.getShips()) {
                            Collections.sort(ms.getJobs(), new ThingComparator(sortBy));
                            thingList = ms.getJobs();
                        }
                        break;
                    case "Persons":
                        Collections.sort(msp.getPersons(), new ThingComparator(sortBy));
                        thingList = msp.getPersons();
                        break;
                    default:
                        break;
                }
            }
        }
        String result = "";
        for (Object t : thingList) {
            result += String.format("%s\n", t.toString());
        }
        textArea.append(String.format("\n\nSorted %s by %s:\n\n%s", target, sortBy, result));
    }

    
    /*Creates a JTree to display the hierarchical relationship of classes
        created by the text file. Adds two buttons to collapse/expand all nodes
        in the JTree.
    */
    private void drawTree(){
        tree = new JTree(createNodes("World"));
        JScrollPane pane2 = new JScrollPane(tree);
        JButton collapseButton = new JButton ("Collapse All");
        JButton expandButton = new JButton("Expand All");
        JPanel buttonPanel = new JPanel();  
        buttonPanel.setBorder(new EmptyBorder(10,0,0,0));
        buttonPanel.add(collapseButton);
        buttonPanel.add(expandButton);
        collapseButton.addActionListener (e -> collapseAll());
        expandButton.addActionListener(e -> expandAll());
        treePanel.add(buttonPanel, BorderLayout.SOUTH);
        treePanel.add(pane2, BorderLayout.CENTER);
        validate ();
    }
    
    
    /*Populates the JTree with its needed nodes based off the text file given.*/
    private DefaultMutableTreeNode createNodes(String title) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(title);
        DefaultMutableTreeNode parentNode;
        for (SeaPort msp : world.getPorts()) {
            parentNode = new DefaultMutableTreeNode("SeaPort " + msp.getName());
            top.add(parentNode);
            parentNode.add(addNewBranch(msp.getDocks(), "Docks")); 
            parentNode.add(addNewBranch(msp.getShips(), "Ships")); 
            parentNode.add(addNewBranch(msp.getQueue(), "Queue")); 
            parentNode.add(addNewBranch(msp.getPersons(), "Persons")); 
        } 
        return top;
    }
    
    
    /*Generically creates a branch of the tree. Returns the top node.*/
    private <T extends Thing> DefaultMutableTreeNode addNewBranch(ArrayList<T> thingList, String name){
        DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(name);
        thingList.forEach((t) -> {
            String txt = t.getName();
            if(t instanceof Dock){
                if(((Dock)t).getShip() != null){
                    txt = String.format("   Ship: %s", ((Dock)t).getShip().getName());
                }
                else{
                    txt = "Empty";
                }
            }
            parentNode.add(new DefaultMutableTreeNode(txt));
        });
        return parentNode;
    }
    
    
    /*Collapses all nodes in JTree*/
    private void collapseAll(){
        for(int i = 0; i < tree.getRowCount()-1; i++){
            tree.collapseRow(i);
        }
    }
    /*Expands all nodes in JTree*/
    private void expandAll(){
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        } 
    } 
}