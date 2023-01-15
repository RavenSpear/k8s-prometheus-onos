<template>
  <div>
    <el-row :gutter="20" class="mgb20">
      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span class="title">物理网络拓扑</span>
          </div>
          <div id="pNetwork" class="topo"></div>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card>
          <div slot="header" class="clearfix">
            <span class="title">网络资源消耗情况</span>
          </div>

          <div id="pStatistic">
            <iframe
              v-if="load"
              v-bind:src="url"
              frameborder="0"
              width="100%"
              height="700px"
            ></iframe>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  getNetworkTopoDevices,
  getNetworkTopoLinks,
  getHosts,
  getAllVNRs,
  getVirtualNetworks,
  getAllLinkResources,
} from "../../../api";

require("vis-network/dist/dist/vis-network.min.css");
const vis = require("vis-network/dist/vis-network.min");

export default {
  data() {
    return {
      devicesNum: 0,
      linksNum: 0,
      hostsNum: 0,
      requestNum: 0,
      vnetNum: 0,
      url: "",
      load: false,
    };
  },
  created() {
    this.loadPhysicalNetwork();
    this.loadVirtualNetwork();
    // this.loadResourceInfo();
    this.makeURL();
  },
  methods: {
    makeURL() {
      this.url = `http://223.3.94.112:3000/d/DSz0UFKVk/chuan-shu-zi-yuan-xu-ni-hua-kong-zhi-shou-ye?orgId=1&theme=light&kiosk`;
      this.load = true;
    },
    async loadPhysicalNetwork() {
      // 请求获取物理网络拓扑数据
      var devicesData = await (await getNetworkTopoDevices()).data.devices;
      var linksData = await (await getNetworkTopoLinks()).data.links;
      var hostsData = await (await getHosts()).data.hosts;

      var linkResources = await (
        await getAllLinkResources()
      ).data.linkResources;
      console.log(linkResources);

      this.devicesNum = devicesData.length;
      this.linksNum = linksData.length / 2;
      this.hostsNum = hostsData.length;

      let nodes = [];
      let edges = [];

      let devicesIdMap = {}; // deviceId与其node id的map
      // let linksMap = {}; // link与其ID的map
      //let clusterIdMap = {};
      //let clusterIndex = 1;

      let nodeIndex = 1;
      devicesData.forEach((device) => {
        // 分配节点编号
        devicesIdMap[device] = nodeIndex;

        let node = {
          id: nodeIndex++,
          label: device,
          shape: "image",
          image: require("../../../assets/img/router.png"),
        };
        nodes.push(node);
      });
      console.log(nodeIndex);
      var maxBwMatrix = new Array();
      for (var i = 0; i < nodeIndex - 1; i++) {
        maxBwMatrix[i] = new Array();
        for (var j = 0; j < nodeIndex - 1; j++) {
          maxBwMatrix[i][j] = 0;
        }
      }

      linkResources.forEach((linkResource) => {
        var a = devicesIdMap[linkResource.src];
        var b = devicesIdMap[linkResource.dst];
        maxBwMatrix[a - 1][b - 1] = linkResource.sumBandwidth * 100;
        console.log(a + "->" + b + ":" + maxBwMatrix[a - 1][b - 1]);
      });

      // 构建二维数组，记录每个链路的

      linksData.forEach((link) => {
        var srcIndex = devicesIdMap[link.src.device];
        var dstIndex = devicesIdMap[link.dst.device];
        let edge = {
          from: srcIndex,
          to: dstIndex,
          label: maxBwMatrix[srcIndex - 1][dstIndex - 1] + "M",
        };
        if (edge.from < edge.to) {
          edges.push(edge);
        }
      });

      hostsData.forEach((host) => {
        let deviceAttached = host.locations[0].elementId;
        let ipAddress = host.ipAddresses[0];
        let label = "节点";
        if (ipAddress == "172.1.113.58") {
          label = "云节点";
        } else if (ipAddress == "223.26.35.1") {
          label = "边缘节点2";
        } else {
          label = "边缘节点1";
        }
        let node = {
          id: nodeIndex,
          // label: "节点" + clusterIndex++,
          label: label,
          shape: "image",
          image: require("../../../assets/img/host.png"),
          title: "ip: " + ipAddress,
        };
        let edge = {
          from: devicesIdMap[deviceAttached],
          to: nodeIndex,
          dashes: true,
        };

        nodes.push(node);
        edges.push(edge);

        nodeIndex++;
      });

      var container = document.getElementById("pNetwork");

      var options = {
        nodes: {
          size: 40,
          font: {
            size: 32,
          },
          borderWidth: 3,
          shadow: true,
        },
        edges: {
          width: 3,
          length: 250,
          hoverWidth: 4,
          color: "orange",
          font: {
            size: 25,
            bold: {
              color: "#343434",
              size: 30,
              face: "arial",
              vadjust: 0,
              mod: "bold",
            },
          },
        },
      };
      var data = {
        nodes: nodes,
        edges: edges,
      };

      this.network = new vis.Network(container, data, options);
    },
    async loadVirtualNetwork() {
      var vnrs = await (await getAllVNRs()).data.VNRs;
      var vnets = await (await getVirtualNetworks()).data.VNets;

      this.requestNum = vnrs.length;
      this.vnetNum = vnets.length;
    },
    // async loadResourceInfo() {
    //   var linkResources = await (
    //     await getAllLinkResources()
    //   ).data.linkResources;
    //   let sum = 0;
    //   let allocated = 0;
    //   linkResources.forEach((linkResource) => {
    //     sum += linkResource.sumBandwidth;
    //     allocated += linkResource.allocatedBandwidth;
    //   });
    //   // this.options1.datasets.data.push(allocated);
    //   // this.options1.datasets.data.push(sum-allocated);
    //   // console.log(sum);
    //   // console.log(allocated);
    // },

    toVNRPage() {
      this.$router.push({
        path: "/virtualNetworkRequest",
      });
    },
    toVNetPage() {
      this.$router.push({
        path: "/virtualNetworkList",
      });
    },
  },
};
</script>

<style scoped>
.mgb20 {
  margin-bottom: 20px;
}

.topo {
  height: 700px;
}

.statistic {
  height: 600px;
}

.title {
  font-size: 26px;
  font-weight: bold;
}

.grid-content {
  display: flex;
  align-items: center;
  height: 100px;
}

.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 20px;
  color: #999;
}

.grid-num {
  font-size: 32px;
  font-weight: bold;
}

.icon-lg-size {
  font-size: 40px;
}

.grid-con-icon {
  font-size: 50px;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  color: #fff;
}

.grid-content-1 .grid-con-icon {
  background: rgb(0, 191, 255);
}

.grid-content-1 .grid-num {
  color: rgb(0, 191, 255);
}

.grid-content-2 .grid-con-icon {
  background: orange;
}

.grid-content-2 .grid-num {
  color: orange;
}

.grid-content-3 .grid-con-icon {
  background: grey;
}

.grid-content-3 .grid-num {
  color: grey;
}

.grid-content-4 .grid-con-icon {
  background: blue;
}

.grid-content-4 .grid-num {
  color: blue;
}

.grid-content-5 .grid-con-icon {
  background: rgb(132, 238, 132);
}

.grid-content-5 .grid-num {
  color: rgb(132, 238, 132);
}

.grid-content-6 .grid-con-icon {
  background: red;
}

.grid-content-6 .grid-num {
  color: red;
}
.statistic {
  height: 600px;
}
</style>