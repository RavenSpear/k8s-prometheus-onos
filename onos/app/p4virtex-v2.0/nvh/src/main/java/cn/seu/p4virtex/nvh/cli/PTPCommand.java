package cn.seu.p4virtex.nvh.cli;

import org.apache.karaf.shell.api.action.Command;
import org.apache.karaf.shell.api.action.lifecycle.Service;
import org.onosproject.cli.AbstractShellCommand;

@Service
@Command(scope = "p4virtex", name = "ptp", description = "PTP Function Test Command")
public class PTPCommand extends AbstractShellCommand {

    @Override
    protected void doExecute() throws Exception {
//        ClockSynService clockSynService = get(ClockSynService.class);
//        print("start clock sync...");
//        clockSynService.clockSyn();
//        print("clock sync finished");
    }
}
