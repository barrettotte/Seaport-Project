/*
File Name: Dock.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class holds a specified ship at its dock to be referenced in World.
*/
package project04_botte;

import java.util.Scanner;


public class Dock extends Thing{
    private Ship ship;
    
    
    //Constructor
    public Dock(Scanner sc) {
        super(sc);
    }
    
    
    //Accessors/Mutators:
    public Ship getShip(){
        return ship;
    }
    public void setShip(Ship s){
        ship = s;
    }
    
    
    public String toString(){
        String shipStr = (ship == null) ? "EMPTY" : ship.toString();
        return String.format("Dock: %s\n     %s\n", super.toString(), shipStr);
    }
}
