
package latin.nodes;

interface BSRule extends Deducer {
    public void recordSet(BooleanSetting setting, boolean sp, DeduceQueue deduceQueue)
            throws ContradictionException;
    public void recordUnset(BooleanSetting setting, boolean sp, RetractQueue retractQueue);
}
