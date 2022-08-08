# GetPostServer

Simple file-base server on Python to provide mock network responses.

### Features:
* Handles (identically) both GET and POST requests.
* Responses can be changed on-the-fly by editing the files, no need to re-launch client app or server.
* Response delay can be specified for each file, for granular testing app behavior in slow network conditions. Delays can be changed on-the-fly as well.
* Allows testing two types of errors: 404 (if file does not exist) and network timeout (if delay is higher than timeout setting in your app).

Query parameters and request body are ignored (just dumped to stdout).

### Usage:
1. Put your mock responses (JSON, XML...) as files into a folder.
1. *(Optional)* If you want to specify individual delays, drop "_delays.json" into the same folder and edit it according to your needs.
1. Open a terminal in that folder.
1. Run Python specifying path to GetPostServer.py and optional arguments (port number and default response delay).

Simplest case - GetPostServer.py in the same folder, port 8000, zero delay:
```
python3 GetPostServer.py
```
"Advanced" case - GetPostServer.py in the sibling folder, port 9123, default delay 2 seconds:
```
python3 ../GetPostServer/GetPostServer.py 9123 2
```

### Requirements:
Cross-platform: Linux, Windows, Mac OS.

Python 3.7+ is required for ThreadingHTTPServer.

### Credits:
Inspired by [some](https://stackabuse.com/serving-files-with-pythons-simplehttpserver-module/) [articles](https://stackoverflow.com/q/66514500) [on a similar](https://flaviocopes.com/python-http-server/) [topics](https://pythonsansar.com/creating-simple-http-server-python/).

