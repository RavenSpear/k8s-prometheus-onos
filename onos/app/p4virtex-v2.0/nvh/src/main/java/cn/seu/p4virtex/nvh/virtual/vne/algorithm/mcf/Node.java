package cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf;

import java.util.ArrayList;
import java.util.List;

/**
 * 网络拓扑图中的节点对象
 */
public class Node {

    public int no;
    public List<Node> outLinks;
    public List<Node> inLinks;

    public Node(int no) {
        this.no = no;
        this.outLinks = new ArrayList<>();
        this.inLinks = new ArrayList<>();
    }

    public void addInLinks(Node node) {
        this.inLinks.add(node);
    }

    public void addOutLinks(Node node) {
        this.outLinks.add(node);
    }

    @Override
    public String toString() {
        return "" + this.no;
    }
}
