<template>
  <div>
    <!-- 面包导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/virtualNetworkDashboard' }"
          >网络资源虚拟化控制</el-breadcrumb-item
        >
        <el-breadcrumb-item>网络服务列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="handle-box">
        <el-button
          size="mini"
          type="primary"
          icon="el-icon-plus"
          @click="registerService"
          class="mr10"
          >服务注册</el-button
        >

        <el-select
          v-model="query.status"
          placeholder="运行状态"
          class="handle-select mr10"
        >
          <el-option key="0" label="运行中" value="0"></el-option>
          <el-option key="1" label="停止" value="1"></el-option>
        </el-select>

        <el-input
          v-model="query.serviceName"
          placeholder="输入服务名称"
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
          align="center"
          prop="serviceName"
          label="网络服务名称"
        ></el-table-column>
        <el-table-column
          align="center"
          prop="vnetName"
          label="所属虚拟网络"
        ></el-table-column>

        <el-table-column
          align="center"
          prop="traffic"
          label="服务内容"
        ></el-table-column>

        <el-table-column align="center" prop="status" label="当前状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 1" type="success">运行</el-tag>
            <el-tag v-else type="danger">停止</el-tag>
          </template>
        </el-table-column>
        <el-table-column align="center" prop="createTime" label="操作">
          <template slot-scope="scope">
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
            <el-button
              type="text"
              icon="el-icon-setting"
              @click="start(scope.row)"
              >切换</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getAllServices } from "../../../api";
export default {
  data() {
    return {
      query: {
        status: "",
        serviceName: "",
      },
      tableData: [],
    };
  },
  created() {
    this.loadTableData();
  },
  methods: {
    async loadTableData() {
      var vnetTraffics = await (await getAllServices()).data.vnetTraffics;
      vnetTraffics.forEach((item) => {
        this.tableData.push({
          serviceName: item.serviceName,
          vnetName: item.vnetName,
          vnetId: item.vnetId,
          status: item.status,
          traffic: this.formatTraffic(item.traffic),
        });
      });
    },
    registerService() {
      this.$router.push({
        path: "/serviceRegister",
      });
    },
    handleSearch() {},
    formatTraffic(traffic) {
      return (
        "( " +
        traffic.srcIP +
        ", " +
        traffic.dstIP +
        ", " +
        traffic.type +
        ", " +
        traffic.srcPort +
        ", " +
        traffic.dstPort +
        " )"
      );
    },
    start(row) {
      console.log(row);
    },
    stop(row) {
      console.log(row);
    },
    change(row) {
      console.log(row);
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