package cn.seu.p4virtex.nvh;

import cn.seu.p4virtex.nvh.store.SimpleVNRStore;
import cn.seu.p4virtex.nvh.virtual.vnr.VNRManager;
import org.junit.Before;
import org.junit.Test;
import org.onlab.junit.TestUtils;
import org.onlab.packet.Ethernet;
import org.onosproject.core.CoreService;

public class VNRManagerTest {

    private VNRManager manager;
    private SimpleVNRStore store;
    private CoreService coreService;

    @Before
    public void setup() {

        coreService = new TestCoreService();


        store = new SimpleVNRStore();
        TestUtils.setField(store, "coreService", coreService);
        store.activate();

        manager = new VNRManager();
        TestUtils.setField(manager, "vnrStore", store);
        manager.activate();
    }

    @Test
    public void test(){
        System.out.println(Ethernet.TYPE_IPV4);
    }

}
