package latin.choices;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface RecordAlts {
    public void recordAlts(BiConsumer<Object,Integer> bic);
    public void recordAlts(Consumer<Alts<?>> aic);
}