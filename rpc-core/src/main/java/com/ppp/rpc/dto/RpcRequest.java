package com.ppp.rpc.dto;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramTypes;
    private String version;
    private String group;

    public String rpcServiceName() {
        return getInterfaceName()
            + StrUtil.blankToDefault(getVersion(), StrUtil.EMPTY)
            + StrUtil.blankToDefault(getGroup(), StrUtil.EMPTY);
    }
}
