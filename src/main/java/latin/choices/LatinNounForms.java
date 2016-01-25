package latin.choices;

import latin.forms.Form;
import latin.forms.Rule;

import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class LatinNounForms {

    static CaseNumber getRenamed(CaseNumber key, Gender gender) {
        if (key.equals(CaseNumber.AblPl)) {
            return CaseNumber.DatPl;
        }
        if (key.caseKey.isVocative()) {
            return key.toCase(Case.Nom);
        }
        if (key.caseKey.isLocative()) {
            return key.toCase(Case.Abl);
        }
        if (gender.isNeuter()) {
            if (key.caseKey.isAccusative()) {
                return key.toCase(Case.Nom);
            }
        }
        return null;
    }

    public interface KeyFormHandler<E> {
        Form getKeyForm(E e, CaseNumber cn, Gender g);
    }

    public interface Handler<E> extends KeyFormHandler<E> {
        default Form getForm(E e, CaseNumber cn, Gender g) {
            Form f = getKeyForm(e, cn, g);
            if (f != null) {
                return f;
            }
            CaseNumber rcn = getRenamed(cn, g);
            if (rcn != null) {
                return getForm(e, rcn, g);
            }
            return null;
        }
    }

    public interface RuleHandler<E> extends Handler<E> {
        Form getStem(E e);
        Rule getRule(E e, CaseNumber cn, Gender g);
        default Form getKeyForm(E e, CaseNumber cn, Gender g) {
            Rule r = getRule(e, cn, g);
            if (r != null) {
                return r.apply(getStem(e));
            }
            return null;
        }
    }

    public static class PairedHandler<E> implements Handler<E> {
        KeyFormHandler<E> handler1;
        KeyFormHandler<E> handler2;
        public PairedHandler(KeyFormHandler<E> handler1, KeyFormHandler<E> handler2) {
            this.handler1 = handler1;
            this.handler2 = handler2;
        }
        public Form getKeyForm(E e, CaseNumber cn, Gender g) {
            Form f = handler1.getKeyForm(e, cn, g);
            if (f == null) {
                f = handler2.getKeyForm(e, cn, g);
            }
            return f;
        }
    }

    public interface StringRuleHandler extends RuleHandler<Form> {
        @Override
        default public Form getStem(Form e) {
            return e;
        }
    }

    public interface KeyFormEntry {
        Form getKeyForm(CaseNumber cn, Gender g);
    }

    public static Form getForm(KeyFormEntry e, CaseNumber cn, Gender g) {
        Form f = e.getKeyForm(cn, g);
        if (f != null) {
            return f;
        }
        CaseNumber rcn = getRenamed(cn, g);
        if (rcn != null) {
            return getForm(e, rcn, g);
        }
        return null;
    }

    public interface RuleEntry extends KeyFormEntry {
        default Form getIrregularForm(CaseNumber cn, Gender g) {
            return null;
        }
        Form getGstem();
        Rule getGstemRule(CaseNumber cn, Gender g);
        default Form getKeyForm(CaseNumber cn, Gender g) {
            Form f = getIrregularForm( cn, g);
            if (f != null) {
                return f;
            }
            Rule r = getGstemRule(cn, g);
            if (r != null) {
                return r.apply(getGstem());
            }
            return null;
        }
    }


    public interface Entry {
        public Form getIrregularForm(CaseNumber cn, Gender g);
        public Form getGstem();
        public Rule getGstemRule(CaseNumber cn, Gender g);
        default Form getLatinNounForm(CaseNumber cn, Gender g) {
            return entryHandler.getForm(this, cn, g);
        }
    }

    public static RuleFormHandler<Entry> entryHandler = new RuleFormHandler<Entry> () {
        @Override
        public Form getIrregularForm(Entry entry, CaseNumber cn, Gender g) {
            return entry.getIrregularForm(cn, g);
        }

        @Override
        public Form getGstem(Entry entry) {
            return entry.getGstem();
        }

        @Override
        public Rule getGstemRule(Entry entry, CaseNumber cn, Gender g) {
            return entry.getGstemRule(cn, g);
        }
    };


    static Form getForm(BiFunction<CaseNumber, Gender, Form> formFunction,
                        CaseNumber cn, Gender g) {
        Form f = formFunction.apply(cn, g);
        if (f != null) {
            return f;
        }
        CaseNumber rcn = getRenamed(cn, g);
        if (rcn != null) {
            return getForm(formFunction, rcn, g);
        }
        return null;
    }

    static interface GstemFormFunction extends BiFunction<CaseNumber, Gender, Form> {

        default public Form getIrregularForm(CaseNumber cn, Gender g) {
            return null;
        }

        public Form getGstem();

        public Rule getGstemRule(CaseNumber cn, Gender g);

        default Form apply(CaseNumber cn, Gender g) {
            Form i = getIrregularForm(cn, g);
            if (i != null) {
                return i;
            }
            Rule r = getGstemRule(cn, g);
            if (r != null) {
                return r.apply(getGstem());
            }
            return null;
        }
    }

}
