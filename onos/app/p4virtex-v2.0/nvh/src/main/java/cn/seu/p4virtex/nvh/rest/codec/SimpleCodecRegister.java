package cn.seu.p4virtex.nvh.rest.codec;

import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import cn.seu.p4virtex.nvh.virtual.vnet.element.SRPolicy;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualDevice;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualHost;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualLink;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualPort;
import cn.seu.p4virtex.nvh.virtual.vnr.VNR;
import org.onosproject.codec.CodecService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(immediate = true)
public class SimpleCodecRegister {

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CodecService codecService;

    @Activate
    protected void activate() {
        codecService.registerCodec(VNR.class, new VNRCodec());
        codecService.registerCodec(VirtualNetwork.class, new VirtualNetworkCodec());
        codecService.registerCodec(VirtualDevice.class, new VirtualDeviceCodec());
        codecService.registerCodec(VirtualPort.class, new VirtualPortCodec());
        codecService.registerCodec(VirtualLink.class, new VirtualLinkCodec());
        codecService.registerCodec(VirtualHost.class, new VirtualHostCodec());
        codecService.registerCodec(SRPolicy.class, new SRPolicyCodec());
        codecService.registerCodec(LinkResource.class, new LinkResourceCodec());
    }

    @Deactivate
    protected void deactivate() {
        codecService.unregisterCodec(VNR.class);
        codecService.unregisterCodec(VirtualNetwork.class);
        codecService.unregisterCodec(VirtualDevice.class);
        codecService.unregisterCodec(VirtualPort.class);
        codecService.unregisterCodec(VirtualLink.class);
        codecService.unregisterCodec(VirtualHost.class);
        codecService.unregisterCodec(SRPolicy.class);
        codecService.unregisterCodec(LinkResource.class);
    }
}
