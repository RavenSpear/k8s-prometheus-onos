package cn.seu.p4virtex.nvh.virtual.vne;

import cn.seu.p4virtex.nvh.virtual.physical.PhysicalNetworkService;
import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Arc;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Commodity;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Flow;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.MCFAlgorithm;
import cn.seu.p4virtex.nvh.virtual.vnet.element.SRPolicy;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualHost;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualLink;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import com.google.ortools.Loader;
import org.apache.commons.io.FileUtils;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Host;
import org.onosproject.net.Link;
import org.onosproject.net.host.HostService;
import org.onosproject.net.link.LinkService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 提供虚拟网络嵌入服务：根据当前剩余的物理网络资源，执行虚拟网络嵌入程序
 */
@Component(immediate = true, service = VNEService.class)
public class VNEManager implements VNEService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PhysicalNetworkService physicalNetworkService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected LinkService linkService;

    @Activate
    public void activate() {
        log.info("启动虚拟网络嵌入执行器...");

        log.info("加载or-tools动态链接库....");
        loadORTools();
        log.info("or-tools动态链接库加载成功");
    }

    @Deactivate
    public void deactivate() {
        log.info("关闭虚拟网络嵌入执行器...");
    }

    /**
     * 采用的是两步走映射策略
     * 1. 节点映射
     * 2. 链路映射
     *
     * @param virtualNetwork
     * @return
     */
    @Override
    public boolean embed(VirtualNetwork virtualNetwork) {

        // 1. 节点映射
        // TODO. 这里只是简单的映射，端到端映射，根据两端的主机地址，确定边缘节点的位置。
        log.info("开始节点映射...");
        // 临时存储节点映射关系
        Map<DeviceId, DeviceId> deviceMap = new HashMap<>();

        Set<VirtualHost> vhosts = virtualNetwork.getHosts();
        for (VirtualHost vhost : vhosts) {
            // 根据虚拟主机的IP地址找到
            DeviceId vDeviceId = vhost.location().deviceId();
            Set<Host> hosts = hostService.getHostsByIp(new ArrayList<>(vhost.ipAddresses()).get(0));
            Host host = new ArrayList<>(hosts).get(0);
            DeviceId deviceId = host.location().deviceId();
            // 完成节点映射
            virtualNetwork.getVirtualDevice(vDeviceId).setRealizedBy(deviceId);
            deviceMap.put(vDeviceId, deviceId);
            log.info("节点映射结果：{}=>{}", vDeviceId, deviceId);
        }

        if(vhosts.isEmpty()){
            // 当前找不到映射节点
            log.info("节点映射失败！找不到映射节点...");
            return false;
        }

        // 2. 链路映射（基于多商品流的虚拟链路映射）
        log.info("开始链路映射...");

        // ============= 构建商品流 =============

        // 每一条虚拟链路看作一条商品流
        AtomicInteger commodityId = new AtomicInteger(); // 为每一条虚拟链路编号
        List<Commodity> commodityList = new ArrayList<>();
        // 临时存储 商品流ID与虚拟链路的映射关系
        Map<Integer, VirtualLink> integerVirtualLinkMap = new HashMap<>();

        for (VirtualLink vlink : virtualNetwork.getLinks()) {

            DeviceId vSrcDeviceId = vlink.src().deviceId();
            DeviceId vDstDeviceId = vlink.dst().deviceId();

            DeviceId srcDeviceId = deviceMap.get(vSrcDeviceId);
            DeviceId dstDeviceId = deviceMap.get(vDstDeviceId);

            // 构建商品流
            int src = physicalNetworkService.getSwitchId(srcDeviceId);
            int dst = physicalNetworkService.getSwitchId(dstDeviceId);
            double quantity = vlink.getBandwidth();

            //每一个（src,dst）看作一个商品流
            Commodity commodity = new Commodity(commodityId.getAndIncrement(), src, dst, quantity);
            commodityList.add(commodity);

            integerVirtualLinkMap.put(commodity.id, vlink);
            log.info("处理链路：{}，其对应的商品流为{}", vlink, commodity);
        }

        // ============= 输入当前网络剩余带宽资源 =============
        List<Arc> arcList = new ArrayList<>();
        Set<LinkResource> linkResources = physicalNetworkService.getLinkResources();
        for (LinkResource linkResource : linkResources) {
            Link link = linkResource.getLink();
            int src = physicalNetworkService.getSwitchId(link.src().deviceId());
            int dst = physicalNetworkService.getSwitchId(link.dst().deviceId());
            double capacity = linkResource.getRemainingBandwidth();
            Arc arc = new Arc(src, dst, capacity);
            arcList.add(arc);
            log.info("当前链路资源：{}", arc);
        }

        // ============= 执行mcf算法 =============
        MCFAlgorithm mcf = new MCFAlgorithm(arcList, commodityList);
        log.info("执行虚拟链路映射....");
        List<Flow> flows = mcf.solve();
        if (flows != null) {
            log.info("嵌入成功");
            // 每个flow代表一个链路映射，即SRPolicy
            for (Flow flow : flows) {
                List<DeviceId> sids = new ArrayList<>();
                flow.path.forEach((n) -> {
                    sids.add(physicalNetworkService.getDeviceId(n));
                });
                // 完成虚拟链路的多路径映射
                SRPolicy srPolicy = new SRPolicy(sids, (int) flow.bandwidth);
                VirtualLink virtualLink = integerVirtualLinkMap.get(flow.commodityId);
                virtualLink.addSRPolicy(srPolicy);
                log.info("链路映射结果：{}=>{}", virtualLink, srPolicy);
            }
            return true;
        } else {
            log.info("嵌入失败");
            // 若找不到解，则表示该虚拟链路无法嵌入到当前的物理网络中
            return false;
        }
    }

    /**
     * 加载or-tools的动态链接库
     */
    private void loadORTools() {
        File temp1,temp2;
        try {
            //Loader.loadNativeLibraries();

            //  创建临时目录
            Path path = Files.createTempDirectory("ortools-java");
            path.toFile().deleteOnExit();
            // 创建临时文件
            temp1 = new File(path.toString(),"libjniortools.so");
            temp1.deleteOnExit();
            temp2 = new File(path.toString(),"libortools.so.9");
            temp1.deleteOnExit();

            // 文件拷贝
            InputStream is1 = this.getClass().getClassLoader().getResourceAsStream("ortools-linux-x86-64/libjniortools.so");
            FileUtils.copyInputStreamToFile(is1, temp1);
            is1.close();
            InputStream is2 = this.getClass().getClassLoader().getResourceAsStream("ortools-linux-x86-64/libortools.so.9");
            FileUtils.copyInputStreamToFile(is2, temp2);
            is2.close();

            log.info("ortools动态链接库目前的位置：{}",temp1.getAbsolutePath());

            // 加载该动态链接库
            System.load(temp1.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
