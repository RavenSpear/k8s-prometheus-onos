package cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * 网络拓扑中链路对象
 */
public class Arc {

    public int src;
    public int dst;
    public double capacity;

    public Arc(int src, int dst, double capacity) {
        this.src = src;
        this.dst = dst;
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("src", this.src)
                .add("dst", this.dst)
                .add("capacity", this.capacity)
                .toString();
    }
}
