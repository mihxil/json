
.PHONY: release
release:
	mvn release:prepare
	mvn -Pdeploy release:perform
