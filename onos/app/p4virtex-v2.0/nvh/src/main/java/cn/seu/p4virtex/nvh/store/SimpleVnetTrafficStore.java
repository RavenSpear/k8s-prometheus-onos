package cn.seu.p4virtex.nvh.store;


import cn.seu.p4virtex.nvh.virtual.traffic.Traffic;
import cn.seu.p4virtex.nvh.virtual.traffic.VnetTraffic;
import org.onosproject.core.CoreService;
import org.onosproject.core.IdGenerator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * 业务流量管理
 */
@Component(immediate = true, service = VnetTrafficStore.class)
public class SimpleVnetTrafficStore implements VnetTrafficStore {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String Virtual_TRAFFIC_IDS_TOPIC = "virtual-traffic-ids-topic";
    private IdGenerator idGenerator;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    private Set<VnetTraffic> vnetTrafficSet;

    @Activate
    public void activate() {
        idGenerator = coreService.getIdGenerator(Virtual_TRAFFIC_IDS_TOPIC);
        vnetTrafficSet = new HashSet<>();
    }

    @Deactivate
    public void deactivate() {
        vnetTrafficSet.clear();
    }

    @Override
    public void addVnetTraffic(VnetTraffic vnetTraffic) {
        this.vnetTrafficSet.add(vnetTraffic);
    }

    @Override
    public void modifyVnet(Traffic traffic, Long newVnetId) {

    }

    @Override
    public void restart(VnetTraffic vnetTraffic) {

    }

    @Override
    public VnetTraffic getVnetTrafficInfo(Traffic traffic) {
        for (VnetTraffic vnetTraffic : vnetTrafficSet) {
            if (vnetTraffic.getTraffic().equals(traffic)) {
                return vnetTraffic;
            }
        }
        return null;
    }
}
