#!/usr/bin/env python # encoding: utf-8

from thrift import Thrift
from thrift.transport import TTransport
from thrift.transport import TSocket
from thrift.protocol import TBinaryProtocol

from service import *
from service.ttypes import *

class GraphWalker:

    def __init__(self, host, port):
        # create connection with the server
        self.socket = TSocket.TSocket(host, port)
        self.transport = TTransport.TBufferedTransport(self.socket)
        self.protocol = TBinaryProtocol.TBinaryProtocol(self.transport)
        self.client = GraphWalkerService.Client(self.protocol)
        self.transport.open()

    def __getattr__(self, name):
        return getattr(self.client, name)

    def close(self):
        self.transport.close()