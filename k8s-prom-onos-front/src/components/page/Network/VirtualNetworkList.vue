<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/virtualNetworkDashboard' }"
          >传输资源虚拟化控制</el-breadcrumb-item
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
              type="text"
              icon="el-icon-plus"
              @click="registerService(scope.row)"
            >
              服务注册
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getVirtualNetworks } from "../../../api";
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
        res.data.VNets.forEach((item) => {
          this.tableData.push({
            vnetId: item.vnetId,
            vnrId: item.vnrId,
            vnetName: item.vnetName,
            createTime: this.formatDate(item.createTime),
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
        vnetName: row.vnetName
      }
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