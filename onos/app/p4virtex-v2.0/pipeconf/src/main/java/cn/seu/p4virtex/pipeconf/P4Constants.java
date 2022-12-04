package cn.seu.p4virtex.pipeconf;

import org.onosproject.net.pi.model.PiActionId;
import org.onosproject.net.pi.model.PiActionParamId;
import org.onosproject.net.pi.model.PiMatchFieldId;
import org.onosproject.net.pi.model.PiPacketMetadataId;
import org.onosproject.net.pi.model.PiTableId;
import org.onosproject.net.pi.runtime.PiPacketOperation;

public class P4Constants {

    private P4Constants() {
    }

    // 流表ID
    public static final PiTableId INGRESS_BASIC_CONTROL_TABLE0 =
            PiTableId.of("ingress.basic_control.basic_table");

    // 匹配域
    public static final PiMatchFieldId STANDARD_METADATA_INGRESS_PORT =
            PiMatchFieldId.of("standard_metadata.ingress_port");
    public static final PiMatchFieldId HDR_ETHERNET_SRC_ADDR =
            PiMatchFieldId.of("hdr.ethernet.src_addr");
    public static final PiMatchFieldId HDR_ETHERNET_DST_ADDR =
            PiMatchFieldId.of("hdr.ethernet.dst_addr");
    public static final PiMatchFieldId HDR_ETHERNET_ETHER_TYPE =
            PiMatchFieldId.of("hdr.ethernet.ether_type");
    public static final PiMatchFieldId HDR_IPV4_SRC_ADDR =
            PiMatchFieldId.of("hdr.ipv4.src_addr");
    public static final PiMatchFieldId HDR_IPV4_DST_ADDR =
            PiMatchFieldId.of("hdr.ipv4.dst_addr");

    // 动作集ID
    public static final PiActionId NO_ACTION = PiActionId.of("NoAction");
    public static final PiActionId INGRESS_BASIC_CONTROL_DROP =
            PiActionId.of("ingress.basic_control.drop");
    public static final PiActionId INGRESS_BASIC_CONTROL_SEND_TO_CONTROLLER =
            PiActionId.of("ingress.basic_control.send_to_controller");
    public static final PiActionId INGRESS_BASIC_CONTROL_SET_EGRESS_PORT =
            PiActionId.of("ingress.basic_control.set_egress_port");

    // 动作参数
    public static final PiActionParamId PORT = PiActionParamId.of("port");

    // packet-in 和 packet-out 消息报文所携带的元数据
    public static final PiPacketMetadataId INGRESS_PORT =
            PiPacketMetadataId.of("ingress_port");
    public static final PiPacketMetadataId OP = PiPacketMetadataId.of("op");
    public static final PiPacketMetadataId PARAM = PiPacketMetadataId.of("param");

    public static final Short OP_NORMAL_VALUE = 0;
    public static final Short OP_MULTICAST_VALUE = 1;


    // 一些静态参数
    public static final int DEFAULT_FLOW_RULE_PRIORITY = 10;
}
