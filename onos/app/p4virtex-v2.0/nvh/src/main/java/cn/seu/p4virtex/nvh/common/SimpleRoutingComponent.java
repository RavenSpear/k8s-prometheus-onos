package cn.seu.p4virtex.nvh.common;

import cn.seu.p4virtex.nvh.MainComponent;
import org.onlab.packet.Ethernet;
import org.onlab.packet.IPv4;
import org.onlab.packet.Ip4Prefix;
import org.onlab.packet.MacAddress;
import org.onlab.packet.VlanId;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.Host;
import org.onosproject.net.HostId;
import org.onosproject.net.Path;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flowobjective.DefaultForwardingObjective;
import org.onosproject.net.flowobjective.FlowObjectiveService;
import org.onosproject.net.flowobjective.ForwardingObjective;
import org.onosproject.net.host.HostService;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.PacketContext;
import org.onosproject.net.packet.PacketPriority;
import org.onosproject.net.packet.PacketProcessor;
import org.onosproject.net.packet.PacketService;
import org.onosproject.net.topology.TopologyService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


/**
 * 执行静态路由配置，取代reactive forwarding
 */
@Component(immediate = true, service = SimpleRoutingComponent.class)
public class SimpleRoutingComponent {

    private Logger log = LoggerFactory.getLogger(getClass());


    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected FlowObjectiveService flowObjectiveService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected PacketService packetService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected MainComponent mainComponent;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected HostService hostService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected TopologyService topologyService;

    private PacketProcessor processor = new SimpleRoutingPacketProcessor();

    private ApplicationId appId;

    /**
     * 一旦启动为 每个交换机安装一条流表规则
     * etherType = ipv4 send_to_controller
     */
    @Activate
    private void activate() {
        appId = mainComponent.appId();
        packetService.addProcessor(processor, PacketProcessor.director(3));
        requestIntercepts();
    }

    /**
     * 删除流表规则
     */
    @Deactivate
    private void deactivate() {
        withdrawIntercepts();
        flowRuleService.removeFlowRulesById(appId);
        packetService.removeProcessor(processor);
    }


    private void requestIntercepts() {
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        selector.matchEthType(Ethernet.TYPE_IPV4);
        packetService.requestPackets(selector.build(), PacketPriority.REACTIVE, appId);
    }

    private void withdrawIntercepts() {
        TrafficSelector.Builder selector = DefaultTrafficSelector.builder();
        selector.matchEthType(Ethernet.TYPE_IPV4);
        packetService.cancelPackets(selector.build(), PacketPriority.REACTIVE, appId);
    }


    private class SimpleRoutingPacketProcessor implements PacketProcessor {

        @Override
        public void process(PacketContext context) {

            if (context.isHandled()) {
                return;
            }

            InboundPacket pkt = context.inPacket();
            Ethernet ethPkt = pkt.parsed();

            if (ethPkt == null) {
                return;
            }

            if (isLLDPPacket(ethPkt)) {
                return;
            }

            if (ethPkt.getEtherType() == Ethernet.TYPE_IPV4) {

                // 目的主机
                HostId id = HostId.hostId(ethPkt.getDestinationMAC(), VlanId.vlanId(ethPkt.getVlanID()));
                Host dst = hostService.getHost(id);
                if (dst == null) {
                    log.info("找不到目的主机，无法为其寻路...");
                    return;
                }

                // 若目的主机 与产生packet-out报文的交换机是同一个交换机
                if (pkt.receivedFrom().deviceId().equals(dst.location().deviceId())) {
                    if (!context.inPacket().receivedFrom().port().equals(dst.location().port())) {
                        installFlowRule(context, dst.location().port());
                    }
                    return;
                }

                // 寻找路由
                Set<Path> paths = topologyService.getPaths(topologyService.currentTopology(),
                        pkt.receivedFrom().deviceId(),
                        dst.location().deviceId());
                if (paths.isEmpty()) {
                    log.info("当前网络拓扑中，找不到路径");
                    return;
                }

                // 选择一条合适的路径
                Path path = pickForwardPathIfPossible(paths, pkt.receivedFrom().port());
                if (path == null) {
                    log.info("当前拓扑中找不到合适的路径...");
                    return;
                }

                installFlowRule(context, path.src().port());


            }
        }
    }

    /**
     * 判断是不是 LLDP 报文
     *
     * @param eth
     * @return
     */
    private boolean isLLDPPacket(Ethernet eth) {
        short type = eth.getEtherType();
        return type == Ethernet.TYPE_LLDP || type == Ethernet.TYPE_BSN;
    }


    /**
     * 选取合适的转发路径
     *
     * @param paths
     * @param notToPort
     * @return
     */

    private Path pickForwardPathIfPossible(Set<Path> paths, PortNumber notToPort) {
        for (Path path : paths) {
            if (!path.src().port().equals(notToPort)) {
                return path;
            }
        }
        return null;
    }


    /**
     * 安装流表规则，指定转发出口
     *
     * @param context
     * @param portNumber
     */
    private void installFlowRule(PacketContext context, PortNumber portNumber) {
        Ethernet inPkt = context.inPacket().parsed();
        TrafficSelector.Builder selectorBuilder = DefaultTrafficSelector.builder();

        if (inPkt.getEtherType() == Ethernet.TYPE_IPV4) {
            IPv4 ipv4Packet = (IPv4) inPkt.getPayload();
            Ip4Prefix matchIp4SrcPrefix =
                    Ip4Prefix.valueOf(ipv4Packet.getSourceAddress(),
                            Ip4Prefix.MAX_MASK_LENGTH);
            Ip4Prefix matchIp4DstPrefix =
                    Ip4Prefix.valueOf(ipv4Packet.getDestinationAddress(),
                            Ip4Prefix.MAX_MASK_LENGTH);

            selectorBuilder.matchEthType(Ethernet.TYPE_IPV4)
                    .matchIPSrc(matchIp4SrcPrefix)
                    .matchIPDst(matchIp4DstPrefix);

        }

        TrafficTreatment treatment = DefaultTrafficTreatment.builder()
                .setOutput(portNumber)
                .build();

        ForwardingObjective forwardingObjective = DefaultForwardingObjective.builder()
                .withSelector(selectorBuilder.build())
                .withTreatment(treatment)
                .withPriority(10)
                .withFlag(ForwardingObjective.Flag.VERSATILE)
                .fromApp(mainComponent.appId())
                .makePermanent()
                .add();

        flowObjectiveService.forward(context.inPacket().receivedFrom().deviceId(),
                forwardingObjective);

        // 发送packet-out
        context.treatmentBuilder().setOutput(portNumber);
        context.send();
    }

    /**
     * 将接收到的数据包从指定出口转发
     *
     * @param context
     * @param portNumber
     */

    private void packetOut(PacketContext context, PortNumber portNumber) {
        context.treatmentBuilder().setOutput(portNumber);
        context.send();
    }


}
