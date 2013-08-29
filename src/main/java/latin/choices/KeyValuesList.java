package latin.choices;

import latin.util.PathId;

import java.util.List;

public class KeyValuesList<KT extends Enum<KT>, VT> extends IdValuesList<VT> {
    final KT key;
    public KeyValuesList(PathId.Element path, KT key, List<VT> vals) {
        super(path.makeChild(key), vals);
        this.key = key;
    }
}
