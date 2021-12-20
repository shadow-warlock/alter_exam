import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class SortTree {
    private Node root;
    private PrintWriter pw;

    void out(){
        out(root);
    }

    public String makeString(){
        return makeString(new StringBuilder(), root);
    }

    private String makeString(StringBuilder str, Node e){
        if(e == null)
            return null;
        if (e.type != Type.NOT) {
            if ((e.leftOperand != null || e.rightOperand != null) && e.parent != null && e.parent.type != e.type){
                str.append("(");
            }
            if (e.leftOperand != null) {
                makeString(str, e.leftOperand);
            }
            str.append(e);
            if (e.rightOperand != null) {
                makeString(str, e.rightOperand);
            }
            if ((e.leftOperand != null || e.rightOperand != null) && e.parent != null && e.parent.type != e.type){
                str.append(")");
            }
        }else{
            str.append("!");
            makeString(str,e.leftOperand);
        }
        if(e.parent == null)
            return  str.toString();
        return null;
    }

    private void out(Node e) {
        if(e == null)
            return;
        if (e.type != Type.NOT) {
            if ((e.leftOperand != null || e.rightOperand != null) && e.parent != null && e.parent.type != e.type){
                System.out.print("(");
                pw.print("(");
            }
            if (e.leftOperand != null) {
                out(e.leftOperand);
            }
            pw.print(e);
            System.out.print(e);
            if (e.rightOperand != null) {
                out(e.rightOperand);
            }
            if ((e.leftOperand != null || e.rightOperand != null) && e.parent != null && e.parent.type != e.type){
                System.out.print(")");
                pw.print(")");
            }
        }else{
            System.out.print("!");
            pw.print("!");
            out(e.leftOperand);
        }
        if(e.parent == null){
            System.out.println();
            pw.println();
        }
        pw.flush();
    }

    private static boolean isOperation(Type type){
        return (type == Type.IMP) || (type == Type.OR) || (type == Type.AND) || (type == Type.NOT);
    }

    private static boolean operationPriority(Type currOperation, Type operation){ //возвращает истину,
        if(operation == null) {
            return true;
        }
       if(!isOperation(currOperation)){                                      //если первый параметр менее приоритетен,
           return false;                                                     //чем второй
       }
       if(operation==Type.NOT && currOperation != Type.NOT){
           return true;
       }
       if(currOperation == Type.IMP){
           return true;
       }
       if((currOperation==Type.OR)&&(operation!=Type.IMP)){
           return true;
       }
       if((currOperation==Type.AND)&&(operation!=Type.OR)&&(operation!=Type.IMP)){
           return true;
       }
       return false;
    }

    private Node add(ArrayList<Element> logStr,Node parent){
        killScobka(logStr);
        Type type = logStr.get(0).type;
        if(logStr.size()<=2){
            int vInd = logStr.size()-1;
            Node op = new Node(logStr.get(vInd).value,null,null,logStr.get(vInd).type,logStr.get(vInd).name,parent);
            if(type==Type.NOT){
                Node not = new Node(null,op,null,Type.NOT,null,parent);
                op.parent = not;
                return not;
            }
            else{
                return op;
            }
        }
        else{
            int scobe=0;
            int currLowPriority = -1;
            Type currLowType = null;
            for (int i = 0; i < logStr.size(); i++) {
                type = logStr.get(i).type;
                if(currLowPriority!=-1){
                    currLowType = logStr.get(currLowPriority).type;
                }
                if (logStr.get(i).type == Type.OPEN) {
                    scobe++;
                    continue;
                }
                if (logStr.get(i).type == Type.CLOSE) {
                    scobe--;
                    continue;
                }
                if (scobe == 0) {
                    if (isOperation(type)) {
                        if(operationPriority(type,currLowType)){
                            currLowPriority=i;
                        }
                    }
                }
            }
            ArrayList<Element> logRight = new ArrayList<>(logStr.subList(currLowPriority+1,logStr.size()));
            if(currLowType==Type.NOT){
                Node curr = new Node(null,null,null,currLowType,null,parent);
                curr.leftOperand = add(logRight, curr);
                return curr;
            }

            else{
                ArrayList<Element> logLeft = new ArrayList<>(logStr.subList(0,currLowPriority));
                Node curr = new Node(null,null,null,currLowType,null,parent);
                Node child1 = add(logLeft,curr);
                Node child2 = add(logRight,curr);
                curr.leftOperand = child1;
                child1.parent = curr;
                curr.rightOperand = child2;
                child2.parent = curr;
                return curr;
            }
        }
    }


    private void createSort(Node main){
        if(main.type == Type.OR || main.type == Type.AND) {
            ArrayList<Node> sortList = new ArrayList<>();
            addToSort(main, sortList);
            sortList.sort(new Comparator<Node>() {
                @Override
                public int compare(Node o1, Node o2) {
                    return o1.compareTo(o2);
                }
            });
            Node subTree = makeSubTree(sortList, main);
            if (main.parent != null) {
                if (main.parent.leftOperand == main) {
                    main.parent.leftOperand = subTree;
                } else {
                    main.parent.rightOperand = subTree;
                }
            } else {
                root = subTree;
            }
        }
    }

    private Node makeSubTree(List<Node> sortList, Node main){
        Node subTree = new Node(null, null, null, main.type, null, main.parent);
        Node current = subTree;
        for (int i = 0; i < sortList.size(); i++) {
            if(i < sortList.size()-1) {
                current.leftOperand = sortList.get(i);
                current.leftOperand.parent = current;
                current.rightOperand = new Node(null, null, null, main.type, null, current);
                current = current.rightOperand;
            }else {
                sortList.get(i).parent = current.parent;
                if(current.parent != null){
                    if(current.parent.leftOperand == current){
                        current.parent.leftOperand = sortList.get(i);
                    }else{
                        current.parent.rightOperand = sortList.get(i);
                    }
                }else{
                    root = sortList.get(i);
                }
            }
        }
        return subTree;
    }

    private void addToSort(Node added, ArrayList<Node> list){
        if(added.leftOperand.type == added.type){
            addToSort(added.leftOperand, list);
        }else{
            if(added.leftOperand.type == Type.OR || added.leftOperand.type == Type.AND)
                createSort(added.leftOperand);
            list.add(added.leftOperand);
        }
        if(added.rightOperand.type == added.type){
            addToSort(added.rightOperand, list);
        }else{
            if(added.rightOperand.type == Type.OR || added.rightOperand.type == Type.AND)
                createSort(added.rightOperand);
            list.add(added.rightOperand);
        }

    }

    private static void killScobka(ArrayList<Element> arrayList){
        while(arrayList.size() > 0 && arrayList.get(0).type == Type.OPEN){
            int skLvl = 0;
            for (Element o : arrayList){
                if(o.type == Type.CLOSE)
                    skLvl--;
                if(o.type == Type.OPEN)
                    skLvl++;
                if(skLvl == 0 && arrayList.indexOf(o) != arrayList.size()-1) {
                    return;
                }
            }
            arrayList.remove(arrayList.size()-1);
            arrayList.remove(0);
        }
    }

    public SortTree(ArrayList<Element> logStr, PrintWriter pw){
        this.pw = pw;
        root = add(logStr,root);
        System.out.println("****************************************************************************************");
        pw.println("****************************************************************************************");
        System.out.println("Выражение введено в сортировочное дерево, оно только сортирует операнды простого выражения генератора для удобства сравнения:");
        pw.println("Выражение введено в сортировочное дерево, оно только сортирует операнды простого выражения генератора для удобства сравнения:");
        System.out.println("ЭТО ВЫРАЖЕНИЕ НАДО СРАВНИТЬ С РЕЗУЛЬТАТОМ РАБОТЫ ПРОГРАММЫ");
        pw.println("ЭТО ВЫРАЖЕНИЕ НАДО СРАВНИТЬ С РЕЗУЛЬТАТОМ РАБОТЫ ПРОГРАММЫ");
        createSort(root);
        out();
        System.out.println("****************************************************************************************");
        pw.println("****************************************************************************************");
        pw.flush();
    }

    private class Node implements Comparable<Node>{
        Boolean value;
        Node parent;
        Node leftOperand;
        Node rightOperand;
        Type type;
        String name;

        protected Node clone(){
            return new Node(value, leftOperand == null ? null : leftOperand.clone(), rightOperand == null? null : rightOperand.clone(), type, name, parent);
        }

        Node(Boolean value,Node leftNode,Node rightNode,Type type,String name, Node parent){
            this.parent=parent;
            this.value = value;
            this.leftOperand = leftNode;
            this.rightOperand = rightNode;
            this.type = type;
            this.name = name;
        }
        @Override
        public String toString() {
            String request = "";
            if(value != null)
                request += value? "1":"0";
            else if (name != null)
                request += name;
            else{
                request = typeToOut(type);
            }
            return request;
        }

        private String typeToOut(Type t){
            if(t == Type.OR)
                return "|";
            if(t == Type.AND)
                return "&";
            if(t == Type.IMP)
                return "=>";
            return "UNKNOWN";
        }

        @Override
        public int compareTo(Node o) {
            if(this.type == Type.NOT && o.type != Type.NOT){
                int comp = this.leftOperand.compareTo(o);
                if(comp == 0)
                    return 1;
                else
                    return comp;
            }
            if(this.type != Type.NOT && o.type == Type.NOT){
                int comp = this.compareTo(o.leftOperand);
                if(comp == 0)
                    return -1;
                else
                    return comp;
            }
            if(this.type == Type.NOT && o.type == Type.NOT){
                return this.leftOperand.compareTo(o.leftOperand);
            }
            if(this.type.ordinal() < o.type.ordinal())
                return -1;
            if(this.type.ordinal() > o.type.ordinal())
                return 1;
            //////////////// for =
            if(this.type == Type.CONST){
                if(this.value)
                    return o.value ? 0 : 1;
                else
                    return o.value ? -1 : 0;
            }
            if(this.type == Type.VALUE){
                return this.name.compareTo(o.name);
            }
            int left = leftCompare(o);
            if(left == 0)
                return rightCompare(o);
            else
                return left;
        }

        private int leftCompare(Node o){
            if(this.leftOperand != null && o.leftOperand != null){
                return this.leftOperand.compareTo(o.leftOperand);
            }else if(this.leftOperand == null && o.leftOperand != null){
                return -1;
            }else if(this.leftOperand != null && o.leftOperand == null){
                return 1;
            }else{
                return 0;
            }
        }
        private int rightCompare(Node o){
            if(this.rightOperand != null && o.rightOperand != null){
                return this.rightOperand.compareTo(o.rightOperand);
            }else if(this.rightOperand == null && o.rightOperand != null){
                return -1;
            }else if(this.rightOperand != null && o.rightOperand == null){
                return 1;
            }else{
                return 0;
            }
        }
    }
}

