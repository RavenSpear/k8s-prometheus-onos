package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualPort;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

public class VirtualPortCodec extends JsonCodec<VirtualPort> {

    private static final String PORT_NUMBER = "number";
    private static final String ELEMENT = "element";

    @Override
    public ObjectNode encode(VirtualPort virtualPort, CodecContext context) {
        ObjectNode result = context.mapper().createObjectNode()
                .put(PORT_NUMBER, virtualPort.number().toLong());
        return result;
    }
}
