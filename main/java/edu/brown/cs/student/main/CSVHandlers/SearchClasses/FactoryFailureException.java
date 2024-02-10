package CSVHandlers.SearchClasses;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an error provided to catch any error that may occur when you create an object from a row.
 * Feel free to expand or supplement or use it for other purposes.
 */
public class FactoryFailureException extends Exception {
  final List<String> row;

  /**
   * Exception that is thrown when a malformed row is encountered
   *
   * @param message - message that is printed
   * @param row - malformed row that is printed
   */
  public FactoryFailureException(String message, List<String> row) {
    super(message);
    this.row = new ArrayList<>(row);
    System.out.println("This row is malformed: " + this.row);
  }
}
