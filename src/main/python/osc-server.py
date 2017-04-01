from pythonosc import dispatcher
from pythonosc import osc_server


def handler(addr, arg, data):
    print(addr, arg, data)


dispatcher = dispatcher.Dispatcher()
dispatcher.map("/filter",
print)

server = osc_server.ThreadingOSCUDPServer(("192.168.0.14", 5000), dispatcher)
print("Serving on {}".format(server.server_address))
server.serve_forever()
