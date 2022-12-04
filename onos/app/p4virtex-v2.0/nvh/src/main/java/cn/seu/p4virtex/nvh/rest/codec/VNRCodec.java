package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnr.VNR;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onlab.packet.IpAddress;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

import java.util.Date;

import static org.onlab.util.Tools.nullIsIllegal;

/**
 * 将VNR转为JSON对象 或 将JSON对象转为VNR
 */
public class VNRCodec extends JsonCodec<VNR> {

    // Json field names
    private static final String VNR_ID = "vnrId";
    private static final String VNET_NAME = "vnetName";
    private static final String BANDWIDTH = "bandwidth";
    private static final String LATENCY = "latency";
    private static final String CLUSTER1IP = "cluster1IP";
    private static final String CLUSTER2IP = "cluster2IP";
    private static final String STATUS = "status";
    private static final String CREATE_TIME = "createTime";

    private static final String MISSING_MEMBER_MSG = " member is required in VNR";

    @Override
    public ObjectNode encode(VNR vnr, CodecContext context) {
        ObjectNode result = context.mapper().createObjectNode()
                .put(VNR_ID, vnr.getId())
                .put(VNET_NAME, vnr.getVnetName())
                .put(BANDWIDTH, vnr.getBandwidth())
                .put(LATENCY, vnr.getLatency())
                .put(CLUSTER1IP, vnr.getCluster1IP().toString())
                .put(CLUSTER2IP, vnr.getCluster2IP().toString())
                .put(STATUS, vnr.getStatus())
                .put(CREATE_TIME, vnr.getCreateTime().getTime());
        return result;
    }

    @Override
    public VNR decode(ObjectNode json, CodecContext context) {
        String vnetName = extractMember(VNET_NAME, json);
        Integer bandwidth = Integer.parseInt(extractMember(BANDWIDTH, json));
        Integer latency = Integer.parseInt(extractMember(LATENCY, json));
        IpAddress cluster1IP = IpAddress.valueOf(extractMember(CLUSTER1IP, json));
        IpAddress cluster2IP = IpAddress.valueOf(extractMember(CLUSTER2IP, json));
        VNR vnr = new VNR(bandwidth, latency, cluster1IP, cluster2IP);
        if (vnetName.equals("")) {
            vnr.setVnetName("虚拟网络D" + new Date().getTime());
        }else{
            vnr.setVnetName(vnetName);
        }
        return vnr;
    }

    private String extractMember(String key, ObjectNode json) {
        return nullIsIllegal(json.get(key), key + MISSING_MEMBER_MSG).asText();
    }
}
