<template>
  <div>
    <el-row :gutter="20" class="mgb20">
      <el-col :span="12">
        <el-card style="height: 400px">
          <div slot="header" class="clearfix">
            <span>物理网络拓扑</span>
          </div>
          <div>
            <network
              class="network"
              ref="network"
              :nodes="nodes"
              :edges="edges"
              :options="options"
            ></network>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-row :gutter="10" class="mgb20">
          <!-- 统计1：虚拟网络请求总数 -->
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="grid-content">
                <i class="el-icon-s-data icon-lg-size"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ requestNum }}</div>
                  <div>虚拟网络请求总数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <!-- 统计2：已经嵌入的虚拟网络数量 -->
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="grid-content">
                <i class="el-icon-s-data icon-lg-size"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ vnetNum }}</div>
                  <div>已嵌入虚拟网络数</div>
                </div>
              </div>
            </el-card>
          </el-col>
          <!-- 统计3：剩余网络带宽资源 -->
          <el-col :span="8">
            <el-card shadow="hover">
              <div class="grid-content">
                <i class="el-icon-s-data icon-lg-size"></i>
                <div class="grid-cont-right">
                  <div class="grid-num">{{ vnetNum }}</div>
                  <div>剩余带宽资源</div>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row class="mgb20">
          <el-card style="height: 260px"></el-card>
        </el-row>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mgb20">
      <el-col :span="12"><el-card style="height: 360px"></el-card></el-col>
      <el-col :span="12"><el-card style="height: 360px"></el-card></el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  data() {
    return {
      requestNum: "10",
      vnetNum: "8",
    };
  },
};
</script>

<style scoped>
.mgb20 {
  margin-bottom: 20px;
}

.grid-content {
  display: flex;
  align-items: center;
  height: 80px;
}

.grid-cont-right {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: #999;
}

.grid-num {
  font-size: 30px;
  font-weight: bold;
}

.icon-lg-size {
  font-size: 40px;
}
</style>