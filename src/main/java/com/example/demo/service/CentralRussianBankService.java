package com.example.demo.service;

import com.example.demo.dto.GetCursOnDateXml;
import com.example.demo.dto.GetCursOnDateXmlResponse;
import com.example.demo.dto.ValuteCursOnDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class CentralRussianBankService extends WebServiceTemplate {

    @Value("${cbr.api.url}")
    private String cbrApiUrl;

    public List<ValuteCursOnDate> getCurrenciesFromCbr() throws DatatypeConfigurationException {
        final GetCursOnDateXml getCursOnDateXML = new GetCursOnDateXml();
        // Convert seconds to milliseconds (1635092297 is in seconds)
       // long timeInMillis = 341252297L * 1000L;

        // Create a GregorianCalendar instance
        GregorianCalendar cal = new GregorianCalendar();

        // Create a Date object and set time in milliseconds
        Date date = new Date();
       // date.setTime(timeInMillis); // Set the correct time

        // Set the calendar's time to the date
        cal.setTime(date);

        // Create XMLGregorianCalendar from GregorianCalendar
        XMLGregorianCalendar xmlGregCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        getCursOnDateXML.setOnDate(xmlGregCal);

        System.out.println(getCursOnDateXML);
        GetCursOnDateXmlResponse response = (GetCursOnDateXmlResponse) marshalSendAndReceive(cbrApiUrl, getCursOnDateXML);

        if (response == null) {
            throw new IllegalStateException("Не удалось получить данные от ЦБ РФ");
        }

        final List<ValuteCursOnDate> courses = response.getGetCursOnDateXmlResult().getValuteData();
        courses.forEach(course -> course.setName(course.getName().trim()));
        return courses;
    }

    public ValuteCursOnDate getCourseForCurrency(String code) throws DatatypeConfigurationException {
        return getCurrenciesFromCbr().stream().filter(currency -> code.equals(currency.getChCode())).findFirst().get();
    }

}