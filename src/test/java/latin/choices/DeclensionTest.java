package latin.choices;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import latin.forms.ModRule;
import latin.util.DomElement;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class DeclensionTest {

    private static Logger logger = LoggerFactory.getLogger(DeclensionTest.class);

    List<String> ruleNames = Lists.newArrayList("First.mf", "First.a", "Second.mf", "Second.n",
                                                "Second.us", "Second.ius", "Second.um",
                                                "Second.ium", "Second.r", "Second.er",
                                                "Third.c.mf", "Third.c.n",
                                                "Third.i.mf", "Third.i.n",
                                                "Third.a.mf", "Third.pi.mf",
                                                "Fourth.mf", "Fourth.n",
                                                "Fourth.us", "Fourth.ū",
                                                "Fifth.c", "Fifth.v");

    void writeRules(Declension.Rules rules, PrintWriter printWriter) {
        printWriter.format(" <rules name=\"%s\">\n", rules.pathId.toString());
        for (CaseNumber cn : CaseNumber.values()) {
            ModRule r = rules.get(cn);
            if (r != null) {
                printWriter.format("   <%s>%s</%s>\n", cn.toString(), r.getSpec(), cn.toString());
            }
        }
        printWriter.format(" </rules>\n");
    }

    void writeAllRules(PrintWriter printWriter) {
        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        printWriter.println("<DeclensionRules>");
        for (String ruleName : ruleNames) {
            Declension.Rules rules = Declension.getRules(ruleName);
            writeRules(rules, printWriter);
        }
        printWriter.println("</DeclensionRules>");
    }

    public void testWrite() throws Exception {
        File file = new File("DeclensionRules.xml");
        PrintWriter printwriter = new PrintWriter(file);
        writeAllRules(printwriter);
        printwriter.close();
    }

    @Test
    public void testReadRules() throws Exception {
        File file = DomElement.getResourceFile(DeclensionTest.class, "DeclensionRules.xml");
        DomElement e = DomElement.fromFile(file);
        boolean okp = true;
        for (DomElement c : e.children("rules")) {
            String rn = c.getAttribute("name");
            logger.info(rn);
            Declension.Rules rules = Declension.getRules(rn);
            Set<CaseNumber> extraKeys = Sets.newHashSet(rules.keySet());
            for (DomElement d : c.children()) {
                String ks = d.getTagName();
                CaseNumber cn = CaseNumber.fromString(ks);
                extraKeys.remove(cn);
                ModRule mr = rules.get(cn);
                if (mr == null) {
                    logger.error("Missing rule " + rn + " " + ks);
                    okp = false;
                } else {
                    String tc = d.getTextContent();
                    String sp = mr.getSpec();
                    if (!tc.equals(sp)) {
                        logger.error("Mismatch " + rn + " " + ks + " " + tc + ":" + sp);
                        okp = false;
                    }
                }
            }
            if (!extraKeys.isEmpty()) {
                logger.error("Extra keys " + rn + " " + extraKeys.toString()); okp = false;
            }
        } Assert.assertTrue(okp);
    }


}