package edu.brown.cs.student;

// Make sure you're using JUnit 5 -- add it via Maven, or get IntelliJ to help.

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.FactoryFailureException;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Search;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.ValueNotFoundException;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

/** This class tests my Search class */
public class SearchTest {

  @Test
  public void valuesPresentAllCSV() throws IOException, FactoryFailureException {

    // value present in one row
    List<List<String>> census1S =
        new Search("census/dol_ri_earnings_disparity.csv", "Black").getResults();
    List<String> census1Correct = List.of("RI", "Black", "$770.26", "30424.80376", "$0.73", "6%");
    assertEquals(census1Correct, census1S.get(0));

    // value present in more than one row
    List<List<String>> census3S =
        new Search("census/postsecondary_education.csv", "Hispanic or Latino").getResults();
    List<List<String>> census3Correct =
        List.of(
            List.of(
                "Hispanic or Latino",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "143",
                "brown-university",
                "0.046263345",
                "Men",
                "1"),
            List.of(
                "Hispanic or Latino",
                "2020",
                "2020",
                "217156",
                "Brown University",
                "207",
                "brown-university",
                "0.066968619",
                "Women",
                "2"));
    assertEquals(census3Correct, census3S);

    // value not present
    List<List<String>> census2S =
        new Search("census/postsecondary_education.csv", "green").getResults();
    List<List<String>> census2Correct = List.of();
    assertEquals(census2Correct, census2S);
  }

  @Test
  public void weirdCSVAll() throws IOException, FactoryFailureException {
    // csv with spaces after commas
    List<List<String>> myTests2 = new Search("myTests/noHeaders.csv", "10").getResults();
    List<List<String>> myTests2Correct =
        List.of(
            List.of("astrid", "10", "20"),
            List.of("kamryn", "10", "16"),
            List.of("'sir dylan'", "10", "6"));
    assertEquals(myTests2Correct, myTests2);

    // non csv file
    List<List<String>> myTests1 = new Search("myTests/nonCSV", "10").getResults();
    List<List<String>> myTests1Correct =
        List.of(
            List.of("astrid", "10", "20"),
            List.of("kamryn", "10", "16"),
            List.of("'sir dylan'", "10", "6"));
    assertEquals(myTests1Correct, myTests1);
  }

  @Test
  public void valuesPresentSpecCol()
      throws IOException, FactoryFailureException, ValueNotFoundException {
    // value present in right col by col name
    List<List<String>> stars2 = new Search("stars/ten-star.csv", "0", true, "X").getResults();
    List<List<String>> stars2Correct =
        List.of(List.of("0", "Sol", "0", "0", "0"), List.of("24", "", "0", "2", "3"));
    assertEquals(stars2Correct, stars2);

    // value present in wrong col
    stars2 = new Search("stars/ten-star.csv", "0.00449", true, "X").getResults();
    stars2Correct = List.of();
    assertEquals(stars2Correct, stars2);

    // value in col index
    stars2 = new Search("stars/ten-star.csv", "0", false, "2").getResults();
    stars2Correct = List.of(List.of("0", "Sol", "0", "0", "0"), List.of("24", "", "0", "2", "3"));
    assertEquals(stars2Correct, stars2);

    // search value that is a phrase
    List<List<String>> myTests1 =
        new Search("myTests/nonCSV", "'sir dylan'", false, "0").getResults();
    List<List<String>> myTests1Correct = List.of(List.of("'sir dylan'", "10", "6"));
    assertEquals(myTests1Correct, myTests1);

    // value present with a match, but not exact i.e. x vs X
    stars2 = new Search("stars/ten-star.csv", "0", true, "x").getResults();
    stars2Correct = List.of(List.of("0", "Sol", "0", "0", "0"), List.of("24", "", "0", "2", "3"));
    assertEquals(stars2Correct, stars2);
  }

  @Test
  public void weirdCSVSpecCol()
      throws IOException, FactoryFailureException, ValueNotFoundException {
    // csv with spaces after commas
    List<List<String>> myTests1 =
        new Search("myTests/CSVWHeaders&Spaces.csv", "male", true, "gender").getResults();
    List<List<String>> myTests1Correct =
        List.of(List.of("Josh", "politician", "male"), List.of("Mong", "engineer", "male"));
    assertEquals(myTests1Correct, myTests1);

    // non csv file
    List<List<String>> myTests4 =
        new Search("myTests/NonCSVWHeaders", "male", true, "gender").getResults();
    List<List<String>> myTests4Correct =
        List.of(List.of("Josh", "politician", "male"), List.of("Mong", "engineer", "male"));
    assertEquals(myTests4Correct, myTests4);
  }

  @Test
  public void throwingErrors() {
    // malformed rows
    assertThrows(
        FactoryFailureException.class, () -> new Search("malformed/malformed_signs.csv", "Gabi"));

    // wrong file path
    assertThrows(IOException.class, () -> new Search("malformed/malfirmed_signs.csv", "Gabi"));

    // search for value with column header that doesn't exist
    assertThrows(
        ValueNotFoundException.class,
        () -> new Search("census/dol_ri_earnings_disparity.csv", "RI", true, "Stute"));

    // search for volume where col index doesn't exist
    assertThrows(
        ValueNotFoundException.class,
        () -> new Search("census/dol_ri_earnings_disparity.csv", "RI", false, "6"));

    // file in restricted area
    assertThrows(IOException.class, () -> new Search("data1", "hi"));
  }
}
