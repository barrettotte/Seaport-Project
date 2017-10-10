/*
File Name: Thing.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class is the base class for all objects "spawned" in a World object
*/
package project04_botte;

import java.util.Scanner;

public class Thing implements Comparable <Thing>{
    private int index;
    private String name;
    private int parent;
    
    //Constructor:
    public Thing(Scanner sc){
        name = (sc.hasNext()) ? sc.next() : "<ERROR>";
        index = (sc.hasNextInt()) ? sc.nextInt() : 0;
        parent = (sc.hasNextInt()) ? sc.nextInt() : 0;
    }
    
    //Accessors/Mutators:
    public int getIndex(){
        return index;
    }
    
    public void setIndex(int i){
        if(i > 0){
            index = i;
        }
    }
    
    public String getName(){
        return name;
    }
    public void setName(String n){
        name = n;
    }
    
    public int getParent(){
        return parent;
    }
    public void setParent(int p){
        if(p > 0){
            parent = p;
        }
    }
    
    public int compareTo(Thing o) {
        boolean comp = 
               (o.getIndex() == this.getIndex()) &&
               (o.getName().equals(this.getName())) &&
               (o.getParent() == this.getParent());
        return (comp) ? 1 : 0;
    } 
    
    
    public String toString(){
        return String.format("%s %d", name, index);
    }
}