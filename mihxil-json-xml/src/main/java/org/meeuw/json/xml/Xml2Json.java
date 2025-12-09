package org.meeuw.json.xml;

import tools.jackson.core.JsonGenerator;

import java.io.*;

import javax.xml.parsers.*;

import org.meeuw.json.Util;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Michiel Meeuwissen
 * @since 0.9
 */
public class Xml2Json {

    final JsonGenerator generator;

    public Xml2Json(OutputStream out) throws IOException {
        generator = Util.getJsonFactory().createGenerator(out);
    }

    public void parse(InputStream input) throws ParserConfigurationException, SAXException, IOException {
        SAXParserFactory parserFactor = SAXParserFactory.newInstance();
        SAXParser parser = parserFactor.newSAXParser();
        parser.parse(input, new DefaultHandler() {
            @Override
            public void startElement(String uri, String localName,
                                     String qName, Attributes attributes) {

            }
            @Override
            public void endElement(String uri, String localName, String qName)
                throws SAXException {

            }


        });
    }
}
