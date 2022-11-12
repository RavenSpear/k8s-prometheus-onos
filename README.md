宿主机：windows server

四台虚拟机：
* 属于k8s集群的master
* 属于k8s集群的worker1、worker2
* mininet
  
虚拟机软件：virtualbox
## Mininet部署（待完善）
Mininet主机操作系统为Ubuntu 18.04 (LTS)，一张网卡（NAT模式）用于连接互联网，三张网卡分属三个不同的内部网络并开启了混杂模式。
### 0x01 配置主机连入互联网
1. 在virtual box上新建一个NAT网络，假设名为NatNetwork
2. Mininet主机在创建时，网卡1的连接方式选择NAT网络，界面名称选择NatNetwork。
### 0x02 配置主机连入内部网络
Mininet主机网卡2、网卡3、网卡4分别接入内部网络internet、internet1、internet2，并且网卡的混杂模式设置为允许虚拟电脑
### 0x03 安装mininent容器并启动（需要先安装docker）
```
# 安装mininent容器
docker pull daimaguicai/mn-stratum:v2.0

# 将项目中的mininet文件放置到mininet宿主机上

# 设置环境变量
alias mn-stratum="docker run --net=host --privileged -it -e DISPLAY=${DISPLAY} -v ~/projects/p4virtex/mininet:/test -v /tmp/.X11-unix:/tmp/.X11-unix --name mn-stratum --hostname mn-stratum daimaguicai/mn-stratum:v2.0"

"~/projects/p4virtex/mininet"替换成项目中mininet的安装位置

# 打开topo.py文件, 关键代码为如下
Intf("eth1",node=net.nameToNode[ "s1" ]) 
Intf("eth2",node=net.nameToNode[ "s8" ])

假设额外安装的网口为eth1和eth2，利用上面的语句就可以实现mininet的端口扩展，直接绑定主机的网口。

# 启动mininet容器
mn-stratum
cd /test
python topo.py

# 至此mininet容器已经启动，并成功绑定主机的两个网口
- 安装运行ONOS

# 安装docker容器
docker pull onosproject/onos:2.5.7

# 设置环境变量
alias onos="docker run --net=host --privileged -it --env JAVA_DEBUG_PORT='0.0.0.0:5005' --name onos onosproject/onos:2.5.7 clean debug"

# 启动onos
onos

# onos需要开启的服务
org.onosproject.netconf
org.onosproject.drivers.bmv2
org.onosproject.lldpprovider
org.onosproject.proxyarp
org.onosproject.hostprovider
org.onosproject.fwd


- onos 连接 mininet

在mininet文件夹下，主机端运行
`make install_netcfg`
```
## k8s集群部署
k8s集群有三台主机（1 master + 2 worker），操作系统均为Ubuntu 18.04 (LTS)，主机间通过mininet互联。
### 前言

1. 在运行ansible安装主机运行环境的速度取决于网络环境，每一步都会有明确的运行结果输出。

2. 在部署k8s集群时，可能会遇到容器镜像拖取失败一直重复的问题，可以通过为docker设置代理的方式解决。具体该为集群中哪台主机配置docker代理，可以通过kubectl describe命令查看镜像拉取失败的pod具体运行在哪台主机上面。docker 配置代理方式的两条命令如下：
```
# mkdir /etc/systemd/system/docker.service.d

# sudo bash -c "cat > /etc/systemd/system/docker.service.d/proxy.conf" <<EOF
[Service]
Environment="HTTP_PROXY=http://ip:port"
Environment="HTTPS_PROXY=http://ip:port"
EOF
```

### 0x01 配置三台主机接入互联网

各台主机在创建时，网卡1的连接方式选择NAT网络，界面名称选择NatNetwork。

### 0x02 配置三台主机接入mininet
1. 各台主机的网卡2分别接入内部网络internet、internet1、internet2，这里的混杂模式保持为关闭

2. 通过修改/etc/netplan/目录下的配置文件，将各台主机接入内部网络的网卡配置为静态公网ip（模拟实际环境）（配置前先提前ping一下这个ip是否存活，尽量选择ping不通的），并且在配置文件中添加到另外两台主机的静态路由，网关设置为自己。配置文件例子如下：
```
network:
  ethernets:
    enp0s10:
      dhcp4: false
      addresses: [172.1.113.58/24]
      routes:
        - to: 17.125.10.1
          via: 172.1.113.58
        - to: 223.26.35.1
          via: 172.1.113.58
  version: 2
```

### 0x03 在三台主机上安装openssh并配置root远程登陆
TIPS：需要使用`sudo chpasswd`命令提前为root设置密码
1. `sudo apt-get install -y openssh-server`
2. `sudo sed -i '/#PermitRootLogin /c PermitRootLogin yes' /etc/ssh/sshd_config`
3. `service ssh restart`

### 0x04 更改三台主机的主机名
使用`sudo hostnamectl set-hostname xxxxxx` 命令将三台主机的主机名分别改为master、worker1、worker2

### 0x05 配置master对所有worker以及自身的ssh免密码登录
TIPS：以下命令以root身份，且仅在master主机上运行
1. `ssh-keygen -t rsa`：所有选项默认
2. `ssh-copy-id worker1 ip`
3. `ssh-copy-id worker2 ip`
4. `cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys`

### 0x06 配置三台主机运行环境并部署prometheus、grafana服务
TIPS: 下列命令以root身份运行，且仅在master主机上运行。配置环境全程采用ansible自动化脚本，所以master主机需要提前安装ansible(`sudo apt-get -y install ansible`)，ansible自动化脚本均在本仓库，可用git将本仓库下载至master主机中。

1. 根据三台主机在mininet中的实际ip修改k8s-ansible-no-sona/inventory/default/host.ini文件；
2. `cd k8s-ansible-no-sona && ansible-playbook inventory/default/sitewithoutsona.yml`，此命令用于在集群所有主机中安装Kubernetes 1.23.12与Docker；
3. `cd calico && kubectl apply -f ./`，此命令用于部署calico网络插件以启用k8s虚拟网络，执行`kubectl get nodes`命令，看到所有节点状态STATUS为Ready表示网络建立成功，再执行后续命令；
4. `cd kube-prometheus && kubectl apply --server-side -f manifests/setup`，此命令用于在部署prometheus-operater，执行`kubectl get all -A`命令，看到所有pod和service全部部署完毕，再执行后续命令；
5. `cd kube-prometheus && kubectl apply -f manifests/`，此命令用于部署prometheus其余组件，执行`kubectl get all -A`命令，看到所有pod和service全部部署完毕，再执行后续命令；
6. `cd grafana && kubectl apply -f ./`，此命令用于部署grafana，执行`kubectl get all -A`命令，检查所有pod和service是否部署完毕。
7. 使用`kubectl label nodes master cluster=cloud`、`kubectl label nodes worker2 cluster=edge`、`kubectl label nodes worker1 cluster=edge`命令给三个节点打上云/边标签。
8. 使用`kubectl proxy --address='0.0.0.0' --accept-hosts='^*$' --port=8009`暴露8009端口
TIPS: 运行`cd k8s-ansible-no-sona && ansible-playbook inventory/default/reset-site.yml`命令可以重置集群，再使用`cd k8s-ansible-no-sona && ansible-playbook inventory/default/sitewithoutsona.yml`重新部署。

## 前端部署
前端部署在windows server的80端口，同时需要将集群中的grafana服务(3000)、prometheus服务(9090)、8009，mininet的onos服务(8181)映射至windows server上。端口映射可使用virtual box自带的NAT端口映射功能。
### 运行前端应用(开发环境)
TIPS: 修改k8s-prom-onos-front中的vue.config.js文件中的host和port字段可以更改前端应用的ip与端口
1. `cd k8s-prom-onos-front && npm install`
2. `npm run serve` 
