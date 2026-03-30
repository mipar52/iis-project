package com.milan.iis_backend.service.implementation;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.service.interfaces.imports.XmlExportService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@Service
public class OktaUserXmlExportService implements XmlExportService {

    @Override
    public Path writeUsersXmlToDisk(List<OktaUser> users, Path outputFile) throws Exception {
        Files.createDirectories(outputFile.getParent());

        Document document = toDocument(users);

        var transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        transformer.transform(new DOMSource(document), new StreamResult(outputFile.toFile()));
        return outputFile;
    }

    @Override
    public List<OktaUser> filterUsersFromXmlByXPath(Path xmlFile, String searchTerm, Boolean isExact) throws Exception {
        final String NS = "http://milan.com/iis/oktauser/xml";
        final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LOWER = "abcdefghijklmnopqrstuvwxyz";

        String normalizedTerm = (searchTerm == null ? "" : searchTerm)
                .replace("'", "")                 // basic injection hardening
                .trim()
                .toLowerCase(Locale.ROOT);

        String base = "/u:oktaUsers/u:oktaUser";
        String contains = "";

        if (!isExact) {
            contains = "contains";
        }
        String expr = normalizedTerm.isBlank()
                ? base
                : base + "[" +
                condition("u:id", normalizedTerm, isExact, UPPER, LOWER) + " or " +
                condition("u:profile/u:firstName", normalizedTerm, isExact, UPPER, LOWER) + " or " +
                condition("u:profile/u:lastName",  normalizedTerm, isExact, UPPER, LOWER) + " or " +
                condition("u:profile/u:email",     normalizedTerm, isExact, UPPER, LOWER) + " or " +
                condition("u:profile/u:login",     normalizedTerm, isExact, UPPER, LOWER) + " or " +
                condition("u:profile/u:mobilePhone", normalizedTerm, isExact, UPPER, LOWER) +
                "]";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().parse(xmlFile.toFile());

        var xPath = XPathFactory.newInstance().newXPath();
        xPath.setNamespaceContext(new SimpleNamespaceContext("u", NS));

        NodeList nodes = (NodeList) xPath.evaluate(expr, doc, XPathConstants.NODESET);

        List<OktaUser> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element oktaUserEl = (Element) nodes.item(i);

            OktaUser u = new OktaUser();
            u.setId(textAtNS(oktaUserEl, NS, "id"));

            OktaUserProfile profile = new OktaUserProfile();
            Element profileEl = firstChildElementNS(oktaUserEl, NS, "profile");
            if (profileEl != null) {
                profile.setFirstName(textAtNS(profileEl, NS, "firstName"));
                profile.setLastName(textAtNS(profileEl, NS, "lastName"));
                profile.setEmail(textAtNS(profileEl, NS, "email"));
                profile.setLogin(textAtNS(profileEl, NS, "login"));
                profile.setMobilePhone(textAtNS(profileEl, NS, "mobilePhone"));
                profile.setSecondEmail(textAtNS(profileEl, NS, "secondEmail"));
            }
            u.setProfile(profile);

            result.add(u);
        }

        return result;
    }

    private static String condition(String valueExpr, String term, boolean exact, String UPPER, String LOWER) {
        String lowered = "translate(normalize-space(" + valueExpr + "), '" + UPPER + "', '" + LOWER + "')";
        String lit = "'" + term + "'";
        if (exact) {
            return lowered + " = " + lit;
        }
        return "contains(" + lowered + ", " + lit + ")";
    }

    /** Minimal NamespaceContext for one prefix. */
    static class SimpleNamespaceContext implements javax.xml.namespace.NamespaceContext {
        private final String prefix;
        private final String ns;

        SimpleNamespaceContext(String prefix, String ns) {
            this.prefix = prefix;
            this.ns = ns;
        }

        @Override public String getNamespaceURI(String p) {
            if (p == null) return XMLConstants.NULL_NS_URI;
            return p.equals(prefix) ? ns : XMLConstants.NULL_NS_URI;
        }
        @Override public String getPrefix(String namespaceURI) { return null; }
        @Override public Iterator<String> getPrefixes(String namespaceURI) { return java.util.Collections.emptyIterator(); }
    }

    private static Element firstChildElementNS(Element parent, String ns, String localName) {
        NodeList nl = parent.getElementsByTagNameNS(ns, localName);
        if (nl.getLength() == 0) return null;
        return (Element) nl.item(0);
    }

    private static String textAtNS(Element parent, String ns, String localName) {
        Element el = firstChildElementNS(parent, ns, localName);
        if (el == null) return null;
        String t = el.getTextContent();
        return (t == null || t.isBlank()) ? null : t;
    }

    @Override
    public Document toDocument(List<OktaUser> users) throws Exception {
        final String NS = "http://milan.com/iis/oktauser/xml";

        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        Document doc = dbf.newDocumentBuilder().newDocument();

        Element root = doc.createElementNS(NS, "oktaUsers");
        doc.appendChild(root);

        for (OktaUser u : users) {
            Element oktaUserEl = doc.createElementNS(NS, "oktaUser");
            root.appendChild(oktaUserEl);

            // id/status
            appendTextNS(doc, oktaUserEl, NS, "id", u.getId());

            // profile (required by XSD)
            Element profileEl = doc.createElementNS(NS, "profile");
            oktaUserEl.appendChild(profileEl);

            var p = u.getProfile();
            appendTextNS(doc, profileEl, NS, "firstName", p != null ? p.getFirstName() : null);
            appendTextNS(doc, profileEl, NS, "lastName",  p != null ? p.getLastName()  : null);
            appendTextNS(doc, profileEl, NS, "mobilePhone",p != null ? p.getMobilePhone(): null);
            appendTextNS(doc, profileEl, NS, "secondEmail",p != null ? p.getSecondEmail(): null);

            // login/email are required in XSD -> ensure not null (empty string still might fail if you later add minLength)
            appendTextNS(doc, profileEl, NS, "login", p != null ? p.getLogin() : "");
            appendTextNS(doc, profileEl, NS, "email", p != null ? p.getEmail() : "");
        }

        return doc;
    }

    private static void appendTextNS(Document doc, Element parent, String ns, String localName, String value) {
        if (value == null) return; // element optional -> skip
        Element el = doc.createElementNS(ns, localName);
        el.setTextContent(value);
        parent.appendChild(el);
    }

    private static String getText(Element parent, String name) {
        var list = parent.getElementsByTagName(name);
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent();
    }

    private static Long parseLong(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Long.parseLong(s.trim()); } catch (Exception e) { return null; }
    }

    private static String checkIfEmpty(String s) {
        return s == null ? "" : s;
    }
}
