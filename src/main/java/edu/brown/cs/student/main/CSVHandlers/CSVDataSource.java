package CSVHandlers;

import java.util.ArrayList;
import java.util.List;

/**
 * This class serves as a proxy and defensive programming mechanism to protect the modification of
 * parsed data
 */
public class CSVDataSource {
  List<List<String>> data;

  public CSVDataSource() {}

  // data state kept here
  public void setData(List<List<String>> data) {
    this.data = data;
  }

  public List<List<String>> getData() {
    // if set data has not been called yet (i.e. no valid "loadcsv" calls yet)
    if (data == null) {
      return new ArrayList<>();
    }
    // returns a defensive copy, rather than the actual reference
    return new ArrayList<>(data);
  }
}
