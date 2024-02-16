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
  For CSVHandling, this proxy class re


# Errors/Bugs

# Tests

# How to
