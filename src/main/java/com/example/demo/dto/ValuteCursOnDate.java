package com.example.demo.dto;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;

@Data
@XmlAccessorType(XmlAccessType.FIELD)  // Use fields directly for JAXB marshalling/unmarshalling
public class ValuteCursOnDate {

    @XmlElement(name = "Vname")  // Maps to XML element <Vname>
    private String name;

    @XmlElement(name = "Vnom")  // Maps to XML element <Vnom>
    private int nominal;

    @XmlElement(name = "Vcurs")  // Maps to XML element <Vcurs>
    private double course;

    @XmlElement(name = "Vcode")  // Maps to XML element <Vcode>
    private String code;

    @XmlElement(name = "VchCode")  // Maps to XML element <VchCode>
    private String chCode;
}
