package cn.seu.p4virtex.nvh.virtual.traffic;

import cn.seu.p4virtex.nvh.store.VnetTrafficStore;
import cn.seu.p4virtex.nvh.virtual.vnr.VNRService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;

@Component(immediate = true, service = VnetTrafficService.class)
public class VnetTrafficManager implements VnetTrafficService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected VnetTrafficStore store;

    @Activate
    public void activate() {
        log.info("启动业务流量管理...");
    }

    @Deactivate
    public void deactivate() {
        log.info("关闭业务流量管理...");
    }

//    @Override
//    public boolean registerSlice(VnetTraffic vnetTraffic) {
//
//
//
//        return false;
//    }
}
