
package latin.setter;

import com.google.common.base.Optional;
import latin.slots.SettingSpecException;
import latin.slots.SettingTraits;
import latin.slots.SlotSettingSpec;

public abstract class AbstractSlot implements ISlot {

    public final String pathString;
    protected Optional<Setter> oValueSetter;
    public final SlotSettingSpec slotSettingSpec;

    public AbstractSlot(String pathString, SlotSettingSpec slotSettingSpec) {
        this.slotSettingSpec = slotSettingSpec;
        this.pathString = pathString;
        this.oValueSetter = Optional.absent();
    }

    @Override
    public SlotSettingSpec getSlotSettingSpec() {
        return slotSettingSpec;
    }

    public String getPathString() {
        return pathString;
    }

    public boolean haveValueSetter() {
        return oValueSetter.isPresent();
    }

    public Setter getValueSetter() {
        return oValueSetter.get();
    }

    public int choiceNameIndex(String choiceName, boolean errorp) throws SettingSpecException {
        return SettingTraits.findChoiceIndex(choiceName, slotSettingSpec.getChoiceNames(), errorp);
    }

}
