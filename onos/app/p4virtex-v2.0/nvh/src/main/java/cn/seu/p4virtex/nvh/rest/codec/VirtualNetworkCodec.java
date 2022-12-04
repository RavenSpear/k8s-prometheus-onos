package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualDevice;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualHost;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualLink;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.codec.CodecContext;
import org.onosproject.codec.JsonCodec;

public class VirtualNetworkCodec extends JsonCodec<VirtualNetwork> {

    private static final String VNET_ID = "vnetId";
    private static final String VNR_ID = "vnrId";
    private static final String VNET_NAME = "vnetName";
    private static final String CREATE_TIME = "createTime";
    private static final String VIRTUAL_DEVICES = "virtualDevices";
    private static final String VIRTUAL_LINKS = "virtualLinks";
    private static final String VIRTUAL_HOSTS = "virtualHosts";


    @Override
    public ObjectNode encode(VirtualNetwork virtualNetwork, CodecContext context) {

        JsonCodec<VirtualDevice> virtualDeviceCodec = context.codec(VirtualDevice.class);
        JsonCodec<VirtualLink> virtualLinkCodec = context.codec(VirtualLink.class);
        JsonCodec<VirtualHost> virtualHostCodec = context.codec(VirtualHost.class);

        ObjectNode result = context.mapper().createObjectNode()
                .put(VNET_ID, virtualNetwork.getId().id())
                .put(VNR_ID, virtualNetwork.getVnrId())
                .put(VNET_NAME, virtualNetwork.getVnetName())
                .put(CREATE_TIME, virtualNetwork.getCreateTime().getTime());

        // 添加虚拟设备信息
        ArrayNode jsonVirtualDevices = result.putArray(VIRTUAL_DEVICES);
        for (VirtualDevice virtualDevice : virtualNetwork.getDevices()) {
            jsonVirtualDevices.add(virtualDeviceCodec.encode(virtualDevice, context));
        }
        result.set(VIRTUAL_DEVICES, jsonVirtualDevices);

        // 添加虚拟链路信息
        ArrayNode jsonVirtualLinks = result.putArray(VIRTUAL_LINKS);
        for (VirtualLink virtualLink : virtualNetwork.getLinks()) {
            jsonVirtualLinks.add(virtualLinkCodec.encode(virtualLink, context));
        }
        result.set(VIRTUAL_LINKS, jsonVirtualLinks);

        // 添加虚拟主机信息
        ArrayNode jsonVirtualHosts = result.putArray(VIRTUAL_HOSTS);
        for (VirtualHost virtualHost : virtualNetwork.getHosts()) {
            jsonVirtualHosts.add(virtualHostCodec.encode(virtualHost, context));
        }
        result.set(VIRTUAL_HOSTS, jsonVirtualHosts);

        return result;
    }

}
