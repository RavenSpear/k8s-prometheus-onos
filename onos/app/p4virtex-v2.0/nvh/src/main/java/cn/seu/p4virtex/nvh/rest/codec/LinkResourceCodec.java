package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

public class LinkResourceCodec extends JsonCodec<LinkResource> {

    private static final String SRC = "src";
    private static final String DST = "dst";
    private static final String SUM_Bandwidth = "sumBandwidth";
    private static final String ALLOCATED_Bandwidth = "allocatedBandwidth";


    public ObjectNode encode(LinkResource linkResource, CodecContext context) {
        ObjectNode result = context.mapper().createObjectNode()
                .put(SRC, linkResource.getLink().src().deviceId().toString())
                .put(DST, linkResource.getLink().dst().deviceId().toString())
                .put(SUM_Bandwidth, linkResource.getBandwidth())
                .put(ALLOCATED_Bandwidth, linkResource.getAllocatedBandwidth());
        return result;
    }
}
