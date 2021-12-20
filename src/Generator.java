import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Created by root on 06.01.18.
 */
public class Generator {
    private String currentName = "a";
    private String data;
    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<String> buffeer = new ArrayList<>();
    private PrintWriter pw;
    private static String min;
    private int n, k;

    public  Generator(PrintWriter pw){
        this.pw = pw;
    }

    public String generate(int n, int k){
        currentName = "a";
        this.n = n;
        this.k = k;
        list = new ArrayList<>();
        buffeer = new ArrayList<>();
        generateStartData();
        for(int i = 0; i < n; i++){
            generateStartReplases();
            pw.println(data);
            System.out.println(data);
            System.out.println("В этом выражении " + list.size() + " переменных");
            pw.println("В этом выражении " + list.size() + " переменных");
        }
        SortAutomaton sort = new SortAutomaton();
        try {
            sort.start(data);
        } catch (Exception e) {
            System.out.println("Автомат не смог проанализировать выражение");
            pw.println("Автомат не смог проанализировать выражение");
        }
        SortTree tree = new SortTree(sort.getRequest(), pw);
        min = tree.makeString();

        System.out.println("Создание усложняющих элементов");
        pw.println("Создание усложняющих элементов");
        for(int i = 0; i < k; i++){
            generatePhantomReplases();
            pw.println(data);
            System.out.println(data);
//            System.out.println("В этом выражении " + list.size() + " переменных");
//            pw.println("В этом выражении " + list.size() + " переменных");
        }
        return data;
    }

    private void generateStartData(){
        int random = (int) (Math.random()*100);
        String a = currentName;
        nextName();
        String b = currentName;
        nextName();
        list.add(a);
        list.add(b);
        if(random < 50) {
            data = "(" + a + "|" + b + ")";
        }else{
            data = "(" + a + "&" + b + ")";
        }
    }

    private void generateStartReplases(){
        while (list.size() > 0){
            int random = (int) (Math.random()*100);
            String a = currentName;
            nextName();
            String b = currentName;
            nextName();
//            String c = currentName;
//            nextName();
            buffeer.add(a);
            buffeer.add(b);
//            buffeer.add(c);
            if(random < 50) {
                myReplaceAll(list.get(0), "(" + a + "|" + b + ")");
            }else{
                myReplaceAll(list.get(0), "(" + a + "&" + b + ")");
            }
//            myReplaceAll(list.get(0), "((" + a + "&" + b + ")|(" + a + "&" + c + "))");
            list.remove(0);
        }
        while (buffeer.size() > 0){
            list.add(buffeer.get(0));
            buffeer.remove(0);
        }
    }

    private void generatePhantomReplases(){
        while (list.size() > 0){
            int random = (int) (Math.random()*100);
            if(random < 17){
                myReplaceAll(list.get(0), "!(!!!" + list.get(0) + "&!!!!!" + list.get(0) + ")");
            }else if(random < 33){
                myReplaceAll(list.get(0), "(" + list.get(0) + "&" + list.get(0)  + ")");
            }else if(random < 49){
                myReplaceAll(list.get(0), "(" + list.get(0) + "|!(" + list.get(0) + "=>" + list.get(0) + "))");
            }else if(random < 65){
                myReplaceAll(list.get(0), "!(!" + list.get(0) + "|!" + list.get(0)  + ")");
            }else if(random < 81){
                myReplaceAll(list.get(0), "(!" + list.get(0) + "=>0)");
            }else if(random < 90){
                String b = currentName;
                nextName();
                list.add(b);
                myReplaceAll(list.get(0), "(" + list.get(0) + "|" + list.get(0) + "&" + b +")");
            }else{
                myReplaceAll(list.get(0), "(!!" + list.get(0)  + ")");
            }
            buffeer.add(list.get(0));
            list.remove(0);
        }
        while (buffeer.size() > 0){
            list.add(buffeer.get(0));
            buffeer.remove(0);
        }
    }

    private void myReplaceAll(String replaced, String newSub){
        int start = 0;
        int index = data.indexOf(replaced, start);
        while (index != -1){
            if(data.charAt(index + replaced.length()) < '0' || data.charAt(index + replaced.length()) > '9'){
                data = data.substring(0, index) + newSub + data.substring(index + replaced.length());
            }
            start = index + newSub.length();
            index = data.indexOf(replaced, start);
        }
    }

    private void nextName(){
        if(currentName.charAt(0) < 'z'){
            currentName = currentName.replace(currentName.charAt(0), (char)(currentName.charAt(0)+1));
        }else{
            if(currentName.length() == 1)
                currentName = "a1";
            else{
                currentName = "a" + (Integer.parseInt(currentName.substring(1))+1);
            }
        }
    }

    public static String getMin() {
        return min;
    }
}
