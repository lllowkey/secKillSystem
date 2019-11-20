package com.lyr.secKill.config;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

import javax.security.auth.login.Configuration;

//当spring容器内没有TomcatEnablededServletContainerFactory这个bean时，把此bean加载进spring中
@Component
public class WebServerConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Override
    public void customize(ConfigurableWebServerFactory configurableWebServerFactory) {
        //使用对应工厂内提供的接口定制化tomcatConnector
        ((TomcatServletWebServerFactory)configurableWebServerFactory).addConnectorCustomizers(new TomcatConnectorCustomizer() {
            @Override
            public void customize(Connector connector) {
                Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();

                //设置keepalivetimeout
                protocol.setKeepAliveTimeout(30000);

                //当客户端发送超过10000个请求则断开
                protocol.setMaxKeepAliveRequests(10000);


            }
        });
    }
}
