package com.milan.iis_backend.module_soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurationSupport;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurationSupport {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet messageDispatcherServlet = new MessageDispatcherServlet();
        messageDispatcherServlet.setApplicationContext(applicationContext);
        messageDispatcherServlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(messageDispatcherServlet, "/ws/*");
    }

    @Bean(name = "oktaUsers")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema oktaUserSchema) {
        DefaultWsdl11Definition defaultWsdl11Definition = new DefaultWsdl11Definition();
        defaultWsdl11Definition.setPortTypeName("OktaUsersPort");
        defaultWsdl11Definition.setLocationUri("/ws/");
        defaultWsdl11Definition.setTargetNamespace("http://milan.com/iis/oktauser/soap");
        defaultWsdl11Definition.setSchema(oktaUserSchema);
        return defaultWsdl11Definition;
    }

    @Bean
    public XsdSchema oktaUserSchema() {
        return new SimpleXsdSchema(new ClassPathResource("static/wsdl/oktauser-soap.xsd"));
    }
}
