package CSVHandlers.SearchFunctionality;

import static java.lang.Integer.parseInt;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Class that searches a CSV dataset and returns rows of the dataset with the value searched for */
public class Search {

  List<List<String>> results;
  /**
   * This constructor searches the entire parsed CSV dataset for rows containing value to print
   *
   * @param value - The value searched for
   */
  public Search(List<List<String>> parsedData, String value) {
    this.results = new ArrayList<>();
    allColsPrint(parsedData, value);
  }

  /**
   * Searches a specific column in the CSV data file for rows that contain the value to print
   *
   * @param value - The value searched for
   * @param headers - boolean used to tell whether headers or index should be used to isolate column
   * @param colHeader - the header name or index of the column that is searched
   */
  public Search(List<List<String>> parsedData, String value, boolean headers, String colHeader)
      throws ValueNotFoundException {

    this.results = new ArrayList<>();
    specColPrint(convertHeader(parsedData.get(0), colHeader, headers), parsedData, value);
  }

  /**
   * Searches the entire parsed data and prints the rows containing the value
   *
   * @param parsedData - parsed CSV data of a 2D list of strings
   * @param value - the value searched in the dataset
   */
  private void allColsPrint(List<List<String>> parsedData, String value) {
    for (List<String> row : parsedData) {
      for (String item : row) {
        if (item.equals(value)) {
          this.results.add(row);
        }
      }
    }
  }

  /**
   * Searches a specific column, defined by column index, to print the rows with the given value
   *
   * @param colIndex - The index of the column to be searched
   * @param parsedData - parsed CSV data of a 2D list of strings
   * @param value - the value searched in the dataset
   */
  private void specColPrint(int colIndex, List<List<String>> parsedData, String value) {

    // want to loop through all the data but only specifically search every row in the given column
    // print when found
    for (List<String> row : parsedData) {
      if (row.get(colIndex).equals(value)) {
        this.results.add(row);
      }
    }
  }

  /**
   * Converts the header of a specific column into the index of that column
   *
   * @param headerRow - The first row of the parsed dataset containing the headers
   * @param header - The specific header to look for in the headerRow
   * @param headers - Whether the header is there or if it is the index
   * @return the index of the column to search in
   */
  private int convertHeader(List<String> headerRow, String header, boolean headers)
      throws ValueNotFoundException {

    int colCounter = 0;
    if (headers) {
      for (String posHeader : headerRow) {
        if (posHeader.toLowerCase(Locale.US).equals(header.toLowerCase(Locale.US))) {
          return colCounter;
        }
        colCounter++;
      }
    } else {
      int colIndex = parseInt(header);
      if (colIndex >= headerRow.size()) {
        throw new ValueNotFoundException("Column index doesn't exist");
      }
      return colIndex;
    }

    // if header is not a match at all, do something
    throw new ValueNotFoundException("Header doesn't exist");
  }

  /**
   * returns the list of found rows
   *
   * @return 2D list of strings that match given value
   */
  public List<List<String>> getResults() {
    return results;
  }
}
