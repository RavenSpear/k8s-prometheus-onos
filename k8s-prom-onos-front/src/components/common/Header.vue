<template>
  <div class="header">
    <div class="collapse-btn" @click="collapseChange">
      <i v-if="!collapse" class="el-icon-s-fold"></i>
      <i v-else class="el-icon-s-unfold"></i>
    </div>
    <div class="logo">应用适配的异质物联网资源虚拟化控制复用系统</div>
    <div class="header-right">
      <!-- 暂时先不写用户登录的按钮 -->
    </div>
  </div>
</template>

<script>
import bus from "../common/bus";
export default {
  name: "vHeader",
  data() {
    return {
      collapse: false,
    };
  },
  methods: {
    // 侧边栏折叠
    collapseChange() {
      this.collapse = !this.collapse;
      bus.$emit("collapse", this.collapse);
    },
  },
  mounted() {
    if (document.body.clientWidth < 1500) {
      this.collapseChange();
    }
  }
};
</script>

<style scoped>
.header {
  position: relative;
  box-sizing: border-box;
  width: 100%;
  height: 100px;
  font-size: 30px;
  color: #fff;
}

.collapse-btn {
  float: left;
  padding: 0 21px;
  cursor: pointer;
  line-height: 100px;
}

.header .logo {
  float: left;
  width: 700px;
  line-height: 100px;
}

.header-right {
  float: right;
  padding-right: 50px;
}
</style>