import Vue from 'vue'
import VueRouter from 'vue-router'

import Home from '../components/common/Home.vue'
import Dashboard from '../components/page/Dashboard.vue'

import VirtualNetworkDashboard from '../components/page/Network/VirtualNetworkDashboard.vue'
//mport AddTenant from '../components/page/Network/AddTenant.vue'
import TenantList from '../components/page/Network/TenantList.vue'
import Piplines from '../components/page/Network/Pipelines.vue'
import TopoConfig from '../components/page/Network/TopoConfig.vue'
import TopoShow from '../components/page/Network/TopoShow.vue'
import PhysicalNetwork from '../components/page/Network/PhysicalNetwork.vue'
import VirtualNetworkCreate from '../components/page/Network/VirtualNetworkCreate.vue'
import VirtualNetworkList from '../components/page/Network/VirtualNetworkList.vue'
import VirtualNetworkTopo from '../components/page/Network/VirtualNetworkTopo.vue'
import CollectorConfig from '../components/page/Network/CollectorConfig.vue'

import ResourceDashboard from '../components/page/Resource/ResourceDashboard.vue'
import ResourceList from '../components/page/Resource/ResourceList.vue'
import DeviceList from '../components/page/Resource/DeviceList.vue'

import TaskDashboard from '../components/page/Task/TaskDashboard.vue'
import TaskList from '../components/page/Task/TaskList.vue'
import TaskCreate from '../components/page/Task/TaskCreate.vue'

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

      //传存算资源监视器资源
      {
        path: "/resourceDashboard",
        component: ResourceDashboard,
        meta: { title: '资源感知首页' }
      },
      {
        path: "/deviceList",
        component: DeviceList,
        meta: { title: '设备感知列表' }
      },
      {
        path: "/resourceList",
        component: ResourceList,
        meta: { title: '资源感知列表' }
      },

      //边云协同任务调度资源
      {
        path: "/taskDashboard",
        component: TaskDashboard,
        meta: { title: '任务首页' }
      },
      {
        path: "/taskList",
        component: TaskList,
        meta: { title: '任务列表' }
      },
      {
        path: "/taskCreate",
        component: TaskCreate,
        meta: { title: '任务部署' }
      },

      //传输资源虚拟化控制资源
      {
        path: "/virtualNetworkDashboard",
        component: VirtualNetworkDashboard,
        meta: { title: '传输资源控制首页' }
      },
      // {
      //   path: "/addTenant",
      //   component: AddTenant,
      //   meta: { title: '添加租户' }
      // },
      {
        path: "/tenantList",
        component: TenantList,
        meta: { title: '租户列表' }
      },
      {
        path: '/pipelines',
        component: Piplines,
        meta: { title: 'P4 Pipeline' }
      },
      {
        path: '/topoConfig',
        component: TopoConfig,
        meta: { title: '网络配置' }
      },
      {
        path: '/topoShow',
        component: TopoShow,
        meta: { title: '网络拓扑' }
      },
      {
        path: '/physicalNetwork',
        component: PhysicalNetwork,
        meta: { title: '物理网络' }
      },
      {
        path: '/createVirtualNetwork',
        component: VirtualNetworkCreate,
        meta: { title: '创建虚拟网络' }
      },
      {
        path: '/virtualNetworkList',
        component: VirtualNetworkList,
        meta: { title: '虚拟网络列表' }
      },
      {
        path: '/virtualNetworkTopo',
        name: "virtualNetworkTopo",
        component: VirtualNetworkTopo,
        meta: { title: '虚拟网络拓扑'}
      },
      {
        path: '/CollectorConfig',
        component: CollectorConfig,
        meta: { title: 'Collecter配置' }
      },
    ]
  }
]

const router = new VueRouter({
  // mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
