package latin.choices;

public interface KeyValues<KT,VT> {
    public Value<VT> getValue(KT key);
    public boolean containsKey(KT key);
}