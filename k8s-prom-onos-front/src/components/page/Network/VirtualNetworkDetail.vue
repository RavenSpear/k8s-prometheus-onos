<template>
  <div>
    <div class="container">
      <div class="desc"><span>虚拟网络映射信息</span></div>

      <div class="desc-small mgt20"><span>节点映射表</span></div>
      <el-table :data="nodeMappingTable" class="table" border>
        <el-table-column
          prop="vDeviceId"
          label="虚拟交换机"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="pDeviceId"
          label="物理交换机"
          align="center"
        ></el-table-column>
      </el-table>

      <div class="desc-small mgt20"><span>链路映射表</span></div>
      <el-table
        :data="linkMappingTable"
        class="table"
        :span-method="handleSpanMethod"
      >
        <el-table-column
          prop="vlink"
          label="虚拟链路"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="vlinkBandwidthProperty"
          label="带宽属性"
          align="center"
        ></el-table-column>
        <el-table-column label="链路映射" align="center">
          <el-table-column
            prop="allocatedBandwidth"
            align="center"
            label="分配带宽"
          >
          </el-table-column>
          <el-table-column prop="path" align="center" label="映射路径">
          </el-table-column>
        </el-table-column>
      </el-table>

      <div class="desc mgt40">
        <span>虚拟网络映射示意图</span>
        <span v-if="isVisible" class="mgl10 toggle" @click="changeStatus"
          >[ 隐藏 ]</span
        >
        <span v-else class="mgl10 toggle" @click="changeStatus">[ 显示 ]</span>
      </div>

      <el-row
        :gutter="20"
        v-show="isVisible"
        type="flex"
        justify="center"
        align="middle"
        class="mgb40"
      >
        <el-col :span="11">
          <el-card class="topo">
            <div slot="header" class="clearfix">
              <span>虚拟网络拓扑</span>
            </div>
            <network
              class="network"
              ref="virtualNetwork"
              :nodes="nodes"
              :edges="edges"
            ></network>
          </el-card>
        </el-col>
        <el-col :span="1">
          <el-image :src="require('@/assets/img/arrow-right.png')"></el-image>
        </el-col>
        <el-col :span="11">
          <el-card class="topo">
            <div slot="header" class="clearfix">
              <span>物理网络拓扑</span>
            </div>
            <network
              class="network"
              ref="physicalNetwork"
              :nodes="xnodes"
              :edges="xedges"
            ></network>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getVirtualNetworkById } from "../../../api";
export default {
  data() {
    return {
      activeNames: [],
      nodeMappingTable: [],
      linkMappingTable: [],
      isVisible: true,
      a: [],
      nodes: [],
      edges: [],
      xnodes: [],
      xedges: [],
    };
  },
  created() {
    this.getData();
  },
  methods: {
    getData() {
      let vnetId = this.$route.query.vnetId;
      getVirtualNetworkById(vnetId).then((res) => {
        let details = res.data;
        console.log(details);
        // 更新虚拟设备表
        details.virtualDevices.forEach((item) => {
          this.nodeMappingTable.push({
            vDeviceId: item.deviceId,
            pDeviceId: item.physicalDeviceId,
          });
        });

        //更新虚拟链路表;
        details.virtualLinks.forEach((item) => {
          item.pathList.forEach((item2) => {
            this.linkMappingTable.push({
              vlink: item.src.device + " => " + item.dst.device,
              vlinkBandwidthProperty: item.bandwidth + " M",
              allocatedBandwidth: item2.allocatedBandwidth + " M",
              path: item2.path.join(" => "),
            });
          });
        });

        // 合并单元格
        this.mergeTableCell(this.linkMappingTable);

        // 显示映射拓扑图
        this.load_topo(details);
      });
    },
    changeStatus() {
      this.isVisible = !this.isVisible;
    },
    mergeTableCell(data) {
      // 合并单元格
      if (data.length > 1) {
        // table的数据超过一条 需要判定是否进行表格
        this.a = []; // 全部清空
        this.a.push(1);

        let cursor = 0; // 指针 遇到不同的值才会移动
        let lastValue = data[0].vlink;

        for (var i = 1; i < data.length; i++) {
          if (data[i].vlink == lastValue) {
            this.a[cursor] += 1;
            this.a.push(0);
          } else {
            // 遇到新的值
            cursor = i;
            lastValue = data[i].vlink;
            this.a.push(1);
          }
        }
      } else if (data.length == 1) {
        this.a = []; // 全部清空
        this.a.push(1);
      }
    },
    handleSpanMethod({ rowIndex, columnIndex }) {
      // 合并单元格
      if (columnIndex == 0 || columnIndex == 1) {
        let rowSpan = this.a[rowIndex];
        return {
          rowspan: rowSpan,
          colspan: 1,
        };
      } else {
        return {
          rowspan: 1,
          colspan: 1,
        };
      }
    },
    load_topo(data) {
      // 加载虚拟网络
      let map = {};
      let node_number = 0;
      data.virtualDevices.forEach((item) => {
        map[item.deviceId] = node_number;
        this.nodes.push({
          id: node_number,
          label: item.deviceId,
          shape: "circle",
          widthConstraint: 60
        });
        node_number++;
      });

      data.virtualLinks.forEach((item) => {
        let a = map[item.src.device];
        let b = map[item.dst.device];
        if (a < b) {
          // 去重
          this.edges.push({ from: a, to: b, label: item.bandwidth + "M" });
        }
      });

      // 加载物理网络
      let xnodeMap = {};
      
      let xnode_number = 0;

      let xlinkMap = {};
      data.virtualLinks.forEach((item) => {

        item.pathList.forEach((item2) => {
          let path = item2.path;
          // 添加节点
          for (var i = 0; i < path.length; i++) {
            if (!(path[i] in xnodeMap)) {
                xnodeMap[path[i]] = xnode_number;
              this.xnodes.push({
                id: xnode_number,
                label: path[i],
                shape: "circle",
                widthConstraint: 60
              });
              xnode_number++;
            }
          }

          // 统计所有的边的
          for (var j = 0; j < path.length - 1; j++) {
            let a = xnodeMap[path[j]];
            let b = xnodeMap[path[j + 1]];

            if (a < b) {
              let key = a + "," + b;
              if (key in xlinkMap) {
                xlinkMap[key] += item2.allocatedBandwidth;
              }else{
                xlinkMap[key] = item2.allocatedBandwidth;
              }
            }
          }
        });
      });


      for(var v in xlinkMap){
        let arr = v.split(",");
        let a = arr[0];
        let b = arr[1];
        this.xedges.push({ from: a, to: b, label: xlinkMap[v] + "M" });
      }

    },
  },
};
</script>

<style scoped>
.mgb20 {
  margin-bottom: 20px;
}

.mgt20 {
  margin-top: 20px;
}
.mgt40 {
  margin-top: 40px;
}

.mgb40 {
  margin-bottom: 40px;
}

.mgl10 {
  margin-left: 10px;
}

.desc {
  margin-bottom: 20px;
  font-size: 22px;
  font-weight: bold;
}

.desc-small {
  margin-bottom: 20px;
  font-size: 20px;
}

.toggle {
  font-size: 15px;
  font-weight: lighter;
  font-style: italic;
  color: grey;
  cursor: pointer;
}

.topo {
  font-size: 20px;
}

.network {
  height: 360px;
}
</style>