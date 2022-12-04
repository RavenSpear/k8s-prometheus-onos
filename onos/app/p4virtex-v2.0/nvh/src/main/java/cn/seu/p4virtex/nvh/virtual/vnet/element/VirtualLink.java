package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onosproject.net.Link;

import java.util.List;
import java.util.Set;

public interface VirtualLink extends VirtualElement,Link {

    Integer getBandwidth();

    void setBandwidth(Integer bandwidth);

    /**
     * 多路径映射
     * 每个SRPolicy代表一条映射路径
     *
     * @return 所有的映射路径
     */
    List<SRPolicy> getSRPolicyList();

    /**
     * 添加多路径映射
     *
     * @param srPolicy 代表一条映射路径，包括路径信息和带宽限制
     */
    void addSRPolicy(SRPolicy srPolicy);

}
