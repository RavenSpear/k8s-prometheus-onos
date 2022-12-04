package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import cn.seu.p4virtex.nvh.virtual.physical.resource.DeviceResource;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;

import java.util.Set;

public interface PhysicalNetworkStore {

    void addSwitchId(DeviceId deviceId, Integer switchId);

    Integer getSwitchId(DeviceId deviceId);

    /**
     * 根据设备编号，获取对应的deviceId获取对应的deviceId
     * @param switchId
     * @return
     */
    DeviceId getDeviceId(Integer switchId);

    void addLinkResource(LinkResource linkResource);

    Set<LinkResource> getLinkResources();

    /**
     * 获取物理链路link对应的物理链路资源对象
     *
     * @param link 物理链路link
     * @return LinkResource 物理链路资源对象
     */
    LinkResource getLinkResource(Link link);

    void addDeviceResource(DeviceResource deviceResource);

    Set<DeviceResource> getDeviceResource();
}
