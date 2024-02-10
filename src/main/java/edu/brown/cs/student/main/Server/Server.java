package edu.brown.cs.student.main.Server;
import static spark.Spark.after;

import edu.brown.cs.student.main.APIHandlers.Broadband;
import edu.brown.cs.student.main.CSVHandlers.LoadCSV;
import edu.brown.cs.student.main.CSVHandlers.Proxy;
import edu.brown.cs.student.main.CSVHandlers.SearchCSV;
import edu.brown.cs.student.main.CSVHandlers.ViewCSV;
import spark.Spark;


public class Server {
  public static void main(String[]args){
    int port = 3232;
    Spark.port(port);

    //NOTE: we need to change the value from * to 'origins we trust'
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
   Proxy proxy = new Proxy();
    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadCSV(proxy));
    Spark.get("viewcsv", new ViewCSV(proxy));
    Spark.get("searchcsv", new SearchCSV());
    Spark.get("broadband", new Broadband());
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);


  }
}
