package com.example.demo.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@Data
@XmlRootElement(name = "GetCursOnDateXmlResult")  // Add @XmlRootElement if this class is used as the root element in XML
public class GetCursOnDateXmlResult {

    @XmlElementWrapper(name = "ValuteData")
    @XmlElement(name = "ValuteCursOnDate")
    private List<ValuteCursOnDate> valuteData = new ArrayList<>();
}
