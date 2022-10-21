<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>虚拟网络</el-breadcrumb-item>
        <el-breadcrumb-item>虚拟网络拓扑</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <el-row :gutter="20">
        <el-col :span="12">
          虚拟拓扑：
          <network
            class="network"
            ref="network"
            :nodes="nodes"
            :edges="edges"
          ></network>
        </el-col>

        <el-col :span="12">
          底层映射拓扑：
          <network
            class="network"
            ref="network"
            :nodes="xnodes"
            :edges="xedges"
          ></network>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getVirtualNetworkTopo } from "../../../api/index";
export default {
  name: "virtualNetworkTopo",
  data() {
    return {
      nodes: [],
      edges: [],
      xnodes: [],
      xedges: [],
    };
  },
  methods: {
    create_mapping_topo(){
      this.xnodes = [
        {id:1,label:"s1", shape: "circle",size: 12},
        {id:2,label:"s2", shape: "circle",size: 12},
        {id:3,label:"s3", shape: "circle",size: 12},
        {id:4,label:"s4", shape: "circle",size: 12},
        {id:5,label:"s5", shape: "circle",size: 12},
        {id:6,label:"s6", shape: "circle",size: 12},
        {id:7,label:"s7", shape: "circle",size: 12},
        {id:8,label:"h1", shape: "box",color: "#ff9900",size: 12},
        {id:9,label:"h2",  shape: "box",color: "#ff9900",size: 12}
      ],
      this.xedges = [
       { from: 1, to: 2, label: "9M" },
       { from: 2, to: 3, label: "1M" },
       { from: 3, to: 4, label: "1M" },
       { from: 4, to: 5, label: "1M" },
       { from: 5, to: 7, label: "9M" },
       { from: 2, to: 6, label: "8M" },
       { from: 6, to: 5, label: "8M" },
       { from: 8, to: 1},
       { from: 9, to: 7}
      ]
    },
    load_topo() {
      let vnetId = this.$route.query.id;
      if (vnetId == undefined) return;
      let query = {
        id: vnetId,
      };
      getVirtualNetworkTopo(query).then((res) => {
        let map = new Map();
        let node_number = 0;
        res.data.nodes.switches.forEach((item) => {
          map.set(item.name, node_number);
          this.nodes.push({
            id: node_number,
            label: item.name,
            shape: "circle",
            size: 12,
          });
          node_number++;
        });

        let host_number = 1;
        res.data.nodes.hosts.forEach((item) => {
          this.nodes.push({
            id: node_number,
            label: "h" + host_number,
            shape: "box",
            color: "#ff9900",
            title: "mac: " + String(item.mac) + " ip: " + String(item.ip),
          });
          let s_id = map.get(item.connect.switch);
          this.edges.push({ from: s_id, to: node_number });

          node_number++;
          host_number++;
        });

        res.data.links.forEach((item) => {
          let a = map.get(item.src.switch);
          let b = map.get(item.dst.switch);
          this.edges.push({ from: a, to: b, label: item.bandwidth + "M" });
        });
      });
    },
  },

  activated() {},
  created() {this.load_topo();
  this.create_mapping_topo()},
};
</script>

<style scoped>
.events {
  text-align: left;
  height: 70px;
}
.network {
  height: 400px;
  border: 1px solid #ccc;
  margin: 5px 0;
}
</style>