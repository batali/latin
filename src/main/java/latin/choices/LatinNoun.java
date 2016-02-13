package latin.choices;

import latin.forms.Form;
import latin.forms.ModRule;
import latin.forms.StringForm;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public class LatinNoun {

    public interface FormEntry {

        Form getForm(CaseNumber cn);
    }

    public interface Entry extends FormEntry {

        Form getGstem();
        Declension.Rules getRules();
        Form getStoredForm(CaseNumber cn);
        Gender getGender();

        default Form getKeyForm(CaseNumber cn, Gender g) {
            Form sf = getStoredForm(cn);
            if (sf != null) {
                return sf;
            }
            ModRule r = getRules().get(cn);
            if (r != null) {
                return r.apply(getGstem());
            }
            return null;
        }

        default Form getForm(CaseNumber cn) {
            return LatinNounForms.getForm(this::getKeyForm, cn, getGender());
        }
    }

    public static class StoredFormMap extends EnumMap<CaseNumber, StringForm>
        implements Function<CaseNumber,Form> {

        public StoredFormMap(Map<CaseNumber,StringForm> m) {
            super(m);
        }

        @Override
        public Form apply(CaseNumber cn) {
            return get(cn);
        }
    }

    public static final Function<CaseNumber, Form> EmptyStoredFormFunction =
        new Function<CaseNumber, Form>() {
            @Override
            public Form apply(CaseNumber caseNumber) {
                return null;
            }
        };

    public static class EntryImpl implements Entry {

        Form gstem;
        Declension.Rules rules;
        Gender gender;
        Function<CaseNumber,Form> storedFormFunction;

        public EntryImpl(Form gstem, Declension.Rules rules, Gender gender,
                         Function<CaseNumber,Form> storedFormFunction) {
            this.gstem = gstem;
            this.rules = rules;
            this.gender = gender;
            this.storedFormFunction = storedFormFunction;
        }

        @Override
        public Form getStoredForm(CaseNumber cn) {
            return storedFormFunction.apply(cn);
        }

        @Override
        public Gender getGender() {
            return gender;
        }

        @Override
        public Declension.Rules getRules() {
            return rules;
        }

        @Override
        public Form getGstem() {
            return gstem;
        }

    }


}