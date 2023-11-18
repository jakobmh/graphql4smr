# GraphQL4SMR

This project contains the sourcecode covering my bachelor thesis. jakobmh 

## dependencies
everything between java 8 and  java 20 should work
(except for bft-smart-integration-demo to build it you need java 8).
code was only run and tested on linux.
Additional dependencies like gnuplot, sqlite3, curl, make, and more might be needed for some functionalities.

## Subfolder
 - graphql4smr-lib: collection of functionalies to implement an Graph on an state machine replication framework:
    - modified uds Scheduler
    - generated graphQL queries and mutations (read,select,delete,insert,update)
    - interface to sql(-ite) dump file 
    - deadlock detection
    - cycle detection in  GraphQL types to avoid using deadlock detection algorithms
 - graphql4smr-demo: demo Application with interactive GraphQL Webinterface and boilerplate for gnuplot, more Information in Readme.md
 - bft-smart-integration-demo  
 - benchmarking: external benchmarking the demo application with and without bft-smart-integration
 
