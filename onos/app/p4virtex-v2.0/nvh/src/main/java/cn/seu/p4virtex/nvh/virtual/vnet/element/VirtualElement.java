package cn.seu.p4virtex.nvh.virtual.vnet.element;

/**
 * 虚拟元素的通用属性
 * 1. 虚拟网络ID
 *
 */
public interface VirtualElement {

    NetworkId networkId();

    void setNetworkId(NetworkId networkId);

}
