<template>
    <iframe v-if="load" v-bind:src="url" frameborder="0" width="100%" height="100%"></iframe>
</template>

<script>
export default {
    data() {
        return {
            url: "",
            load: false
        };
    },
    created() {
        this.makeURL();
        this.load = true;
    },

    methods: {
        makeURL() {
            let containers = this.$route.query;
            if(containers["deviceName"] == "pressure-sensor"){
                this.url = `http://223.3.94.112:3000/d/95UYiuD4k/qi-ya-chuan-gan-qi?orgId=1&var-nodeName=${containers['nodeName']}&refresh=30s&theme=light&kiosk`
            }else if(containers["deviceName"] == "pm-sensor"){
                this.url = `http://223.3.94.112:3000/d/y744ZuD4k/kong-qi-zhi-liang-chuan-gan-qi?orgId=1&var-nodeName=${containers['nodeName']}&refresh=30s&theme=light&kiosk`
            }else{
                this.url = `http://223.3.94.112:3000/d/lNgro_vVk/wen-du-chuan-gan-qi?orgId=1&var-nodeName=${containers['nodeName']}&refresh=30s&theme=light&kiosk`
            }
            console.log(this.url);
        }
    }
};
</script>