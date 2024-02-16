package Server;

import static spark.Spark.after;

import Handlers.BroadbandHandler.DataSource.ACSDataSource;
import Handlers.BroadbandHandler.BroadbandHandler;
import Handlers.Exceptions.DatasourceException;
import Handlers.CSVHandlers.CSVDataSource;
import Handlers.CSVHandlers.LoadCSV;
import Handlers.CSVHandlers.SearchCSV;
import Handlers.CSVHandlers.ViewCSV;
import spark.Spark;

public class Server {
  public static void main(String[] args) throws DatasourceException {
    int port = 3535;
    Spark.port(port);

    // NOTE: we need to change the value from * to 'origins we trust'
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
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
