package com.milan.iis_backend.module_grcp.services;

import com.milan.iis_backend.module_grcp.grcpmodels.CityTemp;
import com.milan.iis_backend.module_grcp.grcpmodels.WeatherService;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DhmzWeatherService implements WeatherService {
    private static final String DHMZ_URL = "https://vrijeme.hr/hrvatska_n.xml";

    @Override
    public List<CityTemp> search(String query) throws Exception {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);

        List<CityTemp> out = new ArrayList<>();

        try (InputStream inputStream = URI.create(DHMZ_URL).toURL().openStream()) {
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            var doc = dbf.newDocumentBuilder().parse(inputStream);

            NodeList cityNodes = doc.getElementsByTagName("Grad");
            for (int i = 0; i < cityNodes.getLength(); i++) {
                Element cityEl = (Element) cityNodes.item(i);

                String city = textOf(cityEl, "GradIme");
                if (city == null) continue;

                if (!q.isBlank() && !city.toLowerCase(Locale.ROOT).contains(q)) continue;

                Element pod = firstChildElement(cityEl, "Podatci");
                String temp = pod == null ? null : textOf(pod, "Temp");
                if (temp != null) temp = temp.trim();
                if (temp == null || temp.isBlank()) continue;

                String weather = textOf(pod, "Vrijeme");
                if (weather != null) weather = weather.trim();
                if (weather == null || weather.isBlank()) continue;

                out.add(new CityTemp(city.trim(), temp, weather));
            }
        }
        return out;
    }

    private static String textOf(Element parent, String tag) {
        NodeList nodeList = parent.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        return nodeList.item(0).getTextContent();
    }

    private static Element firstChildElement(Element parent, String tag) {
        NodeList nodeList = parent.getElementsByTagName(tag);
        if (nodeList.getLength() == 0) return null;
        return (Element) nodeList.item(0);
    }
}
