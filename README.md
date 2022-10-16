# k8s-prometheus-onos

## I. 系统需求
- 操作系统
  - Ubuntu 18.04 (LTS)
- 网络:
  - 至少一张网卡，节点之间互相连通且可以通过ssh通信
- 节点: 
  - 1 master node（主节点）
  - 0 ~ n worker nodes（从节点）

## II. k8s集群部署

### 安装内容
  - Kubernetes 1.23.12
  - OpenvSwitch 2.10.0
  - Docker

### 安装（以下命令的执行需要root权限）
1. 主节点安装git和ansible

```
# apt-get install ansible git -y
```

2. 主节点生成RSA密钥，注意不要生成密码
```
# ssh-keygen -t rsa
```

3. 配置每个节点的主机名，该命令需要在每个节点运行（建议主节点命名为cloud，从节点命名为edge1，edge2）
```
# hostnamectl set-hostname $node-name
```
(可选) 在主节点/etc/hosts中配置主机名同IP的映射，参考如下配置
```
master  192.168.56.101
worker  192.168.56.201
```

4. 拷贝并向所有从节点分发RSA公钥（master + worker）
```
# ssh-copy-id root@$node-name
```

5. 参考```inventory/default/hosts.ini.example```，添加 ```hosts.ini```
```
[master]
192.168.56.101

[worker]
192.168.56.201

[cluster:children]
master
worker
```

6. 参考```inventory/default/group_vars/all.yml.example```添加```all.yml```，在不启用sona的情况下，无需关注```ext_intf```和```ext_gw_ip```的配置

7. 主节点进入```k8s-ansible-no-sona```目录，运行ansible脚本
```
# cd k8s-ansible-no-sona/
# ansible-playbook inventory/default/sitewithoutona.yml
```

### 重置集群
主节点执行以下脚本以重置集群，注意k8s，OvS，Docker软件本身不会被卸载
```
# ansible-playbook inventory/default/reset-site.yml
```
### 可能遇到的问题
- 无法```ssh root@$node-name```，参考[https://www.cnblogs.com/davis12/p/14325197.html](https://www.cnblogs.com/davis12/p/14325197.html)
- 待补充

## 2. k8s-prometheus部署

### 安装内容
  - calico-3.20.6
  - node-exporter-1.3.1
  - kube-state-metrics-2.5.0
  - prometheus-adapter-0.9.1
  - prometheus-operator-0.57.0
  - prometheus-2.36.1

### 安装（以下命令的执行需要root权限）

1. 进入calico目录，部署calico网络插件以启用k8s虚拟网络

```
# cd calico/
# kubectl apply -f calico/
```
查看集群状态，当所有节点状态STATUS为Ready表示网络建立成功
```
# kubectl get nodes
NAME     STATUS   ROLES                  AGE   VERSION
master   Ready    control-plane,master   45h   v1.23.12
```
2. 进入k8s-prometheus目录，建立namespace以及CustomResourceDefinition，启动prometheus-operater
```
# cd k8s-prometheus/
# kubectl apply --server-side -f manifests/setup
```
检查部署情况
```
# kubectl get all -n monitoring
NAME                                      READY   STATUS    RESTARTS   AGE
pod/prometheus-operator-f59c8b954-g7fm6   2/2     Running   0          3m33s

NAME                          TYPE        CLUSTER-IP   EXTERNAL-IP   PORT(S)    AGE
service/prometheus-operator   ClusterIP   None         <none>        8443/TCP   3m33s

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/prometheus-operator   1/1     1            1           3m34s

NAME                                            DESIRED   CURRENT   READY   AGE
replicaset.apps/prometheus-operator-f59c8b954   1         1         1       3m34s

```
3. 部署其余组件
```
# kubectl apply -f manifests/
```
检查部署情况，以及api服务状况
```
# kubectl get all -n monitoring
NAME                                      READY   STATUS    RESTARTS   AGE
pod/kube-state-metrics-6c8846558c-qglmc   3/3     Running   0          3m8s
pod/node-exporter-7krzg                   2/2     Running   0          3m5s
pod/prometheus-adapter-6455646bdc-f4zlf   1/1     Running   0          3m2s
pod/prometheus-adapter-6455646bdc-h6ztj   1/1     Running   0          3m1s
pod/prometheus-k8s-0                      2/2     Running   0          2m53s
pod/prometheus-k8s-1                      2/2     Running   0          2m53s
pod/prometheus-operator-f59c8b954-g7fm6   2/2     Running   0          11m

NAME                          TYPE        CLUSTER-IP       EXTERNAL-IP   PORT(S)             AGE
service/kube-state-metrics    ClusterIP   None             <none>        8443/TCP,9443/TCP   3m8s
service/node-exporter         ClusterIP   None             <none>        9100/TCP            3m6s
service/prometheus-adapter    ClusterIP   10.109.178.74    <none>        443/TCP             3m2s
service/prometheus-k8s        ClusterIP   10.105.211.243   <none>        9090/TCP,8080/TCP   2m55s
service/prometheus-operated   ClusterIP   None             <none>        9090/TCP            2m54s
service/prometheus-operator   ClusterIP   None             <none>        8443/TCP            11m

NAME                           DESIRED   CURRENT   READY   UP-TO-DATE   AVAILABLE   NODE SELECTOR            AGE
daemonset.apps/node-exporter   1         1         1       1            1           kubernetes.io/os=linux   3m6s

NAME                                  READY   UP-TO-DATE   AVAILABLE   AGE
deployment.apps/kube-state-metrics    1/1     1            1           3m9s
deployment.apps/prometheus-adapter    2/2     2            2           3m4s
deployment.apps/prometheus-operator   1/1     1            1           11m

NAME                                            DESIRED   CURRENT   READY   AGE
replicaset.apps/kube-state-metrics-6c8846558c   1         1         1       3m9s
replicaset.apps/prometheus-adapter-6455646bdc   2         2         2       3m4s
replicaset.apps/prometheus-operator-f59c8b954   1         1         1       11m

NAME                              READY   AGE
statefulset.apps/prometheus-k8s   2/2     2m54s
```
以下命令应返回集群节点CPU及内存使用率
```
# kubectl top nodes
NAME     CPU(cores)   CPU%   MEMORY(bytes)   MEMORY%   
master   1153m        9%     9565Mi          60%  
```

以下命令应返回nodes及pods两项APIResource
```
# kubectl get --raw /apis/metrics.k8s.io/v1beta1 | jq .
```
以下命令应返回nodes相应的APIResource，如为空，则服务异常
```
# kubectl get --raw /apis/metrics.k8s.io/v1beta1/nodes | jq .
```
以下命令应返回pods相应的APIResource，如为空，则服务异常
```
# kubectl get --raw /apis/metrics.k8s.io/v1beta1/pods | jq .
```
以下命令应返回自定义指标相应的APIResource，如为空，则服务异常
```
# kubectl get --raw /apis/custom.metrics.k8s.io/v1beta1 | jq . 
```

### 可能遇到的问题

1. 镜像拉取失败。手动拉取镜像，如仍失败，尝试使用系统代理，配置方法：[https://docs.docker.com/config/daemon/systemd/](https://docs.docker.com/config/daemon/systemd/)
```
# grep -rni "image:"
grafana/grafana-deployment.yaml:33:        image: grafana/grafana:8.5.5
node-exporter-daemonset.yaml:38:        image: quay.io/prometheus/node-exporter:v1.3.1
node-exporter-daemonset.yaml:74:        image: quay.io/brancz/kube-rbac-proxy:v0.12.0
# sudo docker pull $image-name:version
```
2. 部分服务启动失败。尝试查看服务状态，或者查看其日志，以进一步定位问题
```
# kubectl describe pod/kube-state-metrics-6c8846558c-qglmc -n monitoring
# kubectl logs pod/kube-state-metrics-6c8846558c-qglmc -n monitoring
```

## III. ONOS服务部署
待完善

## IV. 测试服务部署
待完善


