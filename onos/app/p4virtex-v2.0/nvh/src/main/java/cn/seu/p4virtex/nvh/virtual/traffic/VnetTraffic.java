package cn.seu.p4virtex.nvh.virtual.traffic;

import org.onlab.packet.IpAddress;
import org.onosproject.cli.net.IpProtocol;

/**
 * 网络服务对象
 */
public class VnetTraffic {

    private String serviceName;
    private Long vnetId;
    private Traffic traffic;
    private Integer status; // 0-表示未注册，1-已运行

    public VnetTraffic(String serviceName, Long vnetId, Traffic traffic) {
        this.serviceName = serviceName;
        this.vnetId = vnetId;
        this.traffic = traffic;
        this.status = 1;
    }

    public Traffic getTraffic() {
        return traffic;
    }

    public void setTraffic(Traffic traffic) {
        this.traffic = traffic;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getVnetId() {
        return vnetId;
    }

    public void setVnetId(Long vnetId) {
        this.vnetId = vnetId;
    }

}
