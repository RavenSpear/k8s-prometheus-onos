<template>
  <div>
    <!-- 面包导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>传输资源虚拟化控制</el-breadcrumb-item>
        <el-breadcrumb-item>虚拟网络请求</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <!-- 统计虚拟网络请求数据 -->
    <div class="container">
      <!-- <div class="handle-box">
        <el-button
          type="primary"
          icon="el-icon-plus"
          class="handle-del mr10"
          @click="delAllSelection"
          >创建新请求</el-button
        >
        <el-input
          v-model="query.name"
          placeholder="用户名"
          class="handle-input mr10"
        ></el-input>
        <el-button size="mini" type="primary" icon="el-icon-search" @click="handleSearch"
          >搜索</el-button
        >
      </div> -->

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
            <el-tag v-if="scope.row.status === 0" :type="info">队列中</el-tag>
            <el-tag v-else-if="scope.row.status === 1" :type="success">已嵌入</el-tag>
            <el-tag v-else :type="danger">拒绝</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间"></el-table-column>
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
        name: "",
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
          });
          console.log(item);
        });
      });
    },
    delAllSelection() {},
    handleSearch() {},
  },
};
</script>

<style>
.handle-box {
  margin-bottom: 20px;
}
.handle-input {
  width: 300px;
  display: inline-block;
}

.mr10 {
  margin-right: 10px;
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