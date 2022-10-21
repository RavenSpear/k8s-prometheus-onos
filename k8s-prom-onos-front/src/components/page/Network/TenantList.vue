<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>租户管理</el-breadcrumb-item>
        <el-breadcrumb-item>租户列表</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <div class="container">
      <div class="handle-box">
        <el-button type="primary" icon="el-icon-ali-add1 iconfont" class="mr10" @click="toAddTenantPage"
          >添加租户</el-button
        >
      </div>

      <el-table :data="tableData" class="table">
        <el-table-column type="index" width="100"></el-table-column>
        <el-table-column prop="id" label="租户名称"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-document-add" @click="toCreateVirtualNetwork"
              >添加虚拟网络</el-button
            >
            <el-button
              type="text"
              icon="el-icon-delete"
              class="red"
              @click="handleDelete(scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import { fetchTenantData, deleteTenant } from "../../../api/index";
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
    toCreateVirtualNetwork(){
      this.$router.push(
        {
          path:"/createVirtualNetwork"
        }
      )
    },
    toAddTenantPage(){
      this.$router.push(
        {
          path:"/addTenant"
        }
      )
    },
    getData() {
      fetchTenantData(this.query).then((res) => {
        this.tableData = res.data.tenants;
      });
    },
    handleDelete(row) {
      deleteTenant(row.id).then((response) => {
        if (response.status === 204) {
          // 重新获取数据，相当于刷新当前页面
          this.getData();
        }
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