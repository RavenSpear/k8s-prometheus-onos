package cn.seu.p4virtex.nvh;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.onosproject.net.Device;
import org.onosproject.net.Link;
import org.onosproject.net.NetTestTools;
import org.onosproject.net.SparseAnnotations;
import org.onosproject.net.topology.DefaultGraphDescription;
import org.onosproject.net.topology.GraphDescription;
import org.onosproject.net.topology.impl.TopologyManagerTest;
import shaded.org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SimpleTest extends TopologyManagerTest {


//    public void test1() {
//        Topology topology = this.service.currentTopology();
//        Assert.assertNull("no topo expected", topology);
//        this.submitMyTopologyGraph();
//        this.validateEvents(TopologyEvent.Type.TOPOLOGY_CHANGED);
//        topology = this.service.currentTopology();
//        Graph graph = this.service.getGraph(topology);
//        MutableAdjacencyListsGraph g = new MutableAdjacencyListsGraph(graph.getVertexes(), graph.getEdges());
//        g.removeVertex(new DefaultTopologyVertex(DeviceId.deviceId("of:s1")));
//        System.out.println(g.getEdges());
//    }


    private void submitMyTopologyGraph() {
        Set<Device> devices = ImmutableSet.of(
                NetTestTools.device("s1"),
                NetTestTools.device("s2"),
                NetTestTools.device("s3"),
                NetTestTools.device("s4"),
                NetTestTools.device("s5")
        );

        Set<Link> links = ImmutableSet.of(
                NetTestTools.link("s1", 2, "s2", 2),
                NetTestTools.link("s1", 3, "s3", 2),

                NetTestTools.link("s2", 2, "s1", 2),
                NetTestTools.link("s2", 3, "s4", 2),
                NetTestTools.link("s2", 4, "s5", 2),

                NetTestTools.link("s3", 2, "s1", 3),
                NetTestTools.link("s3", 3, "s4", 3),
                NetTestTools.link("s3", 4, "s5", 3),

                NetTestTools.link("s4", 2, "s2", 3),
                NetTestTools.link("s4", 3, "s3", 3),
                NetTestTools.link("s4", 4, "s5", 4),

                NetTestTools.link("s5", 2, "s2", 4),
                NetTestTools.link("s5", 3, "s3", 4),
                NetTestTools.link("s5", 4, "s4", 4)
        );
        GraphDescription data = new DefaultGraphDescription(4321L, System.currentTimeMillis(), devices, links, new SparseAnnotations[0]);
        this.providerService.topologyChanged(data, (List) null);
    }


//    @Test
//    public void typeTest(){
//       Integer integer = INTInstruction.getBitmap(INTMetadataType.INGRESS_RX_PACKET_COUNT);
//        System.out.println(integer);
//    }

    public void test2() {
        File temp;
        try {
            // 创建一个临时文件
            temp = File.createTempFile("libjniortools", ".so");
            temp.deleteOnExit();

            // 文件拷贝
            InputStream is = this.getClass().getClassLoader().getResourceAsStream("ortools-linux-x86-64/libjniortools.so");
            FileUtils.copyInputStreamToFile(is,temp);
            System.out.println(FileUtils.readFileToString(temp,"UTF8"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
