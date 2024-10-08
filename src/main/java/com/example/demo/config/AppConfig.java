package com.example.demo.config;

import com.example.demo.dto.GetCursOnDateXml;
import com.example.demo.dto.GetCursOnDateXmlResponse;
import com.example.demo.dto.GetCursOnDateXmlResult;
import com.example.demo.dto.ValuteCursOnDate;
import com.example.demo.service.CentralRussianBankService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class AppConfig {

    @Bean
    public WebServiceTemplate webServiceTemplate(Jaxb2Marshaller marshaller) {
        WebServiceTemplate webServiceTemplate = new WebServiceTemplate();
        webServiceTemplate.setMarshaller(marshaller); // Set marshaller for request
        webServiceTemplate.setUnmarshaller(marshaller); // Set unmarshaller for response
        return webServiceTemplate;
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        // Register all classes that are used in XML requests and responses
        marshaller.setClassesToBeBound(
                GetCursOnDateXml.class,
                GetCursOnDateXmlResponse.class,
                GetCursOnDateXmlResult.class,
                ValuteCursOnDate.class
        );
        return marshaller;
    }

    @Bean
    public CentralRussianBankService centralRussianBankService() {
        return new CentralRussianBankService(); // Inject WebServiceTemplate into the service
    }
}
