package org.sunshine.core.tool.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.sunshine.core.tool.util.INetUtils;

/**
 * 服务器信息
 *
 * @author Teamo
 */
@AutoConfiguration
public class ServerInfo implements SmartInitializingSingleton {

    private final ServerProperties serverProperties;

    private String hostName;

    private String ip;

    private Integer port;

    private String ipWithPort;

    public ServerInfo(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void afterSingletonsInstantiated() {
        this.hostName = INetUtils.getHostName();
        this.ip = INetUtils.getHostIp();
        this.port = serverProperties.getPort();
        this.ipWithPort = String.format("%s:%d", ip, port);
    }

    public ServerProperties getServerProperties() {
        return serverProperties;
    }

    public String getHostName() {
        return hostName;
    }

    public String getIp() {
        return ip;
    }

    public Integer getPort() {
        return port;
    }

    public String getIpWithPort() {
        return ipWithPort;
    }
}
