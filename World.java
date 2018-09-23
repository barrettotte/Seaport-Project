/*
File Name: World.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class handles populating the World with Things. The logic behind
          searching for objects takes place here. 
*/

package project04_botte;

import java.util.ArrayList;
import java.util.Scanner;


public class World extends Thing {
    private ArrayList<SeaPort> ports;
    private PortTime time;

    //Constructor
    public World(Scanner sc) {
        super(sc);
        ports = new ArrayList<>();
    }

    //Accessors/Mutators:
    public ArrayList<SeaPort> getPorts() {
        return ports;
    }

    public void setPorts(ArrayList<SeaPort> p) {
        ports = p;
    }

    public PortTime getTime() {
        return time;
    }

    public void setTime(PortTime t) {
        time = t;
    }
 
    
    /*Sanitizes input and calls its helper method depending on the type given*/
    public ArrayList<Thing> searchByType(String rawTarget) {
        String target = rawTarget.toUpperCase().replace(" ", "");

        for (SeaPort msp : ports) {
            switch (target) {
                case "SEAPORT":
                    return traverseForType(ports, null);
                case "DOCK":
                    return traverseForType(msp.getDocks(), null);
                case "PERSON":
                    return traverseForType(msp.getPersons(), null);
                case "SHIP":
                    return traverseForType(msp.getShips(), null);
                case "CSHIP":
                case "CARGOSHIP":
                    return traverseForType(msp.getShips(), CargoShip.class);
                case "PSHIP":
                case "PASSENGERSHIP":
                    return traverseForType(msp.getShips(), PassengerShip.class);
            }
        }
        return null;
    }
    
    
    /*Generic method to eliminate the same loops over and over. When classCheck is null it will let 
      everything through as if this parameter wasnt there. Otherwise it will check if the generic 
      class is an instanceof the classCheck and add it to output string accordingly.
    */
    private <T> ArrayList<Thing> traverseForType(ArrayList<T> thingList, Class<?> classCheck) {
        ArrayList<Thing> results = new ArrayList<>();
        for (T thing : thingList) {
            if (classCheck == null || thing.getClass() == classCheck) {
                results.add((Thing)thing);
            }
        }
        return results;
    }
    
    
    /*Returns a list of all Things with the name given. Searches each SeaPort's
        lists of Docks, Persons, and Ships to find the name*/
    public ArrayList<Thing> searchByName(String target){
        ArrayList<Thing> results = new ArrayList<>();
        
        for(SeaPort msp : ports){
            if(msp.getName().equalsIgnoreCase(target)){
                results.add(msp);
            }
            results.addAll(traverseForName(msp.getDocks(), target));
            results.addAll(traverseForName(msp.getShips(), target));
            results.addAll(traverseForName(msp.getPersons(), target));
        }
        return results;
    }
    
    
    /*Searches a list of generic Things to see if it can find a Thing with the
        given name. Returns the list.*/
    private <T extends Thing> ArrayList<Thing> traverseForName(ArrayList<T> thingList, String name) {
        ArrayList<Thing> results = new ArrayList<>();
        for (T thing : thingList) {
            if(thing.getName().equalsIgnoreCase(name)){
                results.add((Thing)thing);
            }
        }
        return results;
    }
    
    
    /*Searches each SeaPorts list of Persons to find the given skill.
        Returns a list of all Persons with the skill*/
    public ArrayList<Thing> searchBySkill(String target){
        ArrayList<Thing> results = new ArrayList<>();
        for(SeaPort msp : ports){
            for(Person mp : msp.getPersons()){
                if(mp.getSkill().equalsIgnoreCase(target)){
                    results.add(mp);
                }
            }
        }
        return results;
    }
    
    
    /*If the given ship has a parent index that matches a SeaPort, add it to
        the list of ships and the Queue. Otherwise, add it to the Dock
        corresponding with the ship's parent index*/
    public void assignShip(Ship ms, SeaPort msp, Dock md) {
        msp.getShips().add(ms);
        if(md != null){
            md.setShip(ms);
        }
        else{
             msp.getQueue().add(ms);
        }
    }
    
    public void assignPort(SeaPort msp){
        ports.add(msp);
    }
    public void assignPerson(Person mp, SeaPort msp){
        msp.getPersons().add(mp);
    }
    public void assignDock(Dock md, SeaPort msp){
        msp.getDocks().add(md);
    }
    public void assignJob(Job mj, Thing parent){
        if(parent instanceof Ship){
            ((Ship)parent).getJobs().add(mj);
        }
        else{
            ((Dock)parent).getShip().getJobs().add(mj);
            mj.setParent(((Dock)parent).getShip().getIndex());
        }
    }
    
    public String toString(){
        String out = "\nWorld:\n\n";
        for (SeaPort msp : ports) {
            out += msp;
        }
        return out;
    }
}