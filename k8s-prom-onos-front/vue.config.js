module.exports = {
    devServer: {
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
            },

            '/cluster': {
                target: 'http://192.168.1.104:8009/api/v1',
                changeOrigin: true,
                pathRewrite: {
   				 '^/cluster': '',   //重写请求路径
 			    },
            }
        }
    }
}
