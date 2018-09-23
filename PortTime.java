/*
File Name: PortTime.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class acts as a timestamp for ships in the seaport
*/
package project04_botte;


public class PortTime {
    private int time;
    
    
    //Constructor:
    public PortTime(){
        time = 0;
    }
    
    //Accessors/Mutators:
    public int getTime(){
        return time;
    }
    public void setTime(int t){
        if(t >= 0){
            time = t;
        }
    }
    
    
    public String toString(){
        return String.format("Time: %d" , time);
    }
}