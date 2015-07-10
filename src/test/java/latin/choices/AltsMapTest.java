package latin.choices;


public class AltsMapTest {


    /**
    @Test
    public void testFirstRules() {
        Declension.Builders builders = Declension.First.builders();
        builders.rules("mf")
                .add("AccSi=am GenSi=ae DatSi=ae AblSi=ā LocSi=ae")
                .add("NomPl=ae AccPl=ās GenPl=ārum DatPl=īs");
        builders.rules("a").use("mf").add("NomSi=a");
    }

    @Test
    public void testSecondRules() {
        Declension.Builders builders = Declension.Second.builders();
        builders.worker("shared")
                .add("GenSi=ī")
                .add("DatSi=ō")
                .add("AblSi=ō")
                .add("LocSi=ī")
                .add("GenPl=ōrum")
                .add("DatPl=īs");
        builders.worker("mf").use("shared")
            .add("AccSi=um NomPl=ī AccPl=ōs");
        builders.worker("n").use("shared")
                .add("NomPl=a");
        builders.rules("us").use("mf")
            .add("NomSi=us VocSi=e");
        builders.rules("um").use("n");
        builders.worker("iu").add("GenSi=ī,<");
        builders.rules("ius").use("us").putAll("iu").add("VocSi=<");
        builders.rules("ium").use("um").putAll("iu");
    }

    @Test
    public void testThirdRules() {
        Declension.Builders builders = Declension.Third.builders();
        builders.worker("c").add("GenSi=s").rule("DatSi", "ī").rule("AblSi", "e")
                .rule("GenPl", "um").rule("DatPl", "ibus");
        builders.rules("c.mf").use("c").rule("AccSi", "em").rule("NomPl", "ēs")
                .rule("AccPl", "ēs");
        builders.rules("c.n").use("c").rule("NomPl", "a");
        builders.worker("ium").add("GenPl=ium");
        builders.rules("i.n").use("c.n ium").add("NomPl=ia");
        builders.rules("mi").use("c.mf ium");
        Map<String,Declension.Rules> rulesMap = builders.makeRulesMap();
        for(Map.Entry<String,Declension.Rules> e : rulesMap.entrySet()) {
            e.getValue().printRules();;
       }

    }
    */

}