

package latin.setting;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class TopSupporter implements Supporter {

    final Supportable mySupportable;

    public TopSupporter(Supportable supportable) {
        this.mySupportable = supportable;
    }

    @Override
    public boolean supportingAny() {
        return supporting(mySupportable);
    }

    public Supportable getMySupportable() {
        return mySupportable;
    }

    @Override
    public boolean supporting(Supportable supportable) {
        return supportable != null && Objects.equals(supportable.getSupporter(), this);
    }

    @Override
    public boolean addSupported(Supportable supportable) {
        Preconditions.checkArgument(Objects.equals(supportable, mySupportable));
        return true;
    }

    @Override
    public boolean removeSupported(Supportable supportable) {
        Preconditions.checkArgument(Objects.equals(supportable, mySupportable));
        return true;
    }

    @Override
    public boolean retract(Retractor retractor) {
        return Objects.equals(mySupportable.getSupporter(), this) && mySupportable.removeSupport(retractor);
    }

    public boolean trySet(Propagator p) {
        return mySupportable.setSupport(this, p);
    }

}