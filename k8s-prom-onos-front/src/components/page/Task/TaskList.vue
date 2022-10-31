<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>边云协同任务调度</el-breadcrumb-item>
                <el-breadcrumb-item>任务列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <div class="container">
            <el-table :data="tableData">
        <el-table-column prop="name" label="任务名" align="center"></el-table-column>
        <el-table-column prop="version" label="版本"  align="center"></el-table-column>
        <el-table-column prop="taskNum" label="副本数"  align="center"></el-table-column>
        <el-table-column prop="CPUReq" label="CPU需求"  align="center"></el-table-column>
        <el-table-column prop="memReq" label="内存需求"  align="center"></el-table-column>
        <el-table-column prop="diskReq" label="磁盘需求"  align="center"></el-table-column>
        <el-table-column prop="ratio" label="边云负载比"  align="center"></el-table-column>
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <el-button
              type="text"
              icon="el-icon-ali-eye iconfont"
              @click="topoShow(scope.row)"
              >查看</el-button
            >
          </template>
        </el-table-column>
      </el-table>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            tableData: [{
                name: "智慧视频",
                version: "1.1",
                taskNum: "5",
                CPUReq: "100m",
                memReq: "500MB",
                diskReq: "10Gi",
                ratio: "0.67"
            },
            {
                name: "高清视频",
                version: "2.1",
                taskNum: "5",
                CPUReq: "50m",
                memReq: "800MB",
                diskReq: "20Gi",
                ratio: "1.5"
            },
            {
                name: "智能家居",
                version: "1.1",
                taskNum: "8",
                CPUReq: "80m",
                memReq: "600MB",
                diskReq: "30Gi",
                ratio: "1"
            }
            ]
        };
    },
    created() {
        this.getData();
    },
    methods: {
        getData() {

        },
        topoShow() {

        },
        embed() {

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
    font-size: 22px;
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