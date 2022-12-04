package cn.seu.p4virtex.pipeconf;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.tuple.Pair;
import org.onosproject.core.ApplicationId;
import org.onosproject.net.DeviceId;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.flow.DefaultFlowRule;
import org.onosproject.net.flow.DefaultTrafficSelector;
import org.onosproject.net.flow.DefaultTrafficTreatment;
import org.onosproject.net.flow.FlowRule;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.flow.TrafficSelector;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.criteria.PiCriterion;
import org.onosproject.net.pi.model.PiActionId;
import org.onosproject.net.pi.model.PiActionParamId;
import org.onosproject.net.pi.model.PiMatchFieldId;
import org.onosproject.net.pi.model.PiTableId;
import org.onosproject.net.pi.runtime.PiAction;
import org.onosproject.net.pi.runtime.PiActionParam;
import org.onosproject.net.pi.runtime.PiTableAction;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static cn.seu.p4virtex.pipeconf.P4Constants.DEFAULT_FLOW_RULE_PRIORITY;

public class VirtualProgrammableImpl extends AbstractHandlerBehaviour implements VirtualProgrammable {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private DeviceId deviceId;

    @Reference(cardinality = ReferenceCardinality.MANDATORY)
    private FlowRuleService flowRuleService;

    public VirtualProgrammableImpl() {
        init();
    }

    @Override
    public void cleanup() {

    }

    public void init() {
        deviceId = this.data().deviceId();
        flowRuleService = handler().get(FlowRuleService.class);
    }


    @Override
    public void sliceAllocation(TrafficSelector trafficSelector, Integer networkId) {

    }

    @Override
    public void insertVirtualTable(Integer vnetId, TrafficSelector selector, TrafficTreatment treatment, ApplicationId appId) {
        PiTableId tableId = PiTableId.of("ingress.virtual_control.path_select_table");

        PiCriterion match = PiCriterion.builder()
                .matchTernary(PiMatchFieldId.of("local_metadata.vnet_id"), vnetId, 0xffffffff)
                .build();
    }

    @Override
    public void insertVirtualLinkMappingTable(Integer vnetId, Byte vEgressPort, List<Pair<Integer, Integer>> paths, ApplicationId appId) {

        PiTableId tableId = PiTableId.of("ingress.virtual_control.path_select_table");

        PiCriterion match = PiCriterion.builder()
                .matchExact(PiMatchFieldId.of("local_metadata.vnet_id"), vnetId)
                .matchExact(PiMatchFieldId.of("local_metadata.virtual_metadata.virtual_egress_port"), vEgressPort)
                .build();

        List<PiActionParam> actionParams = Lists.newArrayList();
        if(paths.size() == 1){
            PiActionParamId paramId = PiActionParamId.of("pid1");
            PiActionParam param = new PiActionParam(paramId, paths.get(0).getKey());
            actionParams.add(param);
        }else{
            for (int i = 0; i < paths.size(); i++) {
                PiActionParamId paramId1 = PiActionParamId.of("pid" + (i + 1));
                PiActionParam param1 = new PiActionParam(paramId1, paths.get(0).getKey());

                PiActionParamId paramId2 = PiActionParamId.of("w" + (i + 1));
                PiActionParam param2 = new PiActionParam(paramId2, paths.get(0).getValue());

                actionParams.add(param1);
                actionParams.add(param2);
            }
        }

        PiTableAction action = PiAction.builder()
                .withId(PiActionId.of("ingress.virtual_control.select_path_" + paths.size()))
                .withParameters(actionParams)
                .build();

        FlowRule flowRule = buildFlowRule(deviceId, appId, tableId, match, action);

        flowRuleService.applyFlowRules(flowRule);
    }


    @Override
    public void insertSRPolicy(Integer srPolicyId, List<Byte> sids, ApplicationId appId) {
        PiTableId tableId = PiTableId.of("ingress.virtual_control.sr_policy_table");

        PiCriterion match = PiCriterion.builder()
                .matchExact(PiMatchFieldId.of("local_metadata.virtual_metadata.selected_path_id"), srPolicyId)
                .build();

        List<PiActionParam> actionParams = Lists.newArrayList();
        for (int i = 0; i < sids.size(); i++) {
            PiActionParamId paramId = PiActionParamId.of("sid" + (i + 1));
            PiActionParam param = new PiActionParam(paramId, sids.get(i));
            actionParams.add(param);
        }

        PiTableAction action = PiAction.builder()
                .withId(PiActionId.of("ingress.virtual_control.insert_policy_" + sids.size()))
                .withParameters(actionParams)
                .build();
        FlowRule flowRule = buildFlowRule(deviceId, appId, tableId, match, action);
        flowRuleService.applyFlowRules(flowRule);
    }




    @Override
    public void initSwitchId(Integer switchId, ApplicationId appId) {

        PiTableId tableId = PiTableId.of("ingress.int_transit_control.tb_switch_id");

        PiCriterion match = PiCriterion.builder()
                .matchExact(PiMatchFieldId.of("hdr.int_header.$valid$"), 1)
                .build();

        PiActionParamId paramId = PiActionParamId.of("switch_id");
        PiActionParam param = new PiActionParam(paramId, switchId);

        PiTableAction action = PiAction.builder()
                .withId(PiActionId.of("ingress.int_transit_control.init_switch_id"))
                .withParameter(param)
                .build();

        FlowRule flowRule = buildFlowRule(deviceId, appId, tableId, match, action);
        flowRuleService.applyFlowRules(flowRule);
    }


    public FlowRule buildFlowRule(DeviceId deviceId, ApplicationId appId,
                                  PiTableId tableId, PiCriterion piCriterion,
                                  PiTableAction piAction) {
        return DefaultFlowRule.builder()
                .forDevice(deviceId)
                .forTable(tableId)
                .fromApp(appId)
                .withPriority(DEFAULT_FLOW_RULE_PRIORITY)
                .makePermanent()
                .withSelector(DefaultTrafficSelector.builder()
                        .matchPi(piCriterion).build())
                .withTreatment(DefaultTrafficTreatment.builder()
                        .piTableAction(piAction).build())
                .build();
    }


}
