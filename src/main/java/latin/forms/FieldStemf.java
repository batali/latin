
package latin.forms;

import com.google.common.base.Preconditions;
import latin.choices.Alts;

import java.lang.reflect.Field;

public class FieldStemf<ET> implements Stemf<ET> {

    private Field field;

    public FieldStemf(Field field) {
        Preconditions.checkNotNull(field);
        this.field = field;
    }

    public FieldStemf(Class<ET> etClass, String fieldName) {
        this(getField(etClass, fieldName));
    }

    public Formf getStemForm(ET e) {
        try {
            return (Formf) field.get(e);
        }
        catch(IllegalAccessException iae) {
            System.err.println("error duing field " + iae.toString());
        }
        return null;
    }

    public boolean test(ET e) {
        return getStemForm(e) != null;
    }

    public IFormBuilder apply(ET e, Alts.Chooser chooser) {
        Formf formf = getStemForm(e);
        return Forms.applyFormf(formf, chooser);
    }

    public static <T> Field getField(Class<T> tclass, String fieldName) {
        try {
            return tclass.getField(fieldName);
        }
        catch(NoSuchFieldException nsfe) {
            System.err.println("getField " + nsfe.toString());
        }
        return null;
    }

    public String toString() {
        return field.getDeclaringClass().getSimpleName() + "." + field.getName();
    }
}
