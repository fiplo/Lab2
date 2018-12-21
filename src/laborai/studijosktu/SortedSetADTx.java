package laborai.studijosktu;

public interface SortedSetADTx<E> extends SortedSetADT<E> {

    void add(String dataString);

    void load(String fName);

    String toVisualizedString(String dataCodeDelimiter);

    Object clone() throws CloneNotSupportedException;
}