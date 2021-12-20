import java.util.ArrayList;

/**
 * Created by root on 11.12.17.
 */
public class GenerateAutomaton {
    private ArrayList<Element> list = new ArrayList<>();
    private GenerateReader reader;
    private int skCount = 0;
    private String error = "";

    public GenerateAutomaton(){

    }

    public boolean start(String str) {
        try {
            reader = new GenerateReader(str);
            out();
            startState();
            System.out.println();
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
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
                while (reader.get() >= '0' && reader.get() <= '9' && reader.isInputState()){
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
                        error = "было считано '=' и ожидалось '>', этого символа далее не последовало";
                        throw new Exception();
                    }
                }
                else if(reader.isInputState()){
                    if(reader.get() == ')' && skCount == 0 || reader.get() != ')') {
                        System.out.println();
                        System.out.println("Ошибка перехода после считывания переменной");
                        error = "лишняя скобка или недостающая скобка";
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
                                error = "было считано '=' и ожидалось '>', этого символа далее не последовало";
                                throw new Exception();
                            }
                        }
                        else if(reader.get() != ')' || skCount == 0) {
                            System.out.println();
                            System.out.println("Ошибка при попытке перейти к считыванию операции");
                            error = "лишняя скобка или недостающая скобка";
                            throw new Exception();
                        }
                    }else{
                        if(skCount != 0){
                            System.out.println();
                            System.out.println("Не хватает закрывающей скобки");
                            error = "Не хватает закрывающей скобки";
                            throw new Exception();
                        }
                        System.out.println();
                        System.out.println("Считывание окончено ошибок нет");
                        return;
                    }
                }else{
                    System.out.println();
                    System.out.println("не найдена закрывающая скобка");
                    error = "Не хватает закрывающей скобки";
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
                        error = "было считано '=' и ожидалось '>', этого символа далее не последовало";
                        throw new Exception();
                    }
                }
                else if(reader.isInputState()){
                    if(reader.get() == ')' && skCount == 0 || reader.get() != ')') {
                        System.out.println();
                        System.out.println("Ошибка перехода после считывания переменной");
                        error = "лишняя скобка или недостающая скобка";
                        throw new Exception();
                    }
                }
                else{
                    if(skCount != 0){
                        System.out.println();
                        System.out.println("Не хватает закрывающей скобки");
                        error = "Не хватает закрывающей скобки";
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
                        error = "было считано '=' и ожидалось '>', этого символа далее не последовало";
                        throw new Exception();
                    }
                }
                else if(reader.isInputState()){
                    if(reader.get() == ')' && skCount == 0 || reader.get() != ')') {
                        System.out.println();
                        System.out.println("Ошибка перехода после считывания переменной");
                        error = "лишняя скобка или недостающая скобка";
                        throw new Exception();
                    }
                }
                else{
                    if(skCount != 0){
                        System.out.println();
                        System.out.println("Не хватает закрывающей скобки");
                        error = "Не хватает закрывающей скобки";
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
                error = "ошибка в состоянии считывания скобки, отрицания, переменной или константы";
                throw new Exception();
            }
        }
        System.out.println();
        System.out.println("ошибка в состоянии считывания скобки, отрицания, переменной или константы");
        error = "ошибка в состоянии считывания скобки, отрицания, переменной или константы";
        throw new Exception();
    }


    public ArrayList<Element> getRequest(){
        return list;
    }

    private void out(){
        if(reader.isInputState())
            System.out.print(reader.get());
    }

    public String getError() {
        return error;
    }
}
