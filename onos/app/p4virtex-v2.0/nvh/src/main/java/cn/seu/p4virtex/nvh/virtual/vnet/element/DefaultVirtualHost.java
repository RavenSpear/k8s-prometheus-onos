package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.DefaultHost;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.provider.ProviderId;

import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;

public class DefaultVirtualHost extends DefaultHost implements VirtualHost {

    private static final String VIRTUAL = "virtual";
    private static final ProviderId PID = new ProviderId(VIRTUAL, VIRTUAL);

    private NetworkId networkId;

    public DefaultVirtualHost(HostId id, MacAddress mac,
                              VlanId vlan, HostLocation location, Set<IpAddress> ips) {
        super(PID, id, mac, vlan, location, ips, false, DefaultAnnotations.builder().build());
        this.networkId = NetworkId.NONE;
    }


    @Override
    public String toString() {
        return toStringHelper(this)
                .add("ip",this.ipAddresses())
                .toString();
    }

    @Override
    public NetworkId networkId() {
        return this.networkId;
    }

    @Override
    public void setNetworkId(NetworkId networkId) {
        this.networkId = networkId;
    }

}
