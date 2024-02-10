package edu.brown.cs.student.main.Creators;

import edu.brown.cs.student.main.FactoryFailureException;
import java.util.ArrayList;
import java.util.List;

/** Class that is passed into parse to make sure rows are properly created */
public class StringCreator implements CreatorFromRow<List<String>> {

  int numberCols;

  public StringCreator() {
    numberCols = 0;
  }

  /**
   * setter method used to set the initial number of columns every row should have
   *
   * @param num_cols - number of columns stored in giveCols
   */
  public void giveCols(int num_cols) {
    this.numberCols = num_cols;
  }

  /**
   * Creates rows of Strings to pass back to parse
   *
   * @param row - parsed row from Parser
   * @return - the row with trimmed items
   * @throws FactoryFailureException - when the number of cols doesn't match up with numberCols
   */
  public List<String> create(List<String> row) throws FactoryFailureException {
    if (row.size() != this.numberCols) {
      throw new FactoryFailureException("malformed row", row);
    }
    List<String> newRow = new ArrayList<>();
    for (String item : row) {
      newRow.add(item.replace("\"", "'"));
    }
    return newRow;
  }
}
