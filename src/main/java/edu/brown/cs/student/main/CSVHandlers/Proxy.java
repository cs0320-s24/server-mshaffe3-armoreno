package edu.brown.cs.student.main.CSVHandlers;

import java.util.ArrayList;
import java.util.List;

public class Proxy {
  List<List<String>> data;
  public Proxy(List<List<String>> data){
    this.data = data;
  }
  //cache contained here!

  public List<List<String>> getData() {
    return new ArrayList<>(data);
  }

}
