<template>
  <div>
    <!-- 面包导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/virtualNetworkDashboard' }"
          >传输资源虚拟化控制</el-breadcrumb-item
        >
        <el-breadcrumb-item>虚拟网络请求</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计虚拟网络请求数据 -->
    <div class="container">
      <div class="handle-box">
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-plus"
          @click="createVNR"
          class="mr10"
          >创建请求</el-button
        >

        <el-select
          v-model="query.status"
          placeholder="处理状态"
          class="handle-select mr10"
        >
          <el-option key="0" label="队列中" value="0"></el-option>
          <el-option key="1" label="已嵌入" value="1"></el-option>
          <el-option key="2" label="拒绝" value="2"></el-option>
        </el-select>

        <el-input
          v-model="query.seqNo"
          placeholder="输入请求编号"
          class="handle-input mr10"
        ></el-input>
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-search"
          @click="handleSearch"
          >搜索</el-button
        >

        <el-button
          icon="el-icon-refresh"
          type="primary"
          size="mini"
          @click="refreshHandle"
          >刷新
        </el-button>
      </div>

      <el-table :data="tableData" class="table">
        <el-table-column
          align="center"
          prop="id"
          label="请求编号"
        ></el-table-column>
        <el-table-column
          align="center"
          prop="end2end"
          label="端到端连接"
        ></el-table-column>
        <el-table-column
          align="center"
          prop="bandwidth"
          label="带宽需求"
        ></el-table-column>
        <el-table-column
          align="center"
          prop="latency"
          label="传输时延需求"
        ></el-table-column>
        <el-table-column align="center" prop="status" label="处理状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 0" type="info">队列中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" type="success"
              >已嵌入</el-tag
            >
            <el-tag v-else type="danger">拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column
          align="center"
          prop="createTime"
          label="提交时间"
        ></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getAllVNRs } from "../../../api";
export default {
  data() {
    return {
      query: {
        seqNo: "",
        status: "",
      },
      tableData: [],
    };
  },
  created() {
    this.getTableData();
  },
  methods: {
    getTableData() {
      getAllVNRs().then((res) => {
        res.data.VNRs.forEach((item) => {
          this.tableData.push({
            id: item.vnrId,
            end2end: item.cluster1IP + " <=> " + item.cluster2IP,
            bandwidth: item.bandwidth + " Mbps",
            latency: item.latency + " ms",
            status: item.status,
            createTime: this.formatDate(item.createTime),
          });
        });
      });
    },
    createVNR() {
      this.$router.push({
        path: "/createVirtualNetworkRequest",
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
    delAllSelection() {},
    handleSearch() {},
    refreshHandle() {
      this.tableData = [];
      this.getTableData();
    },
  },
};
</script>

<style scoped>
.handle-box {
  margin-bottom: 20px;
}

.handle-select {
  width: 180px;
}

.handle-input {
  width: 300px;
  display: inline-block;
}

.mr10 {
  margin-right: 10px;
}

.mgb20 {
  margin-bottom: 20px;
}
</style>