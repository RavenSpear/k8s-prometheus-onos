package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.traffic.Traffic;
import cn.seu.p4virtex.nvh.virtual.traffic.VnetTraffic;

public interface VnetTrafficStore {


    void addVnetTraffic(VnetTraffic vnetTraffic);

    /**
     * 修改业务流量所属的虚拟网络
     *
     * @param traffic
     * @param newVnetId
     */
    void modifyVnet(Traffic traffic, Long newVnetId);



    void restart(VnetTraffic vnetTraffic);

    VnetTraffic getVnetTrafficInfo(Traffic traffic);


}
