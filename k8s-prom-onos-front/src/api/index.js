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
