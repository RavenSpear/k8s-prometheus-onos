package cn.seu.p4virtex.nvh;

import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Arc;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Commodity;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Flow;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.MCFAlgorithm;
import com.google.ortools.Loader;
import com.sun.jna.Platform;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MCFTest {

    public void test() {

        ClassLoader loader = Loader.class.getClassLoader();

        String RESOURCE_PATH = "ortools-" + Platform.RESOURCE_PREFIX + "/";
        URL resourceURL = loader.getResource(RESOURCE_PATH);
        System.out.println(resourceURL);
        List<Arc> arcs = new ArrayList<>();
        List<Commodity> commodities = new ArrayList<>();

        arcs.add(new Arc(0, 5, 10));
        arcs.add(new Arc(5, 0, 10));
        arcs.add(new Arc(5, 1, 8));
        arcs.add(new Arc(1, 5, 8));
        arcs.add(new Arc(1, 2, 8));
        arcs.add(new Arc(2, 1, 8));
        arcs.add(new Arc(2, 3, 8));
        arcs.add(new Arc(3, 2, 8));
        arcs.add(new Arc(3, 4, 10));
        arcs.add(new Arc(4, 3, 10));
        arcs.add(new Arc(5, 6, 8));
        arcs.add(new Arc(6, 5, 8));
        arcs.add(new Arc(6, 7, 10));
        arcs.add(new Arc(7, 6, 10));
        arcs.add(new Arc(3, 6, 8));
        arcs.add(new Arc(6, 3, 8));

        commodities.add(new Commodity(1, 0, 7, 5));
        commodities.add(new Commodity(2, 7, 0, 5));

        MCFAlgorithm mcf = new MCFAlgorithm(arcs, commodities);
        List<Flow> flows = mcf.solve();
        for (Flow flow : flows) {
            System.out.println(flow);
        }
    }
}
