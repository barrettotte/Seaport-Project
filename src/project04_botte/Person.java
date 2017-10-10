/*
File Name: Person.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class creates a person with a specific skill to be used in a seaport
*/
package project04_botte;

import java.util.Scanner;


public class Person extends Thing{
    private String skill;
    
    //Constructor
    public Person(Scanner sc) {
        super(sc);
        skill = (sc.hasNext()) ? sc.next() : "<ERROR>";
    }
    
    
    //Accessors/Mutators:
    public String getSkill(){
        return skill;
    }
    public void setSkill(String s){
        skill = s;
    }
    
    
    public String toString(){
        return String.format("Person: %s %s", super.toString(), skill);
    }
}