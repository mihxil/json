Json Tools
==========

This is provides several JSON-related tools implemented with
Jackson. It's goal is to be useable with extremely large json
streams, and everything needs to happen streaming.

I tried several tools imlemented in python (python -mjson.tool,
'jsongrep'), but those consumed very much memory when I fead them a
json stream of a Gigabyte or so, and seemed not useable for that.


Formatter
--------
A binary can be downloaded [here](https://github.com/mihxil/mvn-repo/raw/master/releases/org/meeuw/mihxil-json/0.2/mihxil-json-0.2-formatter.jar)


Usage
```
java -jar ~/Download/mihxil-json-0.2-formatter.jar [<infile>] [<outfile>]

infile: defaults to stdin (can explicitely set to stdin as '-'). Can
        be file name but  can also be a remote URL
outfile: default to stdout
```

For a file of nearly one Gb:
```shell
michiel@belono:/tmp$ time java -jar /tmp/mihxil-json-0.1-formatter.jar alldocs.json  alldocs.formatted.json

real	0m27.783s
user	0m19.880s
sys	0m5.686s

michiel@belono:/tmp$ ls -lah alldocs.*
-rw-rw-r--  1 michiel  wheel   1.3G Feb 22 18:17 alldocs.formatted.json
-rw-rw-r--  1 michiel  wheel   928M Feb 22 14:19 alldocs.json
```


Grep
----
A binary can be downloaded [here](https://github.com/mihxil/mvn-repo/raw/master/releases/org/meeuw/mihxil-json/0.2/mihxil-json-0.2-grep.jar)
