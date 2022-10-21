<template>
  <div class="wrapper">
    <vHeader></vHeader>
    <vSidebar></vSidebar>
    <div class="content-box" :class="{ 'content-collapse': collapse }">
      <vTags></vTags>
      <div class="content">
        <transition name="move" mode="out-in">
          <keep-alive :include="tagsList">
            <router-view :key="$route.fullPath"></router-view>
          </keep-alive>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import vHeader from "./Header.vue";
import vSidebar from "./Sidebar.vue";
import vTags from "./Tags.vue";
import bus from "./bus";
export default {
  name: "vHome",
  data() {
    return {
      tagsList: [],
      collapse: false,
    };
  },
  components: { vHeader, vSidebar, vTags },
  created() {
    bus.$on("collapse-content", (msg) => {
      this.collapse = msg;
    });

    bus.$on("tags", (msg) => {
      let arr = [];
      for (let i = 0, len = msg.length; i < len; i++) {
        msg[i].name && arr.push(msg[i].name);
      }
      this.tagsList = arr;
    });
  },
};
</script>

<style>
</style>