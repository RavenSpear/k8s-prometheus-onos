package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.vnet.element.NetworkId;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import com.google.common.collect.ImmutableList;
import org.onosproject.core.CoreService;
import org.onosproject.core.IdGenerator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(immediate = true, service = VirtualNetworkStore.class)
public class SimpleVirtualNetworkStore implements VirtualNetworkStore {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String VNET_ID_TOPIC = "vnet-ids-topic";
    private IdGenerator vnetIdGenerator;

    // 存储虚拟网络所有的对象，包括虚拟交换机、虚拟端口、虚拟链路、虚拟主机
    private Map<NetworkId, VirtualNetwork> networkIdVirtualNetworkMap;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Activate
    public void activate() {
        vnetIdGenerator = coreService.getIdGenerator(VNET_ID_TOPIC);
        networkIdVirtualNetworkMap = new HashMap<>();
    }

    @Deactivate
    public void deactivate() {
    }

    /**
     * 保存虚拟网络对象
     * 1. 正式为该虚拟网络对象分配虚拟网络ID
     * 2. 更新虚拟网络内每个元素的虚拟网络ID属性
     *
     * @param virtualNetwork
     */
    @Override
    public void addVirtualNetwork(VirtualNetwork virtualNetwork) {
        // 分配虚拟网络ID
        Long id = vnetIdGenerator.getNewId() + 1;
        NetworkId networkId = NetworkId.networkId(id);
        virtualNetwork.setId(networkId);
        virtualNetwork.setCreateTime(new Date());
        // 更新该虚拟网络内所有虚拟网络元素的属性
        virtualNetwork.getDevices().forEach(device -> {
            device.setNetworkId(networkId);
        });
        virtualNetwork.getPorts().forEach(port -> {
            port.setNetworkId(networkId);
        });
        virtualNetwork.getLinks().forEach(link -> {
            link.setNetworkId(networkId);
        });
        virtualNetwork.getHosts().forEach(host -> {
            host.setNetworkId(networkId);
        });
        networkIdVirtualNetworkMap.put(networkId, virtualNetwork);
    }

    @Override
    public List<VirtualNetwork> getVirtualNetworks() {
        return new ArrayList<>(networkIdVirtualNetworkMap.values());
    }

    @Override
    public VirtualNetwork getVirtualNetworkById(Long id) {
        return this.networkIdVirtualNetworkMap.get(NetworkId.networkId(id));
    }
}
