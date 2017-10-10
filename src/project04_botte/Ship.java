/*
File Name: Ship.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class holds all attributes a ship would need to dock.
*/
package project04_botte;

import java.util.ArrayList;
import java.util.Scanner;


public class Ship extends Thing{
    private PortTime arrivalTime;
    private PortTime dockTime;
    private double draft;
    private double length;
    private double weight;
    private double width;
    private ArrayList<Job> jobs;
    private boolean finishedJobs;
    
    //Constructor:
    public Ship(Scanner sc) {
        super(sc);
        jobs = new ArrayList<>();
        draft = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        length = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        weight = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        width = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        finishedJobs = false;
    }
    
    
    //Accessors/Mutators:
    public PortTime getArrivalTime(){
        return arrivalTime;
    }
    public void setArrivalTime(PortTime a){
        arrivalTime = a;
    }
    
    public PortTime getDockTime(){
        return dockTime;
    }
    public void setDockTime(PortTime d){
        dockTime = d;
    }
    
    public double getDraft(){
        return draft;
    }
    public void setDraft(double d){
        if(d >= 0.0){
            draft = d;
        }
    }
    
    public double getLength(){
        return length;
    }
    public void setLength(double l){
        if(l >= 0.0){
            length = l;
        }
    }
    
    public double getWeight(){
        return weight;
    }
    public void setWeight(double w){
        if(w >= 0.0){
            weight = w;
        }
    }
    
    public double getWidth(){
        return width;
    }
    public void setWidth(double w){
        if(w >= 0.0){
            width = w;
        }
    }
    
    public ArrayList<Job> getJobs(){
        return jobs;
    }
    public void setJobs(ArrayList<Job> j){
        jobs = j;
    }
    
    public boolean isFinishedJobs(){
        return finishedJobs;
    }
    public void setFinishedJobs(boolean f){
        finishedJobs = f;
    }
    
    
    public String toString(){
        String out = String.format("%s\n     Draft: %.2f\n     Length: %.2f\n     Weight: %.2f\n     Width: %.2f\n     Jobs:",
            super.toString(), draft, length, weight, width);
        
        if(getJobs().isEmpty()){
            return String.format("%s None",out);
        }
        for(Job mj : getJobs()) out += String.format("\n%s", mj.toString());
        return out;
    }
}