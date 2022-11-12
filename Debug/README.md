# Debug

## 容器内服务可达性测试

建立curl容器

```
$ kubectl run mycurlpod --image=curlimages/curl:7.86.0 -i --tty -- sh
```

重入

```
kubectl exec -i --tty mycurlpod -- sh
```

删除

```
kubectl delete pod mycurlpod
```

## 获取一个简易的deployment实例

```
curl https://k8s.io/examples/application/deployment.yaml
```
