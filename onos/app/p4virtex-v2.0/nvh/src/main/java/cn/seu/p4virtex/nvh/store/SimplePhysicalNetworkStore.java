package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.physical.resource.DeviceResource;
import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import com.google.common.collect.ImmutableSet;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.store.service.StorageService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.concurrent.Immutable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 物理网络资源存储
 */
@Component(immediate = true, service = PhysicalNetworkStore.class)
public class SimplePhysicalNetworkStore implements PhysicalNetworkStore {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected StorageService storageService;

    private Map<DeviceId, Integer> switchIdMap;

    private Map<DeviceId, DeviceResource> deviceResourceMap;
    private Map<Link, LinkResource> linkResourceMap;

    @Activate
    public void activate() {

        switchIdMap = new HashMap<>();
        deviceResourceMap = new HashMap<>();
        linkResourceMap = new HashMap<>();

    }

    @Deactivate
    public void deactivate() {

    }

    @Override
    public void addSwitchId(DeviceId deviceId, Integer switchId) {
        switchIdMap.put(deviceId, switchId);
    }

    @Override
    public Integer getSwitchId(DeviceId deviceId) {
        return switchIdMap.get(deviceId);
    }

    @Override
    public DeviceId getDeviceId(Integer switchId) {
        for (Map.Entry<DeviceId, Integer> entry : switchIdMap.entrySet()) {
            if (entry.getValue().equals(switchId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public void addLinkResource(LinkResource linkResource) {
        this.linkResourceMap.put(linkResource.getLink(), linkResource);
    }

    @Override
    public Set<LinkResource> getLinkResources() {
        Set<LinkResource> rs = new HashSet<>();
        for (Map.Entry<Link, LinkResource> entry : this.linkResourceMap.entrySet()) {
            rs.add(entry.getValue());
        }
        return rs;
    }

    @Override
    public LinkResource getLinkResource(Link link) {
        return this.linkResourceMap.get(link);
    }

    @Override
    public void addDeviceResource(DeviceResource deviceResource) {

    }

    @Override
    public Set<DeviceResource> getDeviceResource() {
        return null;
    }


}
