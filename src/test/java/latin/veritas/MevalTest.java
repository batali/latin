
package latin.veritas;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class MevalTest {

    public static boolean beVerbose = false;

    Meval meval;
    List<? extends SlotSpec> slotSpecs;

    @Before
    public void setUp() {
        meval = new Meval();
        meval.booleanSlot("p");
        meval.booleanSlot("q");
        meval.booleanSlot("r");
        meval.booleanSlot("s");
        meval.booleanSlot("t");
        meval.valueSlot("f", "a", "b", "c");
        meval.valueSlot("g", "0", "1");
        slotSpecs = meval.getSlotList();
    }

    public void checkCnf(PropExpression pe, List<List<Psetting>> cnf, boolean verbose) throws Exception {
        boolean okp = true;
        boolean shownArgs = false;
        Meval.Stepper stepper = meval.makeStepper(pe);
        int tc = stepper.getTotalCount();
        for (int i = 0; i < tc; ++i) {
            stepper.setPositionValues(i);
            boolean pv = stepper.evalPropExpression(pe);
            boolean cv = stepper.evalCnf(cnf);
            if (pv != cv) {
                okp = false;
            }
            if (verbose || pv != cv) {
                if (!shownArgs) {
                    System.out.println("***");
                    System.out.println("pe  " + pe.prettyPrint(true));
                    System.out.println("cnf " + cnf.toString());
                    shownArgs = true;
                }
                System.out.println(String.format("\t%s: %s %s",
                        stepper.getValues().toString(), pv, cv));
            }
        }
        if (verbose || !okp) {
            System.out.println(okp ? "ok" : "not ok");
        }
        Assert.assertTrue(okp);
    }

    public void checkCnf(PropExpression pe, boolean verbose) throws Exception {
        List<List<Psetting>> cnf = pe.getCnf(true, Psetting.simpleHandler);
        checkCnf(pe, cnf, verbose);
    }

    public void checkCnf(PropExpression pe) throws Exception {
        checkCnf(pe, beVerbose);
    }

    public void checkCnf(String ss, boolean verbose) throws Exception {
        checkCnf(PropParser.parseProp(ss), verbose);
    }

    public void checkCnf(String ss) throws Exception {
        checkCnf(PropParser.parseProp(ss));
    }

    @Test
    public void testMeval() throws Exception {
        PropExpression p1 = PropParser.parseProp("(p & q) -> r");
        checkCnf(p1);
        checkCnf("(p -> q) == (r -> s)");
        checkCnf("f=a == p");
        checkCnf("(f=a == p) & (f=b == q)");
        checkCnf("(f!=a | q) -> (p ^ t)");
    }

    @Test
    public void testRandomExp() throws Exception {
        for (int i = 0; i < 100; i++) {
            PropExpression pe = RandomExpression.make(slotSpecs, 8);
            List<List<Psetting>> cnf = pe.getCnf(true, Psetting.simpleHandler);
            if (beVerbose) {
                System.out.println("pe  " + pe.prettyPrint(true));
                System.out.println("cnf " + cnf.toString());
            }
            checkCnf(pe, cnf, false);
        }
    }

}