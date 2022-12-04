package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnet.element.SRPolicy;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualLink;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

public class VirtualLinkCodec extends JsonCodec<VirtualLink> {

    private static final String SRC = "src";
    private static final String DST = "dst";
    private static final String DEVICE = "device";
    private static final String PORT = "port";
    private static final String BANDWIDTH = "bandwidth";
    private static final String PHYSICAL_PATH_LIST = "pathList";

    @Override
    public ObjectNode encode(VirtualLink virtualLink, CodecContext context) {

        ObjectNode result = context.mapper().createObjectNode()
                .put(BANDWIDTH, virtualLink.getBandwidth());

        ObjectNode src = result.putObject(SRC);
        src.put(DEVICE, virtualLink.src().deviceId().toString());
        src.put(PORT, virtualLink.src().port().toLong());
        result.set(SRC, src);

        ObjectNode dst = result.putObject(DST);
        dst.put(DEVICE, virtualLink.dst().deviceId().toString());
        dst.put(PORT, virtualLink.dst().port().toLong());
        result.set(DST, dst);

        ArrayNode pathList = result.putArray(PHYSICAL_PATH_LIST);
        JsonCodec<SRPolicy> srPolicyJsonCodec = context.codec(SRPolicy.class);
        for (SRPolicy policy : virtualLink.getSRPolicyList()) {
            pathList.add(srPolicyJsonCodec.encode(policy, context));
        }
        result.set(PHYSICAL_PATH_LIST, pathList);
        return result;
    }

}
