<template>
        <div class="box">
                <div id="mynetwork" class="myChart"></div>
        </div>
</template>
      
<script>
import { getNetworkTopoDevices, getNetworkTopoLinks, getHosts, getClusterNodes, getIoTDevices, getClusterPods, getTaskList } from '../../api'
require("vis-network/dist/dist/vis-network.min.css");
const vis = require("vis-network/dist/vis-network.min");
export default {
        data() {
                return {
                        distributionhashs: {},//node name to cloud/edge
                        taskhashs: {},//{ grafana: { seq: 0, count:0 }}
                        devicehashs: {},//device name to node no.
                        edgehashs: {},//pair hashcode to if its exists.
                        nodehashs: {},//node name to node no.
                        iphashs: {},//ip to node no.
                        nodes: [
                                {
                                        id: 0,
                                        label: "Cloud-1",
                                        title: "Cloud",
                                        shape: "box",
                                        group: 0,
                                        isCloud: true,
                                        margin: 40,
                                        mass: 5
                                },

                                { id: 1, label: "Edge-1", group: 0, isEdge: true, shape: "box", margin: 40, mass: 10 },
                                { id: 2, label: "Edge-2", group: 0, isEdge: true, shape: "box", margin: 40, mass: 10 },
                                { id: 3, label: "Sw-1", group: 1, shape: "circle", physics: false },
                                { id: 4, label: "Sw-2", group: 1, shape: "circle", physics: false },
                                { id: 5, label: "Sw-3", group: 1, shape: "circle", physics: false },

                                {
                                        id: 6, label: "Task-1", group: 2, shape: "box", parent: 0, hidden: true, margin: 20,
                                        mass: 10
                                },
                                {
                                        id: 7, label: "Task-2", group: 2, shape: "box", parent: 0, hidden: true, margin: 20,
                                        mass: 10
                                },
                                {
                                        id: 8, label: "IoT-1", group: 3, shape: "box", parent: 1, hidden: true, margin: 20,
                                        mass: 10
                                },
                                {
                                        id: 9, label: "IoT-2", group: 3, shape: "box", parent: 1, hidden: true, margin: 20,
                                        mass: 10
                                },

                                {
                                        id: 10,
                                        label: "Cloud-2",
                                        shape: "box",
                                        group: 0,
                                        isCloud: true,
                                        margin: 40,
                                        mass: 5
                                },

                                { id: 11, label: "Sw-4", group: 1, isCloud: true, shape: "circle", physics: false },
                                { id: 12, label: "Sw-5", group: 1, isCloud: true, shape: "circle", physics: false },
                                { id: 13, label: "Sw-6", group: 1, isCloud: true, shape: "circle", physics: false },

                                { id: 14, label: "Sw-7", group: 1, shape: "circle", physics: false },
                                { id: 15, label: "Edge-3", group: 0, isEdge: true, shape: "box", margin: 40, mass: 10 },
                        ],
                        edges: [
                                { from: 1, to: 4 },
                                { from: 2, to: 5 },

                                { from: 3, to: 4 },
                                { from: 4, to: 5 },
                                { from: 3, to: 5 },

                                { from: 6, to: 0 },
                                { from: 7, to: 0 },
                                { from: 8, to: 1 },
                                { from: 9, to: 1 },


                                { from: 11, to: 12 },
                                { from: 11, to: 13 },
                                { from: 12, to: 13 },
                                { from: 12, to: 3 },
                                { from: 0, to: 11 },
                                { from: 10, to: 13 },

                                { from: 14, to: 3 },
                                { from: 14, to: 4 },
                                { from: 14, to: 5 },

                                { from: 15, to: 14 },

                        ]
                }
        },
        mounted() {
                this.collectTopoInfo();
        },
        methods: {
                makeVis() {


                        var data = {
                                nodes: this.nodes,
                                edges: this.edges,
                        };
                        //console.log(data)
                        var container = document.getElementById("mynetwork");
                        var options = {
                                nodes: {
                                        size: 40,
                                        font: {
                                                size: 32,
                                        },
                                        borderWidth: 3,
                                        shadow: true,
                                },
                                edges: {
                                        width: 3,
                                        length: 250,
                                        //shadow: true,
                                }
                        };
                        this.network = new vis.Network(container, data, options);
                        this.network.on("beforeDrawing", (ctx) => {
                                //console.log(this.network.getScale()) // 放大缩小倍数
                                //this.isDrowGaphical(ctx, this.isDrowPosition('isEdge'), this.getPositionWidth('isEdge'))
                                this.isDrowGaphical(ctx, this.isDrowPosition('isCloud'), this.getPositionWidth('isCloud'))
                        });
                        this.network.on("doubleClick", (params) => {
                                var id = params.nodes[0];
                                if(id==null) return;
                                var cnode = this.nodes[id];
                                var param = {};
                                switch (this.nodes[id].group) {
                                        case 0:
                                                param['nodeName'] = cnode.title;
                                                //console.log(param);
                                                this.$router.push(
                                                        {
                                                                path: '/nodeDetail',
                                                                query: param
                                                        }
                                                );
                                                break;
                                        case 1:
                                                break;
                                        case 2:
                                                param['deployment'] = cnode.taskName;
                                                param['pods'] = this.taskhashs[cnode.taskName].pods;
                                                //console.log(param);
                                                this.$router.push(
                                                        {
                                                                path: '/TaskDetail',
                                                                query: param
                                                        }
                                                );
                                                break;
                                        case 3:
                                                param['deviceName'] = cnode.deviceName;
                                                param['nodeName'] = this.nodes[cnode.parent].title;
                                                //console.log(param);
                                                this.$router.push(
                                                        {
                                                                path: '/deviceDetail',
                                                                query: param
                                                        }
                                                );
                                                break;
                                        default:
                                                break;
                                }
                        });
                        this.network.on("click", (params) => {
                                var id = params.nodes[0];
                                if (id >= 0 && this.nodes[id].group == 0) {
                                        this.changeSubNodeVisibility(id);
                                } else if (id >= 0 && (this.nodes[id].group == 2 || this.nodes[id].group == 3)) {
                                        //console.log(this.nodes[id].hidden)
                                }
                                //params.restore();
                                //this.network.redraw()
                        });

                },
                isDrowPosition(opt) {
                        let PositionArr = []
                        this.nodes.forEach(element => {
                                if (element[opt]) {
                                        PositionArr.push(element.id)
                                }
                        });
                        let isPosiObj = this.network.getPositions(PositionArr) // 获取位置信息
                        let NewObj = { maxX: null, minX: null, maxY: null, minY: null }
                        for (var item in isPosiObj) {
                                if (isPosiObj[item].x > NewObj.maxX || NewObj.maxX == null) NewObj.maxX = isPosiObj[item].x
                                if (isPosiObj[item].x < NewObj.minX || NewObj.minX == null) NewObj.minX = isPosiObj[item].x
                                if (isPosiObj[item].y > NewObj.maxY || NewObj.maxY == null) NewObj.maxY = isPosiObj[item].y
                                if (isPosiObj[item].y < NewObj.minY || NewObj.minY == null) NewObj.minY = isPosiObj[item].y
                        }
                        return NewObj
                },
                // 获取符合条件的最大宽度与高度
                getPositionWidth(opt) {
                        let removeX = 0
                        let removeY = 0
                        this.nodes.forEach(ele => {
                                if (ele[opt]) {
                                        let obj = this.network.getBoundingBox(ele.id)
                                        if (removeX < (obj.right - obj.left) / 2 + 10) {
                                                removeX = (obj.right - obj.left) / 2 + 10
                                        }
                                        if (removeY < (obj.bottom - obj.top) / 2 + 10) {
                                                removeY = (obj.bottom - obj.top) / 2 + 10
                                        }
                                }
                        })
                        return {
                                removeX,
                                removeY
                        }
                },
                // 画出虚线框
                isDrowGaphical(ctx, obj, SafeArea) {
                        // 最小x 最小y => 最大x 最小y  => 最大x 最大y => 最小x 最大y
                        ctx.save(); // 这里的坑 影响了之前的所有线都成为了虚线
                        ctx.beginPath();
                        ctx.strokeStyle = '#aaa'
                        ctx.setLineDash([10, 10]);
                        ctx.moveTo(obj.minX - SafeArea.removeX, obj.minY - SafeArea.removeY);
                        ctx.lineTo(obj.maxX + SafeArea.removeX, obj.minY - SafeArea.removeY);
                        ctx.lineTo(obj.maxX + SafeArea.removeX, obj.maxY + SafeArea.removeY);
                        ctx.lineTo(obj.minX - SafeArea.removeX, obj.maxY + SafeArea.removeY);
                        ctx.closePath();
                        ctx.stroke();
                        ctx.restore();
                },
                changeSubNodeVisibility(id) {
                        for (var i = 0; i < this.nodes.length; i++) {
                                if (this.nodes[i].parent == id) {
                                        //console.log(i)
                                        let val = !this.nodes[i].hidden;
                                        this.nodes[i].hidden = !this.nodes[i].hidden;
                                        this.network.clustering.updateClusteredNode(i, { hidden: val });
                                }
                        }
                },
                async collectTopoInfo() {
                        this.nodes = [];
                        this.edges = [];
                        var linklist = await (await getNetworkTopoLinks()).data.links;
                        var hostlist = await (await getHosts()).data.hosts;
                        var swlist = await (await getNetworkTopoDevices()).data.devices;

                        //console.log(hostlist)
                        let nodelist = await (await getClusterNodes()).data.items;
                        //console.log(nodelist)
                        let tasklist = await (await getTaskList()).data.items;
                        //console.log(links)
                        //console.log(hosts)

                        let i = 0;//i for nodes
                        for (; i < swlist.length; i++) {
                                let sw = {
                                        id: i,
                                        group: 1,
                                        title: swlist[i],
                                        label: "Sw-" + i,
                                        shape: "circle",
                                        physics: false,
                                        fixed: true
                                }
                                if(i==4){
                                        sw.x = 2400;
                                        sw.y = 500;
                                }
                                if(i==0){
                                        sw.x = 2800;
                                        sw.y = 1000;
                                }
                                if(i==7){
                                        sw.x = 2000;
                                        sw.y = 1000;
                                }
                                if(i==2){
                                        sw.x = 2700;
                                        sw.y = 700;
                                }
                                if(i==6){
                                        sw.x = 2150;
                                        sw.y = 400;
                                }
                                if(i==3){
                                        sw.x = 2650;
                                        sw.y = 400;
                                }
                                if(i==2){
                                        sw.x = 2800;
                                        sw.y = 650;
                                }
                                this.devicehashs[sw.title] = i;
                                //console.log(this.devicehashs)
                                this.nodes.push(sw);
                        }
                        //j for else
                        for (var j = 0; j < linklist.length; j++) {
                                let link = {
                                        from: this.devicehashs[linklist[j].src.device],
                                        to: this.devicehashs[linklist[j].dst.device],
                                }
                                if (this.edgehashs[this.edgeHashCode(link.from, link.to)] == null) {
                                        this.edgehashs[this.edgeHashCode(link.from, link.to)] = 1;
                                        this.edges.push(link);
                                }

                        }

                        for (j = 0; j < tasklist.length; j++) {
                                this.taskhashs[tasklist[j].metadata.name] = { seq: j, count: 0, pods:[] };
                        }
                        //console.log(this.taskhashs)

                        var nodeslength = this.nodes.length;

                        //initialize iphashs according to hostlist
                        for (j = nodeslength; j < nodelist.length + nodeslength; j++) {
                                this.iphashs[hostlist[j - nodeslength].ipAddresses[0]] = j;
                        }
                        //console.log(hostlist);
                        for (; i < nodelist.length + nodeslength; i++) {

                                let nodename = nodelist[i - nodeslength].metadata.name;
                                let distribute = nodelist[i - nodeslength].metadata.labels.cluster
                                let ip;
                                for (const address of nodelist[i - nodeslength].status.addresses) {
                                        if (address.type == "InternalIP") {
                                                ip = address.address;
                                        }
                                }

                                //console.log(nodename + " "+ distribute+" "+ip+" "+i);
                                //console.log(this.iphashs);
                                //console.log(this.iphashs[ip]);

                                this.nodehashs[nodename] = this.iphashs[ip];
                                this.distributionhashs[nodename] = distribute;
                                let node = {
                                        id: i,
                                        title: nodename,
                                        label: "节点-" + (i - nodeslength),
                                        shape: "box",
                                        group: 0,
                                        margin: 40,
                                        mass: 10
                                }
                                if (distribute == "cloud"){
                                        node['isCloud'] = true;
                                        node.label = "云" + node.label;
                                }else{
                                        node.label = "边" + node.label;
                                }
                                let loc = {
                                        from: i,
                                        to: this.devicehashs[hostlist[i - nodeslength].locations[0].elementId]
                                }
                                this.nodes.push(node);
                                this.edges.push(loc);
                                //console.log(ip);

                        }
                        //console.log( this.distributionhashs)
                        //console.log(this.nodehashs)
                        //console.log(this.iphashs)


                        for (var key in this.nodehashs) {
                                //console.log(this.distributionhashs[key])
                                if (this.distributionhashs[key] == "cloud") continue;
                                let devices = await (await getIoTDevices(key)).data.devices;
                                //console.log(devices);
                                nodeslength = this.nodes.length;
                                for (; i < nodeslength + devices.length; i++) {
                                        let node = {
                                                id: i,
                                                //label: devices[i - nodeslength].name,
                                                label: "传感器-" + (i - nodeslength),
                                                title: devices[i - nodeslength].description,
                                                deviceName: devices[i - nodeslength].name,
                                                group: 3,
                                                shape: "box",
                                                parent: this.nodehashs[key],
                                                hidden: false,
                                                margin: 20,
                                                mass: 5
                                        }
                                        let loc = {
                                                from: i,
                                                to: this.nodehashs[key]
                                        }
                                        this.nodes.push(node);
                                        this.edges.push(loc);
                                }
                        }

                        let podlist = await (await getClusterPods()).data.items;
                        //console.log(podlist);

                        nodeslength = this.nodes.length;
                        var seq = i;
                        var reg = /^(.*)-([a-z]|[0-9])+-([a-z]|[0-9])+$/
                        for (; i < nodeslength + podlist.length; i++) {
                                var podname = podlist[i - nodeslength].metadata.name;
                                var taskname = podname.replace(reg, "$1");
                                if (!(taskname in this.taskhashs)) {
                                        continue;
                                }
                                this.taskhashs[taskname].pods.push(podname);
                                let node = {
                                        id: seq,
                                        //label: podlist[i - nodeslength].metadata.name,
                                        label: "任务-" + this.taskhashs[taskname].seq + ": " + this.taskhashs[taskname].count++,
                                        title: podname,
                                        taskName: taskname,
                                        group: 2,
                                        shape: "box",
                                        parent: this.nodehashs[podlist[i - nodeslength].spec.nodeName],
                                        hidden: false,
                                        margin: 20,
                                        mass: 5
                                }
                                let loc = {
                                        from: seq,
                                        to: this.nodehashs[podlist[i - nodeslength].spec.nodeName]
                                }
                                this.nodes.push(node);
                                this.edges.push(loc);
                                seq++;
                        }
                        
                        //console.log(this.taskhashs);
                        //console.log(this.nodes)
                        //console.log(this.edges)
                        this.makeVis();
                },
                edgeHashCode(a, b) {
                        return a > b ? (a + "." + b) : (b + "." + a);
                }
        }
}
</script>
      
<style>
.box {
        width: 100%;
        height: 100%;
}

.myChart {
        width: 100%;
        height: 100%;
}
</style>
      