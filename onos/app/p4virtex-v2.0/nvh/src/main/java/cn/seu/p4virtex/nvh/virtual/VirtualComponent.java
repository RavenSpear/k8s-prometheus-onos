package cn.seu.p4virtex.nvh.virtual;

import cn.seu.p4virtex.nvh.virtual.physical.PhysicalNetworkService;
import cn.seu.p4virtex.nvh.virtual.vne.VNEService;
import cn.seu.p4virtex.nvh.virtual.vnet.VirtualNetworkService;
import cn.seu.p4virtex.nvh.virtual.vnet.element.DefaultVirtualDevice;
import cn.seu.p4virtex.nvh.virtual.vnet.element.DefaultVirtualHost;
import cn.seu.p4virtex.nvh.virtual.vnet.element.DefaultVirtualLink;
import cn.seu.p4virtex.nvh.virtual.vnet.element.DefaultVirtualPort;
import cn.seu.p4virtex.nvh.virtual.vnet.element.SRPolicy;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualDevice;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualHost;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualLink;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualPort;
import cn.seu.p4virtex.nvh.virtual.vnr.VNR;
import cn.seu.p4virtex.nvh.virtual.vnr.VNRService;
import org.onlab.packet.IpAddress;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.HostId;
import org.onosproject.net.HostLocation;
import org.onosproject.net.Link;
import org.onosproject.net.PortNumber;
import org.onosproject.net.link.LinkService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * 虚拟化组件，主要执行两个功能：
 * 1. 虚拟网络映射
 * 2. 虚拟网络测量
 */
@Component(immediate = true, service = VirtualComponent.class)
public class VirtualComponent {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ScheduledExecutorService executorService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected VNRService vnrService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected VNEService vneService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected VirtualNetworkService vnetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PhysicalNetworkService physicalNetworkService;


    @Activate
    public void activate() {
        log.info("启动虚拟网络映射服务模块...");
        executorService = newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::handleRequestQueue, 3, 5, TimeUnit.SECONDS);
        log.info("开始处理请求队列...");
    }

    @Deactivate
    public void deactivate() {
        log.info("取消请求队列处理任务...");
        executorService.shutdown();
        log.info("关闭虚拟网络映射服务...");
    }

    // 处理请求队列
    private void handleRequestQueue() {
        List<VNR> vnrList = vnrService.fetchVNRQueue();
        log.info("当前接收到的vnr数量为：{}，开始处理...", vnrList.size());
        int successCount = 0;
        for (VNR vnr : vnrList) {
            // 将虚拟网络请求转为虚拟网络对象
            VirtualNetwork vnet = convertVNR2VN(vnr);
            log.info("vnet: {}", vnet);
            boolean rs = vneService.embed(vnet);
            if (rs) {// 嵌入成功
                vnetService.addVirtualNetwork(vnet); // 保存该虚拟网络
                vnr.setStatus(1); //修改虚拟网络请求状态
                // 双向绑定 请求id与虚拟网络id
                vnr.setVnetId(vnet.getId().id());
                vnet.setVnrId(vnr.getId());

                // 设置虚拟网络名称
                vnet.setVnetName(vnr.getVnetName());

                // 更新物理网络资源
                updatePhysicalNetworkResource(vnet);

                // 下发对应流表
                // TODO.

                successCount++;
            } else { // 嵌入失败
                vnr.setStatus(2); //修改虚拟网络请求状态
            }
        }

        log.info("处理结果：总请求数{}，成功嵌入数{}", vnrList.size(), successCount);
    }

    /**
     * 将虚拟网络请求对象转化为虚拟网络对象
     * 当前虚拟网络请求的网络拓扑均为端到端连接的简单网络，如下：
     * vhost1 <==> vs1 <=========> vs2 <==> vhost2
     * <p>
     * TODO. 完善虚拟网络请求转化部分，能够解析更复杂的虚拟网络请求
     *
     * @param vnr
     * @return
     */
    private VirtualNetwork convertVNR2VN(VNR vnr) {
        VirtualNetwork vnet = new VirtualNetwork();

        // 定义两个虚拟交换机 vs1和vs2
        DeviceId deviceId1 = DeviceId.deviceId("vs1");
        DeviceId deviceId2 = DeviceId.deviceId("vs2");
        VirtualDevice vs1 = new DefaultVirtualDevice(deviceId1);
        VirtualDevice vs2 = new DefaultVirtualDevice(deviceId2);

        // 定义各个交换机的虚拟端口
        PortNumber p1 = PortNumber.portNumber(1);
        PortNumber p2 = PortNumber.portNumber(2);
        VirtualPort vs1Port1 = new DefaultVirtualPort(vs1, p1);
        VirtualPort vs1Port2 = new DefaultVirtualPort(vs1, p2);
        VirtualPort vs2Port1 = new DefaultVirtualPort(vs2, p1);
        VirtualPort vs2Port2 = new DefaultVirtualPort(vs2, p2);

        ConnectPoint vs1Cp1 = new ConnectPoint(deviceId1, p1);
        ConnectPoint vs2Cp1 = new ConnectPoint(deviceId2, p1);
        ConnectPoint vs1Cp2 = new ConnectPoint(deviceId1, p2);
        ConnectPoint vs2Cp2 = new ConnectPoint(deviceId2, p2);

        // 定义两个虚拟主机
        MacAddress mac1 = MacAddress.valueOf("a4:23:05:00:00:01");
        MacAddress mac2 = MacAddress.valueOf("a4:23:05:00:00:02");
        IpAddress ip1 = vnr.getCluster1IP();
        IpAddress ip2 = vnr.getCluster2IP();
        HostId hostId1 = HostId.hostId(mac1);
        HostId hostId2 = HostId.hostId(mac2);
        HostLocation hostLocation1 = new HostLocation(vs1Cp1, 0);
        HostLocation hostLocation2 = new HostLocation(vs2Cp1, 0);
        Set<IpAddress> ips1 = new HashSet<>();
        ips1.add(ip1);
        Set<IpAddress> ips2 = new HashSet<>();
        ips2.add(ip2);
        VlanId vlanId = VlanId.NONE;
        VirtualHost vHost1 = new DefaultVirtualHost(hostId1, mac1, vlanId, hostLocation1, ips1);
        VirtualHost vHost2 = new DefaultVirtualHost(hostId2, mac2, vlanId, hostLocation2, ips2);

        // 定义虚拟链路 vs1 <===> vs2， 双向的
        VirtualLink virtualLink1 = new DefaultVirtualLink(vs1Cp2, vs2Cp2, vnr.getBandwidth());
        VirtualLink virtualLink2 = new DefaultVirtualLink(vs2Cp2, vs1Cp2, vnr.getBandwidth());

        vnet.addVirtualDevice(vs1);
        vnet.addVirtualDevice(vs2);

        vnet.addVirtualPort(vs1Port1);
        vnet.addVirtualPort(vs1Port2);
        vnet.addVirtualPort(vs2Port1);
        vnet.addVirtualPort(vs2Port2);

        vnet.addVirtualLink(virtualLink1);
        vnet.addVirtualLink(virtualLink2);

        vnet.addVirtualHost(vHost1);
        vnet.addVirtualHost(vHost2);

        return vnet;
    }

    /**
     * 更新物理网络资源情况
     *
     * @param virtualNetwork
     */
    private void updatePhysicalNetworkResource(VirtualNetwork virtualNetwork) {
        // 这里主要更新链路资源信息
        Set<VirtualLink> vlinks = virtualNetwork.getLinks();
        for (VirtualLink vlink : vlinks) {
            List<SRPolicy> srPolicyList = vlink.getSRPolicyList();
            for (SRPolicy srPolicy : srPolicyList) {
                List<DeviceId> sids = srPolicy.getSids();
                for (int i = 0; i < sids.size() - 1; i++) {
                    DeviceId src = sids.get(i);
                    DeviceId dst = sids.get(i + 1);
                    Link link = getDirectLink(src, dst);
                    physicalNetworkService.consumeLinkBW(link, srPolicy.getAllocatedBandwidth());
                }
            }
        }
    }

    /**
     * 获取两个设备之间的直连物理链路
     *
     * @param src
     * @param dst
     * @return
     */
    private Link getDirectLink(DeviceId src, DeviceId dst) {
        Set<Link> outLinks = linkService.getDeviceEgressLinks(src);
        for (Link link : outLinks) {
            if (link.dst().deviceId().equals(dst)) {
                return link;
            }
        }
        return null;
    }
}
