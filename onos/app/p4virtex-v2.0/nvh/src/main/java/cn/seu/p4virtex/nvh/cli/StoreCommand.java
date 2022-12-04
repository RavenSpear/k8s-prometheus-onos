package cn.seu.p4virtex.nvh.cli;

import cn.seu.p4virtex.nvh.virtual.physical.PhysicalNetworkService;
import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;
import org.onosproject.net.DeviceId;

@Service
@Command(scope = "p4virtex", name = "store", description = "Store Test Command")
public class StoreCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "deviceId", required = true, multiValued = false)
    String deviceId = null;

    @Override
    protected void doExecute() throws Exception {
        PhysicalNetworkService service = getService(PhysicalNetworkService.class);
        DeviceId _deviceId = DeviceId.deviceId(deviceId);
        Integer switchId = service.getSwitchId(_deviceId);
        print("deviceId %s => switchId %s", deviceId, switchId);
    }
}
