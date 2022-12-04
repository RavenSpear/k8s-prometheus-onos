package cn.seu.p4virtex.nvh.rest;

import cn.seu.p4virtex.nvh.virtual.physical.PhysicalNetworkService;
import cn.seu.p4virtex.nvh.virtual.physical.resource.LinkResource;
import org.onosproject.rest.AbstractWebResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Set;

/**
 * 物理网络管理API
 */
@Path("pnet")
public class PhysicalNetworkWebResource extends AbstractWebResource {


    private final PhysicalNetworkService physicalNetworkService = get(PhysicalNetworkService.class);

    /**
     * 获取所有的链路的资源信息
     * @return
     */
    @GET
    @Path("resource/link")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllLinksResource() {
        Set<LinkResource> linkResourceSet = physicalNetworkService.getLinkResources();
        return ok(encodeArray(LinkResource.class, "linkResources", linkResourceSet)).build();
    }
}
