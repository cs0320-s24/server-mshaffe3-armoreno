> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your edu.brown.cs.student.main.Server.Server class matches the path specified in the run script. Currently, it is set to execute edu.brown.cs.student.main.Server.Server at `edu/brown/cs/student/main/server/edu.brown.cs.student.main.Server.Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

# Project Details
This project is a server application that provides a web API for data
retrieval and search. It can read and respond to CSV files, as well as the US
Census API. This was designed with the Mesh Network Coalition stakeholder in mind to

# Design Choices
**Proxy classes and Caching:**
For both the CSV datasource (file)and the ACSDatasource (API), we created proxy classes that were
responsible for storing data across handlers in the case of CSV, and storing the cache & wrapping
the ACSDatasource in the case of broadband.
  - For CSVHandling, this proxy class ensures that no server error is encountered if the View or
  Search handler is called without a file loaded, and also returns a defensive copy of the data to
  protect it from mutation.
  - For Broadband ACS data, the ACS Proxy was used to store the cache which holds previously searched
  queries for more efficient access. That way, the ACSDatasource, which was wrapped by the proxy,
  was only asked to actually query the API if the result had not been in the cache.
    - The CacheType enum allows for developer customization of what type of caching of results they
    want, while still remaining secure and defensive against bugs.
    - The Location class allows cache to recognize equality for past searches in an elegant way

**Error handling**
Since the Mesh Community Network Coalition did not place a ton of emphasis on front end appearance,
the display of JSON results is simple yet effective. In the cases where various errors are thrown
in the code, error explanations are added to the responseMaps and used to respond to malformed
queries or other unforseen difficulties, rather than giving 500 errors or crashing the server.
The exceptions are primarily caught by the Handler classes themselves, as they are the closest to
"user interaction" as this program gets.


# Errors/Bugs


# Tests
We tested thoroughly, both unit and integration testing. For CSV Handling, the search and parse
functionalities were mostly tested from the last sprint, so the test suite focuses on the
interaction between them, and how to handle malformed queries.

For APIMockTests, we similarly tested malformed queries, including missing parameters and
standard success responses.

For API Unit tests,

# How to
**Queries:**
FOR CSV QUERIES:
/loadcsv?filepath=FILEPATH_HERE
/viewcsv
/searchcsv?value=VALUE_HERE&identifier=YOUR_IDENTIFIER_HERE
* NOTE: searchcsv identifiers can either be Header names of a specific column, or the numerical index of
that column

FOR ACS API QUERIES:
/broadband?state=STATE_NAME_HERE&county=COUNTY_NAME_HERE
** NOTE: Counties with two words or more should be entered with the '+' symbol between words

**Caching:**
When creating a new BroadbandHandler(), you can pass in a CacheType of MAX_SIZE, NO_LIMIT, or
TIME to decide its eviction policy, as well as a numerical value for max size or time before
deletion. If no cache is desired, NONE can be added as the cachetype.