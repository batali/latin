
package latin.setting;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;

public class SimplePropagatorTest {

    ValueNode<Boolean> pnode;
    ValueNode<Boolean> qnode;
    ValueNode<Boolean> rnode;
    ValueNode<String> fnode;

    StringValuesSpec fspec;

    DisjunctionRule pq;
    DisjunctionRule qr;

    public <T> ValueNode<T> makeNode(String name, ValuesSpec<T> valuesSpec) {
        return new ValueNode<T>(name, valuesSpec);
    }

    public ValueNode<Boolean> makeBooleanNode(String name) {
        return makeNode(name, BooleanValuesSpec.spec);
    }

    public String getNodeAvailable(ValueNode<?> vnode) {
        return vnode.getName() + " " + vnode.getAvailableValueStrings();
    }

    public void printNodeAvailable(ValueNode<?>... vnodes) {
        for (int p = 0; p < vnodes.length; p++){
            System.out.println(getNodeAvailable(vnodes[p]));
        }
    }

    @Before
    public void setUp() {
        pnode = makeBooleanNode("p");
        qnode = makeBooleanNode("q");
        rnode = makeBooleanNode("r");
        pq = new DisjunctionRule(Arrays.asList(pnode.getSetting(0),qnode.getSetting(1)));
        qr = new DisjunctionRule(Arrays.asList(qnode.getSetting(0),rnode.getSetting(1)));
        fspec = new StringValuesSpec(Arrays.asList("a", "b", "c"));
        fnode = makeNode("f", fspec);
    }

    @Test
    public void testBoolean() throws Exception {
        SimplePropagator sp = new SimplePropagator();
        TopSupporter tp1 = new TopSupporter(pnode.getValueSetting(true));
        assertNull(pnode.getSupportedSetting());
        tp1.trySet(sp);
        assertEquals(tp1.getMySupportable(), pnode.getSupportedSetting());
        sp.deduceLoop();
        printNodeAvailable(pnode, qnode, rnode);
        SimpleRetractor sr = new SimpleRetractor();
        tp1.retract(sr);
        assertNull(pnode.getSupportedSetting());
        sr.retractLoop();
        assertNull(qnode.getSupportedSetting());
        assertNull(rnode.getSupportedSetting());
        printNodeAvailable(pnode, qnode, rnode);
        TopSupporter tp2 = new TopSupporter(rnode.getValueSetting(false));
        tp2.trySet(sp);
        sp.deduceLoop();
        printNodeAvailable(pnode, qnode, rnode);
    }

    @Test
    public void testValue() throws Exception {
        SimplePropagator sp = new SimplePropagator();
        TopSupporter tp1 = new TopSupporter(fnode.getValueSetting("a"));
        assertNull(fnode.getSupportedSetting());
        tp1.trySet(sp);
        assertEquals(tp1.getMySupportable(), fnode.getSupportedSetting());
        sp.deduceLoop();
        printNodeAvailable(fnode);
        SimpleRetractor sr = new SimpleRetractor();
        tp1.retract(sr);
        sr.retractLoop();
        printNodeAvailable(fnode);
        TopSupporter tp2 = new TopSupporter(fnode.getValueSetting("a", false));
        TopSupporter tp3 = new TopSupporter(fnode.getValueSetting("b", false));
        tp2.trySet(sp);
        sp.deduceLoop();
        tp3.trySet(sp);
        sp.deduceLoop();
        printNodeAvailable(fnode);
        tp2.retract(sr);
        sr.retractLoop();
        printNodeAvailable(fnode);
    }

}