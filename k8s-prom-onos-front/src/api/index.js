
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
        url: '/network/configuration',
        method: 'post',
        headers: {'Content-Type': 'application/json'},
        data: cfgJSON
    })
}

export const getDevices = () =>{
    return request({
        url:'/devices',
        method:'get'
    })
}
export const getPorts = () =>{
    return request({
        url:'/devices/ports',
        method:'get'
    })
}

export const getLinks = () =>{
    return request({
        url:'/links',
        method:'get'
    })
}

export const getHosts = () =>{
    return request({
        url:'/hosts',
        method:'get'
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
        url: 'cluster/nodes',
        method: 'get'
    })
}

/* Api to get node according to its name from k8s api/v1 */
export const getClusterNode = nodename => {
    return request({
        url: 'cluster/nodes/'+nodename,
        method: 'get'
    })
}

/* Api to get pods from k8s api/v1 */
export const getClusterPods = query => {
    return request({
        url: 'cluster/namespaces/kube-system/pods',
        method: 'get',
        params: query
    })
}

/* Api to get devices from onos api*/
export const getNetworkNodes = () => {
    return request({
        url: 'network/devices',
        method: 'get'
    })
}

/* Api to get tasks from k8s apis/apps/v1 */
export const getTaskList = () => {
    return request({
        url: 'task/namespaces/kube-system/deployments',
        method: 'get'
    })
}

/* Api to post tasks to k8s apis/apps/v1 */
export const createTask = formData => {
    return request({
        url: 'task/namespaces/kube-system/deployments',
        method: 'post',
        data: formData
    })
}

/* Api to post tasks to k8s apis/apps/v1 */
export const deleteTask = name => {
    return request({
        url: 'task/namespaces/kube-system/deployments/' + name,
        method: 'delete'
    })
}