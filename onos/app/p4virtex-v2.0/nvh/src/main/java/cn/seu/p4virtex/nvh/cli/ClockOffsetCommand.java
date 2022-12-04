package cn.seu.p4virtex.nvh.cli;

import org.apache.karaf.shell.api.action.Argument;
import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;

@Service
@Command(scope = "p4virtex", name = "clock_offset", description = "PTP Function Test Command")
public class ClockOffsetCommand extends AbstractShellCommand {

    @Argument(index = 0, name = "deviceId", required = true, multiValued = false)
    String deviceId = null;

    @Override
    protected void doExecute() throws Exception {

    }
}
