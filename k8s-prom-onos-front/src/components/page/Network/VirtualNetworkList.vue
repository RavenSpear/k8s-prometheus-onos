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
        <el-table-column prop="vnetId" label="虚拟网络Id"></el-table-column>
        <el-table-column prop="vnrId" label="请求编号"></el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-document"
              @click="showVnetDetail(scope.row)"
              >详情</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { getVirtualNetwors} from "../../../api";
export default {
  data() {
    return {
      query: {},
      tableData: [
      ],
    };
  },
  created() {
    this.getData();
  },
  methods: {
    getData() {
      getVirtualNetwors().then((res) => {
        res.data.VNets.forEach((item) => {
          this.tableData.push({
            vnetId: item.vnetId,
            vnrId: item.vnrId,
            createTime: this.formatDate(item.createTime)
          });
        });
      });
    },
    showVnetDetail(row){
      let param = {
        vnetId: row.vnetId
      }
      this.$router.push({
        path: "/virtualNetworkDetail",
        query: param
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