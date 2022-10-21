<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>拓扑管理</el-breadcrumb-item>
        <el-breadcrumb-item>网络拓扑</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <network
        class="network"
        ref="network"
        :nodes="nodes"
        :edges="edges"
        :options="options"
      ></network>
    </div>
  </div>
</template>

<script>
import { getDevices, getLinks } from "../../../api/index";

export default {
  data() {
    return {
      nodes: [],
      edges: [],
      options: {
        nodes: {
          shape: "circle",
          size: 12,
          color: {
            border: "grey",
            highlight: {
              border: "black",
              background: "white",
            },
            hover: {
              border: "orange",
              background: "grey",
            },
          },
          font: { color: "black" },
        },
        edges: {},
      },
    };
  },
  created() {
    this.getTopoData();
  },
  methods: {
    getTopoData() {
      getDevices().then((res) => {
        let nodes = res.data.devices;
        getLinks().then((res) => {
          let links = res.data.links;
          this.translateTopoData(nodes, links);
        });
      });
    },
    translateTopoData(nodes, links) {
      let map = new Map();
      nodes.forEach((item, index) => {
        this.nodes.push({ id: index + 1, label: item.id, shape: "circle" });
        map.set(item.id, index + 1);
      });
      links.forEach((item) => {
        let a = map.get(item.dst.device);
        let b = map.get(item.src.device);
        if (a < b) {
          this.edges.push({ from: a, to: b });
        }
      });
    },
  },
  networkEvent(eventName) {
    if (this.networkEvents.length > 500) this.networkEvents = "";
    this.networkEvents += `${eventName}, `;
  },
};
</script>

<style scoped>
* {
  font-family: sans-serif;
}
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