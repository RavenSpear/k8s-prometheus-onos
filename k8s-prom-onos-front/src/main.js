import Vue from 'vue'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css';
import App from './App.vue'
import router from './router'
// 导入自定义阿里图标库
// 下载到本地用法
import './assets/fonts/iconfont.css'

import { Network } from "vue-vis-network";

Vue.config.productionTip = false
Vue.use(ElementUI)
Vue.component("network", Network);

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
