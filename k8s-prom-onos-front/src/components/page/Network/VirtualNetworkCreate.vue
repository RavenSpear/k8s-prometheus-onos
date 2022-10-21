<template>
  <div>
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item>虚拟网络管理</el-breadcrumb-item>
        <el-breadcrumb-item>创建新的虚拟网络</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <div class="container">
      <div class="form-box">
        <el-form :model="form" label-width="80px">
          <el-form-item label="选择租户">
            <el-select v-model="form.tenantId" placeholder="请选择">
              <el-option
                v-for="item in tenants"
                :key="item.id"
                :label="item.id"
                :value="item.id"
              >
              </el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="虚拟拓扑">
            <input ref="vTopo" @change="parseTopoFile()" type="file" />
            <!-- <el-button><i class="el-icon-plus">导入配置文件</i></el-button> -->
          </el-form-item>

          <!-- <el-form-item label="时延需求">
            <el-input v-model="form.transmissionDelay"></el-input>
          </el-form-item> -->
          <!-- 
          <el-form-item label="主机1-IP">
            <el-input v-model="form.h1IP"></el-input>
          </el-form-item>

          <el-form-item label="主机2-IP">
            <el-input v-model="form.h2IP"></el-input>
          </el-form-item> -->

          <el-form-item>
            <el-button type="primary" @click="submit">创建</el-button>
            <!-- <el-button @click="cancel">取消</el-button> -->
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { addVirtualNetwork, fetchTenantData } from "../../../api";
export default {
  data() {
    return {
      tenants: [],
      form: {
        tenantId: "",
        topo: { nodes: { switches: [], hosts: [] }, links: [] },
      },
    };
  },
  created() {
    fetchTenantData().then((res) => {
      this.tenants = res.data.tenants;
    });
  },

  methods: {
    parseTopoFile() {
      let fileName = this.$refs.vTopo.files[0].name;
      let a = fileName.split(".");
      let suffix = a[a.length - 1];
      if (suffix == "json") {
        let _that = this;
        const file = this.$refs.vTopo.files[0];
        const reader = new FileReader();
        reader.readAsBinaryString(file);
        reader.onload = function () {
          let json = JSON.parse(this.result);
          _that.form.topo = json;
        };
      } else {
        alert("不能解析该类型文件，请重新上传文件！");
      }
    },
    submit() {
      if (this.form.tenantId == "") {
        this.$message({
          showClose: true,
          type: "error",
          message: "请选择用户！",
        });
      } else if (this.$refs.vTopo.files.length == 0) {
        this.$message({
          showClose: true,
          type: "error",
          message: "请上传虚拟拓扑文件",
        });
      } else {
        addVirtualNetwork(this.form).then((res) => {
          if (res.data.status == "successful") {
            alert("虚拟网络创建成功！");
            this.$router.push("/virtualNetworkList");
          } else {
            alert("虚拟网络创建失败，请上传正确的虚拟拓扑文件！");
          }
        });
      }
    },
  },
};
</script>

<style>
</style>