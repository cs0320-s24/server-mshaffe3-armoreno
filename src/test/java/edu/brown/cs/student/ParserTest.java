package edu.brown.cs.student;

// Make sure you're using JUnit 5 -- add it via Maven, or get IntelliJ to help.

import static org.junit.jupiter.api.Assertions.*;

import edu.brown.cs.student.main.Creators.Person;
import edu.brown.cs.student.main.Creators.PersonCreator;
import edu.brown.cs.student.main.Creators.StringCreator;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.FactoryFailureException;
import edu.brown.cs.student.main.CSVHandlers.SearchFunctionality.Parser;
import java.io.*;
import java.util.List;
import org.junit.jupiter.api.Test;

/** This class tests my Parser functionality */
public class ParserTest {

  @Test
  public void notCSV() throws IOException, FactoryFailureException {
    // using a StringReader to create a BufferedReader
    BufferedReader string2Buffer = new BufferedReader(new StringReader("Astrid, 20, 2026"));
    Parser<List<String>> string2parser = new Parser<>(string2Buffer, new StringCreator());
    List<List<String>> parsedString = string2parser.parse();
    List<List<String>> correctString = List.of(List.of("Astrid", "20", "2026"));
    assertEquals(parsedString, correctString);

    // using a text file
    BufferedReader text2Buffer = new BufferedReader(new FileReader("data/myTests/nonCSV"));
    Parser<List<String>> text2parser = new Parser<>(text2Buffer, new StringCreator());
    List<List<String>> parsedText = text2parser.parse();
    List<List<String>> correctText =
        List.of(
            List.of("astrid", "10", "20"),
            List.of("jordan", "15", "16"),
            List.of("kamryn", "10", "16"),
            List.of("dior", "15", "9"),
            List.of("juan", "20", "5"),
            List.of("'sir dylan'", "10", "6"),
            List.of("Anna", "9", "2"));
    assertEquals(parsedText, correctText);

    // using a regular CSV
    BufferedReader csv2Buffer = new BufferedReader(new FileReader("data/stars/ten-star.csv"));
    Parser<List<String>> csv2parser = new Parser<>(csv2Buffer, new StringCreator());
    List<List<String>> parsedCSV = csv2parser.parse();
    List<List<String>> correctCSV =
        List.of(
            List.of("StarID", "ProperName", "X", "Y", "Z"),
            List.of("0", "Sol", "0", "0", "0"),
            List.of("1", "", "282.43485", "0.00449", "5.36884"),
            List.of("2", "", "43.04329", "0.00285", "-15.24144"),
            List.of("3", "", "277.11358", "0.02422", "223.27753"),
            List.of("3759", "96 G. Psc", "7.26388", "1.55643", "0.68697"),
            List.of("70667", "Proxima Centauri", "-0.47175", "-0.36132", "-1.15037"),
            List.of("71454", "Rigel Kentaurus B", "-0.50359", "-0.42128", "-1.1767"),
            List.of("71457", "Rigel Kentaurus A", "-0.50362", "-0.42139", "-1.17665"),
            List.of("87666", "Barnard's Star", "-0.01729", "-1.81533", "0.14824"),
            List.of("118721", "", "-2.28262", "0.64697", "0.29354"),
            List.of("24", "", "0", "2", "3"),
            List.of("26", "", "2", "3", "0"));
    assertEquals(parsedCSV, correctCSV);
  }

  @Test
  public void differentCreators() throws IOException, FactoryFailureException {
    // use person creator and make sure people are made properly
    BufferedReader peopleCSV =
        new BufferedReader(new FileReader("data/myTests/personTests/peopleToMake.csv"));
    Parser<Person> people2parser = new Parser<>(peopleCSV, new PersonCreator());
    List<Person> parsedPeople = people2parser.parse();

    List<Person> correctPeople =
        List.of(
            new Person("Astrid", 20, 2026),
            new Person("Mong", 19, 2027),
            new Person("Kamryn", 20, 2026),
            new Person("Nicole", 20, 2026),
            new Person("Brittany", 15, 2030));

    assertEquals(parsedPeople.get(0).getInfo(), correctPeople.get(0).getInfo());
    assertEquals(parsedPeople.get(1).getInfo(), correctPeople.get(1).getInfo());
    assertEquals(parsedPeople.get(2).getInfo(), correctPeople.get(2).getInfo());
    assertEquals(parsedPeople.get(3).getInfo(), correctPeople.get(3).getInfo());
    assertEquals(parsedPeople.get(4).getInfo(), correctPeople.get(4).getInfo());
  }

  @Test
  public void throwingErrors() throws IOException, FactoryFailureException {
    // create throws a FactoryFailureException
    BufferedReader malformedReader =
        new BufferedReader(new FileReader("data/malformed/malformed_signs.csv"));
    Parser<List<String>> malformedParser = new Parser<>(malformedReader, new StringCreator());
    assertThrows(FactoryFailureException.class, malformedParser::parse);
  }
}
