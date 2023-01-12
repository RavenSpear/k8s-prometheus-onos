<template>
  <div>
    <!-- 面包导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/virtualNetworkDashboard' }"
          >网络资源虚拟化控制</el-breadcrumb-item
        >
        <el-breadcrumb-item>服务注册</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="form-box">
        <el-form :model="form" label-width="180px" ref="formRef">
          <el-form-item label="网络服务名称" prop="serviceName">
            <el-col :span="10">
              <el-input
                v-model="form.serviceName"
                placeholder="输入网络服务名称（如视频流服务）"
              ></el-input>
            </el-col>
          </el-form-item>

          <el-form-item label="手动配置">
            <el-switch v-model="form.isManual"></el-switch>
          </el-form-item>

          <el-form-item label="IP地址" prop="ip">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-input
                  v-model="form.traffic.srcIP"
                  placeholder="源IP地址"
                ></el-input>
              </el-col>
              <el-col :span="10">
                <el-input
                  v-model="form.traffic.dstIP"
                  placeholder="目的IP地址"
                ></el-input>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item label="服务端口" prop="port">
            <el-row :gutter="10">
              <el-col :span="10">
                <el-input
                  v-model.number="form.traffic.srcPort"
                  placeholder="源端口号"
                ></el-input>
              </el-col>
              <el-col :span="10">
                <el-input
                  v-model.number="form.traffic.dstPort"
                  placeholder="目的端口号"
                ></el-input>
              </el-col>
            </el-row>
          </el-form-item>

          <el-form-item label="流量类型" prop="type">
            <el-select v-model="form.traffic.type" placeholder="请选择">
              <el-option
                v-for="node in trafficType"
                :key="node.name"
                :label="node.name"
                :value="node.name"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="虚拟网络选择" prop="vnetId">
            <el-select
              v-model="form.vnetId"
              placeholder="请选择"
              :disabled="isFromVnetPage"
            >
              <el-option
                v-for="node in vnetArray"
                :key="node.vnetId"
                :label="node.vnetName"
                :value="node.vnetId"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submit">注册</el-button>
            <el-button @click="cancel">取消</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getVirtualNetworks, registerService } from "../../../api";
import bus from "../../common/bus";
export default {
  data() {
    return {
      form: {
        serviceName: "视频流服务",
        vnetId: "",
        traffic: {
          srcIP: "10.0.0.1",
          dstIP: "10.0.0.2",
          type: "udp",
          srcPort: "5001",
          dstPort: "12345",
        },
        isManual: true
      },
      
      vnetArray: [],
      trafficType: [
        {
          name: "tcp",
        },
        {
          name: "udp",
        },
      ],
      isFromVnetPage: false,
    };
  },
  created() {
    if (this.$route.query.vnetId != null) {
      this.isFromVnetPage = true;
      this.vnetArray.push({
        vnetName: this.$route.query.vnetName,
        vnetId: this.$route.query.vnetId,
      });
      this.form.vnetId = this.$route.query.vnetId;
    } else {
      this.loadVnetSelector();
    }
  },
  methods: {
    async loadVnetSelector() {
      var vnets = await (await getVirtualNetworks()).data.VNets;
      vnets.forEach((vnet) => {
        this.vnetArray.push({
          vnetId: vnet.vnetId,
          vnetName: vnet.vnetName,
        });
      });
    },
    submit() {
      registerService(this.form).then((res) => {
        if (res.data.statusCode === 1) {
          // 注册成功
          alert("注册成功！");
          this.cancel();
        } else {
          alert("注册失败");
        }
        console.log(res);
      });
    },
    cancel() {
      if (this.isFromVnetPage) {
        bus.$emit("close_current_tags", "/virtualNetworkList");
      } else {
        bus.$emit("close_current_tags", "/serviceList");
      }
    },
    reset() {
      this.$refs.formRef.resetFields();
    },
  },
};
</script>

<style>
</style>