<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>传存算资源监视器</el-breadcrumb-item>
                <el-breadcrumb-item>设备感知列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <el-collapse>
                <el-collapse-item v-for="item in edgeList" :key='item.name'>
                    <template slot="title">
                        <p class="title">{{item.name}}</p>
                    </template>
                </el-collapse-item>
            </el-collapse>
        </div>

    </div>
</template>

<script>
import { getClusterNodes } from "../../../api/index";
export default {
    data() {
        return {
            edgeData: {},
            edgeList: []
        };
    },
    created() {
        this.getEdge();
    },
    methods: {
        async getEdge() {
            let nodes = await (await getClusterNodes()).data.items;
            console.log(nodes);
            for (let i = 0; i < nodes.length; i++) {
                if (nodes[i].metadata.labels.cluster == "edge") {
                    let edge = {
                        name: nodes[i].metadata.name,
                        ip: nodes[i].status.addresses[0].address
                    }
                    console.log(edge);
                    this.edgeList.push(edge)
                }
            }
        }
    },
};
</script>