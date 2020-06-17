package com.team19.rentmicroservice.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "test")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema testSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("TestPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.rent-a-car.com/rent-service/soap");
        wsdl11Definition.setSchema(testSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema testSchema() {
        return new SimpleXsdSchema(new ClassPathResource("test.xsd"));
    }

    @Bean(name = "request")
    public DefaultWsdl11Definition defaultWsdl11Definition2(XsdSchema requestSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("RequestPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.rent-a-car.com/rent-service/soap");
        wsdl11Definition.setSchema(requestSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema requestSchema() {
        return new SimpleXsdSchema(new ClassPathResource("request.xsd"));
    }

    @Bean(name = "reservation")
    public DefaultWsdl11Definition defaultWsdl11Definition3(XsdSchema reservationSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("ReservationPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.rent-a-car.com/rent-service/soap");
        wsdl11Definition.setSchema(reservationSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema reservationSchema() {
        return new SimpleXsdSchema(new ClassPathResource("reservation.xsd"));
    }

    @Bean(name = "message")
    public DefaultWsdl11Definition defaultWsdl11Definition4(XsdSchema messageSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("MessagePort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://www.rent-a-car.com/rent-service/soap");
        wsdl11Definition.setSchema(messageSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema messageSchema() {
        return new SimpleXsdSchema(new ClassPathResource("message.xsd"));
    }
}
