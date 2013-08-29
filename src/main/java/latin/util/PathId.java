
package latin.util;

import com.google.common.base.Objects;

import javax.annotation.Nullable;

public class PathId {

    public abstract static class Element {
        public final Object name;
        private final int hash;
        public abstract Element getParent();
        public Element(Object name, int hash) {
            this.name = name;
            this.hash = hash;
        }
        public int hashCode() {
            return hash;
        }
        public boolean equals (Object o) {
            if (o == null || !(o instanceof Element)) {
                return false;
            }
            Element e = (Element)o;
            return hash == e.hash && name.equals(e.name) && Objects.equal(getParent(), e.getParent());
        }
        public String toString() {
            return parentString(getParent()) + name.toString();
        }
        public Element makeChild(Object name) {
            return new Child(this, name);
        }
    }

    static String parentString(@Nullable Element parent) {
        return parent == null ? "" : parent.toString() + ".";
    }

    public static class Root extends Element {
        public Root(Object name) {
            super(name, Objects.hashCode(".", name));
        }
        public Element getParent() {
            return null;
        }
    }

    public static Root makeRoot(Object name) {
        return new Root(name);
    }

    public static Element makePath(Object... names) {
        int nn = names.length;
        Element e = makeRoot(names[0]);
        for (int p = 1; p < nn; p++) {
            e = e.makeChild(names[p]);
        }
        return e;
    }

    public static class Child extends Element {
        final Element parent;
        public Child(Element parent, Object name) {
            super(name, Objects.hashCode(parent, ".", name));
            this.parent = parent;
        }
        public Element getParent() {
            return parent;
        }
    }
}



