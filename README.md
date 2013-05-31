[![Build Status](https://travis-ci.org/mihxil/json.png?)](https://travis-ci.org/mihxil/json)

Json Tools
==========

This is provides several JSON-related tools implemented with
Jackson. It's goal is to be useable with extremely large json
streams, and everything needs to happen streaming.

I tried several tools imlemented in python (python -mjson.tool,
'jsongrep'), but those consumed very much memory when I fead them a
json stream of a Gigabyte or so, and seemed not useable for that.

All tools support a -help argument for an overview of all supported options.


Formatter
--------
A binary can be downloaded [here](https://github.com/mihxil/mvn-repo/raw/master/releases/org/meeuw/mihxil-json/0.3/mihxil-json-0.3-formatter.jar)


Usage
```
java -jar ~/Download/mihxil-json-0.3-formatter.jar [<infile>] [<outfile>]

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
A binary can be downloaded [here](https://github.com/mihxil/mvn-repo/raw/master/releases/org/meeuw/mihxil-json/0.3/mihxil-json-0.3-grep.jar)

This is a streaming 'jsongrep', and works a bit like grep. It e.g. can be used to produce one line abstracts of the records which can easily be processed further by a normal grep or awk or so.

Example
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | java -jar target/mihxil-json-0.3-SNAPSHOT-grep.jar  y.arr.*.e,a
a=b
y.arr.1.e=z
```


Another example on a couchdb database (find documents where certain field has certain value)
```sh
$ jsongrep rows.*.doc.workflow=FOR_REPUBLICATION,rows.*.doc.mid  http://couchdbhost/database/_all_docs?include_docs=true  | grep -A 1 workflow 
```

I hope this also explains how the grep expression works. Currently
only precise and * matches on the keys work, but it would be simple to think up
some more. Also it might be usefull to not need to specify the full path.
