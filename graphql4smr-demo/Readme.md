## graphql4smr-demo

 - this folder contains a demonstration application with various functionality

 - additionaly some convienient boilerplate to plot the (performance)-Test results.

 - everything between java 8 and  java 21 should work

 - code was only testet on linux, additional packages like gnuplot and sqlite3 might be needed.


## build jar
```
./gradlew app:jar
```

## execute help to show all commands
```
java -jar app/build/libs/app.jar --help
```


## execute example4

```
java -jar app/build/libs/app.jar --help
```

open webbrowser on http://localhost:5555/

## create plots with
execute gnuplotexample
```
java -jar app/build/libs/app.jar plot1
```
