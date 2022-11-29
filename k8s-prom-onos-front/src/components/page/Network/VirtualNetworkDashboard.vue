<template>
  <div>
    <el-row :gutter="20" class="mgb20">
      <el-col :span="12">
        <el-row :gutter="10" class="mgb20">
          <!-- 统计1：交换机的数量 -->
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-content-1">
                <i class="el-icon-s-data icon-lg-size grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ devicesNum }}</div>
                  <div>交换机数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 统计2：数据链路的数量 -->
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-content-2">
                <i class="el-icon-s-data icon-lg-size grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ linksNum }}</div>
                  <div>数据链路数</div>
                </div>
              </div>
            </el-card>
          </el-col>

          <!-- 统计3：集群数 -->
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-content-3">
                <i class="el-icon-s-data icon-lg-size grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ hostsNum }}</div>
                  <div>集群节点数</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row class="mgb20">
          <el-card>
            <div slot="header" class="clearfix">
              <span class="title">物理网络拓扑</span>
            </div>
            <div id="pNetwork" class="topo"></div>
          </el-card>
        </el-row>
      </el-col>

      <el-col :span="12">
        <el-row :gutter="10" class="mgb20">
          <!-- 统计1：虚拟网络请求总数 -->
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-content-4" @click="toVNRPage">
                <i class="el-icon-s-data icon-lg-size grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ requestNum }}</div>
                  <div>总请求数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <!-- 统计2：已经嵌入的虚拟网络数量 -->
          <el-col :span="8">
            <el-card
              shadow="hover"
              :body-style="{ padding: '0px' }"
              @click="toVNetPage"
            >
              <div class="grid-content grid-content-5" @click="toVNetPage">
                <i class="el-icon-s-data icon-lg-size grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ vnetNum }}</div>
                  <div>成功嵌入数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <!-- 统计3：拒绝的请求数 -->
          <el-col :span="8">
            <el-card shadow="hover" :body-style="{ padding: '0px' }">
              <div class="grid-content grid-content-6">
                <i class="el-icon-s-data icon-lg-size grid-con-icon"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ requestNum - vnetNum }}</div>
                  <div>拒绝嵌入数</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row class="mgb20">
          <!-- <iframe v-if="load" v-bind:src="url" frameborder="0" width="100%" height="600px"></iframe> -->

          <el-card>
            <div slot="header" class="clearfix">
              <span class="title">网络资源消耗情况</span>
            </div>
            <div id="pStatistic">
              <schart
                class="statistic"
                canvasId="pie"
                :options="options1"
                :data="mydata"
              ></schart>
            </div>
          </el-card>
        </el-row>
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

import Schart from "vue-schart";

export default {
  components: {
    Schart,
  },
  data() {
    return {
      devicesNum: 0,
      linksNum: 0,
      hostsNum: 0,
      requestNum: 0,
      vnetNum: 0,
      url: "",
      load: false,
      mydata:[
        {name:"已分配", value:50}
      ],
      options1: {
        type: "pie",
        title: {
          text: "带宽资源分配图(Mbps)",
        },
        labels: ["已分配带宽", "剩余带宽"],
        legend: {
          position: "left",
        },
        radius: 150,
        datasets: [
          {
            data: [30, 140],
          },
        ],
      },
    };
  },
  created() {
    this.loadPhysicalNetwork();
    this.loadVirtualNetwork();
    this.loadResourceInfo();
    this.makeURL();
  },
  methods: {
    makeURL() {
      this.url = `http://223.3.94.112:3000/d/hb3HbWFVz/chuan-shu-zi-yuan-kong-zhi-shou-ye?from=1669674520325&to=1669696120325&orgId=1&theme=light&viewPanel=4&kiosk`;
      this.load = true;
    },
    async loadPhysicalNetwork() {
      // 请求获取物理网络拓扑数据
      var devicesData = await (await getNetworkTopoDevices()).data.devices;
      var linksData = await (await getNetworkTopoLinks()).data.links;
      var hostsData = await (await getHosts()).data.hosts;

      this.devicesNum = devicesData.length;
      this.linksNum = linksData.length / 2;
      this.hostsNum = hostsData.length;

      let nodes = [];
      let edges = [];

      let devicesIdMap = {}; // deviceId与其node id的map
      // let linksMap = {}; // link与其ID的map
      //let clusterIdMap = {};
      let clusterIndex = 1;

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

      linksData.forEach((link) => {
        let edge = {
          from: devicesIdMap[link.src.device],
          to: devicesIdMap[link.dst.device],
        };
        if (edge.from < edge.to) {
          edges.push(edge);
        }
      });

      hostsData.forEach((host) => {
        let deviceAttached = host.locations[0].elementId;
        let ipAddress = host.ipAddresses[0];
        let node = {
          id: nodeIndex,
          label: "集群" + clusterIndex++,
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
    async loadResourceInfo() {
      var linkResources = await (
        await getAllLinkResources()
      ).data.linkResources;
      let sum = 0;
      let allocated = 0;
      linkResources.forEach(linkResource=>{
        sum += linkResource.sumBandwidth;
        allocated += linkResource.allocatedBandwidth;
      })
      // this.options1.datasets.data.push(allocated);
      // this.options1.datasets.data.push(sum-allocated);
      console.log(sum);
      console.log(allocated);
    },

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
  height: 600px;
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