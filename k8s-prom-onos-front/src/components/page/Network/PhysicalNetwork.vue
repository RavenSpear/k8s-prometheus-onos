<template>
  <div>
    <!-- 面包屑导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item
          >
          网络拓扑</el-breadcrumb-item
        >
        <el-breadcrumb-item>物理网络拓扑</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 主要内容部分 -->
    <div class="container">
      <div class="content-title">导入物理网络配置</div>
      <el-upload
        class="upload-demo"
        drag
        action="http://jsonplaceholder.typicode.com/api/posts/"
        multiple
        accept=".json"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">只支持NetConfig的JSON文件</div>
      </el-upload>
    </div>
  </div>
</template>

<script>
export default {
  name: "PhysicalNetwork",
  data() {
    return {
      fileList: [],
    };
  },
};
</script>

<style scoped>
.content-title {
  font-weight: 400;
  line-height: 50px;
  margin: 10px 0;
  font-size: 22px;
  color: #1f2f3d;
}
</style>