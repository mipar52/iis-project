package com.milan.iis_backend.model.okta;

// dio iz JSONa
// _links: { self: { href: "..." } }
public class OktaLinks {
    public Self self;

    public static OktaLinks self(String href) {
        OktaLinks link = new OktaLinks();
        link.self = new Self();
        link.self.href = href;
        return link;
    }

    public static class Self {
        public String href;
    }
}
