package com.milan.iis_backend.model.okta.dto.xml;

import jakarta.xml.bind.annotation.*;
import lombok.Data;

@XmlRootElement(name = "oktaUser", namespace = "http://milan.com/iis/oktauser/xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class OktaUserXml {
    @XmlElement(namespace = "http://milan.com/iis/oktauser/xml")
    public String status;

    @XmlElement(namespace = "http://milan.com/iis/oktauser/xml")
    public OktaUserProfileXml profile;

    @XmlElement(namespace = "http://milan.com/iis/oktauser/xml")
    public OktaUserCredentialsXml credentials;

    @XmlElement(name = "_links", namespace = "http://milan.com/iis/oktauser/xml")
    public OktaLinksXml links;

    @XmlAccessorType(XmlAccessType.FIELD)

    public static class OktaUserProfileXml {
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String firstName;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String lastName;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String mobilePhone;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String secondEmail;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String login;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String email;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OktaUserCredentialsXml {
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public OktaProviderXml provider;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public OktaPasswordXml password;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OktaProviderXml {
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String type;
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String name;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OktaPasswordXml {
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class OktaLinksXml {
        @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public SelfXml self;

        @XmlAccessorType(XmlAccessType.FIELD)
        public static class SelfXml {
            @XmlElement(namespace = "http://milan.com/iis/oktauser/xml") public String href;
        }
    }
}
