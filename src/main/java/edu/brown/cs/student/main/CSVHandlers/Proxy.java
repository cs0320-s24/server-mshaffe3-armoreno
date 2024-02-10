package edu.brown.cs.student.main.CSVHandlers;

import java.util.ArrayList;
import java.util.List;

public class Proxy {
  List<List<String>> data;
  public Proxy(){
  }
  //cache contained here!
  public void setData(List<List<String>> data){
    this.data = data;
  }

  public List<List<String>> getData() {
    return new ArrayList<>(data);
  }

}
