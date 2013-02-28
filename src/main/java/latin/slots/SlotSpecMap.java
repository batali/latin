
package latin.slots;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class SlotSpecMap extends GetSettingSpecHandler {

    private Map<String,SlotSettingSpec> specMap;

    public SlotSpecMap() {
        this.specMap = Maps.newHashMap();
    }

    @Override
    public SlotSettingSpec getSlotSettingSpec(String pathString) throws SettingSpecException {
        SlotSettingSpec slotSettingSpec = specMap.get(pathString);
        if (slotSettingSpec == null) {
            throw new SettingSpecException("Unknown path " + pathString);
        }
        return slotSettingSpec;
    }

    public void putSlotSettingSpec(String pathString, SlotSettingSpec slotSettingSpec) {
        specMap.put(pathString, slotSettingSpec);
    }

    public void putBooleanSpec(String pathString) {
        putSlotSettingSpec(pathString, BooleanSettingTraits.booleanSpec);
    }

    public void putValueSpec(String pathString, List<String> choiceNames) {
        putSlotSettingSpec(pathString, SettingTraits.getValueSettingSpec(choiceNames));
    }

    public void putValueSpec(String pathString, String... choices) {
        List<String> choiceNames = Lists.newArrayList(choices);
        putValueSpec(pathString, choiceNames);
    }

    public void useSlotSpecs(UseSettingSpecHandler<?> handler) throws SettingSpecException {
        for (Map.Entry<String,SlotSettingSpec> e : specMap.entrySet()) {
            String pathString = e.getKey();
            SlotSettingSpec slotSettingSpec = e.getValue();
            slotSettingSpec.getTraits().useSettingSpec(handler, pathString, slotSettingSpec);
        }
    }

}

