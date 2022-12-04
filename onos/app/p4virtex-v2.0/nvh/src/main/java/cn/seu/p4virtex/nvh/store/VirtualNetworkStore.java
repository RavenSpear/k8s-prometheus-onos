package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;

import java.util.List;

public interface VirtualNetworkStore {

    void addVirtualNetwork(VirtualNetwork virtualNetwork);

    List<VirtualNetwork> getVirtualNetworks();

    VirtualNetwork getVirtualNetworkById(Long id);
}
