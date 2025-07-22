package guru.qa.countrycatalogue.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.text.SimpleDateFormat;

@Configuration
@EnableWs
public class AppConfig {
    public static final String SOAP_NAMESPACE = "countrycatalog";

    @Value("${countrycatalog.base-uri}")
    private String photocatalogUserdataBaseUri;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper om = new ObjectMapper();
        om.setDateFormat(new SimpleDateFormat("dd-MM-yyyy"));
        return om;
    }

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "countrycatalog")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema userdataSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("CountrycatalogPort");
        wsdl11Definition.setLocationUri(photocatalogUserdataBaseUri + "/ws");
        wsdl11Definition.setTargetNamespace(SOAP_NAMESPACE);
        wsdl11Definition.setSchema(userdataSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema photocatalogSchema() {
        return new SimpleXsdSchema(new ClassPathResource("countrycatalog.xsd"));
    }
}