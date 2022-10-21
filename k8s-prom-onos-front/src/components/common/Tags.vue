<template>
  <div class="tags" v-show="showTags">
    <ul>
      <li
        class="tags-li"
        v-for="(item, index) in tagsList"
        :class="{ active: isActive(item.path) }"
        :key="index"
      >
        <router-link class="tags-li-title" :to="item.path">
          {{ item.title }}
        </router-link>
        <span class="tags-li-icon" @click="closeTags(index)"
          ><i class="el-icon-close"></i
        ></span>
      </li>
    </ul>
  </div>
</template>

<script>
import bus from "./bus";
export default {
  name: "vTags",
  data() {
    return {
      tagsList: [],
    };
  },
  methods: {
    setTags(route) {
      
      // some函数依次检查数组中的内容，若遇到第一个满足条件的元素，直接返回true
      const isExist = this.tagsList.some((item) => {
        return item.path === route.fullPath;
      });

      // 如果不存在
      if (!isExist) {
        // 目前暂时支持最多存8个标签，超过8个需要移除多余的
        if (this.tagsList.length >= 8) {
          this.tagsList.shift();
        }
        this.tagsList.push({
          title: route.meta.title,
          path: route.fullPath,
          // 携带component的name
          name: route.matched[1].components.default.name,
        });
        
        bus.$emit("tags", this.tagsList);
      }
    },
    isActive(path) {
      return path === this.$route.fullPath;
    },
    // 关闭单个标签
    closeTags(index) {
      // tagList在指定位置删除元素
      const delItem = this.tagsList.splice(index, 1)[0];
      // 获取下一个要跳转的标签(非末端就取下一个，某端就取前一个)
      const nextItem = this.tagsList[index]
        ? this.tagsList[index]
        : this.tagsList[index - 1];

      if (nextItem) {
        delItem.path === this.$route.fullPath &&
          this.$router.push(nextItem.path);
      } else {
        this.$router.push("/");
      }
    },
  },
  computed: {
    showTags() {
      return this.tagsList.length > 0;
    },
  },
  watch: {
    // 监视路由的变换
    $route(newValue) {
      this.setTags(newValue);
    },
  },
};
</script>

<style>
.tags {
  position: relative;
  height: 70px;
  overflow: hidden;
  background: #fff;
  padding-right: 120px;
  box-shadow: 0 5px 10px #ddd;
}

.tags ul {
  box-sizing: border-box;
  width: 100%;
  height: 100%;
}

.tags-li {
  float: left;
  margin: 3px 5px 2px 3px;
  border-radius: 3px;
  font-size: 20px;
  overflow: hidden;
  cursor: pointer;
  height: 60px;
  line-height: 60px;
  border: 1px solid #e9eaec;
  background: #fff;
  padding: 0 5px 0 12px;
  vertical-align: middle;
  color: #666;
  -webkit-transition: all 0.3s ease-in;
  -moz-transition: all 0.3s ease-in;
  transition: all 0.3s ease-in;
}

.tags-li:not(.active):hover {
  background: #f8f8f8;
}

.tags-li.active {
  color: #fff;
}

.tags-li-title {
  float: left;
  max-width: 300px;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  margin-right: 10px;
  color: #666;
}

.tags-li.active .tags-li-title {
  color: #fff;
}
</style>