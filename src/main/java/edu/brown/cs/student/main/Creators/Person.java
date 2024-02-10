package edu.brown.cs.student.main.Creators;

import java.util.List;

public class Person {
  String name;
  int age;
  int classYear;

  public Person(String myName, int myAge, int myClassYear) {
    this.name = myName;
    this.age = myAge;
    this.classYear = myClassYear;
  }

  public List<String> getInfo() {
    return List.of(this.name, Integer.toString(this.age), Integer.toString(this.classYear));
  }
}
