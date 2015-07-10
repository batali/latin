package latin.choices;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import latin.forms.ModRule;
import latin.forms.Rule;
import latin.util.Splitters;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

class LatinNoun {

    public static class RulesId {

        public final Declension declension;
        public final String subname;
        public final String name;

        RulesId(Declension declension, String subname) {
            this.declension = declension;
            this.subname = subname;
            this.name = declension.toString() + "." + subname;
        }

        public Object makeRuleId(CaseNumber key) {
            return name + "." + key.toString();
        }

        public String toString() {
            return name;
        }

        public Declension getDeclension() {
            return declension;
        }

        public String getSubname() {
            return subname;
        }
    }

    public static class Rules {

        final RulesId rulesId;
        final Map<CaseNumber, Rule> ruleMap;

        public Rules(RulesId rulesId, Map<CaseNumber, Rule> ruleMap) {
            this.rulesId = rulesId;
            this.ruleMap = ruleMap;
        }


        public String rulesString(CaseNumber key) {
            Rule r = ruleMap.get(key);
            if (r != null) {
                return key.toString() + ":" + r.toString();
            } else {
                return null;
            }
        }

        public void printRules() {
            System.out.println(rulesId);
            for (CaseNumber key : CaseNumber.values()) {
                String rs = rulesString(key);
                if (rs != null) {
                    System.out.println(rs);
                }
            }
        }
    }

    public static class RulesetBuilder {

        private Declension declension;
        private Map<String, RulesBuilder> builderMap;
        private Set<String> enumRulesSubnames;

        public RulesetBuilder(Declension declension) {
            this.declension = declension;
            this.builderMap = Maps.newHashMap();
            this.enumRulesSubnames = Sets.newHashSet();
        }

        public class RulesBuilder extends Rules {

            public RulesBuilder(String subname) {
                super(new RulesId(declension, subname), Maps.newHashMap());
                builderMap.put(subname, this);
            }

            public RulesBuilder put(CaseNumber key, Rule rule) {
                ruleMap.put(key, rule);
                return this;
            }

            public RulesBuilder putAll(Map<CaseNumber, Rule> oRuleMap) {
                ruleMap.putAll(oRuleMap);
                return this;
            }

            public RulesBuilder putAll(Rules rules) {
                return putAll(rules.ruleMap);
            }

            public RulesBuilder rule(String ks, String vs) {
                CaseNumber key = CaseNumber.fromString(ks);
                return put(key, new ModRule(rulesId.makeRuleId(key), vs));
            }

            public RulesBuilder use(String ss) {
                for (String sn : Splitters.ssplitter(ss)) {
                    putAll(builderMap.get(sn));
                }
                return this;
            }

            public RulesBuilder add(String ss) {
                for (String rs : Splitters.ssplitter(ss)) {
                    Splitters.esplit(rs, this::rule);
                }
                return this;
            }
            public Rules makeEnumRules() {
                return new Rules(rulesId, new EnumMap<CaseNumber, Rule>(ruleMap));
            }
        }

        public RulesBuilder rules(String subname) {
            return new RulesBuilder(subname);
        }

        public RulesBuilder enumRules(String subname) {
            enumRulesSubnames.add(subname);
            return rules(subname);
        }

        public Map<String,Rules> makeEnumRulesMap() {
            ImmutableMap.Builder<String,Rules> builder = ImmutableMap.builder();
            for (String enumRuleSubname : enumRulesSubnames) {
                builder.put(enumRuleSubname, builderMap.get(enumRuleSubname).makeEnumRules());
            }
            return builder.build();
        }
    }
}


