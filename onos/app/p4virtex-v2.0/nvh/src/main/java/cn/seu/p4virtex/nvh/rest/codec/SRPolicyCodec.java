package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnet.element.SRPolicy;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualPort;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;
import org.onosproject.net.DeviceId;
import org.onosproject.net.link.LinkService;

import javax.ws.rs.core.Link;
import java.util.List;

public class SRPolicyCodec extends JsonCodec<SRPolicy> {

    private static final String PATH = "path";
    private static final String BANDWIDTH = "allocatedBandwidth";

    @Override
    public ObjectNode encode(SRPolicy srPolicy, CodecContext context) {
        ObjectNode result = context.mapper().createObjectNode()
                .put(BANDWIDTH, srPolicy.getAllocatedBandwidth());
        ArrayNode path = result.putArray(PATH);
        List<DeviceId> sids = srPolicy.getSids();
        for (DeviceId sid : sids) {
            path.add(sid.toString());
        }
        result.set(PATH, path);
        return result;
    }
}
