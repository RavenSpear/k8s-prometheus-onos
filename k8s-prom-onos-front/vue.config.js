module.exports = {
    devServer: {
        host:"223.3.94.112",
        port:"80",
        proxy: {
            '/tenants': {
                target: 'http://127.0.0.1:8181/onos/p4virtex',
                changeOrigin: true
            },
            '/vnets': {
                target: 'http://127.0.0.1:8181/onos/p4virtex',
                changeOrigin: true
            },
            '/vnets/topology': {
                target: 'http://127.0.0.1:8181/onos/p4virtex',
                changeOrigin: true
            },
            '/vnets/embed': {
                target: 'http://127.0.0.1:8181/onos/p4virtex',
                changeOrigin: true
            },
            '/network/configuration': {
                target: 'http://127.0.0.1:8181/onos/v1',
                changeOrigin: true,
            },
            '/devices': {
                target: 'http://127.0.0.1:8181/onos/v1',
                changeOrigin: true,
            },
            '/devices/ports': {
                target: 'http://127.0.0.1:8181/onos/v1',
                changeOrigin: true,
            },
            '/links': {
                target: 'http://127.0.0.1:8181/onos/v1',
                changeOrigin: true,
            },
            '/hosts': {
                target: 'http://127.0.0.1:8181/onos/v1',
                changeOrigin: true,
            }
        }
    }
}
