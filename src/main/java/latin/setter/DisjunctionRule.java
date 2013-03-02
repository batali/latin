
package latin.setter;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nullable;
import java.util.List;

public class DisjunctionRule extends AbstractDisjunctionRule {

    public final String name;
    private final List<Setter> setters;
    private @Nullable Setter supported;

    public DisjunctionRule (String name, List<Setter> setters) {
        this.name = name;
        this.setters = setters;
        this.supported = null;
        for (Setter setter : setters) {
            setter.addRule(this);
        }
    }

    @Override
    public List<Setter> getSetters() {
        return setters;
    }

    @Override
    public void addSupported(Setter setter) {
        Preconditions.checkState(supported == null);
        supported = setter;
    }

    @Override
    public void removeSupported(Setter setter) {
        Preconditions.checkState(Objects.equal(supported, setter));
        supported = null;
    }

    @Override
    public void deduce(Propagator propagator) throws ContradictionException {
        disjunctionDeduce(propagator);
    }

    @Override
    public void retract(Propagator propagator) {
        if (supported != null) {
            propagator.recordRetracted(supported, this);
        }
        else if (disjunctionReDeduceTest()) {
            propagator.addRededucer(this);
        }
    }

    public String toString() {
        return setters.toString();
    }

    public boolean hasSupported() {
        return supported != null;
    }

}