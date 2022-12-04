package cn.seu.p4virtex.nvh.virtual.vnet.element;

import com.google.common.collect.ImmutableList;
import org.onosproject.net.ConnectPoint;
import org.onosproject.net.DefaultAnnotations;
import org.onosproject.net.DefaultLink;
import org.onosproject.net.provider.ProviderId;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;


public class DefaultVirtualLink extends DefaultLink implements VirtualLink {

    private static final String VIRTUAL = "virtualLink";
    public static final ProviderId PID = new ProviderId(VIRTUAL, VIRTUAL);

    private NetworkId networkId;

    private Integer bandwidth; // 总的带宽需求

    private List<SRPolicy> srPolicyList; // 所有的映射路径

    public DefaultVirtualLink(ConnectPoint src, ConnectPoint dst, Integer bandwidth) {
        super(PID, src, dst, Type.VIRTUAL, State.ACTIVE, DefaultAnnotations.builder().build());
        this.bandwidth = bandwidth;
        this.srPolicyList = new ArrayList<>();
        this.networkId = NetworkId.NONE;
    }

    @Override
    public Integer getBandwidth() {
        return this.bandwidth;
    }

    @Override
    public void setBandwidth(Integer bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public List<SRPolicy> getSRPolicyList() {
        return ImmutableList.copyOf(this.srPolicyList);
    }

    @Override
    public void addSRPolicy(SRPolicy srPolicy) {
        this.srPolicyList.add(srPolicy);
    }

    @Override
    public NetworkId networkId() {
        return this.networkId;
    }

    @Override
    public void setNetworkId(NetworkId networkId) {
        this.networkId = networkId;
    }


    @Override
    public String toString() {
        return toStringHelper(this)
                .add("src", this.src())
                .add("dst", this.dst())
                .add("bandwidth", this.getBandwidth())
                .toString();
    }
}
