/**
 * Created by root on 08.12.17.
 */
enum Type{
    OPEN,
    CLOSE,
    CONST,
    VALUE,
    NOT,
    AND,
    OR,
    IMP
}
public class Element{
    Type type;
    String name;
    Boolean value;
    public Element(String name, Type type, Boolean value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        if(name != null)
            return name;
        if(value != null)
            return "" + value;
        return "" + type;
    }

}
