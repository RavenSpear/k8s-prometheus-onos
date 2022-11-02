import os, sys, argparse, subprocess
import json

from mininet.cli import CLI
from mininet.log import setLogLevel
from mininet.net import Mininet
from mininet.topo import Topo
from mininet.link import TCLink, Intf
from mininet.node import Node, Switch
import random

from bmv2 import ONOSBmv2Switch, ONOSHost
from stratum import StratumBmv2Switch


CPU_PORT = 255


SWITCH_NUMBER = 1
netcfg_json = dict()
netcfg_json["devices"] = dict()


class MyTopo(Topo):

    def __init__( self, *args, **params ):
        """Topo object.
            Optional named parameters:
            hinfo: default host options
            sopts: default switch options
            lopts: default link options
            calls build()"""
        self.g = MultiGraph()
        self.hopts = params.pop( 'hopts', {} )
        self.sopts = params.pop( 'sopts', {} )
        self.lopts = params.pop( 'lopts', {} )
        # ports[src][dst][sport] is port on dst that connects to src
        self.ports = {}
        self.build( *args, **params )

    def addSwitch( self, name, **opts ):
        """Convenience method: Add switch to graph.
           name: switch name
           opts: switch options
           returns: switch name"""
        if not opts and self.sopts:
            opts = self.sopts
        result = self.addNode( name, isSwitch=True, **opts )

        global SWITCH_NUMBER
        global netcfg_json
        device_json_name = "device:s" + str(SWITCH_NUMBER)
        grpc_port = 50000 + SWITCH_NUMBER
        grpc_url = "grpc://127.0.0.1:"+str(grpc_port)+"?device_id=1"
        driver_name  = "stratum-bmv2"
        pipeconf_name = "org.onosproject.pipelines.basic"
        netcfg_json["devices"][device_json_name] = dict()
        netcfg_json["devices"][device_json_name]["basic"] = dict()
        netcfg_json["devices"][device_json_name]["basic"]["managementAddress"] = grpc_url
        netcfg_json["devices"][device_json_name]["basic"]["driver"] = driver_name
        netcfg_json["devices"][device_json_name]["basic"]["pipeconf"] = pipeconf_name

        SWITCH_NUMBER = SWITCH_NUMBER + 1

        return result

    def addLink( self, node1, node2, port1=None, port2=None,
                 key=None, **opts ):
        """node1, node2: nodes to link together
           port1, port2: ports (optional)
           opts: link options (optional)
           returns: link info key"""
        if not opts and self.lopts:
            opts = self.lopts
        port1, port2 = self.addPort( node1, node2, port1, port2 )
        opts = dict( opts )
        opts.update( node1=node1, node2=node2, port1=port1, port2=port2 )

        global SWITCH_NUMBER
        global netcfg_json
        
        if node1[0] == 's':
            switch_name = "device:"+node1
            port_name = node1+"-eth"+ str(port1)

            if netcfg_json["devices"][switch_name].has_key("ports") == False:
                netcfg_json["devices"][switch_name]["ports"]= dict()
            if netcfg_json["devices"][switch_name]["ports"].has_key(str(port1)) == False:
                netcfg_json["devices"][switch_name]["ports"][str(port1)] = dict()

            netcfg_json["devices"][switch_name]["ports"][str(port1)]["name"] = port_name
            netcfg_json["devices"][switch_name]["ports"][str(port1)]["number"] = port1
            netcfg_json["devices"][switch_name]["ports"][str(port1)]["enabled"] = True
            netcfg_json["devices"][switch_name]["ports"][str(port1)]["removed"] = False
            netcfg_json["devices"][switch_name]["ports"][str(port1)]["type"] = "copper"
            netcfg_json["devices"][switch_name]["ports"][str(port1)]["speed"] = opts["bw"]*1000

        if node2[0] == 's':
            switch_name = "device:"+node2
            port_name = node2+"-"+"eth"+str(port2)

            if netcfg_json["devices"][switch_name].has_key("ports") == False:
                netcfg_json["devices"][switch_name]["ports"]= dict()

            if netcfg_json["devices"][switch_name]["ports"].has_key(str(port2)) == False:
                netcfg_json["devices"][switch_name]["ports"][str(port2)] = dict()

            netcfg_json["devices"][switch_name]["ports"][str(port2)]["name"] = port_name
            netcfg_json["devices"][switch_name]["ports"][str(port2)]["number"] = port2
            netcfg_json["devices"][switch_name]["ports"][str(port2)]["enabled"] = True
            netcfg_json["devices"][switch_name]["ports"][str(port2)]["removed"] = False
            netcfg_json["devices"][switch_name]["ports"][str(port2)]["type"] = "copper"
            netcfg_json["devices"][switch_name]["ports"][str(port2)]["speed"] = opts["bw"]*1000

        return self.g.add_edge(node1, node2, key, opts )



class TestTopo(Topo):
    def __init__(self, *args, **kwargs):
        Topo.__init__(self, *args, **kwargs)
        s1 = self.addSwitch('s1', cls=ONOSBmv2Switch,cpuport=CPU_PORT)
        s2 = self.addSwitch('s2', cls=ONOSBmv2Switch,cpuport=CPU_PORT)
        h1 = self.addHost('h1',cls=ONOSHost)
        h2 = self.addHost('h2',cls=ONOSHost)
        self.addLink(h1,s1,cls=TCLink,bw=1)
        self.addLink(h2,s2,cls=TCLink,bw=1)
        self.addLink(s1,s2,cls=TCLink,bw=1)

class MultiPathTestTopo(MyTopo):
    def __init__(self, *args, **kwargs):
        Topo.__init__(self, *args, **kwargs)
        s1 = self.addSwitch('s1', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s2 = self.addSwitch('s2', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s3 = self.addSwitch('s3', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s4 = self.addSwitch('s4', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s5 = self.addSwitch('s5', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s6 = self.addSwitch('s6', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s7 = self.addSwitch('s7', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s8 = self.addSwitch('s8', cls=StratumBmv2Switch,cpuport=CPU_PORT)

        h1a = self.addHost('h1a',cls=ONOSHost,mac="")
        h1b = self.addHost('h1b',cls=ONOSHost)
        h1c = self.addHost('h1c',cls=ONOSHost)
        h2a = self.addHost('h2a',cls=ONOSHost)
        h2b = self.addHost('h2b',cls=ONOSHost)
        h2c = self.addHost('h2c',cls=ONOSHost)

        self.addLink(h1a,s1,cls=TCLink,bw=50)
        self.addLink(h1b,s1,cls=TCLink,bw=50)
        self.addLink(h1c,s1,cls=TCLink,bw=50)

        self.addLink(h2a,s8,cls=TCLink,bw=50)
        self.addLink(h2b,s8,cls=TCLink,bw=50)
        self.addLink(h2c,s8,cls=TCLink,bw=50)

        # path1: s1->s2->s8 10Mbps
        self.addLink(s1,s2,cls=TCLink,bw=10)
        self.addLink(s2,s8,cls=TCLink,bw=10)

        # path2: s1->s3->s4->s8 8Mbps
        self.addLink(s1,s3,cls=TCLink,bw=8)
        self.addLink(s3,s4,cls=TCLink,bw=8)
        self.addLink(s4,s8,cls=TCLink,bw=8)

        # path3: s1->s5->s6->s7->s8 6Mbps
        self.addLink(s1,s5,cls=TCLink,bw=6)
        self.addLink(s5,s6,cls=TCLink,bw=6)
        self.addLink(s6,s7,cls=TCLink,bw=6)
        self.addLink(s7,s8,cls=TCLink,bw=6)



class TestTopo3(MyTopo):
    def __init__(self, *args, **kwargs):
        Topo.__init__(self, *args, **kwargs)
        s1 = self.addSwitch('s1', cls=StratumBmv2Switch,cpuport=CPU_PORT)

        s2 = self.addSwitch('s2', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s3 = self.addSwitch('s3', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s4 = self.addSwitch('s4', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s5 = self.addSwitch('s5', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s6 = self.addSwitch('s6', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s7 = self.addSwitch('s7', cls=StratumBmv2Switch,cpuport=CPU_PORT)
        s8 = self.addSwitch('s8', cls=StratumBmv2Switch,cpuport=CPU_PORT)


        self.addLink(s1,s2,cls=TCLink,bw=10)
        self.addLink(s2,s3,cls=TCLink,bw=8)
        self.addLink(s2,s6,cls=TCLink,bw=8)
        self.addLink(s3,s4,cls=TCLink,bw=8)
        self.addLink(s4,s5,cls=TCLink,bw=8)
        self.addLink(s5,s6,cls=TCLink,bw=8)
        self.addLink(s5,s7,cls=TCLink,bw=10)
        self.addLink(s6,s8,cls=TCLink,bw=10)   


def main():
    topo = TestTopo3()

    with open("./netcfg.json","w") as f:
        json.dump(netcfg_json,f)

    MTU = 3000
    net = Mininet(topo=topo, controller=None)
    for intf in [intf for sw in net.switches for intf in sw.intfNames()]:
        subprocess.call(['ifconfig', intf, 'mtu', str(MTU)])
    Intf("eth1",node=net.nameToNode[ "s1" ])
    Intf("eth2",node=net.nameToNode[ "s8" ])
    #Intf("veth0",node=net.nameToNode[ "s8" ])

    net.start()
    CLI(net)
    net.stop()

if __name__ == "__main__":
    setLogLevel('info')
    main()
