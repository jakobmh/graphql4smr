# bft-smart-integration-demo

Diese Demo zeigt wie GraphQL auf bft-smart laufen könnte


## Setup
 - java-11 wird für bft-smart benötigt.
 - make, unzip, curl (siehe Makefile)

## Build
download and build dependencies mit:
```
make all
```

## Aufführen ohne GraphQL
Es werden zwei Terminals benötigt. 

1. Terminal
run 4 Servers
```
bash parallel_commands_server_mod.sh
```

2. Terminal
run 1 Client
```
./smartrun_mod.sh CounterClient 1001 0
```

## Aufführen mit GraphQL
Es werden zwei Terminals benötigt. 

1. Terminal
run 4 Servers
```
bash parallel_commands_server_mod_spark.sh
```

2. Terminal
run 1 Client
```
./smartrun_mod.sh CounterClientSpark 1001 0
```
- öffne http://localhost:5555/ im browser
- incrementiere mit 
```
mutation{
  increment(zahl:1)
}
```
