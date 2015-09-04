package latin.choices;

import latin.forms.Rule;
import latin.util.PathId;

import java.util.Map;

public interface KeyRules<K> extends Map<K,Rule>, PathId.Identified {
}