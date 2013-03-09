
package latin.nodes;

import com.google.common.collect.Lists;

import java.util.List;

public class TopSupporter implements Supporter {
    public List<Supported> supportedList;
    public TopSupporter() {
        this.supportedList = Lists.newArrayList();
    }
    public Supported peekSupported() {
        if (supportedList.isEmpty()) {
            return null;
        }
        else {
            return supportedList.get(0);
        }
    }
    public boolean addSupported(Supported supported) {
        if (!supportedList.contains(supported)) {
            this.supportedList.add(supported);
            return true;
        }
        else {
            return false;
        }
    }
    public boolean removeSupported(Supported supported) {
        if (supportedList.contains(supported)) {
            supportedList.remove(supported);
            return true;
        }
        else {
            return false;
        }
    }
    public void retractAll(RetractQueue retractQueue) {
        Supported s = null;
        while ((s = peekSupported())!=null) {
           if (s.unsetSupporter()) {
               retractQueue.addRetracted(s);
           }
        }
    }

    public String toString() {
        return "top:" + supportedList.toString();
    }

    public void collectSupport(SupportCollector supportCollector) {
    }
}