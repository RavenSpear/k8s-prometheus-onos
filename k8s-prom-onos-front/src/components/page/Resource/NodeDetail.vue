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
            this.url = `http://1.117.170.89:3000/d/NHhq3tv4z/jie-dian-xiang-qing-ye?orgId=1&var-nodeName=${containers['nodeName']}&refresh=30s&theme=light&kiosk`
            console.log(this.url);
        }
    }
};
</script>