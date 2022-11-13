<template>
    <iframe v-if="load" v-bind:src="url" frameborder="0" width="100%" height="100%"></iframe>
</template>

<script>
export default {
    data() {
        return {
            url: "http://223.3.94.112:3000/d/h2UbV_D4z/ren-wu-zi-yuan-xiang-qing-ye?orgId=1",
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
            console.log(containers);
            for (var val in containers) {
                this.url += ("&var-container=" + containers[val]);
            }
            console.log(this.url);
            return this.$route.query;
        }
    },
};
</script>