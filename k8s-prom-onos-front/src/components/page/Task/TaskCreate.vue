<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>边云智能协同计算</el-breadcrumb-item>
                <el-breadcrumb-item>任务协同部署</el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <div class="container">
            <div class="form-box">
                <el-form :model="form" :rules="rules" label-width="200px">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="任务名称" label-width="200px" prop="taskname">

                                <el-input v-model="form.taskname" placeholder="任务名称"></el-input>

                            </el-form-item>
                        </el-col>
                        <el-col :span="9">
                            <el-form-item label="子任务数" label-width="200px" prop="tasknum">

                                <el-input v-model.number="form.tasknum" placeholder="1" type="number"></el-input>

                            </el-form-item>
                        </el-col>
                    </el-row>

                    <el-form-item label="镜像版本" label-width="200px" prop="version">
                        <el-col :span="20">
                            <el-input v-model="form.version" placeholder="镜像版本">
                            </el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="CPU需求（m）" label-width="200px">
                        <el-col :span="9">
                            <el-form-item prop="cpureq">
                                <el-input v-model="form.cpureq" placeholder="CPU请求"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="9">
                            <el-input v-model="form.cpulimit" placeholder="CPU限制"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="内存需求（MB）" label-width="200px" prop="memory">
                        <el-col :span="9">
                            <el-form-item prop="memreq">
                                <el-input v-model="form.memreq" placeholder="内存请求"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="9">
                            <el-input v-model="form.memlimit" placeholder="内存限制"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="磁盘需求（GB）" label-width="200px" prop="disk">
                        <el-col :span="9">
                            <el-form-item prop="diskreq">
                                <el-input v-model="form.diskreq" placeholder="磁盘请求"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col class="line" :span="2">-</el-col>
                        <el-col :span="9">
                            <el-input v-model="form.disklimit" placeholder="磁盘限制"></el-input>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="带宽限制（Mbps）" label-width="200px" prop="band">
                        <el-col :span="9">
                            <el-form-item prop="bandreq">
                                <el-input v-model="form.bandreq" placeholder="上行带宽"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col class="line" :span="2">:</el-col>
                        <el-col :span="9">
                            <el-form-item prop="bandlimit">
                                <el-input v-model="form.bandlimit" placeholder="下行带宽"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-form-item>

                    <el-form-item label="绑定虚拟网络" label-width="200px" prop="vnet">
                        <el-col :span="9">
                            <el-select v-model="form.vnrid" placeholder="请选择虚拟网络" clearable>
                                <el-option v-for="VNR in VNRs" :label="VNR.vnetName" :value="VNR.vnrId"
                                    :key="VNR.vnrId"></el-option>
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
import { createTask, getVirtualNetworks } from "../../../api";
export default {
    data() {
        var checkTaskname = (rule, value, callback) => {
            var pattern2 = new RegExp("^[a-zA-Z][a-zA-Z0-9_-]*$");
            if (!pattern2.test(value)) {
                callback(new Error("请使用字母、数字、连字符(-)及下划线(_)的组合构建"));
            }
        };
        var checkTasknum = (rule, value, callback) => {
            if (!Number.isInteger(value)) {
                callback(new Error("请输入值"));
            } else {
                if (value == 0) {
                    callback(new Error("子任务数不能为0"));
                } else if (value < 0) {
                    callback(new Error("子任务数不能为负数"));
                } else if (value > 8) {
                    callback(new Error("子任务数不能大于8"));
                } else {
                    callback();
                }
            }
        };
        var checkCPUreq = (rule, value, callback) => {
            if (this.form.cpureq && (value > 1000 || value < 0)) {
                callback(new Error("CPU请求值为0-1000m"));
            } else {
                callback();
            }
        };
        var checkMemreq = (rule, value, callback) => {
            if (this.form.memreq && (value > 1000 || value < 0)) {
                callback(new Error("内存请求值为0-1GB"));
            } else {
                callback();
            }
        };
        var checkDiskreq = (rule, value, callback) => {
            if (this.form.diskreq && (value > 20 || value < 0)) {
                callback(new Error("内存请求值为0-20GB"));
            } else {
                callback();
            }
        };
        var checkBandreq = (rule, value, callback) => {
            if (this.form.bandreq &&  value < 0) {
                callback(new Error("上行带宽不能为负"));
            } else {
                callback();
            }
        };
        var checkBandlimit = (rule, value, callback) => {
            if (this.form.bandlimit &&  value < 0) {
                callback(new Error("下行带宽不能为负"));
            } else {
                callback();
            }
        };
        return {
            task: {},
            VNRs: [],
            form: {
                taskname: "nginx-dp",
                tasknum: 3,
                version: "nginx:1.14",
                cpureq: "250",
                cpulimit: "500",
                memreq: "64",
                memlimit: "128",
                diskreq: "2",
                disklimit: "4",
                bandreq: "10",
                bandlimit: "10",
                vnrid: null,
                vnrname: ""
            },
            rules: {
                taskname: [
                    { required: true, message: "任务名不为空" },
                    { validator: checkTaskname, trigger: "blur" },
                ],
                tasknum: [
                    { required: true, message: "子任务数不为空" },
                    { validator: checkTasknum, trigger: "blur" },
                ],
                version: [
                    { required: true, message: "镜像版本不为空" }
                ],
                cpureq: [
                    { validator: checkCPUreq, trigger: "change" },
                ],
                memreq: [
                    { validator: checkMemreq, trigger: "change" },
                ],
                diskreq: [
                    { validator: checkDiskreq, trigger: "change" },
                ],
                bandreq: [
                    { validator: checkBandreq, trigger: "change" },
                ],
                bandlimit: [
                    { validator: checkBandlimit, trigger: "change" },
                ]
            },
        };
    },
    created() {
        this.task = this.readFile()
        this.getVNR()

        //console.log(this.VNRs)
    },

    methods: {
        async getVNR() {
            let res = await (await getVirtualNetworks()).data.VNets
            this.VNRs = res
            for (var i = 0; i < this.VNRs.length; i++) {
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
            if (this.task.metadata.name == "" || this.task.metadata.name == null) {
                this.$message({
                    showClose: true,
                    type: "error",
                    message: "请指定任务名",
                });
            } else if (this.task.metadata.name == "" || this.task.metadata.name == null) {
                this.$message({
                    showClose: true,
                    type: "error",
                    message: "请指定镜像及版本",
                });
            } else {
                for (var i = 0; i < this.VNRs.length; i++) {
                    if (this.form.vnrid == this.VNRs[i].vnrId)
                        this.task.spec.template.metadata.annotations['vnrname'] = this.VNRs[i].vnetName
                }
                var newtask = JSON.parse(JSON.stringify(this.task))
                newtask.metadata.name = this.form.taskname;
                newtask.spec.replicas = this.form.tasknum;
                newtask.spec.template.spec.containers[0].image = this.form.version;
                newtask.spec.template.spec.containers[0].resources.requests.cpu = this.form.cpureq;
                newtask.spec.template.spec.containers[0].resources.limits.cpu = this.form.cpulimit;
                newtask.spec.template.spec.containers[0].resources.requests.memory = this.form.memreq;
                newtask.spec.template.spec.containers[0].resources.limits.memory = this.form.memlimit;
                newtask.spec.template.spec.containers[0].resources.requests['ephemeral-storage'] = this.form.diskreq;
                newtask.spec.template.spec.containers[0].resources.limits['ephemeral-storage'] = this.form.disklimit;
                newtask.spec.template.metadata.annotations['kubernetes.io/egress-bandwidth'] = this.form.bandreq;
                newtask.spec.template.metadata.annotations['kubernetes.io/ingress-bandwidth'] = this.form.bandlimit;

                newtask.spec.template.metadata.annotations["kubernetes.io/ingress-bandwidth"] += "M"
                newtask.spec.template.metadata.annotations["kubernetes.io/egress-bandwidth"] += "M"
                newtask.spec.template.spec.containers[0].resources.requests.memory += "Mi"
                newtask.spec.template.spec.containers[0].resources.limits.memory += "Mi"
                newtask.spec.template.spec.containers[0].resources.requests.cpu += "m"
                newtask.spec.template.spec.containers[0].resources.limits.cpu += "m"
                newtask.spec.template.spec.containers[0].resources.requests["ephemeral-storage"] += "Gi"
                newtask.spec.template.spec.containers[0].resources.limits["ephemeral-storage"] += "Gi"
                //this.task.spec.template.metadata.annotations['vnrname'] = null;
                createTask(newtask).then((res) => {
                    if (res.status == 201) {
                        alert("任务创建成功");
                        this.$router.push("/TaskList");
                    }
                }).catch(() => {
                    alert("任务创建失败，任务已存在或参数错误");
                });
            }
        },
        cancel() {
            this.form.taskname = null;
            this.form.tasknum = null;
            this.form.version = null;
            this.form.cpureq = null;
            this.form.cpulimit = null;
            this.form.memreq = null;
            this.form.memlimit = null;
            this.form.diskreq = null;
            this.form.disklimit = null;
            this.form.bandreq = null;
            this.form.bandlimit = null;
            this.form.vnrid = null;
            this.form.vnrname = null;
        }
    },
};
</script>