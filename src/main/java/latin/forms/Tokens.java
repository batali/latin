
package latin.forms;

import latin.choices.Alts;
import latin.choices.Values;
import latin.util.PathId;

import java.util.EnumMap;
import java.util.List;

public class Tokens {

    public interface Stored<KT> {
        public Token getStoredToken(KT key, Alts.Chooser chooser);
    }

    public interface Rules<KT> {
        public TokenRule getTokenRule(KT key, Alts.Chooser chooser);
    }

    public static List<Token> parseTokens(String ts) {
        return Suffix.csplit(ts, StringToken.toStringToken);
    }

    public static class StoredTokensBuilder<KT extends Enum<KT>> extends ValuesMap.Builder<KT,Token> {
        public StoredTokensBuilder(PathId.Element path, Class<KT> ktClass) {
            super(path, ktClass);
        }
        public StoredTokensBuilder stored(Object k, String ts) {
            super.put(k, parseTokens(ts));
            return this;
        }
        public Stored<KT> build() {
            final EnumMap<KT,Values<Token>> enm = new EnumMap<KT,Values<Token>>(ktClass);
            enm.putAll(getMap());
            return new Stored<KT>() {
                @Override
                public Token getStoredToken(KT key, Alts.Chooser chooser) {
                    return Alts.chooseElement(enm.get(key), chooser);
                }
            };
        }
    }


    public static <KT extends Enum<KT>>  StoredTokensBuilder<KT> storedTokensBuilder(PathId.Element path,
                                                                                     Class<KT> ktclass) {
        return new StoredTokensBuilder<KT>(path, ktclass);
    }
}