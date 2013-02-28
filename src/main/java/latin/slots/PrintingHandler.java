
package latin.slots;

public class PrintingHandler<T> implements PathHandler<Void> {

    private PathHandler<T> pathHandler;
    public PrintingHandler(PathHandler<T> pathHandler) {
        this.pathHandler = pathHandler;
    }

    @Override
    public Void onBoolean(String pathString, boolean sv) throws SettingSpecException {
        System.out.println(pathHandler.onBoolean(pathString, sv).toString());
        return null;
    }

    @Override
    public Void onBinary(String pathString, String choiceName, boolean sv) throws SettingSpecException {
        System.out.println(pathHandler.onBinary(pathString, choiceName, sv).toString());
        return null;
    }

    @Override
    public Void onValue(String pathString, String choiceName, int index, boolean sv) throws SettingSpecException {
        System.out.println(pathHandler.onValue(pathString, choiceName, index, sv).toString());
        return null;
    }
}