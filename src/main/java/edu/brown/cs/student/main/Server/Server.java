package Server;

import static spark.Spark.after;

import Handlers.BroadbandHandler.BroadbandHandler;
import Handlers.BroadbandHandler.DataSource.ACSDataSource;
import Handlers.BroadbandHandler.DataSource.CacheType;
import Handlers.CSVHandlers.CSVDataSource;
import Handlers.CSVHandlers.LoadCSV;
import Handlers.CSVHandlers.SearchCSV;
import Handlers.CSVHandlers.ViewCSV;
import Handlers.Exceptions.DatasourceException;
import spark.Spark;

/** this class starts our server and delegates queries to respective handlers */
public class Server {

  /**
   * run to start server
   *
   * @param args - any arguments given to main when server is run
   * @throws DatasourceException - thrown by Datasource if connection to ACS API fails
   */
  public static void main(String[] args) throws DatasourceException {
    int port = 3636;
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
    Spark.get("broadband", new BroadbandHandler(new ACSDataSource(), CacheType.MAX_SIZE, 2));
    Spark.init();
    Spark.awaitInitialization();

    // Links localhost to navigate to
    System.out.println("Server started at http://localhost:" + port);
  }
}
