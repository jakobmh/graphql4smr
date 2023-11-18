
sources = $(wildcard src/*.java)


.PHONY: all
all: graphqllib downloadsmartbft
	javac -d build -cp "lib/*:graphqllib/*" $(sources)


.PHONY: build
build:
	(cd ../graphql4smr-demo/ && ./gradlew app:jar)
	
	



.PHONY: downloadsmartbft
downloadsmartbft:
	if ! [ -d library-master ]; then curl https://codeload.github.com/bft-smart/library/zip/05ebbbfbde6380e59585f4032e4f9bd941614095 --output library-master.zip && unzip library-master.zip && mv library-05ebbbfbde6380e59585f4032e4f9bd941614095 library-master && rm library-master.zip; fi
	(cd library-master && ./gradlew installDist)
	(cp -r library-master/build/install/library/* .)


.PHONY: graphqllib
graphqllib: build
	mkdir -p graphqllib
	(cd graphqllib && if ! [ -f graphql-java-20.2.jar ]; then curl https://repo1.maven.org/maven2/com/graphql-java/graphql-java/20.2/graphql-java-20.2.jar --output graphql-java-20.2.jar; fi)
	(cd graphqllib && if ! [ -f java-dataloader-3.2.0.jar ]; then curl https://repo1.maven.org/maven2/com/graphql-java/java-dataloader/3.2.0/java-dataloader-3.2.0.jar --output java-dataloader-3.2.0.jar; fi)
	(cd graphqllib && cp ../../graphql4smr-demo/app/build/libs/app.jar .)

