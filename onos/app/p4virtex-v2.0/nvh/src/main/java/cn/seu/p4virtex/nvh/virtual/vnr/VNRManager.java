package cn.seu.p4virtex.nvh.virtual.vnr;

import cn.seu.p4virtex.nvh.store.VNRStore;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * 虚拟网络请求管理模块，主要管理对象包括：
 * 1. 管理虚拟网络请求对象
 * 2. 管理虚拟网络请求队列
 */
@Component(immediate = true, service = VNRService.class)
public class VNRManager implements VNRService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private Deque<VNR> requestQueue; // 虚拟网络请求队列

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    protected VNRStore vnrStore;

    @Activate
    public void activate() {
        log.info("启动虚拟网络请求管理...");
        log.info("初始化请求队列...");
        requestQueue = new ArrayDeque<>();
    }

    @Deactivate
    public void deactivate() {
        log.info("关闭虚拟网络请求管理...");
    }

    @Override
    public void submitVNR(VNR vnr) {
        // 存储虚拟网络请求对象
        vnrStore.addVNR(vnr);
        // 将虚拟网络请求对象放置到请求队列中
        requestQueue.addLast(vnr);
    }

    @Override
    public List<VNR> getVNRs() {
        return vnrStore.getAllVNRs();
    }

    @Override
    public VNR getVNRById(Long id) {
        return vnrStore.getVNRById(id);
    }

    @Override
    public List<VNR> fetchVNRQueue() {
        List<VNR> rs = new ArrayList<>();
        while (!requestQueue.isEmpty()) {
            rs.add(requestQueue.removeFirst());
        }
        return rs;
    }
}
