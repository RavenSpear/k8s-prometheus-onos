
import request from '../utils/request'
//================ Tenant API =================

export const fetchTenantData = query => {
    return request({
        url: '/tenants',
        method: 'get',
        params: query
    })
}

export const registerTenant = formData => {
    return request({
        url: '/tenants',
        method: 'post',
        headers: { 'Content-Type': 'application/json' },
        data: formData
    })
}

export const deleteTenant = tenantId => {
    return request({
        url: '/tenants/' + tenantId,
        method: 'delete'
    })
}

export const uploadNetConfigFile = cfgJSON => {
    return request({
        // 原映射
        //url: '/network/configuration',
        url: '/network/network/configuration',
        method: 'post',
        headers: {'Content-Type': 'application/json'},
        data: cfgJSON
    })
}

export const getDevices = () =>{
    return request({
        // 原映射
        //url:'/devices',
        url:'/network/devices',
        method:'get'
    })
}
export const getPorts = () =>{
    return request({
        // 原映射
        //url:'/devices/ports',
        url:'/network/devices/ports',
        method:'get'
    })
}

export const getLinks = () =>{
    return request({
        // 原映射
        // url:'/links',
        url:'/network/links',
        method:'get'
    })
}

export const getHosts = () =>{
    return request({
        // 原映射
        // url:'/hosts',
        url:'/network/hosts',
        method:'get'
    })
}

export const getDeviceOpenPort = deviceId =>{
    return request({
        // 原映射
        //url:'/devices',
        url:`/network/devices/${deviceId}/ports`,
        method:'get'
    })
}


//================ Virtual Network Request API =================

export const submitVNR = formData => {
    return request({
        url: "/vnr",
        method: "post",
        headers: { 'Content-Type': 'application/json' },
        data: formData
    })
}



export const getAllVNRs = () => {
    return request({
        url: "/vnr",
        method: "get"
    })
}




//================ Virtual Network API =================


export const fetchVirtualNetworkData = query => {
    return request({
        url: '/vnets',
        method: 'get',
        params: query
    })
}

export const addVirtualNetwork = formData => {
    return request({
        url: '/vnets',
        method: 'post',
        headers: { 'Content-Type': 'application/json' },
        data: formData
    })
}

export const getVirtualNetworkTopo = query => {
    return request({
        url: '/vnets/topology/'+query.id,
        method: 'get'
    })
}

export const embedVirtualNetworkTopo = query => {
    return request({
        url: '/vnets/embed/'+query.id,
        method: 'get'
    })
}


/* Api to get nodes from k8s api/v1 */
export const getClusterNodes = () => {
    return request({
        url: 'terminal/nodes',
        method: 'get'
    })
}

/* Api to get node according to its name from k8s api/v1 */
export const getClusterNode = nodename => {
    return request({
        url: 'terminal/nodes/'+nodename,
        method: 'get'
    })
}

/* Api to get pods from k8s api/v1 */
export const getClusterPods = query => {
    return request({
        url: 'terminal/namespaces/monitoring/pods',
        method: 'get',
        params: query
    })
}

/* Api to get devices from onos api*/
// export const getNetworkNodes = () => {
//     return request({
//         url: 'network/devices',
//         method: 'get'
//     })
// }

/* Api to get tasks from k8s apis/apps/v1 */
export const getTaskList = () => {
    return request({
        url: 'task/namespaces/monitoring/deployments',
        method: 'get'
    })
}

/* Api to post tasks to k8s apis/apps/v1 */
export const createTask = formData => {
    return request({
        url: 'task/namespaces/monitoring/deployments',
        method: 'post',
        data: formData
    })
}

/* Api to post tasks to k8s apis/apps/v1 */
export const deleteTask = name => {
    return request({
        url: 'task/namespaces/monitoring/deployments/' + name,
        method: 'delete'
    })
}

export const getNetworkTopoDevices = ()=>{
    return request({
        url: '/network/topology/clusters/0/devices',
        method: 'get'
    })
}

export const getNetworkTopoLinks = ()=>{
    return request({
        url: '/network/topology/clusters/0/links',
        method: 'get'
    })
}

export const getIoTDeviceData = nodename =>{
    return request({
        url: '/IoT/'+nodename+'/data/api/v2/',
        method: 'get'
    })
}

export const getIoTDevices = nodename =>{
    return request({
        url: '/IoT/'+nodename+'/metadata/api/v2/device/all',
        method: 'get'
    })
}

export const getEdgexCommand = nodename =>{
    return request({
        url: '/IoT/'+nodename+'/command',
        method: 'get'
    })
}