package cn.seu.p4virtex.nvh.virtual.vnet;

import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;

import java.util.List;

public interface VirtualNetworkService {

    List<VirtualNetwork> getVirtualNetworks();

    void addVirtualNetwork(VirtualNetwork virtualNetwork);

    VirtualNetwork getVirtualNetwork(Long id);
}
