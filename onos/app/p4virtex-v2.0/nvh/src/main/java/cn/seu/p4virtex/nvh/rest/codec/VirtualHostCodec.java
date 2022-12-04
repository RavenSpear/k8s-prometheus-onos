package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualHost;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

import java.util.ArrayList;

public class VirtualHostCodec extends JsonCodec<VirtualHost> {

    private static final String IPADDRESS = "ipAddress";

    @Override
    public ObjectNode encode(VirtualHost virtualHost, CodecContext context) {
        ObjectNode result = context.mapper().createObjectNode()
                .put(IPADDRESS, new ArrayList<>(virtualHost.ipAddresses()).get(0).toString());
        return result;
    }
}
