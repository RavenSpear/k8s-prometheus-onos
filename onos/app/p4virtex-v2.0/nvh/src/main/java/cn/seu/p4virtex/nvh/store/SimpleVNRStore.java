package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.vnr.VNR;
import org.onosproject.core.CoreService;
import org.onosproject.core.IdGenerator;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单实现虚拟网络请求对象的存储
 */
@Component(immediate = true, service = VNRStore.class)
public class SimpleVNRStore implements VNRStore {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String VNR_ID_TOPIC = "vnr-ids-topic";

    private Map<Long, VNR> vnrMap; // 存储虚拟网络请求对象

    private IdGenerator vnrIdGenerator; //用于生成唯一虚拟网络请求ID

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected CoreService coreService;

    @Activate
    public void activate() {
        vnrIdGenerator = coreService.getIdGenerator(VNR_ID_TOPIC);
        vnrMap = new HashMap<>();
    }

    @Deactivate
    public void deactivate() {
        vnrMap.clear();
    }


    @Override
    public void addVNR(VNR vnr) {
        // 生成VNR ID
        Long id = vnrIdGenerator.getNewId() + 1;
        vnr.setId(id);
        vnrMap.put(id, vnr);
    }

    @Override
    public VNR getVNRById(Long id) {
        return vnrMap.get(id);
    }

    @Override
    public List<VNR> getAllVNRs() {
        return new ArrayList<>(vnrMap.values());
    }

}
