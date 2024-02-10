package edu.brown.cs.student.main.Creators;

import static java.lang.Integer.parseInt;

import edu.brown.cs.student.main.FactoryFailureException;
import java.util.List;

public class PersonCreator implements CreatorFromRow<Person> {

  int colNum;

  public PersonCreator() {}

  @Override
  public void giveCols(int col) {
    this.colNum = col;
  }

  @Override
  public Person create(List<String> row) throws FactoryFailureException {
    if (row.size() != this.colNum) {
      throw new FactoryFailureException("malformed row", row);
    }
    return new Person(row.get(0), parseInt(row.get(1)), parseInt(row.get(2)));
  }
}
