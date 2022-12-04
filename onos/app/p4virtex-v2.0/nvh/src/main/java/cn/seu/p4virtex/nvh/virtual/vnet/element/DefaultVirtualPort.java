package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onosproject.net.*;

import static com.google.common.base.MoreObjects.toStringHelper;


public class DefaultVirtualPort extends DefaultPort implements VirtualPort {

    private NetworkId networkId;

    public DefaultVirtualPort(Device device, PortNumber portNumber) {
        super(device, portNumber, true, DefaultAnnotations.builder().build());
        this.networkId = NetworkId.NONE;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("deviceId", this.element().id())
                .add("portNumber", this.number())
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
