package latin.forms;

import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public class ComplexPhrase implements IPhrase {
    public List<IPhrase> phraseList;
    public ComplexPhrase(IPhrase... phrases) {
        this.phraseList = Lists.newArrayList(phrases);
    }
    public Iterator<IForm> iterator() {
        return new Forms.PhraseIterator(phraseList);
    }
    public ComplexPhrase add(IPhrase... phrases) {
        for (IPhrase p : phrases) {
            phraseList.add(p);
        }
        return this;
    }
    public ComplexPhrase clear() {
        this.phraseList.clear();
        return this;
    }
}
