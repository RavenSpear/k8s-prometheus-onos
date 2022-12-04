package cn.seu.p4virtex.nvh.virtual.vnr;

import java.util.List;

public interface VNRService {

    /**
     * 提交虚拟网络请求
     */
    void submitVNR(VNR vnr);

    /**
     * 获取所有的虚拟网络请求对象
     * @return
     */
    List<VNR> getVNRs();

    /**
     * 获取虚拟网络请求对象
     *
     * @Param id 虚拟网络请求id
     *
     * @return
     */
    VNR getVNRById(Long id);

    /**
     * 将当前请求队列中所有的虚拟网络请求全部导出
     *
     * @return
     */
    List<VNR> fetchVNRQueue();



}
