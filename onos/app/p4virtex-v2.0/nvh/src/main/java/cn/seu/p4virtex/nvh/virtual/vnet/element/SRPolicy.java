package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onosproject.net.DeviceId;
import org.onosproject.net.Path;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * sr隧道(策略)
 */
public class SRPolicy {

    private Long networkId;// sr隧道所属虚拟网络
    private Long srPolicyId;// sr隧道id

    private DeviceId src;// sr隧道起点
    private DeviceId dst;// sr隧道终点

    private List<DeviceId> sids;// sr隧道路径（包含起点和终点）

    private Integer allocatedBandwidth; // 分配的可使用的最大带宽

    public SRPolicy() {
        this.sids = new ArrayList<>();
    }

    public SRPolicy(List<DeviceId> sids, Integer allocatedBandwidth) {
        this.sids = sids;
        this.allocatedBandwidth = allocatedBandwidth;
    }

    public Long getNetworkId() {
        return networkId;
    }

    public void setNetworkId(Long networkId) {
        this.networkId = networkId;
    }

    public Long getSrPolicyId() {
        return srPolicyId;
    }

    public void setSrPolicyId(Long srPolicyId) {
        this.srPolicyId = srPolicyId;
    }

    public DeviceId getSrc() {
        return src;
    }

    public void setSrc(DeviceId src) {
        this.src = src;
    }

    public DeviceId getDst() {
        return dst;
    }

    public void setDst(DeviceId dst) {
        this.dst = dst;
    }

    public List<DeviceId> getSids() {
        return sids;
    }

    public void setSids(List<DeviceId> sids) {
        this.sids = sids;
    }

    public Integer getAllocatedBandwidth() {
        return allocatedBandwidth;
    }

    public void setAllocatedBandwidth(Integer allocatedBandwidth) {
        this.allocatedBandwidth = allocatedBandwidth;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("sids", sids.toString())
                .add("allocatedBandwidth", allocatedBandwidth)
                .toString();
    }
}
