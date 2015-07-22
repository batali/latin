package latin.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.junit.Test;

import junit.framework.Assert;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

//try (InputStream inputStream = this.getClass().getResourceAsStream("/" + "DomElementTest.xml")) {

public class DomElementTest {
    //private static Logger logger = LoggerFactory.getLogger(DomElementTest.class);
    @Test
    public void testFileLoad() throws Exception {
        DomElement e = DomElement.fromFile(DomElement.getResourceFile(DomElementTest.class, "DomElementTest.xml"));
        Assert.assertEquals("noun", e.getTagName());
        Assert.assertEquals("m", e.getAttribute("gender"));
        Assert.assertEquals(Arrays.asList("gender", "gstem"), e.attributeNames());
        Map<String,String> targetForms = Maps.newHashMap();
        targetForms.put("NomSi", "ager");
        targetForms.put("AccSi", "agrum");
        Map<String,String> foundForms = Maps.newHashMap();
        for (DomElement c : e.children("form")) {
            foundForms.put(c.getAttribute("key"), c.getTextContent());
        }
        Assert.assertEquals(targetForms, foundForms);
        Set<String> targetTags = Sets.newHashSet("form", "sub");
        Set<String> foundTags = Sets.newHashSet();
        for (DomElement d : e.children()) {
            foundTags.add(d.getTagName());
        }
        Assert.assertEquals(targetTags, foundTags);
    }

    @Test
    public void testFromString() throws Exception {
        String s = "<noun gender=\"m\" gstem=\"agr\" />";
        DomElement e = DomElement.fromString(s);
        Assert.assertEquals("noun", e.getTagName());
        Assert.assertEquals("m", e.getAttribute("gender"));
        Assert.assertEquals(Arrays.asList("gender", "gstem"), e.attributeNames());
    }

    @Test (expected = DomElement.ParseException.class)
    public void testParseException() throws Exception {
        String s = "<noun gender=\"m\" gstem=\"agr\"></verb>";
        DomElement.fromString(s);
        Assert.fail();
    }

}


