package cn.seu.p4virtex.pipeconf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.onlab.packet.DeserializationException;
import org.onlab.packet.Ethernet;
import org.onlab.util.ImmutableByteSequence;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DeviceId;
import org.onosproject.net.Port;
import org.onosproject.net.PortNumber;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.driver.AbstractHandlerBehaviour;
import org.onosproject.net.flow.TrafficTreatment;
import org.onosproject.net.flow.criteria.Criterion;
import org.onosproject.net.flow.instructions.Instruction;
import org.onosproject.net.flow.instructions.Instructions;
import org.onosproject.net.packet.DefaultInboundPacket;
import org.onosproject.net.packet.InboundPacket;
import org.onosproject.net.packet.OutboundPacket;
import org.onosproject.net.pi.model.PiActionId;
import org.onosproject.net.pi.model.PiMatchFieldId;
import org.onosproject.net.pi.model.PiPipelineInterpreter;
import org.onosproject.net.pi.model.PiTableId;
import org.onosproject.net.pi.runtime.PiAction;
import org.onosproject.net.pi.runtime.PiActionParam;
import org.onosproject.net.pi.runtime.PiPacketMetadata;
import org.onosproject.net.pi.runtime.PiPacketOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static cn.seu.p4virtex.pipeconf.P4Constants.INGRESS_BASIC_CONTROL_SEND_TO_CONTROLLER;
import static cn.seu.p4virtex.pipeconf.P4Constants.INGRESS_BASIC_CONTROL_SET_EGRESS_PORT;
import static cn.seu.p4virtex.pipeconf.P4Constants.INGRESS_BASIC_CONTROL_TABLE0;
import static cn.seu.p4virtex.pipeconf.P4Constants.INGRESS_PORT;
import static cn.seu.p4virtex.pipeconf.P4Constants.NO_ACTION;
import static cn.seu.p4virtex.pipeconf.P4Constants.OP;
import static cn.seu.p4virtex.pipeconf.P4Constants.OP_MULTICAST_VALUE;
import static cn.seu.p4virtex.pipeconf.P4Constants.OP_NORMAL_VALUE;
import static cn.seu.p4virtex.pipeconf.P4Constants.PARAM;
import static cn.seu.p4virtex.pipeconf.P4Constants.PORT;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.onlab.util.ImmutableByteSequence.copyFrom;
import static org.onosproject.net.PortNumber.CONTROLLER;
import static org.onosproject.net.PortNumber.FLOOD;
import static org.onosproject.net.flow.instructions.Instruction.Type.GROUP;
import static org.onosproject.net.flow.instructions.Instruction.Type.OUTPUT;
import static org.onosproject.net.pi.model.PiPacketOperationType.PACKET_OUT;

/**
 * ???????????????????????????????????????????????????????????? Pipeliner?????????????????????
 * 1.Treatment???????????? ???????????????????????????output?????????
 * 2.Packet-In???Packet-Out????????????????????????????????????????????????
 * 3.?????????????????????????????????
 */
public class InterpreterImpl extends AbstractHandlerBehaviour implements PiPipelineInterpreter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int PORT_BITWIDTH = 9;

    // ?????????????????????????????????
    private static final Map<Criterion.Type, String> CRITERION_MAP =
            new ImmutableMap.Builder<Criterion.Type, String>()
                    .put(Criterion.Type.IN_PORT, "standard_metadata.ingress_port")
                    .put(Criterion.Type.ETH_DST, "hdr.ethernet.dst_addr")
                    .put(Criterion.Type.ETH_SRC, "hdr.ethernet.src_addr")
                    .put(Criterion.Type.ETH_TYPE, "hdr.ethernet.ether_type")
                    .put(Criterion.Type.IPV4_SRC, "hdr.ipv4.src_addr")
                    .put(Criterion.Type.IPV4_DST, "hdr.ipv4.dst_addr")
                    .build();

    // ?????????????????????????????????table0???
    private static final Map<Integer, PiTableId> TABLE_MAP =
            new ImmutableMap.Builder<Integer, PiTableId>()
                    .put(0, INGRESS_BASIC_CONTROL_TABLE0)
                    .build();

    @Override
    public Optional<PiMatchFieldId> mapCriterionType(Criterion.Type type) {
        return Optional.ofNullable(PiMatchFieldId.of(CRITERION_MAP.get(type)));
    }

    @Override
    public Optional<PiTableId> mapFlowRuleTableId(int flowRuleTableId) {
        return Optional.ofNullable(TABLE_MAP.get(flowRuleTableId));
    }

    /**
     * ??????Output????????????????????????
     *
     * @param treatment
     * @param piTableId
     * @return
     * @throws PiInterpreterException
     */
    @Override
    public PiAction mapTreatment(TrafficTreatment treatment, PiTableId piTableId) throws PiInterpreterException {

        if (treatment.allInstructions().isEmpty()) {
            // ????????????????????????NoAction
            return PiAction.builder().withId(NO_ACTION).build();
        } else if (treatment.allInstructions().size() > 1) {
            // ????????????????????????????????????????????????????????????
            throw new PiInterpreterException("Treatment has multiple instructions");
        }
        // ????????????????????????????????????????????????????????????Output???NoAction??????????????????
        Instruction instruction = treatment.allInstructions().get(0);
        switch (instruction.type()) {
            case OUTPUT:
                if (piTableId.equals(INGRESS_BASIC_CONTROL_TABLE0)) {
                    return outputPiAction((Instructions.OutputInstruction) instruction, INGRESS_BASIC_CONTROL_SET_EGRESS_PORT);
                } else {
                    throw new PiInterpreterException(
                            "Output instruction not supported in table " + piTableId);
                }
            case NOACTION:
                return PiAction.builder().withId(NO_ACTION).build();
            default:
                throw new PiInterpreterException(format(
                        "Instruction type '%s' not supported", instruction.type()));
        }
    }

    /**
     * ??????????????????output???????????????table0??????set_egress_port???send_to_controller??????
     *
     * @param outInstruction
     * @param piActionId
     * @return
     * @throws PiInterpreterException
     */
    private PiAction outputPiAction(Instructions.OutputInstruction outInstruction, PiActionId piActionId)
            throws PiInterpreterException {
        PortNumber port = outInstruction.port();
        if (!port.isLogical()) {
            return PiAction.builder()
                    .withId(piActionId)
                    .withParameter(new PiActionParam(PORT, port.toLong()))
                    .build();
        } else if (port.equals(CONTROLLER)) {
            return PiAction.builder().withId(INGRESS_BASIC_CONTROL_SEND_TO_CONTROLLER).build();
        } else {
            throw new PiInterpreterException(format(
                    "Egress on logical port '%s' not supported", port));
        }
    }

    /**
     * ??????Packet-Out??????
     *
     * @param packet
     * @return
     * @throws PiInterpreterException
     */
    @Override
    public Collection<PiPacketOperation> mapOutboundPacket(OutboundPacket packet) throws PiInterpreterException {

        TrafficTreatment treatment = packet.treatment();

        List<Instruction> instructions = treatment
                .allInstructions()
                .stream()
                .filter(i -> i.type().equals(OUTPUT) || i.type().equals(GROUP))
                .map(i -> (Instructions.OutputInstruction) i)
                .collect(toList());

        if (treatment.allInstructions().size() != instructions.size()) {
            // There are other instructions that are not of type OUTPUT OR GROUP.
            throw new PiInterpreterException("Treatment not supported: " + treatment);
        }

        ImmutableList.Builder<PiPacketOperation> builder = ImmutableList.builder();

        for (Instruction inst : instructions) {
            if (inst.type().equals(OUTPUT)) {
                Instructions.OutputInstruction outputInst = (Instructions.OutputInstruction) inst;
                if (outputInst.port().isLogical() && !outputInst.port().equals(FLOOD)) {
                    throw new PiInterpreterException(format(
                            "Output on logical port '%s' not supported", outputInst.port()));
                } else if (outputInst.port().equals(FLOOD)) {
                    DeviceService deviceService = handler().get(DeviceService.class);
                    for (Port port : deviceService.getPorts(packet.sendThrough())) {
                        builder.add(buildOutputPacketOut(packet.data(), port.number().toLong()));
                    }
                } else {
                    builder.add(buildOutputPacketOut(packet.data(), outputInst.port().toLong()));
                }
            } else {
                Instructions.GroupInstruction groupInstruction = (Instructions.GroupInstruction) inst;
                builder.add(buildGroupPacketOut(packet.data(), groupInstruction.groupId().id()));
            }

        }
        return builder.build();
    }

    private PiPacketOperation buildOutputPacketOut(ByteBuffer data, long port) {

        PiPacketMetadata op = null;
        PiPacketMetadata param = null;

        try {
            op = PiPacketMetadata.builder().withId(OP).withValue(copyFrom(OP_NORMAL_VALUE).fit(16)).build();
            param = PiPacketMetadata.builder().withId(PARAM).withValue(copyFrom(port).fit(16)).build();
        } catch (ImmutableByteSequence.ByteSequenceTrimException e) {
            e.printStackTrace();
        }

        return PiPacketOperation.builder()
                .withType(PACKET_OUT)
                .withData(copyFrom(data))
                .withMetadata(op)
                .withMetadata(param)
                .build();

    }

    private PiPacketOperation buildGroupPacketOut(ByteBuffer data, int groupId) {

        PiPacketMetadata op = null;
        PiPacketMetadata param = null;

        try {
            op = PiPacketMetadata.builder().withId(OP).withValue(copyFrom(OP_MULTICAST_VALUE).fit(16)).build();
            param = PiPacketMetadata.builder().withId(PARAM).withValue(copyFrom(groupId).fit(16)).build();
        } catch (ImmutableByteSequence.ByteSequenceTrimException e) {
            e.printStackTrace();
        }

        return PiPacketOperation.builder()
                .withType(PACKET_OUT)
                .withData(copyFrom(data))
                .withMetadata(op)
                .withMetadata(param)
                .build();
    }

    /**
     * ??????Packet-In??????
     *
     * @param packetIn
     * @param deviceId
     * @return
     * @throws PiInterpreterException
     */
    @Override
    public InboundPacket mapInboundPacket(PiPacketOperation packetIn, DeviceId deviceId) throws PiInterpreterException {
        Ethernet ethPkt;
        try {
            ethPkt = Ethernet.deserializer().deserialize(packetIn.data().asArray(), 0,
                    packetIn.data().size());
        } catch (DeserializationException dex) {
            throw new PiInterpreterException(dex.getMessage());
        }

        // Get the ingress_port metadata.
        Optional<PiPacketMetadata> packetMetadata = packetIn.metadatas()
                .stream().filter(m -> m.id().equals(INGRESS_PORT))
                .findFirst();

        if (packetMetadata.isPresent()) {
            ImmutableByteSequence portByteSequence = packetMetadata.get().value();
            short s = portByteSequence.asReadOnlyBuffer().getShort();
            ConnectPoint receivedFrom = new ConnectPoint(deviceId, PortNumber.portNumber(s));
            ByteBuffer rawData = ByteBuffer.wrap(packetIn.data().asArray());
            return new DefaultInboundPacket(receivedFrom, ethPkt, rawData);
        } else {
            throw new PiInterpreterException(format(
                    "Missing metadata '%s' in packet-in received from '%s': %s",
                    INGRESS_PORT, deviceId, packetIn));
        }
    }


}
