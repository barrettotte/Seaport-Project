/*
File Name: SeaPort.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class holds lists for all docks, ships, and persons located at the
         seaport. The Queue will eventually be used to add ships to docks in an
         orderly fashion.
*/
package project04_botte;

import java.util.ArrayList;
import java.util.Scanner;


public class SeaPort extends Thing{
    private ArrayList<Dock> docks;
    private ArrayList<Ship> queue;          //the list of ships waiting to dock
    private ArrayList<Ship> ships;          //a list of all the ships at this port
    private ArrayList<Person> persons;      //people with skills at this port
    
    //Constructor:
    public SeaPort(Scanner sc) {
        super(sc);
        docks = new ArrayList<>();
        queue = new ArrayList<>();
        ships = new ArrayList<>();
        persons = new ArrayList<>();
    }
    

    //Accessors/Mutators:
    public ArrayList<Dock> getDocks(){
        return docks;
    }
    public void setDocks(ArrayList<Dock> d){
        docks = d;
    }
    
    public ArrayList<Ship> getQueue(){
        return queue;
    }
    public void setQueue(ArrayList<Ship> q){
        queue = q;
    }
    
    public ArrayList<Ship> getShips(){
        return ships;
    }
    public void setShips(ArrayList<Ship> s){
        ships = s;
    }
    
    public ArrayList<Person> getPersons(){
        return persons;
    }
    public void setPersons(ArrayList<Person> p){
        persons = p;
    }
    
    
    public String toString(){
        String out = String.format("\n\nSeaPort: %s", super.toString());
        
        out += "\n\n -------------------- List of all docks: --------------------";
        for(Dock md : docks) out += String.format("\n >%s", md.toString());
        
        out += "\n\n -------------------- List of all ships in queue: --------------------";
        for(Ship ms : queue) out += String.format("\n >%s", ms.toString());
        
        out += "\n\n -------------------- List of all ships: --------------------";
        for(Ship ms : ships) out += String.format("\n >%s", ms.toString());
        
        out += "\n\n -------------------- List of all persons: --------------------";
        for(Person mp : persons) out += String.format("\n >%s", mp.toString());
        return out + "\n";
    }  
}