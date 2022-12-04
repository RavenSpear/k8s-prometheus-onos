/*
 * Copyright 2022-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.seu.p4virtex.pipeconf;

import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.behaviour.PiRegisterProgrammable;
import org.onosproject.net.driver.HandlerBehaviour;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;

import javax.imageio.spi.RegisterableService;
import java.util.List;

/**
 * 底层P4可编程交换机的操作接口
 * 主要包括流表规则的下发、删除和修改
 */
public interface VirtualProgrammable extends HandlerBehaviour {

    /**
     * 清除交换机上所有的配置
     */
    void cleanup();

    /**
     * 流量识别，判断业务流量所属的网络切片
     */
    void sliceAllocation(TrafficSelector trafficSelector, Integer networkId);

    /**
     * 插入虚拟转发表
     * match fields: vnetId + selector + treatment
     * actions: set_egress_port
     *
     * @param vnetId
     * @param selector
     * @param treatment
     * @param appId
     */
    void insertVirtualTable(Integer vnetId, TrafficSelector selector, TrafficTreatment treatment, ApplicationId appId);


    /**
     * 插入虚拟链路映射表
     * match fields: vnetId + vEgressPort
     * actions: path1,path2,path3...
     *
     * @param vnetId
     * @param vEgressPort
     * @param paths
     * @param appId
     */
    void insertVirtualLinkMappingTable(Integer vnetId, Byte vEgressPort, List<Pair<Integer, Integer>> paths, ApplicationId appId);

    /**
     * 插入sr规则
     *
     * @param srPolicyId
     * @param sids
     * @param appId
     */
    void insertSRPolicy(Integer srPolicyId, List<Byte> sids, ApplicationId appId);


    /**
     * 初始化交换机ID
     */
    void initSwitchId(Integer switchId, ApplicationId appId);
}