
/**
 * Created by root on 08.12.17.
 */
public class Input {
    public static void main(String[] args) throws Exception {
        new MyFrame();
//        PrintWriter pw = null;
//        try {
//            pw = new PrintWriter(new File("output.txt"));
//        } catch (FileNotFoundException e) {
//            System.out.println("ошибка в файле вывода");
//        }
//        System.out.println("Работа студентов 6374 группы Макогона Николая и Тимофея Гераймовича");
//        System.out.println("Упрощение логических выражений включающих импликацию, отрицание, И, ИЛИ, константы и переменные");
//        System.out.println("Пример выражения !(c=>e)=>(!(a|b=>d)&!(z|1))");
//        System.out.println("Программа сначала убирает импликацию");
//        System.out.println("Затем убирает повторяющиеся отрицания");
//        System.out.println("Затем убирает отрицания всего кроме переменных (при помощи инверсии поддеревьев)");
//        System.out.println("Затем убирает константы или выражение упростится до констант");
//        System.out.println("Затем программа раскрывает скобки типа (a|b)&c = a&b|b&c");
//        System.out.println("после чего все подузлы выражения сортируются");
//        System.out.println("Далее в цикле применяются закон идемпотентности, закон противоречия, закон исключения третьего, поглощение, склеивание и сортировка");
//        System.out.println("переменные при вводе состоят из одной строчной буквы и любого набора цифр, пробелы вводить нельзя");
//        System.out.println("остальное видно из примера");
//        System.out.println("введие ваше выражение или введите капсом без ковычек слово \"FILE\" - тогда программа считает выражение из файла input.txt");
//        System.out.println("Весь вывод программы попадет в файл output.txt");
//        Automaton automaton = new Automaton(pw);
//        automaton.start();
//        ArrayList<Element> list = automaton.getRequest();
////        for (Element e:list) {
////            System.out.println(e);
////        }
//        LogicTree l = new LogicTree(list, pw);
//        l.out();
//        if (Generator.getMin() != null){
//            System.out.println("Под этой надписью исходное");
//            pw.println("Под этой надписью исходное");
//            System.out.println(Generator.getMin());
//            pw.println(Generator.getMin());
//            System.out.println(Generator.getMin().compareTo(l.makeString()) == 0 ? "Совпало с исходным": "Не совпало с исходным ;-(");
//            pw.println(Generator.getMin().compareTo(l.makeString()) == 0 ? "Совпало с исходным": "Не совпало с исходным ;-(");
//            pw.flush();
//        }
    }
}
