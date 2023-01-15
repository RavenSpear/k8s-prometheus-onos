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
                <el-table-column prop="version" label="版本" align="center"></el-table-column>
                <el-table-column prop="taskNum" label="子任务数" align="center"></el-table-column>
                <el-table-column prop="CPUReq" label="CPU需求（请求/限制）" align="center"></el-table-column>
                <el-table-column prop="memReq" label="内存需求（请求/限制）" align="center"></el-table-column>
                <el-table-column prop="diskReq" label="磁盘需求（请求/限制）" align="center"></el-table-column>
                <el-table-column prop="bandReq" label="带宽限制（上行/下行）" align="center"></el-table-column>
                <el-table-column prop="virtualNetwork" label="绑定虚拟网络" align="center"></el-table-column>
                <el-table-column prop="ratio" label="云边负载比" align="center"></el-table-column>
                <el-table-column label="操作" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" icon="el-icon-ali-eye iconfont" @click="detail(scope.row)">查看
                        </el-button>
                        <el-button type="text" icon="el-icon-delete" class="red" @click="deleterow(scope.row)">删除
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>
    </div>
</template>

<script>
import { getTaskList, getClusterPods, 
    getClusterNode, 
    deleteTask } from '../../../api';
export default {
    data() {
        return {
            tableData: [
                // {
                //     name: "智慧视频",
                //     version: "1.1",
                //     taskNum: "5",
                //     CPUReq: "100m",
                //     memReq: "500MB",
                //     diskReq: "10Gi",
                //     ratio: "0.67"
                // },
                // {
                //     name: "高清视频",
                //     version: "2.1",
                //     taskNum: "5",
                //     CPUReq: "50m",
                //     memReq: "800MB",
                //     diskReq: "20Gi",
                //     ratio: "1.5"
                // },
                // {
                //     name: "智能家居",
                //     version: "1.1",
                //     taskNum: "8",
                //     CPUReq: "80m",
                //     memReq: "600MB",
                //     diskReq: "30Gi",
                //     ratio: "1"
                // }
            ]
        };
    },
    created() {
        this.getData();
    },
    methods: {
        async getData() {
            this.tableData = []
            let tasks = await (await getTaskList()).data.items;
            let podlist = await (await getClusterPods()).data.items;
            //console.log(podlist)
            for (var i = 0; i < tasks.length; i++) {
                var task = { name: "-", version: "-", creationTimestamp: "-", taskNum: "-", CPUReq: "-", memReq: "-", diskReq: "-", bandReq: "-", virtualNetwork: "-", ratio: "-", pods:[] };
                task.name = tasks[i].metadata.name;
                task.version = tasks[i].spec.template.spec.containers[0].image;
                task.creationTimestamp = tasks[i].metadata.creationTimestamp;
                task.taskNum = tasks[i].spec.replicas;
                //console.log(tasks[i]);
                var resources = tasks[i].spec.template.spec.containers[0].resources;

                var regnum = /^[0-9]+$/;
                if ("requests" in resources) {
                    if ("cpu" in resources.requests){ 
                        if(regnum.test(resources.requests.cpu)) task.CPUReq = resources.requests.cpu + "000m";
                        else task.CPUReq = resources.requests.cpu;
                    }
                    if ("memory" in resources.requests) task.memReq = resources.requests.memory;
                    if ("ephemeral-storage" in resources.requests) task.diskReq = resources.requests['ephemeral-storage'];
                }
                if ("limits" in resources) {
                    if ("cpu" in resources.limits){
                        if(regnum.test(resources.limits.cpu)) task.CPUReq += "/" + resources.limits.cpu + "000m";
                        else task.CPUReq  += "/" + resources.limits.cpu;
                    }
                    if ("memory" in resources.limits) task.memReq  += "/" + resources.limits.memory;
                    if ("ephemeral-storage" in resources.limits) task.diskReq  += "/" + resources.limits['ephemeral-storage'];
                }
                if ("annotations" in tasks[i].spec.template.metadata) {
                    var annotations = tasks[i].spec.template.metadata.annotations;
                    //console.log(annotations)
                    if ("kubernetes.io/egress-bandwidth" in annotations) task.bandReq = annotations['kubernetes.io/egress-bandwidth'];
                    if ("kubernetes.io/ingress-bandwidth" in annotations) task.bandReq += "/" + annotations['kubernetes.io/ingress-bandwidth'];

                }

                

                // var labelSelectorobj = tasks[i].spec.selector.matchLabels;
                // var labelSelector = Object.keys(labelSelectorobj)[0] + "=" + Object.values(labelSelectorobj)[0];
                // var params = { "labelSelector": labelSelector };

                
                let pods = [];
                let nodes = [];
                for (var j = 0; j < podlist.length; j++) {
                    let podname = podlist[j].metadata.name;
                    var reg = new RegExp("^"+task.name+"-([a-z]|[0-9])+-([a-z]|[0-9])+$","");    
                    if(reg.test(podname)){
                        pods.push(podname);
                        nodes.push(podlist[j].spec.nodeName);
                    }
                }
                //console.log(pods);
                let distributions = [];
                for (j = 0; j < pods.length; j++) {
                    let node = nodes[j];
                    let distribution = await (await getClusterNode(node)).data.metadata.labels.cluster;
                    distributions.push(distribution);
                    
                }
                task.pods = pods;
                //console.log(distributions);
                var clouds = 0;
                var edges = 0;
                for (j = 0; j < distributions.length; j++) {
                    if (distributions[j] == "cloud") clouds++;
                    if (distributions[j] == "edge") edges++;
                }
                task.ratio = clouds + "/" + edges;
                //console.log(distributions);
                //console.log(task);

                this.tableData.push(task);
            }

        },
        deleterow(row) {
            deleteTask(row.name).then((res) => {
                if (res.status == 200) {
                    alert("任务已删除！");
                    this.getData();
                } else {
                    alert("删除失败！");
                }
            });
        },
        detail(row) {

            let param = {
                deployment: row.name
            };
            param.pods = row.pods
            console.log(param);
            this.$router.push(
                {
                    path: '/TaskDetail',
                    query: param
                }
            );
        }
    }
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