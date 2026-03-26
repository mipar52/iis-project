package com.milan.iis_backend.service.implementation;

import com.milan.iis_backend.model.okta.OktaUser;
import com.milan.iis_backend.model.okta.OktaUserProfile;
import com.milan.iis_backend.service.interfaces.imports.XmlExportService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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

        transformer.transform(new DOMSource(document), new StreamResult(outputFile.toFile()));
        return outputFile;
    }

    @Override
    public List<OktaUser> filterUsersFromXmlByXPath(Path xmlFile, String searchTerm) throws Exception {
        String safe = (searchTerm == null ? "" : searchTerm).replace("'", "");
        String term = safe.trim().toLowerCase(Locale.ROOT);

        // vraca sve ako je query prazan
        String expr;
        if (term.isBlank()) {
            expr = "/oktaUsers/user";
        } else {
            // prijevod za lower i upper case
            String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String LOWER = "abcdefghijklmnopqrstuvwxyz";

            expr =
                    "/oktaUsers/user[" +
                            "contains(translate(firstName, '" + UPPER + "', '" + LOWER + "'), '" + term + "') or " +
                            "contains(translate(lastName,  '" + UPPER + "', '" + LOWER + "'), '" + term + "') or " +
                            "contains(translate(email,     '" + UPPER + "', '" + LOWER + "'), '" + term + "') or " +
                            "contains(translate(login,     '" + UPPER + "', '" + LOWER + "'), '" + term + "') or " +
                            "contains(translate(mobilePhone,'" + UPPER + "', '" + LOWER + "'), '" + term + "') or " +
                            "contains(translate(sourceType,'" + UPPER + "', '" + LOWER + "'), '" + term + "') or " +
                            "contains(translate(id,        '" + UPPER + "', '" + LOWER + "'), '" + term + "')" +
                            "]";
        }

        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        Document doc = dbf.newDocumentBuilder().parse(xmlFile.toFile());

        var xPath = XPathFactory.newInstance().newXPath();
        NodeList nodes = (NodeList) xPath.evaluate(expr, doc, XPathConstants.NODESET);

        List<OktaUser> result = new ArrayList<>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element userEl = (Element) nodes.item(i);
            OktaUser u = new OktaUser();
            OktaUserProfile profile = new OktaUserProfile();
            u.setId(getText(userEl, "id"));
            profile.setFirstName(getText(userEl, "firstName"));
            profile.setLastName(getText(userEl, "lastName"));
            profile.setEmail(getText(userEl, "email"));
            profile.setLogin(getText(userEl, "login"));
            profile.setMobilePhone(getText(userEl, "mobilePhone"));
            //profile.setSourceType(getText(userEl, "sourceType"));
            u.setProfile(profile);

            result.add(u);
        }
        return result;
    }

    @Override
    public org.w3c.dom.Document toDocument(List<OktaUser> users) throws Exception {
        var dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        Document doc = dbf.newDocumentBuilder().newDocument();

        Element root = doc.createElement("oktaUsers");
        doc.appendChild(root);

        for (OktaUser u : users) {
            Element user = doc.createElement("user");
            root.appendChild(user);

            appendText(doc, user, "id", u.getId() == null ? "" : String.valueOf(u.getId()));
            appendText(doc, user, "firstName", checkIfEmpty(u.getProfile().getFirstName()));
            appendText(doc, user, "lastName", checkIfEmpty(u.getProfile().getLastName()));
            appendText(doc, user, "email", checkIfEmpty(u.getProfile().getEmail()));
            appendText(doc, user, "login", checkIfEmpty(u.getProfile().getLogin()));
            appendText(doc, user, "mobilePhone", checkIfEmpty(u.getProfile().getMobilePhone()));
          //  appendText(doc, user, "sourceType", checkIfEmpty(u.getSourceType()));
        }

        return doc;
    }
    private static void appendText(Document doc, Element parent, String name, String value) {
        Element el = doc.createElement(name);
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
