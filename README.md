宿主机：windows server

四台虚拟机：
* 属于k8s集群的master
* 属于k8s集群的worker1、worker2
* mininet
  
虚拟机软件：virtualbox
## mininet部署
待完善
## 集群部署
### 前言（可能遇到的问题）
在运行ansible安装主机运行环境的速度取决于网络环境，每一步都会有明确的运行结果输出。

在部署k8s集群时，可能会遇到容器镜像拖取失败一直重复的问题，可以通过为docker设置代理的方式解决。docker 配置代理方式的两条命令如下：
```
# mkdir /etc/systemd/system/docker.service.d

# sudo bash -c "cat > /etc/systemd/system/docker.service.d/proxy.conf" <<EOF
[Service]
Environment="HTTP_PROXY=http://ip:port"
Environment="HTTPS_PROXY=http://ip:port"
EOF
```
具体该为集群中哪台主机配置docker代理，可以通过kubectl describe命令查看镜像拉取失败的pod具体运行在哪台主机上面。
### 0x00 主机基本信息
集群部署有三台主机（1 master + 2 worker），操作系统均为Ubuntu 18.04 (LTS)，主机间通过mininet互联。

### 0x01 配置三台主机接入互联网

为每台主机配置一张网卡（NAT模式）用于连接互联网

### 0x02 配置三台主机接入mininet
三台主机除了各有一张用于连接互联网的网卡（NAT），还各有一张与mininet位于同一个内部网络的网卡，要注意的是这三台主机分别在不同的内部网络。




### 0x03 在三台主机上安装openssh并配置root远程登陆
TIPS：需要使用`sudo chpasswd`命令提前为root设置密码
1. `sudo apt-get install -y openssh-server`
2. `sudo sed -i '/#PermitRootLogin /c PermitRootLogin yes' /etc/ssh/sshd_config`
3. `service ssh restart`

### 0x04 更改三台主机的主机名
使用`sudo hostnamectl set-hostname xxxxxx` 命令将三台主机的主机名分别改为master、worker1、worker2

### 0x05 配置master对所有worker以及自身的ssh免密码登录
TIPS：以下命令以root身份，仅在master主机上运行
1. `ssh-keygen -t rsa`：所有选项默认
2. `ssh-copy-id worker1 ip`
3. `ssh-copy-id worker2 ip`
4. `cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys`

### 0x06 配置三台主机运行环境并部署prometheus、grafana服务
TIPS: 下列命令以root身份运行，仅在master主机上运行。master主机需要提前安装ansible(`sudo apt-get -y install ansible`)，安装脚本均在本仓库，可用git将本仓库下载至master主机中。

1. 根据三台主机在mininet中的实际ip修改k8s-ansible-no-sona/inventory/default/host.ini文件；
2. `cd k8s-ansible-no-sona && ansible-playbook inventory/default/sitewithoutsona.yml`，此命令用于在集群所有主机中安装Kubernetes 1.23.12与Docker；
3. `cd calico && kubectl apply -f ./`，此命令用于部署calico网络插件以启用k8s虚拟网络，执行`kubectl get nodes`命令，看到所有节点状态STATUS为Ready表示网络建立成功，再执行后续命令；
4. `cd kube-prometheus && kubectl apply --server-side -f manifests/setup`，此命令用于在部署prometheus-operater，执行`kubectl get all -A`命令，看到所有pod和service全部部署完毕，再执行后续命令；
5. `cd kube-prometheus && kubectl apply -f manifests/`，此命令用于部署prometheus其余组件，执行`kubectl get all -A`命令，看到所有pod和service全部部署完毕，再执行后续命令；
6. `cd grafana && kubectl apply -f ./`，此命令用于部署grafana，执行`kubectl get all -A`命令，检查所有pod和service是否部署完毕。

TIPS: 运行`cd k8s-ansible-no-sona && ansible-playbook inventory/default/reset-site.yml`命令可以重置集群，恢复到仅有集群运行环境，但集群还未建立的状态

## 前端部署
前端部署在windows server的80端口，同时需要将集群中的grafana服务(3000)和prometheus服务(9090)，mininet的onos服务(8181)映射至windows server上。端口映射可使用virtual box自带的NAT端口映射功能。
### 运行前端应用(开发环境)
TIPS: 修改k8s-prom-onos-front中的vue.config.js文件中的host和port字段可以更改前端应用的ip与端口
1. `cd k8s-prom-onos-front && npm install`
2. `npm run serve` 

