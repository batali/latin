package latin.choices;

import com.google.common.collect.Maps;

import latin.forms.Mod;
import latin.forms.Token;
import latin.forms.Tokens;
import latin.util.PathId;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public enum Declension {

    First("ae"),
    Second("ī"),
    Third("is"),
    Fourth("ūs"),
    Fifth("eī,ēī");

    public final List<Token> gsiEndings;
    final PathId.Element path;
    Map<String, Rules> rulesMap;


    Declension(String gst) {
        this.gsiEndings = Tokens.parseTokens(gst);
        this.path = new PathId.Root(this);
        this.rulesMap = Maps.newHashMap();
    }

    public static class RuleId {

        public final Declension declension;
        public final String subname;

        RuleId(Declension declension, String subname) {
            this.declension = declension;
            this.subname = subname;
        }

        public String getRulesName() {
            return declension.toString() + "." + subname;
        }

        public Object makeRuleId(CaseNumber key) {
            return getRulesName() + "." + key.toString();
        }

        public String toString() {
            return getRulesName();
        }

        public Declension getDeclension() {
            return declension;
        }

        public String getSubname() {
            return subname;
        }
    }

    public RuleId ruleId(String subname) {
        return new RuleId(this, subname);
    }

    public static class Rules {

        public final RuleId ruleId;
        private final EnumMap<CaseNumber, Alts<Mod>> keyModMap;

        public Rules(RuleId ruleId, Map<CaseNumber, Alts<Mod>> m) {
            this.ruleId = ruleId;
            this.keyModMap = new EnumMap<>(m);
        }

        public RuleId getId() {
            return ruleId;
        }

        public Map<CaseNumber, Alts<Mod>> getMap() {
            return keyModMap;
        }

        public Alts<Mod> getKeyRules(CaseNumber key) {
            return keyModMap.get(key);
        }

        public String rulesString(CaseNumber key) {
            Alts<Mod> m = getKeyRules(key);
            if (m != null) {
                return key.toString() + ":" + m.toString() + " : " + m.getId();
            } else {
                return null;
            }
        }

        public void printRules() {
            System.out.println(ruleId);
            for (CaseNumber key : CaseNumber.values()) {
                String rs = rulesString(key);
                if (rs != null) {
                    System.out.println(rs);
                }
            }
        }
    }

    public PathId.Element getPath() {
        return this.path;
    }

    public Rules getRules(String subname) {
        return rulesMap.get(subname);
    }


}


