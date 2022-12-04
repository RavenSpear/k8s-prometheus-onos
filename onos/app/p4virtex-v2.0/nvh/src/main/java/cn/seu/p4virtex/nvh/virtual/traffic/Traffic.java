package cn.seu.p4virtex.nvh.virtual.traffic;

import org.onlab.packet.Ip4Address;
import org.onlab.packet.TpPort;
import org.onosproject.cli.net.IpProtocol;

import java.util.Objects;

public class Traffic {

    private Ip4Address srcIp;
    private Ip4Address dstIp;
    private IpProtocol ipProtocol;
    private TpPort srcPort;
    private TpPort dstPort;

    public Traffic() {
    }

    public Traffic(String srcIp, String dstIp, String ipProtocol, int srcPort, int dstPort) {
        this.srcIp = Ip4Address.valueOf(srcIp);
        this.dstIp = Ip4Address.valueOf(dstIp);
        this.ipProtocol = IpProtocol.valueOf(ipProtocol);
        this.srcPort = TpPort.tpPort(srcPort);
        this.dstPort = TpPort.tpPort(dstPort);
    }

    public Ip4Address getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(Ip4Address srcIp) {
        this.srcIp = srcIp;
    }

    public Ip4Address getDstIp() {
        return dstIp;
    }

    public void setDstIp(Ip4Address dstIp) {
        this.dstIp = dstIp;
    }

    public IpProtocol getIpProtocol() {
        return ipProtocol;
    }

    public void setIpProtocol(IpProtocol ipProtocol) {
        this.ipProtocol = ipProtocol;
    }

    public TpPort getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(TpPort srcPort) {
        this.srcPort = srcPort;
    }

    public TpPort getDstPort() {
        return dstPort;
    }

    public void setDstPort(TpPort dstPort) {
        this.dstPort = dstPort;
    }

    public Traffic reverse() {
        Traffic traffic = new Traffic();
        traffic.setSrcIp(this.dstIp);
        traffic.setDstIp(this.srcIp);
        traffic.setIpProtocol(this.ipProtocol);
        traffic.setSrcPort(this.dstPort);
        traffic.setDstPort(this.srcPort);
        return traffic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Traffic)) return false;
        Traffic traffic = (Traffic) o;
        return Objects.equals(srcIp, traffic.srcIp) && Objects.equals(dstIp, traffic.dstIp) && ipProtocol == traffic.ipProtocol && Objects.equals(srcPort, traffic.srcPort) && Objects.equals(dstPort, traffic.dstPort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcIp, dstIp, ipProtocol, srcPort, dstPort);
    }
}
