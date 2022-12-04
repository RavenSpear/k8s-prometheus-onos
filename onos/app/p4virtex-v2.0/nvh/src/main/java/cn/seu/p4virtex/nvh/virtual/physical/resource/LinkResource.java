package cn.seu.p4virtex.nvh.virtual.physical.resource;

import org.onosproject.net.Link;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * 定义数据链路的资源属性
 * 1. 最大带宽
 * 2. 传输延迟
 */
public class LinkResource {

    private Link link;

    // 原始属性
    private Integer bandwidth;
    private Double latency;


    private Integer remainingBandwidth; // 剩余链路带宽
    private Integer allocatedBandwidth; // 已分配的链路带宽

    public LinkResource(Link link) {
        this.link = link;
    }

    public LinkResource(Link link, Integer bandwidth){
        this.link = link;
        this.bandwidth = bandwidth;
        this.remainingBandwidth = bandwidth;
        this.allocatedBandwidth = 0;
    }

    public LinkResource(Link link, Integer bandwidth, Double latency) {
        this.link = link;
        this.bandwidth = bandwidth;
        this.latency = latency;
        this.remainingBandwidth = bandwidth;
        this.allocatedBandwidth = 0;
    }

    /**
     * 带宽消耗，更新剩余链路带宽值
     */
    public void consumeBW(Integer costBandwidth) {
        this.allocatedBandwidth += costBandwidth;
        this.remainingBandwidth = this.remainingBandwidth - costBandwidth;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public double getLatency() {
        return latency;
    }

    public void setLatency(double latency) {
        this.latency = latency;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Integer getRemainingBandwidth() {
        return remainingBandwidth;
    }

    public Integer getAllocatedBandwidth() {
        return allocatedBandwidth;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("link", this.link.src().deviceId() + "->" + this.link.dst().deviceId())
                .add("bandwidth", this.bandwidth)
                .add("remainingBandwidth", this.remainingBandwidth)
                .add("allocatedBandwidth", this.allocatedBandwidth)
                .toString();
    }
}
