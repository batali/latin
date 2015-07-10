package latin.choices;

import org.junit.Test;

import java.util.Map;

public class LatinNounTest {

    void showRulesMap(Map<String,LatinNoun.Rules> rulesMap) {
        for (LatinNoun.Rules rules : rulesMap.values()) {
            rules.printRules();
        }
    }

    @Test
    public void testFirst() {
        LatinNoun.RulesetBuilder builder = new LatinNoun.RulesetBuilder(Declension.First);
        builder.enumRules("mf")
               .add("AccSi=am GenSi=ae DatSi=ae AblSi=ā LocSi=ae")
               .add("NomPl=ae AccPl=ās GenPl=ārum DatPl=īs");
        builder.enumRules("a")
               .use("mf").add("NomSi=a");
        showRulesMap(builder.makeEnumRulesMap());
    }

    @Test
    public void testSecondRules() {
        LatinNoun.RulesetBuilder builder = new LatinNoun.RulesetBuilder(Declension.Second);
        builder.rules("shared")
                .add("GenSi=ī")
                .add("DatSi=ō")
                .add("AblSi=ō")
                .add("LocSi=ī")
                .add("GenPl=ōrum")
                .add("DatPl=īs");
        builder.enumRules("mf").use("shared")
                .add("AccSi=um NomPl=ī AccPl=ōs");
        builder.enumRules("n").use("shared")
                .add("NomPl=a");
        builder.enumRules("us").use("mf")
               .add("NomSi=us VocSi=e");
        builder.enumRules("um").use("n").add("NomSi=um");
        builder.rules("iu").add("GenSi=ī,<");
        builder.enumRules("ius").use("us iu").add("VocSi=<");
        builder.enumRules("ium").use("um iu");
        showRulesMap(builder.makeEnumRulesMap());
    }

}