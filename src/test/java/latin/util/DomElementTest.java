package latin.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import java.util.Map;

//try (InputStream inputStream = this.getClass().getResourceAsStream("/" + "DomElementTest.xml")) {

public class DomElementTest {
    private static Logger logger = LoggerFactory.getLogger(DomElementTest.class);
    @Test
    public void testFileLoad() throws Exception {
        Element e = DomElement.fromFile(DomElement.getResourceFile(DomElementTest.class, "DomElementTest.xml"));
        ImmutableMap<String,String> targetAttributes = ImmutableMap.<String,String>builder()
                                                                   .put("gender", "m")
                                                                   .put("gstem", "agr")
                                                                   .build();
        Assert.assertEquals(targetAttributes, DomElement.attributesMap(e));
        ImmutableMap<String,String> targetForms = ImmutableMap.<String,String>builder()
                                                              .put("NomSi", "ager")
                                                              .put("AccSi", "agrum")
                                                              .build();
        Map<String,String> foundForms = Maps.newHashMap();
        for (Element f : DomElement.childTagElements(e, "form")) {
            foundForms.put(f.getAttribute("key"), f.getTextContent());
        }
        Assert.assertEquals(targetForms, foundForms);
    }
}


