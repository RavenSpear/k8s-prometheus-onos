require('events').EventEmitter.defaultMaxListeners = 20;
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
            },

            /* Apis for k8s api/v1 */
            '/cluster': {
                target: 'http://192.168.1.104:8009/api/v1',
                changeOrigin: true,
                pathRewrite: {
   				 '^/cluster': '',  
 			    },
            },

            /* Apis for k8s apis/custom.metrics.k8s.io/v1beta1 */
            '/costum-metrics': {
                target: 'http://192.168.1.104:8009/apis/custom.metrics.k8s.io/v1beta1',
                changeOrigin: true,
                pathRewrite: {
   				 '^/costum-metrics': '',   
 			    },
            },

            /* Apis for k8s apis/metrics.k8s.io/v1beta1 */
            '/metrics': {
                target: 'http://192.168.1.104:8009/apis/metrics.k8s.io/v1beta1',
                changeOrigin: true,
                pathRewrite: {
   				 '^/metrics': '',   
 			    },
            },

            /* Apis for onos api/v1 */
            '/network': {
                target: 'http://192.168.1.104:8181/onos/v1',
                changeOrigin: true,
                pathRewrite: {
   				 '^/network': '',   
 			    },
            }
        }
    }
}
