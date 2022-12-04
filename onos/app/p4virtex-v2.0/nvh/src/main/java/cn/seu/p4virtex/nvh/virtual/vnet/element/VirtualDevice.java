/*
 * Copyright 2015-present Open Networking Foundation
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
package cn.seu.p4virtex.nvh.virtual.vnet.element;

import org.onosproject.net.ConnectPoint;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;

/**
 * 虚拟device
 * <p>
 * 1:1映射
 */
public interface VirtualDevice extends VirtualElement,Device {

    // 设置端口映射
    void setRealizedBy(DeviceId deviceId);

    // 获取映射端口
    DeviceId getRealizedBy();

}
