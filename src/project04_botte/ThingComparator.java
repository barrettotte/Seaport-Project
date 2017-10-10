/*
File Name: ThingComparator.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: Comparator class to perform sorts on Lists by name
*/

package project04_botte;

import java.util.Comparator;

public class ThingComparator implements Comparator<Thing> {
    private String attribute;

    public ThingComparator(String attribute) {
        this.attribute = attribute.toUpperCase();
    }

    public int compare(Thing t1, Thing t2) {
       
        switch (attribute) {
            case "NAME":
                return t1.getName().compareTo(t2.getName());
        }
        return -999;
    }
}