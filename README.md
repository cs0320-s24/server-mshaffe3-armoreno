> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your edu.brown.cs.student.main.Server.Server class matches the path specified in the run script. Currently, it is set to execute edu.brown.cs.student.main.Server.Server at `edu/brown/cs/student/main/server/edu.brown.cs.student.main.Server.Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
This project is a server application that provides a web API for data
retrieval and search. It can read and respond to CSV files, as well as the US
Census API. This was designed with the Mesh Network Coalition stakeholder in mind to

# Design Choices
**Proxy classes:**
For both the CSV datasource (file)and the ACSDatasource (API), we created proxy classes that were
responsible for storing data across handlers in the case of CSV, and storing the cache & wrapping
the ACSDatasource in the case of broadband.
  For CSVHandling, this proxy class ensures that no server error is encountered if the View or
  Search handler is called without a file loaded, and also returns a defensive copy of the data to
  protect it from mutation.
  For Broadband ACS data, the ACS Proxy was used to store the cache which holds previously searched
  queries for more efficient access. That way, the ACSDatasource, which was wrapped by the proxy,
  was only asked to actually query the API if the result had not been in the cache.


# Errors/Bugs

# Tests
  **TestCache** - tests all possible cache options including: no cache, time-limit eviction policy, size eviction policy,
                and normal cache

  **APIMockTests** - tests our API using a mock data source for things including: successful query, both bad arguments, one bad argument

  **TestCSVHandler** - tests CSV handler and all its functionalities including: loading a file, viewing a loaded file, 
                    viewing an unloaded file, searching with headers, searching with index, and searching in general

  **APITest** - tests our API by sending requests to the ACS API for things including: successful query, county doesn't 
                exist in a state, state doesn't exist, and that we can successfully query after an error query



  

# How to
