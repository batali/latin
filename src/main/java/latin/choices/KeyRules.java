package latin.choices;

import latin.forms.ModRule;
import latin.util.PathId;

import java.util.Map;

public interface KeyRules<K> extends Map<K,ModRule>, PathId.Identified {
}