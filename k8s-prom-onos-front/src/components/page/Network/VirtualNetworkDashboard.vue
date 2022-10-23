<!-- <template>
  <div>
    <el-row :gutter="20" class="mgb20">
      <el-col :span="12">
        <el-card style="height: 400px">
          <div slot="header" class="clearfix">
            <span>物理网络拓扑</span>
          </div>
          <div>
            <network
              class="network"
              ref="network"
              :nodes="nodes"
              :edges="edges"
              :options="options"
            ></network>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-row :gutter="10" class="mgb20">
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="grid-content">
                <i class="el-icon-s-custom icon-lg-size"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ tenantNumber }}</div>
                  <div>当前租户数量</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"
              ><div class="grid-content">
                <i class="el-icon-ali-diqiu iconfont icon-lg-size"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ sumBandwidthResource }} M</div>
                  <div>剩余带宽资源</div>
                </div>
              </div></el-card
            >
          </el-col>
          <el-col :span="8">
            <el-card shadow="hover"
              ><div class="grid-content">
                <i
                  class="el-icon-ali-ziyuan-xianxing iconfont icon-lg-size"
                ></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ virtualNetworkNumber }}</div>
                  <div>虚拟网络数量</div>
                </div>
              </div></el-card
            >
          </el-col>
        </el-row>
        <el-row class="mgb20">
          <el-card style="height: 260px"></el-card>
        </el-row>
      </el-col>
    </el-row>

    <!-- <el-row :gutter="20" class="mgb20">
      <el-col :span="12">
        <el-card> </el-card>
      </el-col>
      <el-col :span="12">
        <el-card> </el-card>
      </el-col>
    </el-row> -->
  </div>
</template> -->

<!-- <script>
// import Schart from "vue-schart";
import { getDevices, getLinks, getPorts, getHosts,fetchTenantData,fetchVirtualNetworkData } from "../../../api/index";

export default {
  name: "dashboard",
  data() {
    return {
      fullscreen: false,
      tenantNumber: 0,
      sumBandwidthResource: 62,
      virtualNetworkNumber: 0,
      nodes: [],
      edges: [],
      options: {},
    };
  },
  // components: {
  //   Schart,
  // },
  computed: {},
  methods: {
    toggleApi() {
      this.$fullscreen.toggle();
    },
    changeDate() {},
    translateTopoData(nodes, links, ports, hosts) {
      let map = new Map();
      nodes.forEach((item) => {
        // 截取字符串
        let node_id = parseInt(item.id.slice(8));
        this.nodes.push({
          id: node_id,
          label: item.id,
          size: 20,
          shape: "image",
          image: require("../../assets/img/router.png"),
        });
        map.set(item.id, node_id);
      });
      // 先处理一下port
      let portMap = new Map();
      ports.forEach((item) => {
        portMap.set(item.port, item.portSpeed);
      });
      links.forEach((item) => {
        let a = map.get(item.dst.device);
        let b = map.get(item.src.device);
        if (a < b) {
          // 获取链路的最大带宽,以Mbps为单位
          let bandwidth = portMap.get(item.src.port) / 1000;
          this.edges.push({ from: a, to: b, label: bandwidth + "M" });
        }
      });
      let node_index = nodes.length;

      hosts.forEach((item) => {
        ++node_index;
        let edge_switch_name = item.locations[0].elementId;
        let edge_switch_id = map.get(edge_switch_name);
        this.nodes.push({
          id: node_index,
          label: "server",
          size: 20,
          shape: "image",
          image: require("../../assets/img/host.png"),
        });
        this.edges.push({ from: node_index, to: edge_switch_id, dashes: true });
      });
    },
  },
  created() {
    // 嵌套查询（TODO. 可能不太美观，await没有用上去，以后再说吧）
    getDevices().then((res) => {
      let nodes = res.data.devices;
      getLinks().then((res) => {
        let links = res.data.links;
        getPorts().then((res) => {
          let ports = res.data.ports;
          getHosts().then((res) => {
            let hosts = res.data.hosts;
            this.translateTopoData(nodes, links, ports, hosts);
          });
        });
      });
    });

    fetchTenantData().then((res)=>{
        this.tenantNumber = res.data.tenants.length;
    });

    fetchVirtualNetworkData().then((res)=>{
        this.virtualNetworkNumber = res.data.vnets.length;
    });

  },
};
</script> -->


<style scoped>
.network {
  height: 300px;
  margin: 5px 0;
}

.icon-lg-size {
  font-size: 40px;
}

.el-row {
  margin-bottom: 20px;
}

.grid-content {
  display: flex;
  align-items: center;
  height: 80px;
}

.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.grid-num {
  font-size: 30px;
  font-weight: bold;
}

.grid-con-icon {
  font-size: 50px;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
  color: #fff;
}

.grid-con-1 .grid-con-icon {
  background: rgb(45, 140, 240);
}

.grid-con-1 .grid-num {
  color: rgb(45, 140, 240);
}

.grid-con-2 .grid-con-icon {
  background: rgb(100, 213, 114);
}

.grid-con-2 .grid-num {
  color: rgb(45, 140, 240);
}

.grid-con-3 .grid-con-icon {
  background: rgb(242, 94, 67);
}

.grid-con-3 .grid-num {
  color: rgb(242, 94, 67);
}

.user-info {
  display: flex;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 2px solid #ccc;
  margin-bottom: 20px;
}

.user-avator {
  width: 120px;
  height: 120px;
  border-radius: 50%;
}

.user-info-cont {
  padding-left: 50px;
  flex: 1;
  font-size: 14px;
  color: #999;
}

.user-info-cont div:first-child {
  font-size: 30px;
  color: #222;
}

.user-info-list {
  font-size: 14px;
  color: #999;
  line-height: 25px;
}

.user-info-list span {
  margin-left: 70px;
}

.mgb20 {
  margin-bottom: 20px;
}

.todo-item {
  font-size: 14px;
}

.todo-item-del {
  text-decoration: line-through;
  color: #999;
}

.schart {
  width: 100%;
  height: 300px;
}
</style>
