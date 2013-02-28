
package latin.forms;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.sun.xml.internal.xsom.impl.scd.Iterators;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class DocElement {

    private Element docElement;

    public DocElement(Element docElement) {
        this.docElement = docElement;
        docElement.normalize();
    }

    public DocElement(Document doc) {
        this(doc.getDocumentElement());
    }

    public String getNodeName() {
        return docElement.getNodeName();
    }

    public static class ChildElementIterator extends AbstractIterator<Element> {
        private Iterator<Element> prevIterator;
        private String tagName;
        private NodeList nodeList;
        private int position;
        private int nodeListSize;
        public ChildElementIterator(Iterator<Element> prevIterator, Object tag) {
            this.prevIterator = prevIterator;
            this.tagName = tag.toString();
            this.position = 0;
            this.nodeListSize = 0;
            this.nodeList = null;
        }
        @Override
        protected Element computeNext() {
            while(true) {
                if (position < nodeListSize) {
                    Node node = nodeList.item(position++);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        if (element.getTagName().equals(tagName)) {
                            return element;
                        }
                    }
                }
                else if (prevIterator.hasNext()) {
                    Element el = prevIterator.next();
                    nodeList = el.getChildNodes();
                    position = 0;
                    nodeListSize = nodeList.getLength();
                }
                else {
                    return endOfData();
                }
            }
        }
    }

    public String getAttribute(String key) {
        return docElement.getAttribute(key);
    }

    public static class ChildElements implements Iterable<Element> {

        private Element baseElement;
        private List<Object> tags;

        public ChildElements(Element baseElement, List<Object> tags) {
            this.baseElement = baseElement;
            this.tags = tags;
        }

        public ChildElements(Element baseElement, Object... tags) {
            this(baseElement, Arrays.asList(tags));
        }

        @Override
        public Iterator<Element> iterator() {
            Iterator<Element> it = Iterators.singleton(baseElement);
            for (Object tag : tags) {
                it = new ChildElementIterator(it, tag);
            }
            return it;
        }

    }

    public List<String> getStrings(Element baseElement, Object... tags) {
        List<String> stringList = Lists.newArrayList();
        for (Element e : new ChildElements(baseElement, tags)) {
            stringList.addAll(Suffix.csplit(e.getTextContent()));
        }
        return stringList;
    }

    public List<String> getStrings(Object... tags) {
        return getStrings(docElement, tags);
    }

    public static DocElement open(File xmlFile)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return new DocElement(dBuilder.parse(xmlFile));
    }

    public static DocElement fromString(String s)
            throws SAXException, ParserConfigurationException, IOException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        StringReader sr = new StringReader(s);
        InputSource sc = new InputSource(sr);
        return new DocElement(dBuilder.parse(sc));
    }
}
