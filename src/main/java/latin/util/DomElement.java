package latin.util;


import com.google.common.collect.AbstractIterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DomElement {

    Element element;

    public static class ParseException extends Exception {
        public ParseException(SAXException se) {
            super(se);
        }
    }

    public static File getResourceFile(Class c, String subpath) {
        return new File(c.getResource("/").getPath(), subpath);
    }

    DomElement (Element element) {
        this.element = element;
    }

    public static DomElement fromDocument(Document document) {
        Element e = document.getDocumentElement();
        e.normalize();
        return new DomElement(e);
    }

    static DocumentBuilder getDocumentBuilder() {
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder();
        }
        catch(ParserConfigurationException pce) {
            throw new RuntimeException(pce);
        }
    }

    public static DomElement fromInputSource(InputSource sc) throws IOException, ParseException {
        try {
            return fromDocument(getDocumentBuilder().parse(sc));
        }
        catch (SAXException se) {
            throw new ParseException(se);
        }
    }

    public static DomElement fromInputStream(InputStream st) throws IOException, ParseException {
            return fromInputSource(new InputSource(st));
    }

    public static DomElement fromString(String s) throws IOException, ParseException {
        StringReader sr = new StringReader(s);
        return fromInputSource(new InputSource(sr));
    }

    public static DomElement fromFile(File xmlFile) throws IOException, ParseException {
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            return fromInputStream(fis);
        }
    }

    public String getTagName() {
        return element.getTagName();
    }

    public String getAttribute(String attributeName) {
        return element.getAttribute(attributeName);
    }

    public String getTextContent() {
        return element.getTextContent();
    }

    public static class ElementAttributeNames extends AbstractList<String> {
        private NamedNodeMap namedNodeMap;
        public ElementAttributeNames(NamedNodeMap namedNodeMap) {
            this.namedNodeMap = namedNodeMap;
        }
        @Override
        public int size() { return namedNodeMap.getLength(); }
        @Override
        public String get(int index) { return namedNodeMap.item(index).getNodeName(); }
    }

    public ElementAttributeNames attributeNames() {
        return new ElementAttributeNames(element.getAttributes());
    }

    public static class ChildElements implements Iterable<DomElement> {
        NodeList nodeList;

        public ChildElements(NodeList nodeList) {
            this.nodeList = nodeList;
        }

        public class ElementsIterator extends AbstractIterator<DomElement> {
            int position;

            public ElementsIterator() {
                this.position = 0;
            }

            @Override
            protected DomElement computeNext() {
                while (position < nodeList.getLength()) {
                    Node node = nodeList.item(position++);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        return new DomElement((Element) node);
                    }
                }
                return endOfData();
            }
        }

        @Override
        public Iterator<DomElement> iterator() {
            return new ElementsIterator();
        }
    }

    public ChildElements children() {
        return new ChildElements(element.getChildNodes());
    }

    public ChildElements children(String tagName) {
        return new ChildElements(element.getElementsByTagName(tagName));
    }

}

