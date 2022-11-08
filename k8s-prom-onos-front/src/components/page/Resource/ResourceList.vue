<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>传存算资源监视器</el-breadcrumb-item>
                <el-breadcrumb-item>资源感知列表</el-breadcrumb-item>
            </el-breadcrumb>
        </div>

        <div class="container">
            <el-collapse v-model="activeNames" style="border:none;margin-left:30px;">
                <el-collapse-item name="1">
                    <template slot="title">
                        <p class="title">云端资源列表</p>
                    </template>
                    <template>
                        <el-table :data="cloudData" style="width: 100%">
                            <el-table-column prop="name" label="节点名" align="center">
                            </el-table-column>
                            <el-table-column prop="IPv4Address" label="IP地址" align="center">
                            </el-table-column>
                            <el-table-column prop="os" label="操作系统" align="center">
                            </el-table-column>
                            <el-table-column prop="creationTimestamp" label="创建时间" align="center">
                            </el-table-column>
                            <el-table-column prop="status" label="状态" align="center">
                            </el-table-column>
                            <el-table-column label="查看详情" align="center">
                                <template slot-scope="scope">
                                    <el-button type="text" icon="el-icon-ali-eye iconfont" @click="topoShow(scope.row)">
                                        查看</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </template>
                </el-collapse-item>
                <el-collapse-item name="2">
                    <template slot="title">
                        <p class="title">边端资源列表</p>
                    </template>
                    <template>
                        <el-table :data="edgeData" style="width: 100%">
                            <el-table-column prop="name" label="节点名" align="center">
                            </el-table-column>
                            <el-table-column prop="IPv4Address" label="IP地址" align="center">
                            </el-table-column>
                            <el-table-column prop="os" label="操作系统" align="center">
                            </el-table-column>
                            <el-table-column prop="creationTimestamp" label="创建时间" align="center">
                            </el-table-column>
                            <el-table-column prop="status" label="状态" align="center">
                            </el-table-column>
                            <el-table-column label="查看详情" align="center">
                                <template slot-scope="scope">
                                    <el-button type="text" icon="el-icon-ali-eye iconfont" @click="topoShow(scope.row)">
                                        查看</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </template>
                </el-collapse-item>
                <el-collapse-item name="3">
                    <template slot="title">
                        <p class="title">传输资源列表</p>
                    </template>
                    <template>
                        <el-table :data="switchData" style="width: 100%">
                            <el-table-column prop="name" label="设备名称" align="center">
                            </el-table-column>
                            <el-table-column prop="deviceType" label="设备类型" align="center">
                            </el-table-column>
                            <el-table-column prop="portNum" label="端口数量" align="center">
                            </el-table-column>
                            <el-table-column prop="protocol" label="支持协议" align="center">
                            </el-table-column>
                            <el-table-column prop="status" label="状态" align="center">
                            </el-table-column>
                            <el-table-column label="查看详情" align="center">
                                <template slot-scope="scope">
                                    <el-button type="text" icon="el-icon-ali-eye iconfont" @click="topoShow(scope.row)">
                                        查看</el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </template>
                </el-collapse-item>
            </el-collapse>

        </div>
    </div>
</template>

<script>
import { getClusterNodes, getDevices, getDeviceOpenPort} from "../../../api/index";
export default {
    data() {
        return {
            cloudData: [
            // {
            //     id: "1",
            //     name: "Cloud-1",
            //     IPv4Address: "192.168.1.104",
            //     os: "linux",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "2",
            //     name: "Cloud-2",
            //     IPv4Address: "192.168.1.103",
            //     os: "linux",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // }
            ],
            edgeData: [
            // {
            //     id: "1",
            //     name: "Edge-1",
            //     IPv4Address: "10.201.22.23",
            //     os: "linux",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "2",
            //     name: "Edge-2",
            //     IPv4Address: "172.10.145.3",
            //     os: "linux",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "3",
            //     name: "Edge-3",
            //     IPv4Address: "169.117.90.123",
            //     os: "linux",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // }
            ],
            switchData: [
            // {
            //     id: "1",
            //     name: "SW-1",
            //     portNum: "8",
            //     protocol: "OpenFlow1.4",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "2",
            //     name: "SW-2",
            //     portNum: "8",
            //     protocol: "OpenFlow1.3",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "3",
            //     name: "SW-3",
            //     portNum: "4",
            //     protocol: "OpenFlow1.3",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "4",
            //     name: "SW-4",
            //     portNum: "8",
            //     protocol: "P4Runtime",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "5",
            //     name: "SW-5",
            //     portNum: "8",
            //     protocol: "P4Runtime",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "6",
            //     name: "SW-6",
            //     portNum: "4",
            //     protocol: "P4Runtime",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // },
            // {
            //     id: "7",
            //     name: "SW-7",
            //     portNum: "4",
            //     protocol: "OpenFlow1.3",
            //     status: "正在运行",
            //     creationTimestamp: "2020-01-08",
            // }
            ],
            activeNames: ['1', '2', '3'] 
        };
    },
    created() {
        this.getData();
    },
    methods: {
        async getData() {
            this.cloudData = []
            this.edgeData = []
            //this.switchData = []
            const node_items = await (await getClusterNodes()).data.items;
            const switch_items = await (await getDevices()).data.devices;
            for (const value of node_items) {
                var node = { name: "-", IPv4Address: "-", os: "-", creationTimestamp: "-", status: "-", location: "-"};
                node.name = value.metadata.name
                for (const address of value.status.addresses){
                    if(address.type == "InternalIP"){
                        node.IPv4Address = address.address
                    }
                }
                node.os = value.status.nodeInfo.operatingSystem
                node.creationTimestamp = value.metadata.creationTimestamp
                for (const condition of value.status.conditions){
                    if(condition.type == "Ready"){
                        node.status = (condition.status.toUpperCase() == "TRUE"?"就绪":"未就绪")
                    }
                }
                node.location =  value.metadata.labels.cluster
                if(node.location == "cloud"){
                    this.cloudData.push(node)
                }else{
                    this.edgeData.push(node)
                }
            }
            for (const value of switch_items) {
                var device = {name:"-", deviceType:"-", portNum:"-", protocol:"-", status:"-"};
                //device.name = value.annotations.name.slice(7);
                device.name = value.annotations.name;
                device.deviceType = value.type
                device.portNum = await (await getDeviceOpenPort(value.id)).data.ports.length;
                device.protocol = value.annotations.protocol;
                device.status = (value.available.toString().toUpperCase() == "TRUE"?"就绪":"未就绪");
                console.log(device)
                this.switchData.push(device);
            }
        }
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