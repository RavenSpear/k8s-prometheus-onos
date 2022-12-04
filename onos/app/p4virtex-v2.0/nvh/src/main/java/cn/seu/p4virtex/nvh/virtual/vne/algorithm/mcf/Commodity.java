package cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * 代表端到端连接
 */
public class Commodity {

    public int id;
    public int src;
    public int dst;
    public double quantity;

    public Commodity(int id, int src, int dst, double quantity) {
        this.id = id;
        this.src = src;
        this.dst = dst;
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("id", this.id)
                .add("src", this.src)
                .add("dst", this.dst)
                .add("quantity", this.quantity)
                .toString();
    }
}
