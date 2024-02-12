package CSVHandlers;

import java.util.ArrayList;
import java.util.List;

public class CSVDataSource {
  List<List<String>> data;

  public CSVDataSource() {}

  // data state kept here
  public void setData(List<List<String>> data) {
    this.data = data;
  }

  public List<List<String>> getData() {
    if (data == null) {
      return new ArrayList<>();
    }
    return new ArrayList<>(data);
  }
}
