
package latin.setter;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import latin.slots.SettingSpecException;
import latin.slots.SlotSettingSpec;

public class BinarySlot extends AbstractSlot {

    private final BinarySetter trueSetter;
    private final BinarySetter falseSetter;

    public BinarySlot(String pathString, SlotSettingSpec slotSettingSpec) {
        super(pathString, slotSettingSpec);
        this.trueSetter = new BinarySetter(slotSettingSpec.getChoiceNames().get(1)) {
            @Override
            public boolean getValue() {
                return true;
            }
        };
        this.falseSetter = new BinarySetter(slotSettingSpec.getChoiceNames().get(0)) {
            @Override
            public boolean getValue() {
                return false;
            }
        };
    }

    public BinarySlot getBinarySlot() {
        return this;
    }

    public abstract class BinarySetter extends Setter {
        private String choiceName;
        public BinarySetter(String choiceName) {
            super();
            this.choiceName = choiceName;
        }
        public String getChoiceName() {
            return choiceName;
        }
        public int getIndex() {
            return 0;
        }
        public BinarySetter getOpposite() {
            return getValue() ? falseSetter : trueSetter;
        }
        @Override
        public BinarySlot getSlot() {
            return getBinarySlot();
        }
        @Override
        public void announceSupported(Propagator propagator) throws ContradictionException {
            Preconditions.checkState(!oValueSetter.isPresent());
            oValueSetter = Optional.of(asSetter());
            super.announceSupported(propagator);
        }
        @Override
        public void announceRetracted(Propagator propagator, SupportRule stopAfter) {
            Preconditions.checkState(Objects.equal(oValueSetter.orNull(), this));
            oValueSetter = Optional.absent();
            super.announceRetracted(propagator, stopAfter);
        }

    }

    public BinarySetter getBooleanSetter(boolean sv) {
        return sv ? trueSetter : falseSetter;
    }

    @Override
    public BinarySetter getSetter (String choiceName, boolean sv) throws SettingSpecException {
        int cni = choiceNameIndex(choiceName, true);
        Preconditions.checkElementIndex(cni, 2);
        return getBooleanSetter((cni == 1) == sv);
    }

    public BinarySetter getBinarySetter(String choiceName, boolean sv) throws SettingSpecException {
        BinarySetter binarySetter = getBooleanSetter(sv);
        if (!binarySetter.getChoiceName().equalsIgnoreCase(choiceName)) {
            throw new SettingSpecException("bnary mismatch");
        }
        return binarySetter;
    }

}
