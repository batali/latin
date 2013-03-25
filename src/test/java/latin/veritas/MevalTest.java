
package latin.veritas;

import com.google.common.collect.Lists;
import latin.util.Shuffler;
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
        meval.valueSlot("h", "i", "j", "k", "l");
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

    @Test
    public void testWake() throws Exception {
        boolean bev = beVerbose;
        int minw = 2;
        int maxw = 10;
        for (int i = 0; i < 100; i++) {
            int tw = minw + Shuffler.nextInt(maxw+1-minw);
            PropExpression pe = RandomExpression.wake(slotSpecs, tw);
            List<List<Psetting>> cnf = pe.getCnf(true, Psetting.simpleHandler);
            if (bev) {
                System.out.println("pe  " + pe.prettyPrint(true) + " " + pe.weight() + "/" + tw);
                System.out.println("cnf " + cnf.toString());
            }
            checkCnf(pe, cnf, false);
        }
    }

    @Test
    public void testRandomSettings() throws Exception {
        int mins = 2;
        int maxs = 5;
        int ns = 10;
        List<List<Psetting>> rcnf = Lists.newArrayList();
        for (int i = 0; i < ns; i++) {
            int ts = mins + Shuffler.nextInt(maxs+1-mins);
            List<Psetting> psettings = Lists.newArrayList(RandomExpression.randomSettings(slotSpecs, ts));
            System.out.println(psettings.toString());
            int pcs = rcnf.size();
            boolean ap = Psetting.adjoinSettingList(psettings, rcnf);
            int acs = rcnf.size();
            if (ap) {
                System.out.println("\tadded");
                if (acs < pcs + 1) {
                    System.out.println("\tremoved " + (pcs+1-acs));
                }
            }
            else {
                System.out.println("\tnot added");
            }
        }
        System.out.println("rcnf");
        for (List<Psetting> pl : rcnf) {
            System.out.println(pl.toString());
        }
    }

}