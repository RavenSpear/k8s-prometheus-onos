<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>租户管理</el-breadcrumb-item>
        <el-breadcrumb-item>租户列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <el-table :data="tableData" class="table">
        <!-- <el-table-column type="index" width="100"></el-table-column> -->
        <el-table-column
          prop="virtualNetworkId"
          label="虚拟网络Id"
        ></el-table-column>
        <el-table-column prop="tenantId" label="所属租户"></el-table-column>
        <el-table-column prop="status" label="状态"></el-table-column>
        <!-- <el-table-column prop="transmissionDelay" label="传输延迟(ms)"></el-table-column> -->
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-ali-eye iconfont"
              @click="topoShow(scope.row)"
              >查看拓扑</el-button
            >
            <el-button
              type="text"
              icon="el-icon-plus"
              class="red"
              @click="embed(scope.row)"
              >嵌入</el-button
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
      tableData: [],
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