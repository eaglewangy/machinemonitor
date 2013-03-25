import string,cgi,time, urllib.parse
from os import curdir, sep
from http.server import BaseHTTPRequestHandler, HTTPServer
#import pri

class MyHandler(BaseHTTPRequestHandler):
	# our servers current set ip address that clients should look for.
	server_address = "192.168.1.10"
	def do_GET(self): 
		qs = {}
		path = self.path
		if '?' in path:
			path, tmp = path.split('?', 1)
			qs = urllib.parse.parse_qs(tmp)
			MyHandler.server_address = qs['up_address']
			print('new server address: ', MyHandler.server_address)


def main():
    try:
        server = HTTPServer(('', 8080), MyHandler)
        print('started httpserver...')
        server.serve_forever()
    except KeyboardInterrupt:
        print('^C received, shutting down server')
        server.socket.close()

if __name__ == '__main__':
    main()