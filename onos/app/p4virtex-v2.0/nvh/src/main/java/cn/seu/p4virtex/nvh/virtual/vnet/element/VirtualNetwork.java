package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onosproject.net.DeviceId;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.MoreObjects.toStringHelper;


/**
 * 虚拟网络对象
 */
public class VirtualNetwork {

    private NetworkId id;
    private String vnetName; //虚拟网络名称

    private Set<VirtualDevice> devices;
    private Set<VirtualPort> ports;
    private Set<VirtualLink> links;
    private Set<VirtualHost> hosts;

    private Date createTime;
    private String desc;

    private Long vnrId; // 其对应的虚拟网络请求编号

    public VirtualNetwork() {
        this.devices = new HashSet<>();
        this.ports = new HashSet<>();
        this.links = new HashSet<>();
        this.hosts = new HashSet<>();
    }

    public String getVnetName() {
        return vnetName;
    }

    public void setVnetName(String vnetName) {
        this.vnetName = vnetName;
    }

    public NetworkId getId() {
        return id;
    }

    public void setId(NetworkId id) {
        this.id = id;
    }

    public Set<VirtualDevice> getDevices() {
        return devices;
    }

    public void setDevices(Set<VirtualDevice> devices) {
        this.devices = devices;
    }

    public Set<VirtualPort> getPorts() {
        return ports;
    }

    public void setPorts(Set<VirtualPort> ports) {
        this.ports = ports;
    }

    public Set<VirtualLink> getLinks() {
        return links;
    }

    public void setLinks(Set<VirtualLink> links) {
        this.links = links;
    }

    public Set<VirtualHost> getHosts() {
        return hosts;
    }

    public void setHosts(Set<VirtualHost> hosts) {
        this.hosts = hosts;
    }

    public Long getVnrId() {
        return vnrId;
    }

    public void setVnrId(Long vnrId) {
        this.vnrId = vnrId;
    }

    public void addVirtualDevice(VirtualDevice device) {
        this.devices.add(device);
    }

    public void addVirtualPort(VirtualPort port) {
        this.ports.add(port);
    }

    public void addVirtualLink(VirtualLink link) {
        this.links.add(link);
    }

    public void addVirtualHost(VirtualHost host) {
        this.hosts.add(host);
    }

    public VirtualDevice getVirtualDevice(DeviceId deviceId) {
        for (VirtualDevice device : this.getDevices()) {
            if (device.id().equals(deviceId)) {
                return device;
            }
        }
        return null;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("devices", this.getDevices())
                .add("ports",this.getPorts())
                .add("links",this.getLinks())
                .add("hosts",this.getHosts())
                .toString();
    }
}
