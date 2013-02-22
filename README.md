Json Tools
==========

Currently contains only a streaming json formatter based on
jackson. So it doesn't use much memory and you can easily format files
of many many megabytes.

A binary can be downloaded here:

Usage

java -jar ~/github/mihxil/json/target/mihxil-json-0.1-SNAPSHOT-jar-with-dependencies.jar [<infile>] [<outfile>]

infile: defaults to stdin (can explicitely set to stdin as '-')
outfile: default to stdout


For a file of nearly one Gb:

michiel@belono:/tmp$ time java -jar ~/github/mihxil/json/target/mihxil-json-0.1-SNAPSHOT-jar-with-dependencies.jar alldocs.json  alldocs.formatted.json

real	0m27.783s
user	0m19.880s
sys	0m5.686s


michiel@belono:/tmp$ ls -lah alldocs.*
-rw-rw-r--  1 michiel  wheel   1.3G Feb 22 18:17 alldocs.formatted.json
-rw-rw-r--  1 michiel  wheel   928M Feb 22 14:19 alldocs.json
