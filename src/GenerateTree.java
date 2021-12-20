import java.awt.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class GenerateTree {
    private Node root;
    private StringBuilder sb;

    void out(){
        out(root);
    }

    private void out(Node e) {
        if(e == null)
            return;
        if (e.type != Type.NOT) {
            if ((e.leftOperand != null || e.rightOperand != null) && e.parent != null && e.parent.type != e.type){
                System.out.print("(");
                sb.append("(");
            }
            if (e.leftOperand != null) {
                out(e.leftOperand);
            }
            sb.append(e);
            System.out.print(e);
            if (e.rightOperand != null) {
                out(e.rightOperand);
            }
            if ((e.leftOperand != null || e.rightOperand != null) && e.parent != null && e.parent.type != e.type){
                System.out.print(")");
                sb.append(")");
            }
        }else{
            System.out.print("!");
            sb.append("!");
            out(e.leftOperand);
        }
        if(e.parent == null){
            System.out.println();
            sb.append("\n");
        }
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
            return str.toString();
        return null;
    }

    private static boolean isOperation(Type type){
        return (type == Type.IMP) || (type == Type.OR) || (type == Type.AND) || (type == Type.NOT);
    }

    private static boolean operationPriority(Type currOperation, Type operation){ //возвращает истину,
        if(operation == null) {
            return true;
        }
        if(!isOperation(currOperation)){ //если первый параметр менее приоритетен,
            return false; //чем второй
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

    private void killMoreThenOneNot(Node current){
        if(current == null)
            return;
        if(current.parent != null && current.parent.type == Type.NOT && current.type == Type.NOT){
            if(current.parent.parent != null){
                if(current.parent == current.parent.parent.leftOperand){
                    current.parent.parent.leftOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent.parent;
                }else{
                    current.parent.parent.rightOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent.parent;
                }
            }else{
                root = current.leftOperand;
                current.leftOperand.parent = null;
            }
        }
        killMoreThenOneNot(current.leftOperand);
        killMoreThenOneNot(current.rightOperand);
    }

    private void killOrAndWithConst(Node current, boolean isOr){
        if(current == null)
            return;
        killOrAndWithConst(current.leftOperand, isOr);
        killOrAndWithConst(current.rightOperand, isOr);
        if((current.type == Type.OR && isOr) || (current.type == Type.AND && !isOr)){
            if(current.leftOperand.type == Type.CONST){
                if(current.leftOperand.value == isOr){
                    current.type = Type.CONST;
                    current.value = isOr;
                    current.leftOperand = null;
                    current.rightOperand = null;
                    killOrAndWithConst(current.parent, true);
                    killOrAndWithConst(current.parent, false);
                }else{
                    current.name = current.rightOperand.name;
                    current.type = current.rightOperand.type;
                    current.value = current.rightOperand.value;
                    current.leftOperand = current.rightOperand.leftOperand;
                    if(current.leftOperand != null)
                        current.leftOperand.parent = current;
                    current.rightOperand = current.rightOperand.rightOperand;
                    if(current.rightOperand != null)
                        current.rightOperand.parent = current;
                    killOrAndWithConst(current.parent, true);
                    killOrAndWithConst(current.parent, false);

                }
            }else if(current.rightOperand.type == Type.CONST) {
                if (current.rightOperand.value == isOr) {
                    current.type = Type.CONST;
                    current.value = isOr;
                    current.leftOperand = null;
                    current.rightOperand = null;
                    killOrAndWithConst(current.parent, true);
                    killOrAndWithConst(current.parent, false);
                } else {
                    current.name = current.leftOperand.name;
                    current.type = current.leftOperand.type;
                    current.value = current.leftOperand.value;
                    current.rightOperand = current.leftOperand.rightOperand;
                    if(current.rightOperand != null)
                        current.rightOperand.parent = current;
                    current.leftOperand = current.leftOperand.leftOperand;
                    if(current.leftOperand != null)
                        current.leftOperand.parent = current;
                    killOrAndWithConst(current.parent, true);
                    killOrAndWithConst(current.parent, false);
                }
            }
        }
    }

    private void killImp(Node
                                 current){
        if(current!=null){
            killImp(current.leftOperand);
            killImp(current.rightOperand);
            if(current.type==Type.IMP){
                current.leftOperand= new Node(null,current.leftOperand,null,Type.NOT,null,current);
                current.leftOperand.leftOperand.parent = current.leftOperand;
                current.type = Type.OR;
            }
        }
    }
    //!(c=>e)=>(!(a|b=>d)&!(z|1))
    private void killAllNot(Node current){
        if(current == null || current.type == Type.VALUE)
            return;
        if(current.type == Type.NOT){
            if(current.parent != null){
                if(current.parent.leftOperand == current){
                    current.parent.leftOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent;
                }else{
                    current.parent.rightOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent;
                }
                makeAllChildrenNot(current.leftOperand);
            }else{
                root = current.leftOperand;
                current.leftOperand.parent = null;
                makeAllChildrenNot(root);
            }
        }
        killAllNot(current.leftOperand);
        killAllNot(current.rightOperand);
    }

    private boolean killEquals(Node current){
        boolean ret;
        if(current == null)
            return false;
        ret = killEquals(current.leftOperand);
        ret = ret | killEquals(current.rightOperand);
        if(current.type == Type.OR || current.type == Type.AND){
            if(current.rightOperand.type == current.type){
                if(current.rightOperand.leftOperand.compareTo(current.leftOperand) == 0){
                    current.rightOperand = current.rightOperand.rightOperand;
                    current.rightOperand.parent = current;
                    killOrAndWithConst(current, true);
                    killOrAndWithConst(current, false);
                    return true;
                }else if(current.rightOperand.leftOperand.type == Type.NOT){
                    if(current.rightOperand.leftOperand.leftOperand.compareTo(current.leftOperand) == 0){
                        current.rightOperand = current.rightOperand.rightOperand;
                        current.rightOperand.parent = current;
                        current.leftOperand.value = current.type == Type.OR;
                        current.leftOperand.type = Type.CONST;
                        current.leftOperand.rightOperand = null;
                        current.leftOperand.leftOperand = null;
                        killOrAndWithConst(current, true);
                        killOrAndWithConst(current, false);
                        return true;
                    }
                }
            }else{
                ret = ret | findAndKillEquals(current.leftOperand, current.rightOperand, current);
            }
        }
        return ret;
    }

    private boolean findAndKillEquals(Node n1, Node n2, Node parent){
        if(n1.type != Type.NOT && n2.type != Type.NOT || n1.type == Type.NOT && n2.type == Type.NOT) {
            if (n1.compareTo(n2) == 0) {
                parent.name = n1.name;
                parent.type = n1.type;
                parent.value = n1.value;
                parent.leftOperand = n1.leftOperand;
                if (parent.leftOperand != null)
                    parent.leftOperand.parent = parent;
                parent.rightOperand = n2.rightOperand;
                if (parent.rightOperand != null)
                    parent.rightOperand.parent = parent;
                killOrAndWithConst(parent, true);
                killOrAndWithConst(parent, false);
                return true;
            }
        }else if(n1.type == Type.NOT && n2.type != Type.NOT){
            if (n1.leftOperand.compareTo(n2) == 0) {
                parent.value = parent.type == Type.OR;
                parent.type = Type.CONST;
                parent.leftOperand = null;
                parent.rightOperand = null;
                killOrAndWithConst(parent.parent, true);
                killOrAndWithConst(parent.parent, false);
                return true;
            }
        }else if(n1.type != Type.NOT && n2.type == Type.NOT){
            if (n1.compareTo(n2.leftOperand) == 0) {
                parent.value = parent.type == Type.OR;
                parent.type = Type.CONST;
                parent.leftOperand = null;
                parent.rightOperand = null;
                killOrAndWithConst(parent.parent, true);
                killOrAndWithConst(parent.parent, false);
                return true;
            }
        }
        return false;
    }

    private boolean absorption(Node main){
        boolean ret = false;
        if(main.type == Type.OR || main.type == Type.AND){
            ret = absorption(main.leftOperand);
            ret = ret | absorption(main.rightOperand);
        }
        if(main.type == Type.OR) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.AND)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    boolean finded = false;
                    if(i !=
                            j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.AND)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        if(iList.size() < jList.size()){
                            for(int k = 0; k < iList.size(); k++){
                                finded = false;
                                for(int g = 0; g < jList.size(); g++) {
                                    if(iList.get(k).compareTo(jList.get(g)) == 0) {
                                        finded = true;
                                    }
                                }
                                if (!finded)
                                    break;
                            }
                        }else{
                            finded = false;
                        }
                        if(finded){
                            findAndKill(main, valuesList.get(j));
                            return true;
                        }
                    }
                }
            }
        }
        if(main.type == Type.AND) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.OR)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    boolean finded = false;
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.OR)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        if(iList.size() < jList.size()){
                            for(int k = 0; k < iList.size(); k++){
                                finded = false;
                                for(int g = 0; g < jList.size(); g++) {
                                    if(iList.get(k).compareTo(jList.get(g)) == 0) {
                                        finded = true;
                                    }
                                }
                                if (!finded)
                                    break;
                            }
                        }else{
                            finded = false;
                        }
                        if(finded){
                            findAndKill(main, valuesList.get(j));
                            return true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean kyb(Node main){
        boolean ret = false;
        if(main.type == Type.OR || main.type == Type.AND){
            ret = kyb(main.leftOperand);
            ret = ret | kyb(main.rightOperand);
        }
        if(main.type == Type.OR) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            if(valuesList.size()<3){

                return ret;
            }
            System.out.println(valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.AND)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.AND)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        for(int k = 0; k < valuesList.size(); k++){
                            boolean finded = false;
                            if(k != j && k != i){
                                ArrayList<Node> kList = new ArrayList<>();
                                if(valuesList.get(k).type == Type.AND)
                                    addTo(valuesList.get(k), kList);
                                else
                                    kList.add(valuesList.get(k));
                                int not = 0, not1 = 0, not2 = 0, not3 = 0;
                                ArrayList<Node> list1 = check1(iList, jList);
                                if(list1.isEmpty()) {
                                    list1 = check2(iList, jList);
                                    not++;
                                    not1 = 1;
                                }
                                ArrayList<Node> list2 = check1(iList, kList);
                                if(list2.isEmpty()) {
                                    list2 = check2(iList, kList);
                                    not++;
                                    not2 = 1;
                                }
                                ArrayList<Node> list3 = check1(jList, kList);
                                if(list3.isEmpty()) {
                                    list3 = check2(jList, kList);
                                    not++;
                                    not3 = 1;
                                }
                                if(!list1.equals(list2) && !list2.equals(list3) && !list1.equals(list3)){
                                    if(not == 1){
                                        if(not1 == 1){
                                            findAndKill(main, valuesList.get(k));
                                        }else if(not2 == 1)
                                            findAndKill(main, valuesList.get(j));
                                        else
                                            findAndKill(main, valuesList.get(i));
                                        return true;
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        if(main.type == Type.AND) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.OR)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.OR)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        for(int k = 0; k < valuesList.size(); k++){
                            boolean finded = false;
                            if(k != j && k != i){
                                ArrayList<Node> kList = new ArrayList<>();
                                if(valuesList.get(k).type == Type.OR)
                                    addTo(valuesList.get(k), kList);
                                else
                                    kList.add(valuesList.get(k));
                                int not = 0, not1 = 0, not2 = 0, not3 = 0;
                                ArrayList<Node> list1 = check1(iList, jList);
                                if(list1.isEmpty()) {
                                    list1 = check2(iList, jList);
                                    not++;
                                    not1 = 1;
                                }
                                ArrayList<Node> list2 = check1(iList, kList);
                                if(list2.isEmpty()) {
                                    list2 = check2(iList, kList);
                                    not++;
                                    not2 = 1;
                                }
                                ArrayList<Node> list3 = check1(jList, kList);
                                if(list3.isEmpty()) {
                                    list3 = check2(jList, kList);
                                    not++;
                                    not3 = 1;
                                }
                                if(!list1.equals(list2) && !list2.equals(list3) && !list1.equals(list3)){
                                    if(not == 1){
                                        if(not1 == 1){
                                            findAndKill(main, valuesList.get(k));
                                        }else if(not2 == 1)
                                            findAndKill(main, valuesList.get(j));
                                        else
                                            findAndKill(main, valuesList.get(i));
                                        return true;
                                    }else if(not == 2){

                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private ArrayList<Node> check1(List<Node> l1, List<Node> l2){
        ArrayList<Node> ret = new ArrayList<>();
        for (int i = 0; i < l1.size(); i++){
            for (int j = 0; j < l2.size(); j++) {
                if (l1.get(i).compareTo(l2.get(j)) == 0){
                    ret.add(l1.get(i));
                }
            }
        }
        return ret;
    }

    private ArrayList<Node> check2(List<Node> l1, List<Node> l2){
        ArrayList<Node> ret = new ArrayList<>();
        for (int i = 0; i < l1.size(); i++){
            for (int j = 0; j < l2.size(); j++) {
                Node not1 = null, not2 = null;
                if(l1.get(i).type == Type.NOT && l2.get(j).type != Type.NOT){
                    not1 = l1.get(i).leftOperand;
                    not2 = l2.get(j);
                }
                if(l1.get(i).type != Type.NOT && l2.get(j).type == Type.NOT){
                    not1 = l1.get(i);
                    not2 = l2.get(j).leftOperand;
                }
                if(not1 == null && not2 == null)
                    continue;
                if (not1.compareTo(not2) == 0){
                    ret.add(l1.get(i));
                }
            }
        }
        return ret;
    }

    private void addTo(Node added, ArrayList<Node> list){
        if(added.leftOperand.type == added.type){
            addTo(added.leftOperand, list);
        }else{
            list.add(added.leftOperand);
        }
        if(added.rightOperand.type == added.type){
            addTo(added.rightOperand, list);
        }else{
            list.add(added.rightOperand);
        }
    }

    private boolean absorptionElementary(Node main){
        boolean ret = false;
        if(main.type == Type.OR || main.type == Type.AND){
            ret = absorptionElementary(main.leftOperand);
            ret = ret | absorptionElementary(main.rightOperand);
        }
        if(main.type == Type.OR) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.AND)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    boolean finded = false;
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.AND)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));

                        Node not = null;
                        if(iList.size() < jList.size()){
                            for(int k = 0; k < iList.size(); k++) {
                                if (iList.get(k).type == Type.VALUE)
                                    not = new Node(null, iList.get(0).clone(), null, Type.NOT, null, null);
                                else if (iList.get(k).type == Type.NOT)
                                    not = iList.get(k).leftOperand.clone();

                                else {
                                    not = iList.get(k).clone();
                                    getNot(not);
                                }
                                for (int g = 0; g < jList.size(); g++) {
                                    if (not.compareTo(jList.get(g)) == 0) {
// System.out.println("-----------------------------------");
// System.out.println(valuesList);
// System.out.println(iList);
// System.out.println(jList);
                                        finded = true;
                                    }
                                }
                                if (!finded)
                                    break;
                            }
                        }else{
                            finded = false;
                        }
                        if(finded){
                            findAndKill(valuesList.get(j), not);
                            findAndKill(valuesList.get(j), not);
                            return true;
                        }
                    }
                }
            }
        }
        if(main.type == Type.AND) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.OR)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    boolean finded = false;
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.OR)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        Node not = null;
                        if(iList.size() < jList.size()){
                            for(int k = 0; k < iList.size(); k++) {
                                if (iList.get(k).type == Type.VALUE)
                                    not = new Node(null, iList.get(k).clone(), null, Type.NOT, null, null);
                                else if (iList.get(k).type == Type.NOT)
                                    not = iList.get(k).leftOperand.clone();

                                else {
                                    not = iList.get(k).clone();
                                    getNot(not);
                                }
                                for (int g = 0; g < jList.size(); g++) {
                                    if (not.compareTo(jList.get(g)) == 0) {
                                        finded = true;
// System.out.println("***************************************");
// System.out.println(valuesList);
// System.out.println(iList);
// System.out.println(jList);
                                    }
                                }
                                if (!finded)
                                    break;
                            }
                        }else{
                            finded = false;
                        }
                        if(finded){
                            findAndKill(valuesList.get(j), not);
                            return true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean closeScobe(Node main){
        if(main == null)
            return false;
        boolean ret = false;
        if(main.type == Type.OR || main.type == Type.AND){
            ret = closeScobe(main.leftOperand);
            ret = ret | closeScobe(main.rightOperand);
        }

        if(main.type == Type.OR) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.AND)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    if(i != j && valuesList.get(i).compareTo(valuesList.get(j)) != 0){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.AND)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        if(iList.size() <= jList.size() && iList.size() > 1){
                            for(int k = 0; k < iList.size(); k++){
                                for(int g = 0; g < jList.size(); g++) {
                                    if(iList.get(k).compareTo(jList.get(g)) == 0) {
// System.out.println("-----------------------------");
// System.out.println(valuesList);
// System.out.println(iList);
// System.out.println(jList);
                                        Node and = new Node(null, null, null, Type.AND, null, null);
                                        Node outSkobe = iList.get(k);
                                        iList.remove(k);
                                        jList.remove(g);
                                        and.leftOperand = outSkobe;
                                        outSkobe.parent = and;
                                        Node or = new Node(null, null, null, Type.OR, null, and);
                                        if(iList.size() > 1)
                                            or.leftOperand = makeSubTree(iList, valuesList.get(i));
                                        else
                                            or.leftOperand = iList.get(0);
                                        or.leftOperand.parent = or;
                                        if(jList.size() > 1)
                                            or.rightOperand = makeSubTree(jList, valuesList.get(j));
                                        else
                                            or.rightOperand = jList.get(0);
                                        or.rightOperand.parent = or;
                                        and.rightOperand = or;
                                        or.parent = and;
                                        Node newNode = new Node(null, main, and, main.type, null, main.parent);
                                        if(main.parent != null){
                                            if(main.parent.leftOperand == main){
                                                main.parent.leftOperand = newNode;
                                            }else{
                                                main.parent.rightOperand = newNode;
                                            }
                                        }else{
                                            root = newNode;
                                            newNode.parent = null;
                                        }
                                        and.parent = newNode;
                                        main.parent = newNode;
// System.out.println("Hello");
// System.out.println("main parent = " + main.parent);
// System.out.println("new node parent = " + newNode.parent);
                                        findAndKill(newNode, valuesList.get(i));
                                        findAndKill(newNode, valuesList.get(j));
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(main.type == Type.AND) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.OR)
                    addTo(valuesList.get(i), iList);
                else
                    iList.add(valuesList.get(i));
                for(int j = 0; j < valuesList.size(); j++){
                    if(i != j && valuesList.get(i).compareTo(valuesList.get(j)) != 0){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.OR)
                            addTo(valuesList.get(j), jList);
                        else
                            jList.add(valuesList.get(j));
                        if(iList.size() <= jList.size() && iList.size() >
                                1){
                            for(int k = 0; k < iList.size(); k++){
                                for(int g = 0; g < jList.size(); g++) {
                                    if(iList.get(k).compareTo(jList.get(g)) == 0) {
// System.out.println("******************************");
// System.out.println(valuesList);
// System.out.println(iList);
// System.out.println(jList);
                                        Node and = new Node(null, null, null, Type.OR, null, null);
                                        Node outSkobe = iList.get(k);
                                        iList.remove(k);
                                        jList.remove(g);
                                        and.leftOperand = outSkobe;
                                        outSkobe.parent = and;
                                        Node or = new Node(null, null, null, Type.AND, null, and);
                                        if(iList.size() > 1)
                                            or.leftOperand = makeSubTree(iList, valuesList.get(i));
                                        else
                                            or.leftOperand = iList.get(0);
                                        or.leftOperand.parent = or;
                                        if(jList.size() > 1)
                                            or.rightOperand = makeSubTree(jList, valuesList.get(j));
                                        else
                                            or.rightOperand = jList.get(0);
                                        or.rightOperand.parent = or;
                                        and.rightOperand = or;
                                        or.parent = and;

                                        Node newNode = new Node(null, main, and, main.type, null, main.parent);
                                        if(main.parent != null){
                                            if(main.parent.leftOperand == main){
                                                main.parent.leftOperand = newNode;
                                            }else{
                                                main.parent.rightOperand = newNode;
                                            }
                                        }else{
                                            root = newNode;
                                        }
                                        and.parent = newNode;
                                        main.parent = newNode;
                                        findAndKill(main, valuesList.get(i));
                                        findAndKill(main, valuesList.get(j));
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private boolean bonding(Node main){
        if(main.type == Type.OR || main.type == Type.AND){
            bonding(main.leftOperand);
            bonding(main.rightOperand);
        }
        if(main.type == Type.OR) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.AND)
                    addToSort(valuesList.get(i), iList);
                for(int j = 0; j < valuesList.size(); j++){
                    boolean finded = true;
                    Node killed = null;
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.AND)
                            addToSort(valuesList.get(j), jList);
                        if(iList.size() == jList.size()){
                            for(int k = 0; k < iList.size(); k++){
                                if(iList.get(k).compareTo(jList.get(k)) != 0){
                                    if(!finded){
                                        killed = null;
                                        break;
                                    }
                                    killed = new Node(null, iList.get(k), null, Type.NOT, null, null);
                                    finded = false;
                                }
                            }
                        }
                        if(!finded && killed != null){
                            if(find(valuesList.get(j), killed)) {
                                findAndKill(valuesList.get(j), killed);
                                findAndKill(valuesList.get(i), killed.leftOperand);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        if(main.type == Type.AND) {
            ArrayList<Node> valuesList = new ArrayList<>();
            addTo(main, valuesList);
            for(int i = 0; i < valuesList.size(); i++){
                ArrayList<Node> iList = new ArrayList<>();
                if(valuesList.get(i).type == Type.OR)
                    addToSort(valuesList.get(i), iList);
                for(int j = 0; j < valuesList.size(); j++){
                    boolean finded = true;
                    Node killed = null;
                    if(i != j){
                        ArrayList<Node> jList = new ArrayList<>();
                        if(valuesList.get(j).type == Type.OR)
                            addToSort(valuesList.get(j), jList);
                        if(iList.size() == jList.size()){
                            for(int k = 0; k < iList.size(); k++){
                                if(iList.get(k).compareTo(jList.get(k)) != 0){
                                    if(!finded){
                                        killed = null;
                                        break;
                                    }
                                    killed = new Node(null, iList.get(k), null, Type.NOT, null, null);
                                    finded = false;
                                }
                            }
                        }
                        if(!finded && killed != null){
                            if(find(valuesList.get(j), killed)) {
                                findAndKill(valuesList.get(j), killed);
                                findAndKill(valuesList.get(i), killed.leftOperand);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean find(Node main, Node killed){
        boolean ret = false;
        if(main != null && (main.type != Type.NOT || killed.type == Type.NOT)){
            ret = find(main.leftOperand, killed);
            ret = ret | find(main.rightOperand, killed);
            if(main.compareTo(killed) == 0) {
                ret = true;
            }
        }
        return ret;
    }

    private boolean findAndKill(Node main, Node killed){
        boolean ret = false;
        if(main != null && (main.type != Type.NOT || killed.type == Type.NOT)){
            ret = findAndKill(main.leftOperand, killed);
            ret = ret | findAndKill(main.rightOperand, killed);
            if(main.compareTo(killed) == 0){
                if(main.parent != null){
                    if(main.parent.leftOperand == main){
                        main.parent.rightOperand.parent = main.parent.parent;
                        if(main.parent.parent != null) {
                            if(main.parent.parent.leftOperand == main.parent){
                                main.parent.parent.leftOperand = main.parent.rightOperand;
                            }
                            else{
                                main.parent.parent.rightOperand = main.parent.rightOperand;
                            }
                        }
                        else{
                            root = main.parent.rightOperand;
                        }
                    }else{
                        main.parent.leftOperand.parent = main.parent.parent;
                        if(main.parent.parent != null) {
                            if(main.parent.parent.leftOperand == main.parent)
                                main.parent.parent.leftOperand = main.parent.leftOperand;
                            else
                                main.parent.parent.rightOperand = main.parent.leftOperand;
                        }
                        else{
                            root = main.parent.leftOperand;
                        }
                    }
                }else {
                    System.out.println("impossible");
                }
                ret = true;
            }
        }
        return ret;
    }

    private void makeAllChildrenNot(Node current){
        if(current == null)
            return;
        else if(current.type == Type.IMP)
            return;
        else if(current.type == Type.OR){
            current.type = Type.AND;
        }
        else if(current.type == Type.AND){
            current.type = Type.OR;
        }
        else if(current.type == Type.CONST){
            current.value = !current.value;
        }
        else if(current.type == Type.NOT){
            if(current.parent != null){
                if(current.parent.leftOperand == current){
                    current.parent.leftOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent;
                }else{
                    current.parent.rightOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent;
                }
            }else {
                root = current.leftOperand;
                current.leftOperand.parent = null;
            }
            return;
        }
        else if(current.type == Type.VALUE){
            if(current.parent != null){
                if(current.parent.leftOperand == current){
                    current.parent.leftOperand = new Node(null, current, null, Type.NOT, null, current.parent);
                    current.parent = current.parent.leftOperand;
                }else{
                    current.parent.rightOperand = new Node(null, current, null, Type.NOT, null, current.parent);
                    current.parent = current.parent.rightOperand;
                }
            }else {
                root = new Node(null, current, null, Type.NOT, null, null);
                current.parent = root;
            }
        }
        makeAllChildrenNot(current.leftOperand);
        makeAllChildrenNot(current.rightOperand);
    }

    private void getNot(Node current){
        if(current == null)
            return;
        else if(current.type == Type.IMP)
            return;
        else if(current.type == Type.OR){
            current.type = Type.AND;
        }
        else if(current.type == Type.AND){
            current.type = Type.OR;
        }
        else if(current.type == Type.CONST){
            current.value = !current.value;
        }
        else if(current.type == Type.NOT){
            if(current.parent != null){
                if(current.parent.leftOperand == current){
                    current.parent.leftOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent;
                }else{
                    current.parent.rightOperand = current.leftOperand;
                    current.leftOperand.parent = current.parent;
                }
            }else {
                current.leftOperand.parent = null;
            }
            return;
        }
        else if(current.type == Type.VALUE){
            if(current.parent != null){
                if(current.parent.leftOperand == current){
                    current.parent.leftOperand = new Node(null, current, null, Type.NOT, null, current.parent);
                    current.parent = current.parent.leftOperand;
                }if(current.parent.rightOperand == current){
                    current.parent.rightOperand = new Node(null, current, null, Type.NOT, null, current.parent);
                    current.parent = current.parent.rightOperand;
                }
            }else {
                current.parent = new Node(null, current, null, Type.NOT, null, null);
            }
        }
        getNot(current.leftOperand);
        getNot(current.rightOperand);
    }


    private boolean openSkobe(Node current){
        boolean req = false;
        if(current != null){
            req = openSkobe(current.leftOperand);
            req = req | openSkobe(current.rightOperand);
            if(current.type == Type.AND){
                if(current.leftOperand.type == Type.OR){
                    Node subAnd1 = new Node(null, null, null, Type.AND, null, current);
                    Node subAnd2 = new Node(null, null, null, Type.AND, null, current);
                    subAnd1.leftOperand = current.rightOperand.clone();
                    subAnd1.leftOperand.parent = subAnd1;
                    subAnd1.rightOperand = current.leftOperand.leftOperand;
                    subAnd1.rightOperand.parent = subAnd1;
                    subAnd2.leftOperand =
                            current.rightOperand.clone();
                    subAnd2.leftOperand.parent = subAnd2;
                    subAnd2.rightOperand = current.leftOperand.rightOperand;
                    subAnd2.rightOperand.parent = subAnd2;
                    current.type = Type.OR;
                    current.leftOperand = subAnd1;
                    current.rightOperand = subAnd2;
                    return true;
                }
                if(current.rightOperand.type == Type.OR){
                    Node subAnd1 = new Node(null, null, null, Type.AND, null, current);
                    Node subAnd2 = new Node(null, null, null, Type.AND, null, current);
                    subAnd1.leftOperand = current.leftOperand.clone();
                    subAnd1.leftOperand.parent = subAnd1;
                    subAnd1.rightOperand = current.rightOperand.leftOperand;
                    subAnd1.rightOperand.parent = subAnd1;
                    subAnd2.leftOperand = current.leftOperand.clone();
                    subAnd2.leftOperand.parent = subAnd2;
                    subAnd2.rightOperand = current.rightOperand.rightOperand;
                    subAnd2.rightOperand.parent = subAnd2;
                    current.type = Type.OR;
                    current.leftOperand = subAnd1;
                    current.rightOperand = subAnd2;
                    return true;
                }
            }
        }
        return req;
    }


    //!(!(a&b)|!(!(!d=>!(!(c)))))
//!!c|!!d|!!!f|!!!w
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


    public GenerateTree(ArrayList<Element> logStr){
        sb = new StringBuilder();
        root = add(logStr,root);
        System.out.println("Выражение введено в дерево:");
        sb.append("Выражение введено в дерево:" + "\n");
        out();
        killImp(root);
        System.out.println("раскрыты импликации:");
        sb.append("раскрыты импликации:" + "\n");
        createSort(root);
        out();
        killMoreThenOneNot(root);
        System.out.println("убраны двойные отрицания");
        sb.append("убраны двойные отрицания" + "\n");
        createSort(root);
        out();
        killAllNot(root);
        System.out.println("убраны отрицания всего кроме переменных (Закон Де Моргана):");
        sb.append("убраны отрицания всего кроме переменных (Закон Де Моргана):" + "\n");
        createSort(root);
        out();
        killOrAndWithConst(root, true);
        killOrAndWithConst(root, false);
        System.out.println("убраны константы:");
        sb.append("убраны константы:" + "\n");
        createSort(root);
        out();
        createSort(root);
        System.out.println("выражение отсортировано:");
        sb.append("выражение отсортировано:" + "\n");
        out();
        boolean isUpdate = true;
        while (isUpdate){
            while (isUpdate) {
                while (isUpdate){
                    createSort(root);
                    isUpdate = killEquals(root);
                    isUpdate = isUpdate | bonding(root);
                    isUpdate = isUpdate | absorption(root);
                }
                kyb(root);
                isUpdate = isUpdate | closeScobe(root);
            }
            isUpdate = isUpdate | absorptionElementary(root);
        }

        createSort(root);
        System.out.println("Применены склеивание, поглощение и законы идмепотентности, противоречия, исключения третьего (по необходимости избавление от констант), так же сворачиваются все скобки");
        sb.append("Применены склеивание, поглощение и законы идмепотентности, противоречия, исключения третьего (по необходимости избавление от констант), так же сворачиваются все скобки" + "\n");
        out();
    }

    public String getLog(){
        return sb.toString();
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
            else if
                    (name != null)
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
            return "!";
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

    private String getStrType(Node node){
        if(node.type == Type.NOT){
            return "!";
        }
        if(node.type == Type.AND){
            return "&";
        }
        if(node.type == Type.OR){
            return "|";
        }
        if(node.type == Type.IMP){
            return "=>";
        }
        if(node.type == Type.CONST){
            if(node.value){
                return "TRUE";
            }
            else{
                return "FALSE";
            }
        }
        if(node.type == Type.VALUE){
            return node.name;
        }
        return null;
    }

    private void draw(int x1,int x2,int y, int size, Node current, Graphics gr){
        if(current!=null){
            gr.setFont(new Font(null, Font.BOLD, 16));
            int h = gr.getFontMetrics().getHeight();
            int w = gr.getFontMetrics().stringWidth(getStrType(current));
            int x=x1+(x2-x1)/2;
            if(current.type==Type.VALUE){
                gr.setColor(Color.BLACK);
                gr.drawString(getStrType(current), x + size/2 - w/2,(int)(y + size - h/2));
            }
            if(current.type==Type.IMP){
                gr.setColor(Color.RED);
                gr.fillOval(x-2,y-2,size+4,size+4);
                gr.setColor(Color.WHITE);
                gr.fillOval(x,y,size,size);
                gr.setColor(Color.BLACK);
                gr.drawString(getStrType(current), x + size/2 - w/2,(int)(y + size - h/2));
            }
            if(current.type==Type.NOT){
                gr.setColor(Color.BLACK);
                gr.fillOval(x,y,size,size);
                gr.setColor(Color.WHITE);
                gr.fillOval(x+3,y+3,size-6,size-6);
                gr.setColor(Color.BLACK);
                gr.drawString(getStrType(current), x + size/2 - w/2,(int)(y + size - h/2)); }
            if(current.type==Type.AND){
                gr.setColor(Color.GREEN);
                gr.fillOval(x-2,y-2,size+4,size+4);
                gr.setColor(Color.WHITE);
                gr.fillOval(x,y,size,size);
                gr.setColor(Color.BLACK);
                gr.drawString(getStrType(current), x + size/2 - w/2,(int)(y + size - h/2)); }
            if(current.type==Type.OR){
                gr.setColor(Color.BLUE);
                gr.fillOval(x-2,y-2,size+4,size+4);
                gr.setColor(Color.WHITE);
                gr.fillOval(x,y,size,size);
                gr.setColor(Color.BLACK);
                gr.drawString(getStrType(current),x+size/2,(int)(y+size/1.5));
            }
            if(current.type==Type.CONST){
                gr.setColor(Color.BLACK);
                gr.drawString(getStrType(current), x + size/2 - w/2,(int)(y + size - h/2)); }
            gr.setColor(Color.BLACK);
            int lcount = getCount(current.leftOperand);
            int rcount = getCount(current.rightOperand);

            x=(int)((lcount/(double)(lcount+rcount))*(x2-x1))+x1;
// gr.setColor(Color.CYAN);
// gr.drawLine(x1,y,x1,y+1000);
// gr.drawLine(x2,y,x2,y+1000);
// gr.drawLine(x,y,x,y+1000);
            if(current.leftOperand!=null){
                draw(x1,x,y+size*2,size,current.leftOperand, gr);
// gr.setColor(Color.RED);
                gr.drawLine((x1+(x2-x1)/2)+size/2,y+size,x1+size/2 + (x-x1)/2,y+size*2);
            }
            if(current.rightOperand!=null){
                draw(x,x2,y+size*2,size,current.rightOperand, gr);
// gr.setColor(Color.BLACK);
                gr.drawLine((x1+(x2-x1)/2)+size/2,y+size,x+size/2 + (x2-x)/2,y+size*2);
            }
        }
    }

    void drawTree(Graphics gr){
        draw(0, getCount(root)*24, 24, 24,root, gr);
    }

    private int hight;
    private void getH(Node current,int count){
        if(current!=null){
            if(count>hight){
                hight = count;
            }
            getH(current.leftOperand,count+1);
            getH(current.rightOperand, count+1);
        }
    }
    int getHight(){
        hight = 0;
        getH(root,1);
        return hight;
    }

    private int count;
    private void getC(Node current){
        if(current!=null){
            count++;
            getC(current.leftOperand);
            getC(current.rightOperand);
        }
    }
    private int getCount(Node curr){
        count=0;
        getC(curr);
        return count;
    }
    public int getFullCount(){
        return getCount(root);
    }
}