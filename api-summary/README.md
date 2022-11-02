# API SUMMARY

本文档梳理并阐明k8s-prometheus-onos前端所调用的后端api。后端部分包含若干api组，分别映射到k8s及ONOS的不同api服务。

## 一、后端API

### `/network`

#### API映射
```
http://front-ip:port/network -> http://onos-ip:port/onos/v1
```
#### API资源

1. `获取网络设备列表`

- Request

```
GET /network/devices
```

- Response

```
{
  "devices": [
    {
      "id": "device:s1",
      "type": "SWITCH",
      "available": true,
      "role": "MASTER",
      "mfr": "Open Networking Foundation",
      "hw": "BMv2 simple_switch",
      "sw": "Stratum",
      "serial": "unknown",
      "driver": "stratum-bmv2:org.onosproject.pipelines.basic",
      "chassisId": "0",
      "lastUpdate": "1667300925492",
      "humanReadableLastUpdate": "connected 19h29m ago",
      "annotations": {
        "driver": "stratum-bmv2",
        "locType": "none",
        "managementAddress": "grpc://127.0.0.1:50001?device_id=1",
        "name": "device:s1",
        "p4DeviceId": "1",
        "protocol": "P4Runtime, gNMI, gNOI"
      }
    },
    ···
  ]
}
```

2. `获取网络设备安装的流表项`

- Request

```
GET /network/flows/{deviceId}
```

- Response

```
{
  "devices": [
    {
      "groupId": 0,
      "state": "ADDED",
      "life": 77137,
      "liveType": "UNKNOWN",
      "lastSeen": 1667378063754,
      "packets": 84,
      "bytes": 12306,
      "id": "281475604904862",
      "appId": "org.onosproject.core",
      "priority": 5,
      "timeout": 0,
      "isPermanent": true,
      "deviceId": "device:s1",
      "tableId": 2130487575,
      "tableName": "ingress.table0_control.table0",
      "treatment": {
        "instructions": [
          {
            "type": "OUTPUT",
            "port": "CONTROLLER"
          }
        ],
        "clearDeferred": true,
        "deferred": []
      },
      "selector": {
        "criteria": [
          {
            "type": "ETH_TYPE",
            "ethType": "0x800"
          }
        ]
      }
    },
    ···
  ]
}
```

### `/cluster`

#### API映射

```
http://front-ip:port/cluster -> http://k8s-ip:port/api/v1
```

#### API资源

1. `获取集群中节点列表`

- Request

```
GET /cluster/nodes
```

- Response

```

```






### `/metrics`

1. 映射

```
http://front-ip:port/metrics -> http://onos-ip:port/apis/metrics.k8s.io/v1beta1
```
2. 资源

### `/custom-metrics`

1. 映射

```
http://front-ip:port/custom-metrics -> http://onos-ip:port/apis/custom.metrics.k8s.io/v1beta1
```

2. 资源

