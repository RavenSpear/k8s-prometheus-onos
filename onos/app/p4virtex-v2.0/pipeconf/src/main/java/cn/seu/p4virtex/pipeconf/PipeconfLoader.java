package cn.seu.p4virtex.pipeconf;

import org.onosproject.net.behaviour.Pipeliner;
import org.onosproject.net.pi.model.DefaultPiPipeconf;
import org.onosproject.net.pi.model.PiPipeconf;
import org.onosproject.net.pi.model.PiPipeconfId;
import org.onosproject.net.pi.model.PiPipelineInterpreter;
import org.onosproject.net.pi.model.PiPipelineModel;
import org.onosproject.net.pi.service.PiPipeconfService;
import org.onosproject.p4runtime.model.P4InfoParser;
import org.onosproject.p4runtime.model.P4InfoParserException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

import static org.onosproject.net.pi.model.PiPipeconf.ExtensionType.BMV2_JSON;
import static org.onosproject.net.pi.model.PiPipeconf.ExtensionType.P4_INFO_TEXT;

/**
 * 加载注册 pipeconf
 */
@Component(immediate = true)
public class PipeconfLoader {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final PiPipeconfId PIPECONF_ID = new PiPipeconfId("cn.seu.p4virtex.pipeconf");
    private static final URL P4INFO_URL = PipeconfLoader.class.getResource("/virtual.p4info.txt");
    private static final URL BMV2_JSON_URL = PipeconfLoader.class.getResource("/virtual.json");

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private PiPipeconfService piPipeconfService;


    private PiPipeconf piPipeconf;

    /**
     * 注册 pipeconf
     */
    @Activate
    protected void activate() {

        piPipeconf = buildPipeconf();
        // 若已经被安装过则先卸载再安装
        if (piPipeconfService.getPipeconf(piPipeconf.id()).isPresent()) {
            piPipeconfService.unregister(piPipeconf.id());
            log.info("该pipeline-{}已存在，正在卸载,,,", piPipeconf.id());
        }
        piPipeconfService.register(piPipeconf);
        log.info("安装pipeline-{}.", piPipeconf.id());
    }

    @Deactivate
    protected void deactivate() {
        piPipeconfService.unregister(piPipeconf.id());
        log.info("卸载pipeline-{}.", piPipeconf.id());
    }


    /**
     * 构建 pipeconf
     * p4info - 控制器与数据平面的通信API
     * bmv2_json - bmv2交换机的执行文件（修改底层包处理逻辑）
     * PiPipelineInterpreter -
     * Pipeliner -
     * PortStatisticsDiscovery -
     *
     * @return
     * @throws P4InfoParserException
     */
    private PiPipeconf buildPipeconf() {

        PiPipelineModel pipelineModel = null;
        try {
            pipelineModel = P4InfoParser.parse(P4INFO_URL);
        } catch (P4InfoParserException e) {
            e.printStackTrace();
        }

        return DefaultPiPipeconf.builder()
                .withId(PIPECONF_ID)
                .withPipelineModel(pipelineModel)
                .addBehaviour(PiPipelineInterpreter.class, InterpreterImpl.class)
                .addBehaviour(Pipeliner.class, PipelinerImpl.class)
                .addBehaviour(VirtualProgrammable.class, VirtualProgrammableImpl.class)
                .addExtension(P4_INFO_TEXT, P4INFO_URL)
                .addExtension(BMV2_JSON, BMV2_JSON_URL)
                .build();
    }
}
