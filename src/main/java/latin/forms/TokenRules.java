
package latin.forms;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nullable;

public class TokenRules {

    public static class AddedStringRule extends StringToken implements TokenRule {
        public AddedStringRule(String string) {
            super(string);
        }
        public String getSpec() {
            return string;
        }
        public Token apply(@Nullable Token token) {
            return Tokens.pair(token, this);
        }
    }

    public static class SequenceTokenRule implements TokenRule {
        final ImmutableList<TokenRule> rules;
        public SequenceTokenRule(List<TokenRule> ruleList) {
            this.rules = ImmutableList.copyOf(ruleList);
        }
        @Override
        public Token apply(@Nullable Token token) {
            for (TokenRule r : rules) {
                token = r.apply(token);
            }
            return token;
        }
        @Override
        public String getSpec() {
            StringBuilder sb = new StringBuilder();
            for (TokenRule r : rules) {
                sb.append(r.getSpec());
            }
            return sb.toString();
        }
    }

    public static final TokenRule noopRule = new TokenRule () {
        @Override
        public String getSpec() {
            return ":";
        }
        @Override
        public Token apply(@Nullable Token token) {
            return token;
        }
    } ;

    public static TokenRule makeRule(List<TokenRule> rules) {
        int s = rules.size();
        if (s == 0) {
            return noopRule;
        }
        else if (s == 1) {
            return rules.get(0);
        }
        else {
            return new SequenceTokenRule(rules);
        }
    }

    public static class ButLast implements TokenRule {
        final int endOffset;
        public ButLast(int endOffset) {
            this.endOffset = endOffset;
        }
        public Token apply(@Nullable Token token) {
            return (token == null) ? null : token.subSequence(0, token.length() - endOffset);
        }
        @Override
        public String getSpec() {
            return Strings.padEnd("", endOffset, '-');
        }
    }

    public static final ButLast removeLastRule = new ButLast(1);

    public static TokenRule getButLast(int endOffset) {
        if (endOffset == 1) {
            return removeLastRule;
        }
        else {
            return new ButLast(endOffset);
        }
    }

    static Token transformChar(Token t, int l, int p, char c) {
        return Tokens.pair(t.subSequence(0, p),
                           Tokens.pair(new StringToken(c),
                                       t.subSequence(p+1, l)));
    }

    public static int lastVowelPosition(CharSequence cs, int l) {
        for (int p = l-1; p>=0; p--) {
            if (Suffix.isVowel(cs.charAt(p))) {
                return p;
            }
        }
        return -1;
    }

    public static final TokenRule unaccenterRule = new TokenRule() {
        @Override
        public String getSpec() {
            return ">";
        }
        public Token apply(@Nullable Token token) {
            if (token != null) {
                int l = token.length();
                int p = lastVowelPosition(token, l);
                if (p >= 0) {
                    char c = token.charAt(p);
                    if (Suffix.isAccented(c)) {
                        return transformChar(token, l, p, Suffix.unaccented(c));
                    }
                }
            }
            return token;
        }
    };

    public static final TokenRule accenterRule = new TokenRule() {
        @Override
        public String getSpec() {
            return "<";
        }
        public Token apply(@Nullable Token token) {
            if (token != null) {
                int l = token.length();
                int p = lastVowelPosition(token, l);
                if (p >= 0) {
                    char c = token.charAt(p);
                    if (!Suffix.isAccented(c)) {
                        return transformChar(token, l, p, Suffix.accented(c));
                    }
                }
            }
            return token;
        }
    };

    public static final TokenRule duplicateLast = new TokenRule() {
        @Override
        public String getSpec() {
            return "+";
        }
        @Override
        public Token apply(@Nullable Token token) {
            if (token == null || token.isEmpty()) {
                return token;
            }
            else {
                return new PairToken(token, new StringToken(token.endChar(1)));
            }
        }
    };

    static TokenRule parseRule(String trs) {
        List<TokenRule> rules = Lists.newArrayList();
        int sp = 0;
        int ep = trs.length();
        while (sp < ep) {
            int cp = trs.indexOf(':', sp);
            if (cp >= 0) {
                if (cp > 0) {
                    rules.add(getButLast(cp));
                }
                sp = cp+1;
                continue;
            }
            int fc = trs.charAt(sp);
            if (fc == ':') {
                sp += 1;
            }
            else if (fc == '<') {
                rules.add(accenterRule);
                sp += 1;
            }
            else if (fc == '>') {
                rules.add(unaccenterRule);
                sp += 1;
            }
            else if (fc == '+') {
                rules.add(duplicateLast);
                sp += 1;
            }
            else if (fc == '-') {
                int np = sp+1;
                while (np < ep && trs.charAt(np) == '-') {
                    np += 1;
                }
                int ec = np - sp;
                if (ec == 1) {
                    rules.add(removeLastRule);
                }
                else {
                    rules.add(new ButLast(ec));
                }
                sp = np;
            }
            else {
                rules.add(new AddedStringRule(trs.substring(sp, ep)));
                sp = ep;
            }
        }
        if (rules.isEmpty()) {
            return noopRule;
        }
        else if (rules.size() == 1) {
            return rules.get(0);
        }
        else {
            return new SequenceTokenRule(rules);
        }
    }

    public static final Function<String,TokenRule> toTokenRule = new Function<String, TokenRule>() {
        @Override
        public TokenRule apply(@Nullable String s) {
            return s == null ? null : parseRule(s);
        }
    };

    public static List<TokenRule> parseRules(String rs) {
        return Suffix.csplit(rs, toTokenRule);
    }

    public static String rulesToString(List<TokenRule> rl) {
        StringBuilder sb = new StringBuilder();
        boolean needc = false;
        for (TokenRule r : rl) {
            if (needc) {
                sb.append(',');
            }
            sb.append(r.getSpec());
            needc = true;
        }
        return sb.toString();
    }


}



