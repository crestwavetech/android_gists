import http.server
import argparse
import time
import os
import ast

parser = argparse.ArgumentParser()
parser.add_argument("port", type=int, nargs='?', default=8000)
parser.add_argument("delay", type=float, nargs='?', default=0)
args = parser.parse_args()

class ServerHandler(http.server.SimpleHTTPRequestHandler):

    def do_GET(self):
      # alternative: skip `super().do_GET()` to cause `IOException: unexpected end of stream`
      time.sleep(self.get_delay())
      super().do_GET()

    def do_POST(self):
      self.do_GET()
      content_len = int(self.headers.get('content-length', 0))
      post_body = self.rfile.read(content_len)
      print(post_body)

    def get_delay(self):
      try:
        with open("_delays.json", 'r') as file:
          data = ast.literal_eval(file.read())
          stripped_path = self.path[1:].partition('?')[0]
          return float(data[stripped_path])
      except (FileNotFoundError, ValueError, KeyError, SyntaxError):
        return args.delay


server = http.server.ThreadingHTTPServer(("", args.port), ServerHandler)

print("serving at port", args.port)
server.serve_forever()


# CHANGELOG:
# 2021.03.17: ignore query params when calculating delay

