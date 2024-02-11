package server;

import static spark.Spark.after;

import APIHandlers.ACSDataSource;
import APIHandlers.BroadbandHandler;
import CSVHandlers.LoadCSV;
import CSVHandlers.Proxy;
import CSVHandlers.SearchCSV;
import CSVHandlers.ViewCSV;
import spark.Spark;

public class Server {
  public static void main(String[] args) {
    int port = 3232;
    Spark.port(port);

    // NOTE: we need to change the value from * to 'origins we trust'
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });
    Proxy proxy = new Proxy();

    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadCSV(proxy));
    Spark.get("viewcsv", new ViewCSV(proxy));
    Spark.get("searchcsv", new SearchCSV(proxy));
    Spark.get("broadband", new BroadbandHandler(new ACSDataSource()));
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
