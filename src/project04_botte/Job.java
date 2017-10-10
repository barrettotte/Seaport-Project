/*
File Name: Job.java
Date: 10-10-2017
Author: Barrett Otte
Purpose: Uses threading to simulate a job occuring. A job requires 
        workers with special skills to be performed. If the workers cannot
        be found at the port, the job is cancelled.
*/
package project04_botte;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.*;
import javax.swing.*;


public class Job extends Thing implements Runnable{

    private enum Status {RUNNING, SUSPENDED, WAITING, DONE}
    
    private double duration;
    private ArrayList<String> requirements;
    
    private boolean suspendFlag, cancelFlag;
    private JButton suspendButton;
    private JButton cancelButton;
    private Status status;
    private JProgressBar progressBar;
    private JPanel buttonPanel;
    private boolean isFinished;
    private Thread jobThread;
    
    private ArrayList<Person> workers;
    private String myPort;
    private boolean waitBeforeLeave;
    
    //Constructor
    public Job(Scanner sc) {
        super(sc);
        duration = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        requirements = new ArrayList<>();
        while(sc.hasNext()){
            requirements.add(sc.next());
        }
        suspendFlag = false;
        cancelFlag = false;
        suspendButton = new JButton("Suspend");
        cancelButton = new JButton("Cancel");
        status = Status.SUSPENDED;
        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        isFinished = false;
        jobThread = new Thread(this);
        waitBeforeLeave = false;
        workers = new ArrayList<>(requirements.size());
    }
    
    
    public void showGUI(JPanel panel){
        buttonPanel = panel;
        panel.add(progressBar);
        panel.add(suspendButton);
        panel.add(cancelButton);
        suspendButton.addActionListener((ActionEvent e) -> {    toggleSuspendFlag();   });
        cancelButton.addActionListener((ActionEvent e) ->  {    toggleCancelFlag();    });
    }
    
    
    /* Check to see if all worker slots are filled */
    public boolean jobCanBeStarted(){
        for (Person worker : workers) {
            if (worker == null) {
                showStatus(Status.WAITING);
                return false;
            }
        }
        return true;
    }
    
    
    public ArrayList<Person> getWorkers(){
        return workers;
    }
    public void setWorkers(ArrayList<Person> w){
        workers = w;
    }
    public void addWorker(Person p){
        workers.add(p);
    }
    
    public void setPort(String msp){
        myPort = msp;
    }
    public String getPort(){
        return myPort;
    }
    
    
    /*My attempt at making a thread safe function to start a job and 
            intialize the UI associated
    */
    public synchronized void startJob(){
        isFinished = false;
        waitBeforeLeave = false;
        jobThread.start();
    }
    
    
    /*Disables the UI for the job when it is completed.*/
    public void endJob(){
        progressBar.setVisible(false);
        buttonPanel.remove(progressBar);
        suspendButton.setVisible(false);
        buttonPanel.remove(suspendButton);
        cancelButton.setVisible(false);
        buttonPanel.remove(cancelButton);
        isFinished = true;
        waitBeforeLeave = false;
    }
    
    
    /*Used to show the UI of the job waiting before being cancelled*/
    public void couldNotFindResources(){
        waitBeforeLeave = true;
        duration = 2;
        jobThread.start();
    }
    
    
    
    /*The thread function used to simulate work being done.*/
    public void run() {
        long time = System.currentTimeMillis();
        long startTime = time;
        long stopTime = time + 1000 * (long)duration;
        double timeNeeded = stopTime - time;
        while (time < stopTime && !cancelFlag) {
            try {
                Thread.sleep(100);
            } 
            catch (InterruptedException e) {}
            if (waitBeforeLeave) {
                showStatus(Status.WAITING);
                time += 100;
                progressBar.setValue((int) (((time - startTime) / timeNeeded) * 100));
            } 
            else if (!suspendFlag) {
                showStatus(Status.RUNNING);
                time += 100;
                progressBar.setValue((int) (((time - startTime) / timeNeeded) * 100));
            } 
            else {
                showStatus(Status.SUSPENDED);
            }
        }
        progressBar.setValue(100);
        if(!waitBeforeLeave){
            showStatus(Status.DONE);
        }
        isFinished = true;   
    }
    
    public void toggleSuspendFlag(){
        suspendFlag = !suspendFlag;
    }
    
    public void toggleCancelFlag(){
        cancelFlag = true;
        isFinished = true;
    }
    
    /*Update UI to reflect the job's status*/
    private void showStatus(Status st){
        status = st;
        switch(status){
            case RUNNING:
                suspendButton.setBackground(Color.GREEN);
                suspendButton.setText("Running");
                break;
            case SUSPENDED:
                suspendButton.setBackground(Color.YELLOW);
                suspendButton.setText("Suspended");
                break;
            case WAITING:
                suspendButton.setBackground(Color.ORANGE);
                suspendButton.setText("Waiting");
                break;
            case DONE:
                suspendButton.setBackground(Color.RED);
                suspendButton.setText("Done");
                break;
        }
    }
    
    //Accessors/Mutators:
    public double getDuration(){
        return duration;
    }
    public void setDuration(double d){
        if(d >= 0.0){
            duration = d;
        }
    }
    
    public ArrayList<String> getRequirements(){
        return requirements;
    }
    public void setRequirements(ArrayList<String> r){
        requirements = r;
    }
    
    public boolean isFinished(){
        return isFinished;
    }
    
    public String toString(){
        String out = String.format("        Duration: %f\n        Requirements:", duration);
        for(String s : requirements){
            out += String.format("\n                %s", s);
        }
        return out;
    }
} 