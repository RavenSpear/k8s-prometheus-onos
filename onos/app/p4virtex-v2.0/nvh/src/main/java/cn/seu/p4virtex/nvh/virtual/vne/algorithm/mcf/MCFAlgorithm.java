package cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 虚拟链路映射算法：MCF
 */
public class MCFAlgorithm {

    private List<Arc> arcs;
    private List<Commodity> commodities;

    private List<Node> nodes;
    private Map<Integer, Node> nodeMap;

    private Map<String, MPVariable> variableMap;
    private MPSolver solver;

    public MCFAlgorithm(List<Arc> arcs, List<Commodity> commodities) {
        this.arcs = arcs;
        this.commodities = commodities;
        this.variableMap = new HashMap<>();
        initNodes();
        initSolver();
    }

    private void initNodes() {
        nodeMap = new HashMap<>();
        nodes = new ArrayList<>();

        for (Arc arc : arcs) {
            Node node1 = nodeMap.getOrDefault(arc.src, new Node(arc.src));
            Node node2 = nodeMap.getOrDefault(arc.dst, new Node(arc.dst));
            node1.addOutLinks(node2);
            node2.addInLinks(node1);
            nodeMap.put(arc.src, node1);
            nodeMap.put(arc.dst, node2);
        }

        for (int i : nodeMap.keySet()) {
            nodes.add(nodeMap.get(i));
        }
    }

    private void initSolver() {
        solver = MPSolver.createSolver("SCIP");
    }

    public List<Flow> solve() {
        // 构建变量x[k,i,j] k-商品流的编号 (i,j)- link
        double infinity = java.lang.Double.POSITIVE_INFINITY;
        for (Arc arc : arcs) {
            for (Commodity commodity : commodities) {
                String variableName = "x[" + commodity.id + "," + arc.src + "," + arc.dst + "]";
                MPVariable x = solver.makeIntVar(0.0, infinity, variableName);
                variableMap.put(variableName, x);
            }
        }

        // 添加约束条件
        // 约束1：链路带宽限制
        for (Arc arc : arcs) {
            String constraintName = "capacity[" + arc.src + "," + arc.dst + "]";
            MPConstraint c = solver.makeConstraint(0, arc.capacity, constraintName);
            for (Commodity commodity : commodities) {
                String variableName = "x[" + commodity.id + "," + arc.src + "," + arc.dst + "]";
                MPVariable variable = variableMap.get(variableName);
                c.setCoefficient(variable, 1);
            }
        }
        // 约束2：流通性约束
        for (Commodity commodity : commodities) {
            for (Node node : nodes) {
                String constraintName = "continuity[" + commodity.id + "," + node.no + "]";
                MPConstraint c = solver.makeConstraint(constraintName);
                // 流出的流量
                for (Node outNode : node.outLinks) {
                    String variableName = "x[" + commodity.id + "," + node.no + "," + outNode.no + "]";
                    MPVariable variable = variableMap.get(variableName);
                    c.setCoefficient(variable, 1);
                }

                // 流入的流量
                for (Node inNode : node.inLinks) {
                    String variableName = "x[" + commodity.id + "," + inNode.no + "," + node.no + "]";
                    MPVariable variable = variableMap.get(variableName);
                    c.setCoefficient(variable, -1);
                }

                if (node.no == commodity.src) {
                    c.setBounds(commodity.quantity, commodity.quantity);
                } else if (node.no == commodity.dst) {
                    c.setBounds(-commodity.quantity, -commodity.quantity);
                } else {
                    c.setBounds(0, 0);
                }
            }
        }

        // 目标函数
        MPObjective objective = solver.objective();
        for (MPVariable mp : solver.variables()) {
            objective.setCoefficient(mp, 1);
        }
        objective.minimization();

        final MPSolver.ResultStatus resultStatus = solver.solve();

        // 存在最优解
        if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
            List<Flow> res = new ArrayList<>();
            double minBandwidth = Double.MAX_VALUE;
            // DFS解析
            for (Commodity commodity : commodities) {
                int src = commodity.src;
                int dst = commodity.dst;
                List<Integer> path = new ArrayList<>();
                dfs(commodity.id, src, dst, minBandwidth, path, res, variableMap, nodeMap);
            }
            return res;
        } else {
            return null;
        }
    }

    private void dfs(int commodityId, int src, int target, double minBandwidth, List<Integer> path, List<Flow> res, Map<String, MPVariable> variableMap, Map<Integer, Node> nodeMap) {

        path.add(src);

        if (src == target) {
            Flow flow = new Flow(commodityId);
            flow.path = new ArrayList<>(path);
            flow.bandwidth = minBandwidth;
            res.add(flow);
            path.remove(path.size() - 1);
            return;
        }

        Node node = nodeMap.get(src);

        for (Node tmp : node.outLinks) {
            String variableName = "x[" + commodityId + "," + src + "," + tmp.no + "]";
            double value = variableMap.get(variableName).solutionValue();
            if (value != 0) {
                dfs(commodityId, tmp.no, target, Math.min(minBandwidth, value), path, res, variableMap, nodeMap);
            }
        }
        path.remove(path.size() - 1);
    }
}

