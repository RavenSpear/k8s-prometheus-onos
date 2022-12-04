package cn.seu.p4virtex.nvh.virtual.vne;

import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;

public interface VNEService {

    /**
     * 嵌入虚拟网络服务
     * @param virtualNetwork
     * @return
     */
    boolean embed(VirtualNetwork virtualNetwork);
}
