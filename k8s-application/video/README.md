# k8s应用服务部署

## 视频转发服务（video）

转发服务包含视频转发代理以及视频推流服务

- 部署视频转发代理

转发代理有两个端口提供服务，端口80用以查询当前的视频流转发状态，端口1935用以接收视频推流，并响应视频拉流的请求

```
$ cd video/
$ kubectl apply -f video-proxy/
```

- 部署视频推流

在每个可能的部署节点上部署待推流的视频

```
$ sudo mkdir /video
$ sudo cp {dir-to-video}/{video-name}.mp4 /video/demo.mp4
$ chmod -R 777 /video
```

按需求修改副本数，视频推流服务的pod被强制分散部署，因此副本数应小于等于节点数，副本数将决定最终代理处可访问的视频url资源数量

```
$ cat /video-streaming/ffmpeg-deployment.yaml
...
    replicas: 1
...
```
部署推流
```
$ kubectl apply -f video-streaming/
```

- 服务检测

检查视频推流信息统计

```
curl http://{node-ip}:30800/stat
```

统计信息中显示有视频流url名，如`video-streaming-f4b7b99f6-dbblg`，可通过VLC等视频播放器看

```
rtmp://{node-ip}:31935/live/{streaming-name}
```

