<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>存传算资源感知复用</el-breadcrumb-item>
                <el-breadcrumb-item>设备感知列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-collapse v-if="wait" v-model="activeName">
                <el-collapse-item v-for="item in edgeList" :key='item' :name='item'>
                    <template slot="title">
                        <p class="title">{{item}}</p>
                    </template>
                    <el-table :data="edgeDevices[item]" style="width: 100%">
                            <el-table-column prop="name" label="设备名" align="center">
                            </el-table-column>
                            <el-table-column prop="description" label="描述" align="center">
                            </el-table-column>
                            <el-table-column prop="created" label="创建时间" align="center">
                            </el-table-column>
                            <el-table-column prop="protocols" label="协议" align="center">
                            </el-table-column>
                            <el-table-column prop="operatingState" label="状态" align="center">
                            </el-table-column>
                            <el-table-column label="查看详情" align="center">
                                <template slot-scope="scope">
                                    <el-button type="text" icon="el-icon-ali-eye iconfont" @click="detail(scope.row)">
                                        查看</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                </el-collapse-item>
            </el-collapse>
        </div>

    </div>
</template>

<script>
import { getClusterNodes, getIoTDevices } from "../../../api/index";
export default {
    data() {
        return {
            edgeDevices: {},
            edgeList: [],
            wait: false,
            activeName: []
        };
    },
    async created() {
        await this.getEdge();
        this.wait = true;
    },

    methods: {
        async getEdge() {
            let nodes = await (await getClusterNodes()).data.items;
            //console.log(nodes);
            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].metadata.labels.cluster == "edge") {
                    
                    this.edgeList.push(nodes[i].metadata.name);
                    this.activeName.push(nodes[i].metadata.name);
                }
            }
            //this.activeName = this.edgeList;
            console.log(this.edgeList);
            for(let i = 0; i < this.edgeList.length; i++){
                let devices = await (await getIoTDevices(this.edgeList[i])).data.devices;
                for(let j = 0; j < devices.length; j++){
                    var d = new Date(devices[j].created);
                    devices[j].created = d.toDateString();
                    var protocols = Object.keys(devices[j].protocols);
                    devices[j].protocols = "";
                    for(let k = 0; k < protocols.length; k++){
                        devices[j].protocols += (", "+protocols[k])
                    }
                    devices[j].protocols = devices[j].protocols.substring(2);
                    //console.log(devices[j].protocols)
                    devices[j].operatingState = devices[j].operatingState == "UP"?"就绪":"未就绪";
                    devices[j].node = this.edgeList[i]
                }
                this.edgeDevices[this.edgeList[i]] = devices;
            }
            console.log(this.edgeDevices);
        },

        detail(row) {
            let param = {
                nodeName: row.node,
                deviceName: row.name
            };
            console.log(param);
            this.$router.push(
                {
                    path: '/deviceDetail',
                    query: param
                }
            );
        }
    },
};
</script>