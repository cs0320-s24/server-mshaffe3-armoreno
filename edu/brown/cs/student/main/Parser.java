package CSVHandlers.SearchClasses;

import edu.brown.cs.student.main.Creators.CreatorFromRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class takes in a generic, called T, through CreatorFromRow to parse CSV data into T objects
 */
public class Parser<T> {

  private final BufferedReader csvReader;

  private final CreatorFromRow<T> creatorObj;

  /**
   * Constructor initializes csvReader and creatorObj
   *
   * @param CSV - CSV data that is stored in a BufferedReader
   * @param creatorObject - Generic object that is used to convert parsed rows into T objects
   */
  public Parser(BufferedReader CSV, CreatorFromRow<T> creatorObject) {
    this.csvReader = CSV;
    this.creatorObj = creatorObject;
  }

  /**
   * parse creates a double array and reads csvReader rows to pass to creatorObj to convert to T
   *
   * @return a list of T
   * @throws IOException - thrown when csvReader returns an error when ready() is called on it
   * @throws FactoryFailureException - thrown by creatorObj if a row is malformed
   */
  public List<T> parse() throws FactoryFailureException, IOException {

    List<T> objectList = new ArrayList<>();

    // read next line and use the split method to split by commas
    List<String> stringRow =
        List.of(
            this.csvReader
                .readLine()
                .split(" *,(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\")) *"));

    // let the creatorObj know how many columns the data should be
    this.creatorObj.giveCols(stringRow.size());

    // give it to the creatorObject to a row of objects and add to objectList
    objectList.add(this.creatorObj.create(stringRow));

    String currentLine;
    // add it to the list of objects
    while ((currentLine = this.csvReader.readLine()) != null) {

      // use the split method to split by commas
      stringRow =
          List.of(currentLine.split(" *,(?=([^\\\"]*\\\"[^\\\"]*\\\")*(?![^\\\"]*\\\")) *"));

      // give it to the creatorObject to a row of objects and add to objectList
      objectList.add(this.creatorObj.create(stringRow));
    }

    // return parsed info as a list
    return objectList;
  }
}
