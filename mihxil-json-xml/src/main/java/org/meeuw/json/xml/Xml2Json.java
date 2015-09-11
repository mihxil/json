package org.meeuw.json.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.meeuw.json.Util;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.core.JsonGenerator;

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
