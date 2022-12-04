package cn.seu.p4virtex.nvh.rest;

import cn.seu.p4virtex.nvh.virtual.vnr.VNR;
import cn.seu.p4virtex.nvh.virtual.vnr.VNRService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.rest.AbstractWebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.onlab.util.Tools.readTreeFromStream;

/**
 * 虚拟网络请求管理API
 */
@Path("vnr")
public class VirtualNetworkRequestWebResource extends AbstractWebResource {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final VNRService vnrService = get(VNRService.class);


    /**
     * 提交虚拟网络请求
     *
     * @param stream 虚拟网络请求json
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postVNR(InputStream stream) {
        try {
            // 解析虚拟网络请求对象
            ObjectNode jsonTree = readTreeFromStream(mapper(), stream);
            VNR vnr = codec(VNR.class).decode(jsonTree, this);

            // 提交虚拟网络网络请求
            vnrService.submitVNR(vnr);

            // 构建返回参数
            ObjectNode root = mapper().createObjectNode();
            root.put("status", "success");
            root.put("id", vnr.getId());
            return ok(root).build();

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 查询所有虚拟网络请求的信息
     *
     * @return
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllVNRs() {
        List<VNR> rs = vnrService.getVNRs();
        return ok(encodeArray(VNR.class, "VNRs", rs)).build();
    }

    /**
     * 根据虚拟网络请求id，获取虚拟网络请求的所有信息
     *
     * @param vnrId 虚拟网络请求Id
     * @return
     */
    @GET
    @Path("{vnrId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getVNR(@PathParam("vnrId") Long vnrId) {
        VNR vnr = vnrService.getVNRById(vnrId);
        return ok(codec(VNR.class).encode(vnr, this)).build();
    }
}
