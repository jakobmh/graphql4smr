
# TODO start server, benchmark, stop server

.PHONY: buildandbenchmark
buildandbenchmark: build benchmark
	node createsmallreport.js

.PHONY: benchmark
benchmark: graphql-bench/app/cli/bin/run
	{ java -jar ../graphql4smr-demo/app/build/libs/app.jar  &}; \
	pid=$$!; \
	sleep 3; \
	./graphql-bench/app/cli/bin/run query --config=config.query.custom.yaml --outfile="myreport.json"; \
	r=$$?; \
	kill $$pid; \
	exit $$r

.PHONY: build
build: graphql-bench/app/cli/bin/run
	(cd ../graphql4smr-demo/ && ./gradlew app:jar)


graphql-bench/app/cli/bin/run:
	git clone https://github.com/hasura/graphql-bench.git
	(cd graphql-bench/app/cli && yarn install)
