import java.util.ArrayList;

/**
 * Created by root on 11.12.17.
 */
public class SortAutomaton {
    private ArrayList<Element> list = new ArrayList<>();
    private GenerateReader reader;
    private int skCount = 0;

    public void start(String str) throws Exception {
        reader = new GenerateReader(str);
        System.out.println("Автомат для сортировочного дерева получил выражение: ");
        out();
        startState();
        System.out.println();
    }

    private void startState() throws Exception {
        while(reader.isInputState()) {
            if(reader.get() == ')'){
                return;
            }
            if (reader.get() >= 'a' && reader.get() <= 'z') {
                String name = "" + reader.get();
                reader.read();
                out();
                while (reader.get() >= '0' && reader.get() <= '9'){
                    name += reader.get();
                    reader.read();
                    out();
                }
                list.add(new Element(name, Type.VALUE, null));
                if(reader.get() == '&'){
                    list.add(new Element(null, Type.AND, null));
                    reader.read();
                    out();
                    continue;
                }
                else if(reader.get() == '|'){
                    list.add(new Element(null, Type.OR, null));
                    reader.read();
                    out();
                    continue;
                }
                else if(reader.get() == '='){
                    reader.read();
                    out();
                    if(reader.get() == '>'){
                        list.add(new Element(null, Type.IMP, null));
                        reader.read();
                        out();
                        continue;
                    }
                    else {
                        System.out.println();
                        System.out.println("Ошибка при попытке считать операцию");
                        throw new Exception();
                    }
                }
                else if(reader.isInputState()){
                    if(reader.get() == ')' && skCount == 0 || reader.get() != ')') {
                        System.out.println();
                        System.out.println("Ошибка перехода после считывания переменной");
                        throw new Exception();
                    }
                }
                else{
                    System.out.println();
                    System.out.println("Считывание окончено ошибок нет");
                    return;
                }
            }
            else if(reader.get() == '!'){
                list.add(new Element(null, Type.NOT, null));
                reader.read();
                out();
                continue;
            }
            else if(reader.get() == '('){
                skCount++;
                list.add(new Element(null, Type.OPEN, null));
                reader.read();
                out();
                startState();
                if(reader.get() == ')') {
                    skCount--;
                    list.add(new Element(null, Type.CLOSE, null));
                    reader.read();
                    out();
                    if(reader.isInputState()){
                        if(reader.get() == '&'){
                            list.add(new Element(null, Type.AND, null));
                            reader.read();
                            out();
                            continue;
                        }
                        else if(reader.get() == '|'){
                            list.add(new Element(null, Type.OR, null));
                            reader.read();
                            out();
                            continue;
                        }
                        else if(reader.get() == '='){
                            reader.read();
                            out();
                            if(reader.get() == '>'){
                                list.add(new Element(null, Type.IMP, null));
                                reader.read();
                                out();
                                continue;
                            }
                            else {
                                System.out.println();
                                System.out.println("Ошибка при попытке считать операцию");
                                throw new Exception();
                            }
                        }
                        else if(reader.get() != ')' || skCount == 0) {
                            System.out.println();
                            System.out.println("Ошибка при попытке перейти к считыванию операции");
                            throw new Exception();
                        }
                    }else{
                        if(skCount != 0){
                            System.out.println();
                            System.out.println("Не хватает закрывающей скобки");
                            throw new Exception();
                        }
                        System.out.println();
                        System.out.println("Считывание окончено ошибок нет");
                        return;
                    }
                }else{
                    System.out.println();
                    System.out.println("не найдена закрывающая скобка");
                    throw new Exception();
                }
            }
            else if(reader.get() == '1'){
                list.add(new Element(null, Type.CONST, true));
                reader.read();
                out();
                if(reader.get() == '&'){
                    list.add(new Element(null, Type.AND, null));
                    reader.read();
                    out();
                    continue;
                }
                else if(reader.get() == '|'){
                    list.add(new Element(null, Type.OR, null));
                    reader.read();
                    out();
                    continue;
                }
                else if(reader.get() == '='){
                    reader.read();
                    out();
                    if(reader.get() == '>'){
                        list.add(new Element(null, Type.IMP, null));
                        reader.read();
                        out();
                        continue;
                    }
                    else {
                        System.out.println();
                        System.out.println("Ошибка при попытке считать операцию");
                        throw new Exception();
                    }
                }
                else if(reader.isInputState()){
                    if(reader.get() == ')' && skCount == 0 || reader.get() != ')') {
                        System.out.println();
                        System.out.println("Ошибка перехода после считывания переменной");
                        throw new Exception();
                    }
                }
                else{
                    if(skCount != 0){
                        System.out.println();
                        System.out.println("Не хватает закрывающей скобки");
                        throw new Exception();
                    }
                    System.out.println();
                    System.out.println("Считывание окончено ошибок нет");
                    return;
                }
            }
            else if(reader.get() == '0'){
                list.add(new Element(null, Type.CONST, false));
                reader.read();
                out();
                if(reader.get() == '&'){
                    list.add(new Element(null, Type.AND, null));
                    reader.read();
                    out();
                    continue;
                }
                else if(reader.get() == '|'){
                    list.add(new Element(null, Type.OR, null));
                    reader.read();
                    out();
                    continue;
                }
                else if(reader.get() == '='){
                    reader.read();
                    out();
                    if(reader.get() == '>'){
                        list.add(new Element(null, Type.IMP, null));
                        reader.read();
                        out();
                        continue;
                    }
                    else {
                        System.out.println("Ошибка при попытке считать операцию");
                        throw new Exception();
                    }
                }
                else if(reader.isInputState()){
                    if(reader.get() == ')' && skCount == 0 || reader.get() != ')') {
                        System.out.println();
                        System.out.println("Ошибка перехода после считывания переменной");
                        throw new Exception();
                    }
                }
                else{
                    if(skCount != 0){
                        System.out.println();
                        System.out.println("Не хватает закрывающей скобки");
                        throw new Exception();
                    }
                    System.out.println();
                    System.out.println("Считывание окончено ошибок нет");
                    return;
                }
            }
            else{
                System.out.println();
                System.out.println("ошибка в состоянии считывания скобки, отрицания, переменной или константы");
                throw new Exception();
            }
        }
        System.out.println();
        System.out.println("ошибка в состоянии считывания скобки, отрицания, переменной или константы");
        throw new Exception();
    }


    public ArrayList<Element> getRequest(){
        return list;
    }

    private void out(){
        if(reader.isInputState())
            System.out.print(reader.get());
    }

}
