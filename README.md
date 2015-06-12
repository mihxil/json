[![Build Status](https://travis-ci.org/mihxil/json.png?)](https://travis-ci.org/mihxil/json)

Json Tools
==========

This is provides several JSON-related tools implemented with
Jackson. Its goal is to be useable with extremely large json
streams, and everything needs to happen streaming.

I tried several tools imlemented in python (python -m json.tool,
'jsongrep'), but those consumed very much memory when I fed them a
json stream of a Gigabyte or so, and seemed not useable for that, so I implemented similar tools based on
jackson2 in java. They are streaming and don't need much memory, and can deal with huge streams of json.

All tools support a -help argument for an overview of all supported options.

Download
--------
The executable jars are packaged in a zip, which can be downloaded
[here](https://github.com/mihxil/mvn-repo/raw/master/releases/org/meeuw/mihxil-json/0.5/mihxil-json-0.5-package.zip).

This zip also contain executable scripts to call them with `java -jar`, which will work in a unix or osx environment, and can be unzipped somewhere in your path.


Formatter
--------
Usage
```
jsonformat [<infile>] [<outfile>]

infile: defaults to stdin (can explicitely set to stdin as '-'). Can
        be file name but  can also be a remote URL
outfile: default to stdout
```

For a file of nearly one Gb:
```shell
michiel@belono:/tmp$ time jsonformat alldocs.json  alldocs.formatted.json

real	0m27.783s
user	0m19.880s
sys	0m5.686s

michiel@belono:/tmp$ ls -lah alldocs.*
-rw-rw-r--  1 michiel  wheel   1.3G Feb 22 18:17 alldocs.formatted.json
-rw-rw-r--  1 michiel  wheel   928M Feb 22 14:19 alldocs.json
```


Grep
----
This is a streaming 'jsongrep', and works a bit like grep. It e.g. can be used to produce one line abstracts of the records which can easily be processed further by a normal grep or awk or so.



Example
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.arr[1].e
y.arr[1].e=z
```

It is possible to specify more then one match
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.arr[1].e,a
a=b
y.arr[1].e=z
```

You can use wildcards in the path:
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.arr[*].e
y.arr[1].e=z
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.*[*].d
y.arr[1].d=z
```

If it does not match a value but an object or an array, it will be reported like this:
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.arr,y
y.arr=[...]
y={...}
```

Unless you specify a different output format:
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep -output PATHANDFULLVALUE y.arr,y
y.arr=[{"d":"y"},{"e":"z"}]
y={"c":"x","arr":[{"d":"y"},{"e":"z"}]}
```


It is also possible to match certain values:
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.arr[*].*=z
y.arr[1].e=z
```

That can also be done using regular expressions
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  y.arr[*].*~[xz]
y.arr[1].e=z
```

You can find objects missing a certain key
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  'y.arr[*] ! contains d'
y.arr[1]={...}
```

You can match directly inside the tree ('...' means 'an abitrary path)
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  '...e'
y.arr[1].e=z
```

It's possible to match on object containing a certain key:
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  '...arr[*] contains d'
y.arr[0]={...}
```
or the inverse
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep  '...arr[*] ! contains d'
y.arr[1]={...}
```

Matching can be implemented with a javascript function as well:
```sh
$ echo "{a:'b', y: {c:'x', arr:[{d:'y'}, {e:'z'}]}}"  | jsongrep -output KEYANDFULLVALUE '...arr[*] function(doc) { return doc.d == "y"; }'
[0]={"d":"y"}
```


It can also accept a second optional parameter which is a file or an URL:
```sh
$ jsongrep  y.arr[*].*~[xz] test.json
y.arr[1].e=z
```

It is possible to output less
```sh
$ jsongrep  -output VALUE  y.arr[*].*~[xz] test.json
z
$ jsongrep  -output KEY  y.arr[*].*~[xz] test.json
e
$ jsongrep  -output PATH  y.arr[*].*~[xz] test.json
y.arr[1].e
$ jsongrep  -output KEYANDVALUE  y.arr[*].*~[xz] test.json
e=z
```

Another example on a couchdb database (find documents where certain field has certain value)
```sh
$ jsongrep rows.*.doc.workflow=FOR_REPUBLICATION,rows.*.doc.mid  http://couchdbhost/database/_all_docs?include_docs=true  |
                grep -A 1 workflow
```
separators
----------
jsongrep supports the '-sep', '-recordsep' and '-record' parameters. They are intended for example to generate one line abstracts of a bunch of json records.
E.g. create a file with 3 fields per line, separated by a tab. The 3 fields are 3 different keys from an array of json objects.
```sh
$ jsongrep -output VALUE -sep "     "  -record '*' '*.mid,*.publishDate,*.lastModified'  es.all.json  | sort > es.txt
```
The -record parameter defines what constitutes the start of a new record. If this matches a 'recordsep' will be outputted (this defaults to a newline).
Normally between matches a newline is outputted, but when you use -record you'd probably don't want that. In this case with -sep a tab is outputted. 


TODO
----
Currently only precise and * matches on the keys are implemented, but it would be simple to think up
some other matches. E.g. regular expression matching in keys too.

When using separators with jsongrep it would be nice if you could sort the resulting record, and perhaps also fill in the not found keys with e.g. an empty string.
