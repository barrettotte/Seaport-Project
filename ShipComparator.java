/*
File Name: ShipComparator.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: Comparator class to perform sorts on Ships by a variety of attributes
*/
package project04_botte;

import java.util.Comparator;


public class ShipComparator implements Comparator<Ship> {
    private String attribute;

    public ShipComparator(String attribute) {
        this.attribute = attribute.toUpperCase();
    }

    public int compare(Ship c1, Ship c2) {
       
        switch (attribute) {
            case "WEIGHT":
                if (c1.getWeight() == c2.getWeight()) {
                    return 0;
                } 
                else if (c1.getWeight() > c2.getWeight()) {
                    return 1;
                } 
                else {
                    return -1;
                }
            case "LENGTH":
                if (c1.getLength() == c2.getLength()) {
                    return 0;
                } 
                else if (c1.getLength() > c2.getLength()) {
                    return 1;
                } 
                else {
                    return -1;
                }
            case "DRAFT":
                if (c1.getDraft() == c2.getDraft()) {
                    return 0;
                } 
                else if (c1.getDraft() > c2.getDraft()) {
                    return 1;
                } 
                else {
                    return -1;
                }
            case "WIDTH":
                if (c1.getWidth() == c2.getWidth()) {
                    return 0;
                } 
                else if (c1.getWidth() > c2.getWidth()) {
                    return 1;
                } 
                else {
                    return -1;
                }
            case "NAME":
                return c1.getName().compareTo(c2.getName());
        }
        return -999;
    }
}
