package cn.seu.p4virtex.nvh.store;

import cn.seu.p4virtex.nvh.virtual.vnr.VNR;

import java.util.List;

public interface VNRStore {

    void addVNR(VNR vnr);

    VNR getVNRById(Long id);

    List<VNR> getAllVNRs();
}
