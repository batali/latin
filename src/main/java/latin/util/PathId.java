
package latin.util;

import com.google.common.base.Objects;

import javax.annotation.Nullable;

public class PathId {

    public final Object name;
    private final int hash;

    PathId(Object name, int hash) {
        this.name = name;
        this.hash = hash;
    }

    public interface Identified {
        PathId getPathId();
    }

    public int hashCode() {
        return hash;
    }

    public boolean equals (Object o) {
        if (o == null || !(o instanceof PathId)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        PathId e = (PathId)o;
        return hash == e.hash && name.equals(e.name) && Objects.equal(getParent(), e.getParent());
    }

    public String toString() {
        return parentString(getParent()) + name.toString();
    }

    public PathId getParent() {
        return null;
    }

    public PathId makeChild(Object childName) {
        return new Child(this, childName);
    }

    static class Child extends PathId {
        PathId parent;
        public Child(PathId parent, Object name) {
            super(name, Objects.hashCode(parent, ".", name));
            this.parent = parent;
        }
        public PathId getParent() {
            return parent;
        }
    }

    static String parentString(@Nullable PathId parent) {
        return parent == null ? "" : parent.toString() + ".";
    }

    public static PathId makeRoot(Object name) {
        return new PathId(name, Objects.hashCode("." + name));
    }

    public static PathId makePath(Object rootName, Object... childNames) {
        PathId e = makeRoot(rootName);
        for (Object childName : childNames) {
            e = e.makeChild(childName);
        }
        return e;
    }

}



