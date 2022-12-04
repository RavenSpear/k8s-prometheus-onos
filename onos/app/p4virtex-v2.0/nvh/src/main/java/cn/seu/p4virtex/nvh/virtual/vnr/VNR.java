package cn.seu.p4virtex.nvh.virtual.vnr;

import org.onlab.packet.IpAddress;

import java.util.Date;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * 虚拟网络请求对象
 */
public class VNR {

    private Long id; // 请求编号
    private String vnetName; // 生成的虚拟网络名称

    private Integer bandwidth;
    private Integer latency;
    private IpAddress cluster1IP;
    private IpAddress cluster2IP;

    private Integer status; // 0-队列中 1-已嵌入 2-拒绝

    private Date createTime; // 创建时间，也是进入请求队列的时间
    private Date embeddingTime; // 成功嵌入时间
    private Date rejectTime; // 拒绝嵌入时间

    private Long vnetId; // 若映射成功，其对应的虚拟网络ID

    public VNR(Integer bandwidth, Integer latency, IpAddress cluster1IP, IpAddress cluster2IP) {
        this.bandwidth = bandwidth;
        this.latency = latency;
        this.cluster1IP = cluster1IP;
        this.cluster2IP = cluster2IP;

        // 默认初始配置
        this.createTime = new Date();
        this.status = 0;
    }

    public String getVnetName() {
        return vnetName;
    }

    public void setVnetName(String vnetName) {
        this.vnetName = vnetName;
    }

    public Integer getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    public Integer getLatency() {
        return latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    public IpAddress getCluster1IP() {
        return cluster1IP;
    }

    public void setCluster1IP(IpAddress cluster1IP) {
        this.cluster1IP = cluster1IP;
    }

    public IpAddress getCluster2IP() {
        return cluster2IP;
    }

    public void setCluster2IP(IpAddress cluster2IP) {
        this.cluster2IP = cluster2IP;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEmbeddingTime() {
        return embeddingTime;
    }

    public void setEmbeddingTime(Date embeddingTime) {
        this.embeddingTime = embeddingTime;
    }

    public Date getRejectTime() {
        return rejectTime;
    }

    public void setRejectTime(Date rejectTime) {
        this.rejectTime = rejectTime;
    }

    public Long getVnetId() {
        return vnetId;
    }

    public void setVnetId(Long vnetId) {
        this.vnetId = vnetId;
    }

    @Override
    public String toString() {

        return toStringHelper(this)
                .add("bandwidth", bandwidth)
                .add("latency",latency)
                .add("cluster1IP",cluster1IP)
                .add("cluster2IP",cluster2IP)
                .add("createTime",createTime.getTime())
                .toString();
    }
}
