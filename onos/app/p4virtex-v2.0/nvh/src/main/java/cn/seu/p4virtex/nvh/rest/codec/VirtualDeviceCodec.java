package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnet.VirtualNetworkService;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualDevice;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualPort;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

public class VirtualDeviceCodec extends JsonCodec<VirtualDevice> {

    private static final String DEVICE_ID = "deviceId";
    private static final String PHYSICAL_DEVICE_ID = "physicalDeviceId";
    private static final String VIRTUAL_PORTS = "ports";

    @Override
    public ObjectNode encode(VirtualDevice virtualDevice, CodecContext context) {
        VirtualNetworkService virtualNetworkService = context.getService(VirtualNetworkService.class);
        ObjectNode result = context.mapper().createObjectNode()
                .put(DEVICE_ID, virtualDevice.id().toString())
                .put(PHYSICAL_DEVICE_ID, virtualDevice.getRealizedBy().toString());
        ArrayNode jsonVirtualPorts = result.putArray(VIRTUAL_PORTS);
        // 该虚拟设备所属的虚拟网络
        VirtualNetwork virtualNetwork = virtualNetworkService.getVirtualNetwork(virtualDevice.networkId().id());

        JsonCodec<VirtualPort> virtualPortCodec = context.codec(VirtualPort.class);
        virtualNetwork.getPorts().forEach(port -> {
            if (port.element().id().equals(virtualDevice.id())) {
                jsonVirtualPorts.add(virtualPortCodec.encode(port, context));
            }
        });
        result.set(VIRTUAL_PORTS, jsonVirtualPorts);
        return result;
    }


}
