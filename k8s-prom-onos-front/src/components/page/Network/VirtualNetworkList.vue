<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>传输资源虚拟化控制</el-breadcrumb-item>
        <el-breadcrumb-item>虚拟网络列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">

      <el-table :data="tableData" class="table">
        <el-table-column
          prop="virtualNetworkId"
          label="虚拟网络Id"
        ></el-table-column>
        <el-table-column prop="bandwidth" label="带宽需求"></el-table-column>
        <el-table-column prop="delay" label="时延需求"></el-table-column>
        <el-table-column prop="traffic" label="业务流量"></el-table-column>
        <el-table-column prop="status" label="网络状态"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-ali-eye iconfont"
              @click="topoShow(scope.row)"
              >查看</el-button
            >
            <el-button
              type="text"
              icon="el-icon-delete"
              class="red"
              @click="topoShow(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { fetchVirtualNetworkData, embedVirtualNetworkTopo } from "../../../api";
export default {
  data() {
    return {
      query: {},
      tableData: [{
        virtualNetworkId: "1546137",
        bandwidth: "10M",
        delay: "1ms",
        traffic: "10.0.0.1->10.0.0.2",
        status: "正在运行"

      },
      {
        virtualNetworkId: "1546137",
        bandwidth: "10M",
        delay: "1ms",
        traffic: "10.0.0.1->10.0.0.2",
        status: "正在运行"

      },
      {
        virtualNetworkId: "1546137",
        bandwidth: "10M",
        delay: "1ms",
        traffic: "10.0.0.1->10.0.0.2",
        status: "正在运行"

      }
    
    ],
    };
  },
  created() {
    this.getData();



  },
  methods: {
    getData() {
      fetchVirtualNetworkData(this.query).then((res) => {
        this.tableData = res.data.vnets;
      });
    },
    topoShow(row) {
      this.$router.push({
        path: "/virtualNetworkTopo",
        query: {
          id: row.virtualNetworkId,
        },
      });
    },
    
    embed(row) {
      let q = {};
      q.id = row.virtualNetworkId;
      console.log(q.id);
      embedVirtualNetworkTopo(q).then((res) => {
        console.log(res);
      });
    },
  },
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 120px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}
.table {
  width: 100%;
  font-size: 14px;
}
.red {
  color: #ff0000;
}
.mr10 {
  margin-right: 10px;
}
.table-td-thumb {
  display: block;
  margin: auto;
  width: 40px;
  height: 40px;
}
</style>