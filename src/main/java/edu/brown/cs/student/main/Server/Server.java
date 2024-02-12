package Server;

import static spark.Spark.after;

import APIHandlers.ACSDataSource;
import APIHandlers.ACSProxy;
import APIHandlers.BroadbandHandler;
import CSVHandlers.LoadCSV;
import CSVHandlers.CSVDataSource;
import CSVHandlers.SearchCSV;
import CSVHandlers.ViewCSV;
import spark.Spark;

public class Server {
  public static void main(String[] args) {
    int port = 3535;
    Spark.port(port);

    // NOTE: we need to change the value from * to 'origins we trust'
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    CSVDataSource CSVDataSource = new CSVDataSource();
    ACSProxy proxy = new ACSProxy();
    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadCSV(CSVDataSource));
    Spark.get("viewcsv", new ViewCSV(CSVDataSource));
    Spark.get("searchcsv", new SearchCSV(CSVDataSource));
    Spark.get("broadband", new BroadbandHandler(proxy));
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
