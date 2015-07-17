
package latin.choices;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import latin.forms.DocElement;
import latin.forms.Form;

import java.io.File;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ParseFileTest {

    static File wordsDir = new File("words");


    public Map<String,String> getAttributesMap(Element e) {
        Map<String,String> m = Maps.newHashMap();
        NamedNodeMap namedNodeMap = e.getAttributes();
        for (int i = 0; i < namedNodeMap.getLength(); i++) {
            Node inode = namedNodeMap.item(i);
            m.put(inode.getNodeName(), inode.getNodeValue());
        }
        return m;
    }

    public void parseElement(Element e, int d) {
        String estring = Strings.padEnd("", d, ' ');
        estring += e.getNodeName();
        estring += " : " + getAttributesMap(e).toString();
        NodeList nodeList = e.getChildNodes();
        int s = nodeList.getLength();
        boolean hasElements = false;
        Node n;
        for (int i = 0; i < s; i++) {
            n = nodeList.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                hasElements = true;
                break;
            }
        }
        if (!hasElements) {
            String tc = e.getTextContent().trim();
            if (!tc.isEmpty()) {
                estring += " : " + tc;
            }
        }
        System.out.println(estring);
        for (int j = 0; j < s; j++) {
            n = nodeList.item(j);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                parseElement((Element) n, d + 1);
            }
        }
    }

    @Test
    public void testParseFile() throws Exception {
        File fXmlFile = new File(wordsDir, "staff.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        //optional, but recommended
        //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
        doc.getDocumentElement().normalize();

        System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

        NodeList nList = doc.getElementsByTagName("staff");

        System.out.println("----------------------------");

        for (int temp = 0; temp < nList.getLength(); temp++) {

            Node nNode = nList.item(temp);

            System.out.println("\nCurrent Element :" + nNode.getNodeName());

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                Element eElement = (Element) nNode;

                System.out.println("Staff id : " + eElement.getAttribute("id"));
                System.out.println("First Name : " + eElement.getElementsByTagName("firstname").item(0).getTextContent());
                System.out.println("Last Name : " + eElement.getElementsByTagName("lastname").item(0).getTextContent());
                System.out.println("Nick Name : " + eElement.getElementsByTagName("nickname").item(0).getTextContent());
                System.out.println("Salary : " + eElement.getElementsByTagName("salary").item(0).getTextContent());

                /*

                NamedNodeMap namedNodeMap = eElement.getAttributes();
                for (int i = 0; i < namedNodeMap.getLength(); i++) {
                    Node inode = namedNodeMap.item(i);
                    System.out.println(Integer.toString(i) + " : " + inode.getNodeName() + " : " + inode.getNodeValue() + " : " + inode.getTextContent());
                }
                NodeList nodeList = eElement.getChildNodes();
                for (int j = 0; j < nodeList.getLength(); j++) {
                    Node n = nodeList.item(j);
                    if (n.getNodeType() == Node.ELEMENT_NODE) {
                        System.out.println(Integer.toString(j) + " : " + n.getNodeName() + " : " + n.getTextContent());
                    }
                }
                */
                parseElement(doc.getDocumentElement(), 0);
            }
        }
    }

    static class NounParser {
        LatinNoun.NounEntry entry;
        public NounParser() {
            this.entry = new LatinNoun.NounEntry();
        }
        public void parseDoc(DocElement docElement) {
            Element topElement = docElement.getElement();
            for (Node n : DocElement.elementAttributes(topElement)) {
                entry.setAttribute(n.getNodeName(), n.getNodeValue());
            }
            for (Element e : DocElement.childElements(topElement)) {
                if (e.getTagName().equals("form")) {
                    entry.storeForm(e.getAttribute("key"), e.getTextContent());
                }
            }

        }
        public LatinNoun.NounEntry getEntry() {
            return entry;
        }
    }

    @Test
    public void testParseWord() throws Exception {
        DocElement docElement = DocElement.fromFile(new File(wordsDir, "ager.xml"));
        Element topElement = docElement.getElement();
        for (Node n : DocElement.elementAttributes(topElement)) {
            System.out.println(n.getNodeName() + " : " + n.getNodeValue());
        }
        for (Element e : DocElement.childElements(topElement)) {
            if (e.getTagName().equals("form")) {
                System.out.println(e.getAttribute("key") + " : " + e.getTextContent());
            }
        }
        for (Element e : DocElement.childTagElements(topElement, "form")) {
            System.out.println(e.getAttribute("key") + ": " + e.getTextContent());
        }
    }

    @Test
    public void testParseEntry() throws Exception {
        DocElement docElement = DocElement.fromFile(new File(wordsDir, "animal.xml"));
        NounParser parser = new NounParser();
        parser.parseDoc(docElement);
        LatinNoun.NounEntry e = parser.getEntry();
        for (CaseNumber k : CaseNumber.values()) {
            Form f = e.getForm(k);
            System.out.println(k.toString() + ": " + f.toString());
        }
    }


    public void parseEntry(String id, DocElement docElement) throws Exception {
        /*
        NounForms.EntryBuilder builder = new NounForms.EntryBuilder(id);
        builder.parseXml(docElement);
        NounForms.FormEntry entry = builder.makeEntry();
        System.out.println(entry.getSpec());
        FormMap <CaseNumber> formMap = builder.getFormMap();
        for (Map.Entry<CaseNumber,Formf> e : formMap.entrySet()) {
            CaseNumber key = e.getKey();
            Formf mformf = e.getValue();
            List<String> estrings = NounForms.getForms(entry, key);
            List<String> mstrings = Suffix.getStrings(mformf);
            System.out.println(key.toString() + " " + mstrings.toString() + " " + estrings.toString());
            Assert.assertEquals(key.toString(), mstrings, estrings);
        }
        */
    }

    /*
    @Test
    public void testParseEntry() throws Exception {
        parseEntry("aqua");
        parseEntry("bellum");
        parseEntry("dominus");
        parseEntry("filius");
        parseEntry("mare");
        parseEntry("ager");
        parseEntry("puer");
        parseEntry("cornu");
        parseEntry("portus");
        parseEntry("dies");
        parseEntry("res");
        parseEntry("agricola");
        parseEntry("animal");
        parseEntry("genu");
        parseEntry("rex");
        parseEntry("princeps");
        parseEntry("urbs");
        parseEntry("hortus");
        parseEntry("lapis");
        parseEntry("leo");
        parseEntry("litus");
        parseEntry("mensa");
        parseEntry("nomen");
        parseEntry("vir");
        parseEntry("puella");
        parseEntry("via");
    }

    @Test
    public void parseStringTest() throws Exception {
        String xmls = "<noun rules=\"Third.c.mf\"><forms>" +
                "<gstem>nomen</gstem>" +
                "<NomSi>nomen</NomSi>" +
                "</forms></noun>";
        parseEntry("nomen", DocElement.fromString(xmls));
    }
    */


}