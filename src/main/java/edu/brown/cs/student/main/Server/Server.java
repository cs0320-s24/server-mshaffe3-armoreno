package Server;

import static spark.Spark.after;

import APIHandlers.*;
import APIHandlers.Exceptions.DatasourceException;
import CSVHandlers.CSVDataSource;
import CSVHandlers.LoadCSV;
import CSVHandlers.SearchCSV;
import CSVHandlers.ViewCSV;
import spark.Spark;

public class Server {
  public static void main(String[] args) throws DatasourceException {
    int port = 3535;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "http");
          response.header("Access-Control-Allow-Methods", "http");
        });
    CSVDataSource CSVDataSource = new CSVDataSource();

    // Setting up the handler for the GET /order and /activity endpoints
    Spark.get("loadcsv", new LoadCSV(CSVDataSource));
    Spark.get("viewcsv", new ViewCSV(CSVDataSource));
    Spark.get("searchcsv", new SearchCSV(CSVDataSource));
    Spark.get("broadband", new BroadbandHandler(new ACSDataSource()));
    Spark.init();
    Spark.awaitInitialization();

    // Notice this link alone leads to a 404... Why is that?
    System.out.println("Server started at http://localhost:" + port);
  }
}
