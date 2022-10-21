<template>
  <div>
    <!-- 面包屑导航 -->
    <div class="crumbs">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item> 拓扑管理</el-breadcrumb-item>
        <el-breadcrumb-item>网络配置</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <!-- 主要内容部分 -->
    <div class="container">
      <div class="content-title">导入物理网络配置文件</div>
      <el-upload
        class="upload-demo"
        drag
        action="#"
        accept=".json"
        :http-request="uploadFile"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          只支持NetConfig协议的JSON文件
        </div>
      </el-upload>
    </div>
  </div>
</template>

<script>
import { uploadNetConfigFile } from "../../../api/index";
export default {
  data() {
    return {
      fileList: [],
    };
  },
  methods: {
    uploadFile: function (e) {
      let file = e.file;
      let reader = new FileReader();
      reader.onload = async (ev) => {
        try {
          let document = JSON.parse(ev.target.result);
          uploadNetConfigFile(document).then((res) => {
            if (res.status === 200) {
              alert("导入成功！");
              this.$router.push({path:'/TopoShow',query: {}});
            }
          });
        } catch (ex) {
          console.log(ex);
          return false;
        }
      };
      reader.readAsBinaryString(file);
    },
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