
package latin.slots;

public abstract class GetSettingSpecHandler {

    public abstract SlotSettingSpec getSlotSettingSpec(String pathString) throws SettingSpecException;

    public <T> SettingHandler<T> getSettingHandler (Class<T> cls,
                                                    final PathHandler<? extends T> pathHandler) {
        return new SettingHandler<T>() {
            @Override
            public T getValueSetting(String pathString, String choiceName, boolean sv) throws SettingSpecException {
                SlotSettingSpec slotSettingSpec = getSlotSettingSpec(pathString);
                return slotSettingSpec.getTraits().getValueSetting(pathHandler, pathString, choiceName, sv, slotSettingSpec.getChoiceNames());
            }
            @Override
            public T getBooleanSetting(String pathString, boolean sv) throws SettingSpecException {
                SlotSettingSpec slotSettingSpec = getSlotSettingSpec(pathString);
                return slotSettingSpec.getTraits().getBooleanSetting(pathHandler, pathString, sv, slotSettingSpec.getChoiceNames());
            }
        };
    }
}
