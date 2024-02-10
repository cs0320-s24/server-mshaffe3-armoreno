package edu.brown.cs.student.main.CSVHandlers.SearchFunctionality;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/** The Main class of our project. This is where execution begins. */
public final class Main {
  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  /**
   * runs Search based on amount of args
   *
   * @param args - arguments user inputs
   */
  private Main(String[] args) {

    try {
      try {
        try {
          //  Block of code to try
          System.out.println("searching...");
          if (args.length == 2) {

            List<List<String>> allSearch = new Search(args[0], args[1]).getResults();
            for (List<String> item : allSearch) {
              System.out.println(item);
            }
          } else if (args.length == 4) {

            List<List<String>> specSearch =
                new Search(args[0], args[1], args[2].toLowerCase(Locale.US).equals("true"), args[3])
                    .getResults();
            for (List<String> item : specSearch) {
              System.out.println(item);
            }

          } else {
            System.out.println(
                "improper argument input! should follow format : ./run <path> <value> for thorough search");
            System.out.println(
                "or : ./run <file or path from Data> <value> <true/false> <header> for specific search");
            System.out.println("for more information on running this program, read the README!");
          }
        } catch (IOException e) {
          System.out.print(" File not found " + e.getMessage());
        }
      } catch (FactoryFailureException i) {
        System.out.println("exiting...");
      }

    } catch (ValueNotFoundException g) {
      System.out.println("Header doesn't exist");
    }
  }

  /** Called when the user runs program */
  private void run() {
    System.out.println("all done!");
  }
}
