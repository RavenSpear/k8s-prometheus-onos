package cn.seu.p4virtex.nvh.virtual.physical;

import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;

import java.util.Set;

public interface PhysicalNetworkService {

    /**
     * 初始化物理网络资源
     */
    void initPhysicalResource();

    Integer getSwitchId(DeviceId deviceId);

    /**
     * 根据设备编号，获取对应的deviceId获取对应的deviceId
     * @param switchId
     * @return
     */
    DeviceId getDeviceId(Integer switchId);

    /**
     * 获取网络节点资源
     */

    /**
     * 获取所有的数据链路资源
     */
    Set<LinkResource> getLinkResources();


    /**
     * 消耗链路带宽资源
     */
    void consumeLinkBW(Link link, Integer consumedBW);

}
