
package latin.choices;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import latin.forms.DocElement;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class ParseFileTest {

    static File wordsDir = new File("words");

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
            }
        }
    }

    public void parseEntry(String id) throws Exception {
//        DocElement docElement = DocElement.fromFile(Forms.wordsFile(id));
//        parseEntry(id, docElement);
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


}