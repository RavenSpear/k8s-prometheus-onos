package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onlab.packet.ChassisId;
import org.onosproject.net.DefaultDevice;
import org.onosproject.net.DeviceId;
import org.onosproject.net.provider.ProviderId;

import java.util.Objects;

import static com.google.common.base.MoreObjects.toStringHelper;

public class DefaultVirtualDevice extends DefaultDevice implements VirtualDevice {

    private static final String VIRTUAL = "virtual";
    private static final ProviderId PID = new ProviderId(VIRTUAL, VIRTUAL);

    private NetworkId networkId; // 对应的虚拟网络ID
    private DeviceId phyDeviceId; //对应的物理网络设备ID

    public DefaultVirtualDevice(DeviceId id) {
        super(PID, id, Type.VIRTUAL, VIRTUAL, VIRTUAL, VIRTUAL, VIRTUAL,
                new ChassisId(0));
        this.phyDeviceId = DeviceId.NONE;
        this.networkId = NetworkId.NONE;
    }

    @Override
    public void setRealizedBy(DeviceId deviceId) {
        this.phyDeviceId = deviceId;
    }

    @Override
    public DeviceId getRealizedBy() {
        return this.phyDeviceId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", this.id)
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
