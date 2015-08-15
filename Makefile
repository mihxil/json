
.PHONY: release
release:
	mvn release:prepare
	mvn -Pdeploy release:perfomr
