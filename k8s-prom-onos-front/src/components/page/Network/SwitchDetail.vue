<template>
  <iframe
    v-if="load"
    v-bind:src="url"
    frameborder="0"
    width="100%"
    height="100%"
  ></iframe>
</template>

<script>
export default {
  data() {
    return {
      url: "",
      load: false,
    };
  },
  created() {
    this.makeURL();
    this.load = true;
  },
  methods: {
    makeURL() {
        let query = this.$route.query;
        this.url = "http://223.3.94.112:3000/d/8Eea7KKVz/jiao-huan-ji-xiang-qing-ye?orgId=1&theme=light&kiosk&var-deviceId="+query.switchName;
    },
  },
};
</script>

<style>
</style>