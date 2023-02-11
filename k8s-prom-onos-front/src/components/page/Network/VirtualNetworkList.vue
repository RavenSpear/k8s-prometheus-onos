<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/virtualNetworkDashboard' }"
          >感知数据传输优化</el-breadcrumb-item
        >
        <el-breadcrumb-item>虚拟网络列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="handle-box">
        <el-input
          v-model="query.vnetName"
          placeholder="输入虚拟网络名称"
          class="handle-input mr10"
        ></el-input>
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-search"
          @click="handleSearch"
          >搜索</el-button
        >
      </div>

      <el-table :data="tableData" class="table">
        <el-table-column
          prop="vnetId"
          label="虚拟网络Id"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="vnetName"
          label="虚拟网络名称"
          align="center"
        ></el-table-column>
        <el-table-column
          prop="vnrId"
          label="请求编号"
          align="center"
        ></el-table-column>
        <el-table-column prop="status" label="运行状态" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="success">运行中</el-tag>
            <el-tag v-else type="danger">未运行</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          prop="createTime"
          label="嵌入时间"
          align="center"
        ></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-document"
              @click="showVnetDetail(scope.row)"
              >详情</el-button
            >
            <el-button
              v-if="scope.row.status === 0"
              type="text info"
              icon="el-icon-open"
              @click="start(scope.row)"
              >运行</el-button
            >
            <el-button
              v-else
              type="text"
              icon="el-icon-turn-off"
              @click="stop(scope.row)"
              >停止</el-button
            >

            <el-button type="text" icon="el-icon-delete" @click="del(scope.row)"
              >删除</el-button
            >

            <!-- <el-button
              type="text"
              icon="el-icon-plus"
              @click="registerService(scope.row)"
            >
              服务注册
            </el-button> -->
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import {
  getVirtualNetworks,
  startVirtualNetworkById,
  stopVirtualNetworkById,
  deleteVirtualNetwork,
} from "../../../api";
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
      getVirtualNetworks().then((res) => {
        let index = 0;
        res.data.VNets.forEach((item) => {
          this.tableData.push({
            index: index++,
            vnetId: item.vnetId,
            vnrId: item.vnrId,
            vnetName: item.vnetName,
            createTime: this.formatDate(item.createTime),
            status: item.status,
          });
        });
      });
    },
    showVnetDetail(row) {
      let param = {
        vnetId: row.vnetId,
      };
      this.$router.push({
        path: "/virtualNetworkDetail",
        query: param,
      });
    },
    registerService(row) {
      let param = {
        vnetId: row.vnetId,
        vnetName: row.vnetName,
      };
      this.$router.push({
        path: "/serviceRegister",
        query: param,
      });
    },
    formatDate(timestamp) {
      var date = new Date(timestamp); //时间戳为10位需*1000，时间戳为13位的话不需乘1000
      var Y = date.getFullYear() + "-";
      var M =
        (date.getMonth() + 1 < 10
          ? "0" + (date.getMonth() + 1)
          : date.getMonth() + 1) + "-";
      var D =
        (date.getDate() < 10 ? "0" + date.getDate() : date.getDate()) + " ";
      var h =
        (date.getHours() < 10 ? "0" + date.getHours() : date.getHours()) + ":";
      var m =
        (date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()) +
        ":";
      var s =
        date.getSeconds() < 10 ? "0" + date.getSeconds() : date.getSeconds();
      return Y + M + D + h + m + s;
    },
    handleSearch() {},
    start(row) {
      startVirtualNetworkById(row.vnetId);
      row.status = 1;
    },
    stop(row) {
      stopVirtualNetworkById(row.vnetId);
      row.status = 0;
    },
    del(row) {
      let res = confirm("确认删除？");
      if (res == true) {
        this.tableData.splice(row.index, 1);
        deleteVirtualNetwork(row.vnetId);
      }
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