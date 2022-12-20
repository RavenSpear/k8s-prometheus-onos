<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>传输资源虚拟化控制</el-breadcrumb-item>
        <el-breadcrumb-item>创建虚拟网络请求</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="form-box">
        <el-form
          ref="vnrFormRef"
          :model="form"
          :rules="rules"
          label-width="180px"
        >
          <el-form-item label="虚拟网络名称" prop="vnetName">
            <el-col :span="10">
              <el-input
                v-model="form.vnetName"
                placeholder="输入虚拟网络名称"
              ></el-input>
            </el-col>
          </el-form-item>

          <el-form-item label="带宽需求(Mbps)" prop="bandwidth">
            <el-col :span="10">
              <el-input
                v-model.number="form.bandwidth"
                placeholder="最大带宽需求"
              ></el-input>
            </el-col>
          </el-form-item>

          <el-form-item label="时延需求(毫秒)" prop="latency">
            <el-col :span="10">
              <el-input
                v-model.number="form.latency"
                placeholder="最大传输时延"
              ></el-input>
            </el-col>
          </el-form-item>

          <el-form-item label="节点1选择" prop="cluster1IP">
            <el-select v-model="form.cluster1IP" placeholder="请选择">
              <el-option
                v-for="node in clusterNodes"
                :key="node.ip"
                :label="node.name"
                :value="node.ip"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="节点2选择" prop="cluster2IP">
            <el-select v-model="form.cluster2IP" placeholder="请选择">
              <el-option
                v-for="node in clusterNodes"
                :key="node.ip"
                :label="node.name"
                :value="node.ip"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="submit">提交</el-button>
            <el-button @click="cancel">取消</el-button>
            <el-button type="warning" @click="reset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { submitVNR } from "../../../api";
import bus from "../../common/bus";
export default {
  data() {
    var checkBandwidthValue = (rule, value, callback) => {
      if (!Number.isInteger(value)) {
        callback(new Error("请输入值"));
      } else {
        if (value == 0) {
          callback(new Error("带宽值不能为0"));
        } else if (value < 0) {
          callback(new Error("带宽值不能为负数"));
        } else {
          callback();
        }
      }
    };
    var checkLatencyValue = (rule, value, callback) => {
      if (!Number.isInteger(value)) {
        callback(new Error("请输入值"));
      } else {
        if (value == 0) {
          callback(new Error("传输延时值不能为0"));
        } else if (value < 0) {
          callback(new Error("传输延时值不能为负数"));
        } else {
          callback();
        }
      }
    };
    var checkCluster1IP = (rule, value, callback) => {
      if (this.form.cluster2IP && value === this.form.cluster2IP) {
        callback(new Error("节点1不能选择与节点2同一个节点"));
      } else {
        callback();
      }
    };
    var checkCluster2IP = (rule, value, callback) => {
      if (this.form.cluster1IP && value === this.form.cluster1IP) {
        callback(new Error("节点2不能选择与节点1同一个节点"));
      } else {
        callback();
      }
    };
    return {
      form: {
        vnetName: "虚拟网络",
        bandwidth: "",
        latency: "",
        cluster1IP: "",
        cluster2IP: "",
      },
      rules: {
        bandwidth: [
          { required: true, message: "带宽需求不为空" },
          { validator: checkBandwidthValue, trigger: "blur" },
        ],
        latency: [
          { required: true, message: "延时需求不为空" },
          { validator: checkLatencyValue, trigger: "blur" },
        ],
        cluster1IP: [
          { required: true, message: "节点1未选择" },
          { validator: checkCluster1IP, trigger: "change" },
        ],
        cluster2IP: [
          { required: true, message: "节点2未选择" },
          { validator: checkCluster2IP, trigger: "change" },
        ],
      },
      clusterNodes: [
        {
          name: "边缘节点1",
          ip: "17.125.10.1",
        },
        {
          name: "边缘节点2",
          ip: "223.26.35.1",
        },
        {
          name: "云端节点",
          ip: "172.1.113.58",
        },
        {
          name: "本机测试节点1",
          ip: "10.0.0.1",
        },
        {
          name: "本机测试节点2",
          ip: "10.0.0.2",
        },
        {
          name: "本机测试节点3",
          ip: "10.0.0.3",
        },
      ],
    };
  },
  created() {},

  methods: {
    parseTopoFile() {},

    submit() {
      // 先校验表单提交的数据是否符合规范
      this.$refs.vnrFormRef.validate((valid) => {
        if (valid) {
          submitVNR(this.form).then((res) => {
            if (res.data.status == "success") {
              alert("虚拟网络请求提交成功！");
              bus.$emit("close_current_tags", "/virtualNetworkRequest");
            } else {
              alert("虚拟网络请求提交失败！");
            }
          });
        } else {
          return false;
        }
      });
    },
    cancel() {
      // 关闭当前页面，跳转到虚拟网络请求详情页
      bus.$emit("close_current_tags", "/virtualNetworkRequest");
    },
    reset() {
      this.$refs.vnrFormRef.resetFields();
    },
  },
};
</script>

<style>
</style>