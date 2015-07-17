package latin.util;


import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ImmutableMap;

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
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public abstract class DomElement {

    public static File getResourceFile(Class c, String subpath) {
        return new File(c.getResource("/").getPath(), subpath);
    }

    public static Element fromDocument(Document document) {
        Element e = document.getDocumentElement();
        e.normalize();
        return e;
    }

    public static Element fromInputSource(InputSource sc)
        throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return fromDocument(dBuilder.parse(sc));
    }

    public static Element fromInputStream(InputStream st)
        throws SAXException, ParserConfigurationException, IOException {
        return fromInputSource(new InputSource(st));
    }

    public static Element fromString(String s)
        throws SAXException, ParserConfigurationException, IOException {
        StringReader sr = new StringReader(s);
        return fromInputSource(new InputSource(sr));
    }

    public static Element fromFile(File xmlFile)
        throws SAXException, ParserConfigurationException, IOException {
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            return fromInputStream(fis);
        }
    }

    public static class ElementAttributes extends AbstractList<Node> {
        private NamedNodeMap namedNodeMap;
        public ElementAttributes(NamedNodeMap namedNodeMap) {
            this.namedNodeMap = namedNodeMap;
        }
        @Override
        public Node get(int index) {
            return namedNodeMap.item(index);
        }
        @Override
        public int size() {
            return namedNodeMap.getLength();
        }
    }

    public static class ChildElements implements Iterable<Element> {
        NodeList nodeList;

        public ChildElements(NodeList nodeList) {
            this.nodeList = nodeList;
        }

        protected boolean elementTest (Element element) {
            return true;
        }

        public class ELementsIterator extends AbstractIterator<Element> {
            int position;

            public ELementsIterator() {
                this.position = 0;
            }

            @Override
            protected Element computeNext() {
                while (position < nodeList.getLength()) {
                    Node node = nodeList.item(position++);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        if (elementTest(element)) {
                            return element;
                        }
                    }
                }
                return endOfData();
            }
        }

        @Override
        public Iterator<Element> iterator() {
            return new ELementsIterator();
        }
    }

    public static class ChildTagElements extends ChildElements {
        String tag;
        public ChildTagElements (NodeList nodeList, String tag) {
            super(nodeList);
            this.tag = tag;
        }
        @Override
        protected boolean elementTest(Element element) {
            return element.getTagName() == tag;
        }
    }

    public static ChildElements childElements(Element base) {
        return new ChildElements(base.getChildNodes());
    }

    public static ChildTagElements childTagElements (Element base, String tag) {
        return new ChildTagElements(base.getChildNodes(), tag);
    }

    public static ElementAttributes elementAttributes(Element e) {
        return new ElementAttributes(e.getAttributes());
    }

    public static Map<String,String> attributesMap(Element e) {
        ImmutableMap.Builder<String,String> b = ImmutableMap.builder();
        for (Node n : elementAttributes(e)) {
            b.put(n.getNodeName(), n.getNodeValue());
        }
        return b.build();
    }

}

