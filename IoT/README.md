# 于边缘节点部署EdgeX Foundey物联网网关

## `一、部署流程`

### 安装docker-compose

apt-get源版本老旧无法使用，建议参照如下链接进行独立安装

```
https://docs.docker.com/compose/install/other/
```

### 生成部署脚本

```
$ git clone https://github.com/edgexfoundry/edgex-compose.git
$ git checkout main
$ ./edgex-compose/compose-builder/make gen ds-mqtt mqtt-broker no-secty ui
$ cp ./edgex-compose/compose-builder/docker-compose.yml docker-compose.yml
```

### 配置部署脚本

对IoT目录下的```docker-compose.yml```作如下修改：

```
 # docker-compose.yml

 device-mqtt:
    ...
    environment:
      DEVICE_DEVICESDIR: /custom-config/devices
      DEVICE_PROFILESDIR: /custom-config/profiles
      ...
    volumes:
    - ./custom-config:/custom-config
    # 冒号前的路径须指向IoT目录下custom-config/
    ...
```

### Edgex Foundry服务部署

```
$ docker-compose pull
$ docker-compose up -d
```

### 虚拟MQTT设备部署

- { absolute path to ./mqtt-scripts }指IoT下mqtt-scripts的绝对路径

- { edgex-network }可以通过`docker network ls`查看

- { docker ip of container:edgex-mqtt-broker }可通过`docker inspect edgex-mqtt-broker`查看


```
$ docker run -d --restart=always --name=mqtt-scripts -v {absolute path to ./mqtt-scripts}:/scripts dersimn/mqtt-scripts --net={edgex-network} --url mqtt://{docker ip of container:edgex-mqtt-broker} --dir /scripts
```

## `二、服务状态检查`

### 通过MQTT客户端检查虚拟IoT设备状态

- 安装MQTTX客户端（推荐）

```
sudo snap install mqttx
```
- 与MQTT broker建立连接

新建`Connetion`，输入mqtt-broker的ip（按docker-compose.yml的配置可以是127.0.0.1），输入Connection的名字，建立连接。

- 订阅相关Topic

  - `订阅DataTopic`。IoT设备在这个Topic周期性主动发布数据。

  - `订阅CommandTopic`。device-mqtt在这个Topic主动发布控制数据。

  - `订阅ResponseTopic`。IoT设备的响应数据在这个Topic发布。

### 通过RESTful API检查device-mqtt服务状态

- 获取设备列表

此命令应返回`IoT/custom-config/devices/my.custom.device.config.toml: [DeviceList]`中的设备列表

```
$ curl --request GET http://localhost:59882/api/v2/device/all
```

- 获取设备数据

以下命令应返回设备产生的数据

```
# 获取所有Topic中的数据
$ curl --request GET http://localhost:59880/api/v2/reading/all

# 获取CommandTopic中的数据，根据设备名过滤
$ curl --request GET http://localhost:59880/api/v2/event/device/name/{device-name}

# 根据资源名过滤获取数据
$ curl --request GET http://localhost:59880/api/v2/reading/resourceName/{resource-name}
```

- 检查设备活性

以下命令应返回设备的`pong`回应

```
$ curl http://localhost:59882/api/v2/device/name/{device-name}/ping
```

## `三、服务重置`

### 关闭虚拟设备

```
$ docker stop mqtt-scripts
$ docker rm mqtt-scripts
```

### 关闭device-mqtt服务

- 删除设备

```
$ curl --request GET http://localhost:59881/api/v2/device/all | jq .
$ curl --request DELETE http://localhost:59881/api/v2/device/name/<device-name>
```

- 解除device-mqtt服务注册

```
$ curl --request DELETE http://localhost:8500/v1/kv/edgex/devices/2.0/device-mqtt?recurse=true
```

- 关闭device-mqtt服务

```
docker-compose stop device-mqtt
docker rm edgex-device-mqtt
```




