package cn.seu.p4virtex.nvh;

import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Arc;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Commodity;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.Flow;
import cn.seu.p4virtex.nvh.virtual.vne.algorithm.mcf.MCFAlgorithm;
import com.google.ortools.Loader;
import com.sun.jna.Platform;
import org.onosproject.cfg.ComponentConfigService;
import org.onosproject.codec.CodecService;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用于配置
 */
@Component(immediate = true, service = MainComponent.class)
public class MainComponent {

    private Logger log = LoggerFactory.getLogger(getClass());

    private static final String APP_NAME = "cn.seu.p4virtex.nvh";

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private ComponentConfigService compCfgService;

    private ApplicationId appId;

    @Activate
    private void activate() {
        appId = coreService.registerApplication(APP_NAME);
        log.info("重新配置LLDP服务参数...");
        preSetOtherComponentsProperties();
        log.info("启动网络虚拟化服务...");
    }

    @Deactivate
    private void deactivate() {
        log.info("关闭网络虚拟化服务...");
        unsetOtherComponentsProperties();
        log.info("还原LLDP服务参数...");
    }

    public ApplicationId appId() {
        return this.appId;
    }

    private void preSetOtherComponentsProperties() {
        //关闭bddp
        compCfgService.preSetProperty("org.onosproject.provider.lldp.impl.LldpLinkProvider",
                "useBddp", "false", false);
        // lldp的探测频率从3s改为10s
        compCfgService.preSetProperty("org.onosproject.provider.lldp.impl.LldpLinkProvider",
                "probeRate", "10000", false);
        compCfgService.preSetProperty("org.onosproject.fwd.ReactiveForwarding",
                "flowTimeout  ", "100000", false);

    }

    private void unsetOtherComponentsProperties() {
        compCfgService.unsetProperty("org.onosproject.provider.lldp.impl.LldpLinkProvider", "useBddp");
        compCfgService.unsetProperty("org.onosproject.provider.lldp.impl.LldpLinkProvider", "probeRate");
        compCfgService.unsetProperty("org.onosproject.fwd.ReactiveForwarding","flowTimeout");
    }



}
