/*
File Name: JobTableModel.java
Date: 10-01-2017
Author: Barrett Otte
Purpose: This class is used to format and control the JTable used to display
    jobs.
*/
package project04_botte;

import java.util.ArrayList;
import javax.swing.table.*;


public class JobTableModel extends AbstractTableModel{
    private ArrayList<String[]> data;
    private String[] header;
    
    JobTableModel(String[] header){
        this.header = header;
        data = new ArrayList<>();
    }
    
    public int getRowCount(){
        return data.size();
    }
    public int getColumnCount(){
        return header.length;
    }
    public String getValueAt(int row, int col){
        return data.get(row)[col];
    }
    public String getColumnName(int index){
        return header[index];
    }
    
    public void add(Ship ship, java.util.HashMap<Integer, Thing> masterMap, Job jobToAdd){
        String[] row = new String[3];
        row[0] = ship.getName();
        Thing parent = masterMap.get(ship.getParent());
        row[1] = masterMap.get(parent.getParent()).getName() + " -- " + parent.getName();
        row[2] = jobToAdd.getName();
        data.add(row);
        fireTableDataChanged();
    }
      
    public void remove(String name){
        for(String[] arr : data){
            if(arr[2].equals(name)){
                //System.out.println("Removing Job " + name + "");
                //System.out.println("Job Model Size: " + data.size());
                data.remove(arr);
                fireTableDataChanged();
                break;
            }
        }
    }
}