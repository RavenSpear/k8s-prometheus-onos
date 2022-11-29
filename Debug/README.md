# Debug

## 容器内服务可达性测试

建立curl容器

```
$ kubectl run mycurlpod --image=curlimages/curl:7.86.0 -i --tty -- sh
```

重入

```
$ kubectl exec -i --tty mycurlpod -- sh
```

删除

```
$ kubectl delete pod mycurlpod
```

## 获取一个简易的deployment实例

```
$ curl https://k8s.io/examples/application/deployment.yaml
```

## 去除主节点污点

```
$ kubectl taint node {node-name} node-role.kubernetes.io/master:NoSchedule-
```

## 重启kubelet

```
$ systemctl stop kubelet
$ systemctl daemon-reload
$ systemctl restart kubelet
```

## k8s配置信息

- 基于kueadm启动的kubectl配置参数

```
$ cat /var/lib/kubelet/kubeadm-flags.env
```

- cni配置文件

```
$ cat /etc/cni/net.d/10-flannel.conflist
```

- kubeadm配置

```
$ cat /etc/systemd/system/kubelet.service.d/10-kubeadm.conf
```

- 查看kubelet启动参数

```
$ ps -ef | grep kubelet
```