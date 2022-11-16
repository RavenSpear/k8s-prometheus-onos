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
      "mfr": "Open Networking Foundation",
      "hw": "BMv2 simple_switch",
      "sw": "Stratum",
      "lastUpdate": "1667300925492",
      "annotations": {
        "driver": "stratum-bmv2",
        "name": "device:s1",
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

3. `获取网络中的主机`

- Request

```
GET /network/hosts
```

- Response

```
{
  "hosts": [
    {
      "id": "08:00:27:59:9E:67/None",
      "mac": "08:00:27:59:9E:67",
      "ipAddresses": [
        "172.1.113.58"
      ],
      "locations": [
        {
          "elementId": "device:s5",
          "port": "4"
        }
      ]
    }
  ]
}
```

4. `获取设备端口`

- Request

```
GET /network/devices/{id}/ports
```

- Response

```
{
  "id": "device:s1",
  "type": "SWITCH",
  "available": true,
  "mfr": "Open Networking Foundation",
  "hw": "BMv2 simple_switch",
  "sw": "Stratum",
  "lastUpdate": "1667300925492",
  "annotations": {
    "driver": "stratum-bmv2",
    "name": "device:s1",
    "protocol": "P4Runtime, gNMI, gNOI"
  },
  "ports": [
    {
      "element": "device:s1",
      "port": "[s1-eth1](1)",
      "isEnabled": true,
      "type": "copper",
      "portSpeed": 10000
    },
    {
      "element": "device:s1",
      "port": "[eth1](2)",
      "isEnabled": true,
      "type": "copper",
      "portSpeed": 10000
    }
  ]
}
```
5. `获取拓扑设备`

- Request

```
GET /network/topology/clusters/{id}/devices
```

- Response

```
{
  "devices": [
    "device:s1",
    "device:s2",
    "device:s3",
    "device:s4",
    "device:s5",
    "device:s6",
    "device:s7",
    "device:s8"
  ]
}
```

6. `获取拓扑链路`

- Request

```
GET /network/topology/clusters/{id}/links
```

- Response

```
{
  "links": [
    {
      "src": {
        "port": "[s1-eth1](1)",
        "device": "device:s1"
      },
      "dst": {
        "port": "1",
        "device": "device:s2"
      },
      "type": "DIRECT",
      "state": "ACTIVE"
    }
  ]
}
```
### `/terminal`

#### API映射

```
http://front-ip:port/terminal -> http://k8s-ip:port/api/v1
```

#### API资源

1. `获取集群中节点列表`

- Request

```
GET /terminal/nodes
```

- Response

```
{
    "items": [
        {
            "metadata": {
                "name": "master",
                "creationTimestamp": "2022-11-01T12:46:43Z"
            },
            "status": {
                "addresses": [
                    {
                        "type": "InternalIP",
                        "address": "172.1.113.58"
                    }
                ],
                "conditions": [
                    {
                        "type": "Ready",
                        "status": "True"
                    }
                ]
            },
            "nodeInfo": {
                "operatingSystem": "linux",
                "architecture": "amd64"
            }
        },
        ...
    ]
}
        
```
### `/task`
#### API映射
```
http://front-ip:port/task -> http://k8s-ip:port/apis/apps/v1
```

#### API资源

1. `获取集群中任务列表`

- Request

```
GET task/namespaces/default/deployments
```

- Response

```
{
  "items": [
    {
      "metadata": {
        "name": "nginx-dp",
        "namespace": "default",
        "creationTimestamp": "2021-07-19T03:04:19Z",
        "annotations": {
          "kubernetes.io/ingress-bandwidth": "10M",
          "kubernetes.io/egress-bandwidth": "10M" 
        }
      },
      "spec": {
        "replicas": 1,
        "selector": {
          "matchLabels": {
            "app": "nginx"
          }
        },
        "template": {
          "metadata": {
            "creationTimestamp": null,
            "labels": {
              "app": "nginx"
            }
          },
          "spec": {
            "containers": [
              {
                "name": "nginx",
                "image": "nginx:1.14",
                "ports": [
                  {
                    "containerPort": 80,
                    "protocol": "TCP"
                  }
                ],
                "resources": {
                  "limits": {
                    "cpu": "500m",
                    "ephemeral-storage": "4Gi",
                    "memory": "128Mi"
                  },
                  "requests": {
                    "cpu": "250m",
                    "ephemeral-storage": "2Gi",
                    "memory": "64Mi"
                  }
                }
              }
            ]
          }
        },
        "strategy": {
          "type": "RollingUpdate",
          "rollingUpdate": {
            "maxUnavailable": "25%",
            "maxSurge": "25%"
          }
        }
      },
      "status": {
        "observedGeneration": 1,
        "replicas": 1,
        "updatedReplicas": 1,
        "readyReplicas": 1,
        "availableReplicas": 1
      }
    }
  ]
}
```

2. `创建集群任务`

- Request

```
POST task/namespaces/default/deployments
```

- Request Body

```
{
  "apiVersion": "apps/v1",
  "kind": "Deployment",
  "metadata": {
    "name": "nginx-dp"
  },
  "spec": {
    "replicas": 1,
    "revisionHistoryLimit": 10,
    "selector": {
      "matchLabels": {
        "app": "nginx"
      }
    },
    "template": {
      "metadata": {
        "labels": {
          "app": "nginx"
        },
        "annotations": {
          "kubernetes.io/ingress-bandwidth": "1M",
          "kubernetes.io/egress-bandwidth": "1M"
        }
      },
      "spec": {
        "containers": [
          {
            "name": "nginx",
            "image": "nginx:1.14",
            "resources": {
              "requests": {
                "memory": "64Mi",
                "cpu": "250m",
                "ephemeral-storage": "2Gi"
              },
              "limits": {
                "memory": "128Mi",
                "cpu": "500m",
                "ephemeral-storage": "4Gi"
              }
            },
            "ports": [
              {
                "containerPort": 80
              }
            ]
          }
        ]
      }
    }
  }
}
```

### `/IoT`
#### API映射
```
http://front-ip:port/IoT -> http://k8s-ip:port/
```

#### API资源

1. `获取边节点IoT设备列表`

- Request

```
GET IoT/{nodename}/metadata/api/v2/device/all
```

- Response

```
{
    "apiVersion":"v2",
    "statusCode":200,
    "totalCount":3,
    "devices":[
        {
            "created":1668128330690,
            "modified":1668128330690,
            "id":"efbaf607-48f7-4eec-90b5-27717ad7474a",
            "name":"pressure-sensor",
            "description":"RYSIM 气压传感器",
            "adminState":"UNLOCKED",
            "operatingState":"UP",
            "labels":[
                "MQTT"
            ],
            "serviceName":"device-mqtt",
            "profileName":"pressure-device-profile",
            "protocols":{
                "mqtt":{
                    "CommandTopic":"CommandTopic"
                }
            }
        }
    ]
}
```

2. `获取设备数据`

- Request

```
GET IoT/{nodename}/data/api/v2/
```

- Response

```
{
    "apiVersion":"v2",
    "statusCode":200,
    "totalCount":61791,
    "readings":[
        {
            "id":"4693ca7d-cabe-453e-a0ad-aad31059fd07",
            "origin":1668515670007453700,
            "deviceName":"temperature-sensor",
            "resourceName":"temperaturevalue",
            "profileName":"temperature-device-profile",
            "valueType":"String",
            "value":"25.7"
        }
    ]
}
```
## 二、前端页面

### `I. 系统首页`

展示系统全局拓扑。

拓扑中存在四种节点：terminal节点、network节点、task节点和IoT节点。节点之间通过链路连接，task节点和IoT节点仅能连接到terminal节点，network节点可以连接其他network节点以及terminal节点。双击terminal节点以显示/隐藏接入的task节点和IoT节点。

-  `全局拓扑图`

实现待定

### `II. 资源感知首页`

展示系统全局资源占用情况。

包含系统磁盘总量、内存总量、总运行时间、CPU总数。动态展示实时CPU负载，内存负载，磁盘负载。全局CPU使用情况，全局内存使用情况，全局磁盘使用情况。

- `资源感知页面`

页面通过嵌入grafana Dashboard实现。

### `III. 资源感知列表`

展示集群中terminal节点，network节点的列表。

- `云端资源列表`

| 表项 | 资源 |
| :-: | :-: |
| 节点名 | terminal/node.item[i].metadata.name |
| IP地址 | terminal/node.item[i].status.addresses[x(type==InternalIP)].address |
| 操作系统 | terminal/node.item[i].nodeInfo.operatingSystem |
| 创建时间 | terminal/node.item[i].metadata.creationTimestamp |
| 状态 | terminal/node.item[i].status.conditions[x(type==Ready)].status==True?就绪:未就绪 |
| 查看详情 | button(name) |

- `边端资源列表`

内容同云端资源列表

- `传输资源列表`

| 表项 | 资源 |
| :-: | :-: |
| 设备名称 | network/devices[i].annotations.name |
| 设备类型 | network/devices[i].type |
| 端口数量 | network/devices/{id}/ports.length |
| 支持协议 | network/devices[i].annotations.protocol |
| 状态 | network/devices[i].available==true?就绪:未就绪 |
| 查看详情 | button(id) |


### `子页面：Terminal节点资源详情页`

跳转自`资源感知列表`-云端资源列表/边端资源列表中的查看详情按钮。

展示选中节点的资源占用情况。

包含节点磁盘总量、内存总量、总运行时间、CPU总数。动态展示实时CPU负载，内存负载，磁盘负载。节点CPU使用情况，全局内存使用情况，全局磁盘使用情况。

- `Terminal节点资源详情页`

页面通过嵌入grafana Dashboard实现。

### `子页面：Network节点资源详情页`

跳转自资源感知列表中的查看详情按钮。

展示选中节点的网络数据统计。

包含节点安装的流表项目数量，每个端口的线速，状态，转发的报文数量。
每个端口实时带宽的折线图。

- `Network节点资源详情页`

实现待定

### `IV. 设备感知列表`

展示系统Terminal节点接入的设备列表。

- `设备感知列表`

| 表项 | 资源 |
| :-: | :-: |
| 设备名 | IoT/{nodename}/metadata/api/v2/device/all.devices[i].name |
| 描述 | IoT/{nodename}/metadata/api/v2/device/all.devices[i].description |
| 创建时间 | IoT/{nodename}/metadata/api/v2/device/all.devices[i].created |
| 协议 | IoT/{nodename}/metadata/api/v2/device/all.devices[i].protocols |
| 状态 | IoT/{nodename}/metadata/api/v2/device/all.devices[i].operatingState |
| 查看详情 | button(name) |

- `设备详情`

页面通过嵌入grafana Dashboard实现。

### `V. 任务首页`

展示集群中部署任务的资源使用情况。

- `任务首页`

页面通过嵌入grafana Dashboard实现。

| Panel | promSQL |
| :-: | :-: |
| 总任务数 | count(kube_deployment_created{namespace="monitoring"}) |
| 总任务副本数 | sum(kube_deployment_spec_replicas{namespace="monitoring"}) |
| 任务内存使用率 | sum(container_memory_working_set_bytes{container!="POD", namespace="monitoring"})/sum(node_memory_MemTotal_bytes)*100 |
| 任务磁盘使用率 | sum(container_fs_usage_bytes{namespace="monitoring"}) /sum(node_filesystem_size_bytes)*100 |
| 任务CPU使用率 | sum(rate(container_cpu_usage_seconds_total{container!="POD", namespace="monitoring"}[1m]))/sum(rate(node_cpu_seconds_total[1m]))*100 |
| 任务内存用量 | sum(container_memory_working_set_bytes{container!="POD", namespace="monitoring"}) |
| 任务磁盘用量 | sum(container_fs_usage_bytes{container!="POD", namespace="monitoring"}) |
| 任务CPU用量 | sum(rate(container_cpu_usage_seconds_total{container!="POD", namespace="monitoring"}[1m]))/sum(rate(node_cpu_seconds_total[1m]))*100 |

### `VI. 任务列表`

展示集群中部署任务的列表。

- `任务列表`

| 表项 | 资源 |
| :-: | :-: |
| 任务名称 | task/item[i].metadata.name |
| 镜像版本 | task/item[i].spec.template.spec.containers[0].image |
| 创建时间 | task/item[i].metadata.creationTimestamp |
| 副本数| task/item[i].spec.replicas |
| CPU需求 | task/item[i].spec.template.spec.containers[0].resources.limits/requests.cpu |
| 内存需求 | task/item[i].spec.template.spec.containers[0].resources.limits/requests.memory |
| 磁盘需求 | task/item[i].spec.template.spec.containers[0].resources.limits/requests.ephemeral-storage |
| 带宽需求 | task/item[i].metadata.annotations."kubernetes.io/ingress-bandwidth/kubernetes.io/egress-bandwidth |
| 边云负载比 |  |
| 查看详情  | button(id) |

- `任务资源详情页`

页面通过嵌入grafana Dashboard实现。

| Panel | promSQL |
| :-: | :-: |
| 当前任务副本数 | sum(kube_deployment_spec_replicas{deployment=~"$deployment",namespace="monitoring"}) |
| 当前任务CPU使用率 | sum(rate(container_cpu_usage_seconds_total{container!="POD", pod=~"$pod",namespace="monitoring"}[1m]))/sum(rate(node_cpu_seconds_total[1m]))*100 |
| 当前任务内存使用率 | sum(container_memory_working_set_bytes{container!="POD", pod=~"$pod", namespace="monitoring"})/sum(node_memory_MemTotal_bytes)*100 |
| 当前任务磁盘使用率 | sum(container_fs_usage_bytes{container!="POD", pod=~"$pod", namespace="monitoring"}) /sum(node_filesystem_size_bytes)*100 |
| 当前任务内存用量 | sum(container_memory_working_set_bytes{container!="POD", pod=~"$pod",namespace="monitoring"}) by (pod) |
| 当前任务磁盘用量 | sum(container_fs_usage_bytes{container!="POD", pod=~"$pod",namespace="monitoring"}) by (pod) |
| 当前任务CPU用量 | sum(rate(container_cpu_usage_seconds_total{container!="POD", pod=~"$pod", namespace="monitoring"}[1m])) by (pod) / on() group_left() sum(rate(node_cpu_seconds_total[1m])) |

### `VII. 任务部署`

填写表单以在系统中部署任务。

- `任务部署表单`

| 表项 | 资源 |
| :-: | :-: |
| 任务名称 | task/item[i].metadata.name |
| 镜像版本 | task/item[i].spec.template.spec.containers[0].image |
| 副本数| task/item[i].spec.replicas |
| CPU需求 | task/item[i].spec.template.spec.containers[0].resources.limits/requests.cpu |
| 内存需求 | task/item[i].spec.template.spec.containers[0].resources.limits/requests.memory |
| 磁盘需求 | task/item[i].spec.template.spec.containers[0].resources.limits/requests.ephemeral-storage |
| 带宽需求 | task/item[i].metadata.annotations."kubernetes.io/ingress-bandwidth/kubernetes.io/egress-bandwidth |

### `VIII. 虚拟网络首页`

展示系统中虚拟网络的拓扑。

- `虚拟网络拓扑`

实现待定

### `IX. 虚拟网络列表`

展示系统中虚拟网络列表。

- `虚拟网络列表`

实现待定

### `X. 创建虚拟网络`

填写表单以在系统中部署虚拟网络。

- `虚拟网络表单`

实现待定