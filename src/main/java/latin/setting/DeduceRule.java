
package latin.setting;

public interface DeduceRule extends Supporter {
    public boolean deduceCheck();
    public boolean deduce(Propagator propagator);
    public boolean retractCheck();
}