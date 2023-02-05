# k8s应用服务部署

参考链接：https://nightlies.apache.org/flink/flink-docs-master/zh/docs/deployment/resource-providers/standalone/kubernetes/#getting-started

## FLINK流处理引擎部署

```
# Configuration 和 service 的定义
$ kubectl create -f flink-configuration-configmap.yaml
$ kubectl create -f jobmanager-service.yaml
# 为集群创建 deployment
$ kubectl create -f jobmanager-session-deployment-non-ha.yaml
$ kubectl create -f taskmanager-session-deployment.yaml
```

## 服务状态检查

```
$ kubectl proxy
$ curl http://localhost:8001/api/v1/namespaces/default/services/flink-jobmanager:webui/proxy
```
