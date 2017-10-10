/*
File Name: PassengerShip.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class holds all values for transporting people. A cargoship cannot
         accept more people if there are no available rooms.
*/
package project04_botte;

import java.util.Scanner;


public class PassengerShip extends Ship {
    private int numberOfOccupiedRooms;
    private int numberOfPassengers;
    private int numberOfRooms;
    
    
    //Constructors:
    public PassengerShip(Scanner sc){
        super(sc);
        numberOfPassengers = (sc.hasNextInt()) ? sc.nextInt() : 0;
        numberOfRooms = (sc.hasNextInt()) ? sc.nextInt() : 0;
        numberOfOccupiedRooms = (sc.hasNextInt()) ? sc.nextInt() : 0;
    }
    
    
   //Accessors/Mutators:
    public int getOccupiedRooms(){
        return numberOfOccupiedRooms;
    }
    public void setOccupiedRooms(int o){
        if(o >= 0){
            numberOfOccupiedRooms = o;
        }
    }
    
    public int getPassengers(){
        return numberOfPassengers;
    }
    public void setPassengers(int p){
        if(p >= 0){
            numberOfPassengers = p;
        }
    }
    
    public int getRooms(){
        return numberOfRooms;
    }
    public void setRooms(int r){
        if(r >= 0){
            numberOfRooms = r;
        }
    }
    
    
    public String toString(){
        return String.format("Passenger Ship: %s\n     Occupied Rooms: %d\n     Passengers: %d\n     Rooms: %d\n",
                super.toString(), numberOfOccupiedRooms, numberOfPassengers, numberOfRooms);
    }    
}