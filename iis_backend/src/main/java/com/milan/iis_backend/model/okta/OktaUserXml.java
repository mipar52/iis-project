package com.milan.iis_backend.model.okta;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "oktaUser")
public class OktaUserXml {
    @XmlElement public String firstName;
    @XmlElement public String lastName;
    @XmlElement public String email;
    @XmlElement public String login;
    @XmlElement public String mobilePhone;
    @XmlElement public String secondEmail;
}
