/*
File Name: CargoShip.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class inherits from Ship to hold cargo values.
*/
package project04_botte;

import java.util.Scanner;


public class CargoShip extends Ship{
    private double cargoValue;
    private double cargoVolume;
    private double cargoWeight;

    
    //Constructor:
    public CargoShip(Scanner sc) {
        super(sc);
        cargoWeight = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        cargoVolume = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
        cargoValue = (sc.hasNextDouble()) ? sc.nextDouble() : 0.0;
    }
    
    
    //Accessors/Mutators:
    public double getCargoValue(){
        return cargoValue;
    }
    public void setCargoValue(double v){
        if(v >= 0.0){
            cargoValue = v;
        }
    }
    
    public double getCargoVolume(){
        return cargoVolume;
    }
    public void setCargoVolume(double v){
        if(v >= 0.0){
            cargoVolume = v;
        }
    }
    
    public double getCargoWeight(){
        return cargoWeight;
    }
    public void setCargoWeight(double w){
        if(w >= 0.0){
            cargoWeight = w;
        }
    }
    
    
    public String toString(){
        return String.format("Cargo Ship: %s\n     Cargo Value: %.2f\n     Cargo Volume: %.2f\n     Cargo Weight: %.2f\n", 
                super.toString(), cargoValue, cargoVolume, cargoWeight);
    }
}