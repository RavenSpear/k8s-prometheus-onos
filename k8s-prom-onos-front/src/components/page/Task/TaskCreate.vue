<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>边云协同任务调度</el-breadcrumb-item>
                <el-breadcrumb-item>任务部署</el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <div class="container">
            <div class="form-box">
                <el-form :model="task" label-width="200px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="任务名称" label-width="200px">

                                <el-input v-model="task.metadata.name" placeholder="任务名称"></el-input>

                            </el-form-item>
                        </el-col>
                        <el-col :span="9">
                            <el-form-item label="子任务数" label-width="200px">

                                <el-input v-model.number="task.spec.replicas" placeholder="1" type="number"></el-input>

                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-form-item label="镜像版本" label-width="200px">
                        <el-col :span="20">
                            <el-input v-model="task.spec.template.spec.containers[0].image" placeholder="镜像版本">
                            </el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="CPU需求（m）" label-width="200px">
                        <el-col :span="9">
                            <el-input v-model="task.spec.template.spec.containers[0].resources.requests.cpu"
                                placeholder="CPU请求"></el-input>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="9">
                            <el-input v-model="task.spec.template.spec.containers[0].resources.limits.cpu"
                                placeholder="CPU限制"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="内存需求（Mi）" label-width="200px">
                        <el-col :span="9">
                            <el-input v-model="task.spec.template.spec.containers[0].resources.requests.memory"
                                placeholder="内存请求"></el-input>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="9">
                            <el-input v-model="task.spec.template.spec.containers[0].resources.limits.memory"
                                placeholder="内存限制"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="磁盘需求（Gi）" label-width="200px">
                        <el-col :span="9">
                            <el-input
                                v-model="task.spec.template.spec.containers[0].resources.requests['ephemeral-storage']"
                                placeholder="磁盘请求"></el-input>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="9">
                            <el-input
                                v-model="task.spec.template.spec.containers[0].resources.limits['ephemeral-storage']"
                                placeholder="磁盘限制"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="带宽限制（M）" label-width="200px">
                        <el-col :span="9">
                            <el-input
                                v-model="task.spec.template.metadata.annotations['kubernetes.io/egress-bandwidth']"
                                placeholder="上行带宽"></el-input>
                        </el-col>
                        <el-col class="line" :span="2">:</el-col>
                        <el-col :span="9">
                            <el-input
                                v-model="task.spec.template.metadata.annotations['kubernetes.io/ingress-bandwidth']"
                                placeholder="下行带宽"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="绑定虚拟网络" label-width="200px">
                        <el-col :span="9">
                            <el-select v-model="task.spec.template.metadata.annotations.vnrid" placeholder="请选择虚拟网络" clearable>
                                <el-option v-for="VNR in VNRs" :label="VNR.vnetName" :value="VNR.vnrId" :key="VNR.vnrId"></el-option>
                            </el-select>
                        </el-col>
                    </el-form-item>

                    <el-form-item label-width="150px">
                        <el-button type="primary" @click="submit">创建</el-button>
                        <el-button @click="cancel">取消</el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    </div>
</template>

<script>
import { createTask,getVirtualNetworks } from "../../../api";
export default {
    data() {
        return {
            task: {},
            VNRs:[]
        };
    },
    created() {
        this.task = this.readFile()
        this.getVNR()
        
        //console.log(this.VNRs)
    },

    methods: {
        async getVNR(){
            let res = await (await getVirtualNetworks()).data.VNets
            this.VNRs = res
            for(var i = 0; i < this.VNRs.length; i++) {
                this.VNRs[i].vnrId = this.VNRs[i].vnrId + "";
            }
        },
        readFile() {
            var task = require('./task.json');
            //console.log(task);
            return task;
        },
        submit() {
            console.log(this.VNRs)
            if (this.task.metadata.name == ""||this.task.metadata.name == null) {
                this.$message({
                    showClose: true,
                    type: "error",
                    message: "请指定任务名",
                });
            } else if (this.task.metadata.name == ""||this.task.metadata.name == null) {
                this.$message({
                    showClose: true,
                    type: "error",
                    message: "请指定镜像及版本",
                });
            } else {
                for(var i = 0; i < this.VNRs.length; i++) {
                    if(this.task.spec.template.metadata.annotations['vnrid'] == this.VNRs[i].vnrId)
                    this.task.spec.template.metadata.annotations['vnrname'] = this.VNRs[i].vnetName
                }
                //this.task.spec.template.metadata.annotations['vnrname'] = null;
                createTask(this.task).then((res) => {
                    if (res.status==201) {
                        alert("任务创建成功");
                        this.$router.push("/TaskList");
                    }
                }).catch(()=>{
                    alert("任务创建失败，任务已存在或参数错误");
                });
            }
        },
        cancel() {
            this.task.metadata.name = null;
            this.task.spec.replicas = null;
            this.task.spec.template.spec.containers[0].image = null;
            this.task.spec.template.spec.containers[0].resources.requests.cpu = null;
            this.task.spec.template.spec.containers[0].resources.limits.cpu = null;
            this.task.spec.template.spec.containers[0].resources.requests.memory = null;
            this.task.spec.template.spec.containers[0].resources.limits.memory = null;
            this.task.spec.template.spec.containers[0].resources.requests['ephemeral-storage'] = null;
            this.task.spec.template.spec.containers[0].resources.limits['ephemeral-storage'] = null;
            this.task.spec.template.metadata.annotations['kubernetes.io/egress-bandwidth'] = null;
            this.task.spec.template.metadata.annotations['kubernetes.io/ingress-bandwidth'] = null;
            this.task.spec.template.metadata.annotations['vnrid'] = null;
            this.task.spec.template.metadata.annotations['vnrname'] = null;
        }
    },
};
</script>