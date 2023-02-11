import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '../components/common/Home.vue'
import Dashboard from '../components/page/Dashboard.vue'

import VirtualNetworkDashboard from '../components/page/Network/VirtualNetworkDashboard.vue'
import VirtualNetworkList from '../components/page/Network/VirtualNetworkList.vue'
import VirtualNetworkDetail from '../components/page/Network/VirtualNetworkDetail.vue'
import VirtualNetworkRequest from '../components/page/Network/VirtualNetworkRequest.vue'
import VirtualNetworkRequestCreate from '../components/page/Network/VirtualNetworkRequestCreate.vue'
import ServiceList from '../components/page/Network/ServiceList.vue'
import ServiceRegister from '../components/page/Network/ServiceRegister.vue'
import SwitchDetail from '../components/page/Network/SwitchDetail.vue'

import ResourceDashboard from '../components/page/Resource/ResourceDashboard.vue'
import ResourceList from '../components/page/Resource/ResourceList.vue'
import DeviceList from '../components/page/Resource/DeviceList.vue'

import TaskDashboard from '../components/page/Task/TaskDashboard.vue'
import TaskList from '../components/page/Task/TaskList.vue'
import TaskCreate from '../components/page/Task/TaskCreate.vue'

import TaskDetail from '../components/page/Task/TaskDetail.vue'
import DeviceDetail from '../components/page/Resource/DeviceDetail.vue'
import NodeDetail from '../components/page/Resource/NodeDetail.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: "/",
    redirect: "/dashboard"
  },
  {
    path: '/',
    component: Home,
    meta: { title: '系统首页' },
    children: [
      {
        path: "/dashboard",
        component: Dashboard,
        meta: { title: '系统首页' }
      },

      //存传算资源感知复用
      {
        path: "/resourceDashboard",
        component: ResourceDashboard,
        meta: { title: '资源感知概览' }
      },
      {
        path: "/deviceList",
        component: DeviceList,
        meta: { title: '设备感知列表' }
      },
      {
        path: "/deviceDetail",
        component: DeviceDetail,
        meta: { title: '设备详情' }
      },
      {
        path: "/resourceList",
        component: ResourceList,
        meta: { title: '资源感知列表' }
      },
      {
        path: "/nodeDetail",
        component: NodeDetail,
        meta: { title: '节点详情' }
      },

      //边云智能协同计算资源
      {
        path: "/taskDashboard",
        component: TaskDashboard,
        meta: { title: '边云任务概览' }
      },
      {
        path: "/taskList",
        component: TaskList,
        meta: { title: '边云任务列表' }
      },
      {
        path: "/taskCreate",
        component: TaskCreate,
        meta: { title: '任务协同部署' }
      },
      {
        path: "/taskDetail",
        component: TaskDetail,
        meta: { title: '任务资源详情' }
      },

      //感知数据传输优化资源
      {
        path: "/virtualNetworkDashboard",
        component: VirtualNetworkDashboard,
        meta: { title: '网络资源控制概览' }
      },
      {
        path: '/createVirtualNetworkRequest',
        component: VirtualNetworkRequestCreate,
        meta: { title: '创建虚拟网络请求' }
      },
      {
        path: '/virtualNetworkRequest',
        component: VirtualNetworkRequest,
        meta: { title: '虚拟网络请求' }
      },
      {
        path: '/virtualNetworkList',
        component: VirtualNetworkList,
        meta: { title: '虚拟网络列表' }
      },
      {
        path: "/virtualNetworkDetail",
        component: VirtualNetworkDetail,
        meta: { title: '虚拟网络详情' }
      },
      {
        path: "/serviceList",
        component: ServiceList,
        meta: { title: '虚拟网络服务列表' }
      },
      {
        path: "/serviceRegister",
        component: ServiceRegister,
        meta: { title: '虚拟网络服务注册' }
      },
      {
        path:"switchDetail",
        component: SwitchDetail,
        meta: { title: '交换机详情' }
      }
    ]
  }
]

const router = new VueRouter({
  // mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router