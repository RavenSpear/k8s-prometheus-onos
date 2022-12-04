package cn.seu.p4virtex.nvh.rest;

import cn.seu.p4virtex.nvh.virtual.vnet.VirtualNetworkService;
import cn.seu.p4virtex.nvh.virtual.vnet.element.VirtualNetwork;
import org.onosproject.rest.AbstractWebResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * 虚拟网络管理API
 */
@Path("vnet")
public class VirtualNetworkWebResource extends AbstractWebResource {


    private final VirtualNetworkService virtualNetworkService = get(VirtualNetworkService.class);

    /**
     * 获取所有的虚拟网络对象
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVirtualNetworks() {
        List<VirtualNetwork> virtualNetworks = virtualNetworkService.getVirtualNetworks();
        return ok(encodeArray(VirtualNetwork.class, "VNets", virtualNetworks)).build();
    }

    /**
     * 根据虚拟网络ID，获取该虚拟网络的详细映射信息
     *
     * @return
     */
    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVirtualNetwork(@PathParam("id") Long id) {
        VirtualNetwork virtualNetwork = virtualNetworkService.getVirtualNetwork(id);
        return ok(codec(VirtualNetwork.class).encode(virtualNetwork, this)).build();
    }

}
