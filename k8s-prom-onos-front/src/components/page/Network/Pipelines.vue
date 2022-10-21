<template>
  <div>
    <!-- 面包屑导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item> P4 Pipeline</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="handle-box">
        <el-button type="primary" icon="el-icon-ali-add1 iconfont" class="mr10"
          >添加</el-button
        >
        <el-input
          v-model="query.name"
          placeholder="Pipeline名称"
          class="handle-input mr10"
        ></el-input>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch"
          >搜索</el-button
        >
      </div>
      <el-table :data="tableData" class="table">
        <el-table-column label="名称" prop="name"></el-table-column>
        <el-table-column label="创建时间" prop="createTime"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-delete"
              class="red"
              @click="handleDelete(scope.$index, scope.row)"
              >删除</el-button
            >
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          background
          layout="total, prev, pager, next"
          :current-page="query.pageIndex"
          :page-size="query.pageSize"
          :total="pageTotal"
          @current-change="handlePageChange"
        ></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "pipelines",
  data() {
    return {
      query: {
        name: "",
        pageIndex: 1,
        pageSize: 10,
      },
      tableData: [
        {
          name: "base pipeline",
          createTime: "2022-01-28",
        },
        {
          name: "srv6 pipeline",
          createTime: "2022-01-27",
        },
      ],
      pageTotal: 0,
    };
  },
  methods: {
    handleDelete(index, row) {
      console.log(index, row);
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