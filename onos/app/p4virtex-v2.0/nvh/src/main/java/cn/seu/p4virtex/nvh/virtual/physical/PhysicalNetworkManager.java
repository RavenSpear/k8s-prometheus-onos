package cn.seu.p4virtex.nvh.virtual.physical;

import cn.seu.p4virtex.nvh.store.PhysicalNetworkStore;
import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import org.onlab.util.SharedScheduledExecutorService;
import org.onlab.util.SharedScheduledExecutors;
import org.onosproject.mastership.MastershipService;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Link;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.host.HostService;
import org.onosproject.net.topology.Topology;
import org.onosproject.net.topology.TopologyEdge;
import org.onosproject.net.topology.TopologyGraph;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

/**
 * 用于底层物理网络的管理
 * 1. 负责物理网络资源的初始化、查询和更新
 */
@Component(immediate = false, service = PhysicalNetworkService.class)
public class PhysicalNetworkManager implements PhysicalNetworkService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private MastershipService mastershipService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private DeviceService deviceService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private PhysicalNetworkStore physicalNetworkStore;

    private final DeviceListener deviceListener = new InternalDeviceListener();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Activate
    private void activate() {
        log.info("启动物理网络资源管理...");
        log.info("开启设备监听：一旦连入新的网络设备，则为其分配编号");
        deviceService.addListener(deviceListener);

        // TODO. 目前实现物理网络资源初始化的方法 不够灵活
        scheduleTask(this::initPhysicalResource, 60, TimeUnit.SECONDS);
        log.info("请尽快连接网络拓扑，60s后将初始化整个物理网络资源");
    }

    @Deactivate
    private void deactivate() {
        deviceService.removeListener(deviceListener);
        log.info("移除设备监听");
        executorService.shutdown();
        log.info("关闭物理网络资源管理...");
    }

    /**
     * 初始化物理网络资源（静态配置）
     * TODO. 以后有机会再修改，目前用于调试阶段
     */
    @Override
    public void initPhysicalResource() {
        Topology topology = topologyService.currentTopology();
        TopologyGraph graph = topologyService.getGraph(topology);
        Set<TopologyEdge> edges = graph.getEdges();
        log.info("初始化物理网络资源...");
        // 配置物理链路资源属性
        for (TopologyEdge edge : edges) {

            int srcSpeed = (int) deviceService.getPort(edge.link().src()).portSpeed() / 1000;
            int dstSpeed = (int) deviceService.getPort(edge.link().dst()).portSpeed() / 1000;
            int bandwidth = Math.min(srcSpeed, dstSpeed);
            LinkResource linkResource = new LinkResource(edge.link(), bandwidth);
            physicalNetworkStore.addLinkResource(linkResource);
            log.info("链路资源：{}->{}，分配的带宽{}", edge.src().deviceId(), edge.dst().deviceId(), linkResource.getBandwidth());
        }

        // 初始化所有交换机的编号
        for (Device device : deviceService.getDevices()) {
            DeviceId deviceId = device.id();
            int switchId = Integer.parseInt(deviceId.toString().substring(8));
            physicalNetworkStore.addSwitchId(deviceId, switchId);
            log.info("交换机id：{}，分配的编号{}", device.id(), switchId);
        }

        log.info("物理网络资源初始化完成");
    }

    public void scheduleTask(Runnable task, int delayTime, TimeUnit timeUnit) {
        SharedScheduledExecutors.newTimeout(
                () -> executorService.execute(task),
                delayTime,
                timeUnit
        );
    }


    @Override
    public Integer getSwitchId(DeviceId deviceId) {
        return physicalNetworkStore.getSwitchId(deviceId);
    }

    @Override
    public DeviceId getDeviceId(Integer switchId) {
        return physicalNetworkStore.getDeviceId(switchId);
    }

    @Override
    public Set<LinkResource> getLinkResources() {
        return physicalNetworkStore.getLinkResources();
    }

    @Override
    public void consumeLinkBW(Link link, Integer consumedBW) {
        // 获取对应的物理链路资源对象
        LinkResource linkResource = physicalNetworkStore.getLinkResource(link);
        linkResource.consumeBW(consumedBW);
    }

    private class InternalDeviceListener implements DeviceListener {

        @Override
        public boolean isRelevant(DeviceEvent event) {
            switch (event.type()) {
                case DEVICE_ADDED:
                case DEVICE_AVAILABILITY_CHANGED:
                    break;
                default:
                    // Ignore other events.
                    return false;
            }
            // Process only if this controller instance is the master.
            final DeviceId deviceId = event.subject().id();
            return mastershipService.isLocalMaster(deviceId);
        }

        @Override
        public void event(DeviceEvent event) {

            Device device = event.subject();
            DeviceId deviceId = device.id();
            if (deviceService.isAvailable(deviceId)) {
                // 先不考虑随机生成switch id的情况.暂时先自己设置switchId
                int switchId = Integer.parseInt(deviceId.toString().substring(8));
                physicalNetworkStore.addSwitchId(deviceId, switchId);
                log.info("交换机{}接入到网络，为其分配的序列编号为{}", deviceId, switchId);
            }
        }
    }


}
