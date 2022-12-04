package cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class Flow {

    // 商品编号
    public int commodityId;
    // 路径信息
    public List<Integer> path;
    // 带宽值
    public double bandwidth;

    public Flow(int commodityId) {
        this.commodityId = commodityId;
        this.bandwidth = 0; //表示未分配
        this.path = new ArrayList<>();
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("commodityId", this.commodityId)
                .add("path", this.path)
                .add("bandwidth", this.bandwidth)
                .toString();
    }
}
