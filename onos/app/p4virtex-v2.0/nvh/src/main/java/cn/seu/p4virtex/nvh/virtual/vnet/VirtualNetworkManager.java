package cn.seu.p4virtex.nvh.virtual.vnet;

import cn.seu.p4virtex.nvh.store.VirtualNetworkStore;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


@Component(immediate = true, service = VirtualNetworkService.class)
public class VirtualNetworkManager implements VirtualNetworkService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected VirtualNetworkStore vnetStore;

    @Activate
    public void activate() {
        log.info("启动虚拟网络管理...");
    }

    @Deactivate
    public void deactivate() {
        log.info("关闭虚拟网络管理...");
    }

    @Override
    public List<VirtualNetwork> getVirtualNetworks() {
        return vnetStore.getVirtualNetworks();
    }

    @Override
    public void addVirtualNetwork(VirtualNetwork virtualNetwork) {
        vnetStore.addVirtualNetwork(virtualNetwork);
    }

    @Override
    public VirtualNetwork getVirtualNetwork(Long id) {
        return vnetStore.getVirtualNetworkById(id);
    }
}
