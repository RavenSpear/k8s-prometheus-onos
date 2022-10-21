<template>
  <div class="crumbs">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item>网络遥测</el-breadcrumb-item>
      <el-breadcrumb-item>Collector配置</el-breadcrumb-item>
    </el-breadcrumb>
    <div class="container">
      <div class="form-box">
        <el-form :model="form" label-width="80px">
          <el-form-item label="Collector">
            <el-input v-model="form.id"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="submit">提交</el-button>
            <!-- <el-button @click="cancel">取消</el-button> -->
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { registerTenant } from "../../../api/index";
export default {
  data() {
    return {
      form: {
        id: "",
      },
    };
  },
  methods: {
    submit() {
      if (
        this.form.id == null ||
        this.form.id == undefined ||
        this.form.id == ""
      ) {
        alert("租户名不能为null或空");
      } else {
        registerTenant(this.form).then((res) => {
          if (res.status === 204) {
            alert("租户注册成功！");
            this.$router.push("/tenantList");
          }
        });
      }7
    },
    cancel() {},
  },
};
</script>

<style>
</style>