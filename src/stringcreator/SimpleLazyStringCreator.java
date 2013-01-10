package stringcreator;

public class SimpleLazyStringCreator extends StringCreator {

    private String string;

    public SimpleLazyStringCreator(String string) {
        this.string = string;
    }

    @Override
    public String getString(int maxWidth) {
        return string;
    }
}
