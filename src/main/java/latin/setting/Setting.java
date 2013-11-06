
package latin.setting;

public interface Setting extends Supportable {

    public Setting getOpposite();
    public int getValueIndex();
    public boolean getPolarity();
    public int getStatus();
    public boolean isSatisfiable();
    public Setting getSupported();

    interface Rule extends DeduceRule {
        public void recordSet(Setting setting, boolean bv);
        public void recordUnset(Setting setting, boolean bv);
    }

    public boolean addRule(Rule rule);
    public boolean removeRule(Rule rule);

}