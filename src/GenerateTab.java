import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by root on 10.01.18.
 */
public class GenerateTab extends JPanel {
    private String data = "";
    private JScrollPane sp, sp2, sp3, sp4, sp5, sp6;
    private JTextArea text, little, full, answer, compaire, logs;
    private TreePanel treePanel;
    private JLabel kLable;
    private JLabel nLable;
    private int n, k;
    private Button upK, downK, upN, downN, btnMake;
    private Generator g;
    private PrintWriter pw = null;
    private final String info = "Задайте параметры генерации 1 параметр отвечает за размеры простого выражения, 2 за усложнение";
    public GenerateTab(Dimension size){
        setLayout(new FlowLayout());
        Font font = new Font(null, Font.ITALIC, 21);
        Font btnFont = new Font(null, Font.PLAIN, 21);
        try {
            pw = new PrintWriter(new File("output.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("ошибка в файле вывода");
        }
        g = new Generator(pw);
        kLable = new JLabel();
        kLable.setFont(font);
        kLable.setVisible(true);
        kLable.setText(String.valueOf(k));
        nLable = new JLabel();
        nLable.setFont(font);
        nLable.setVisible(true);
        nLable.setText(String.valueOf(n));
        text = new JTextArea();
        text.setFont(font);
        text.setSize((int) (size.width*0.99), size.height);
        text.setVisible(true);
        text.append(info);
        text.setLineWrap(true);
        text.setEditable(false);
        answer = new JTextArea(3, size.width/21);
        answer.setFont(font);
        answer.setVisible(true);
        answer.append("Результат работы программы: ");
// answer.setLineWrap(true);
        answer.setEditable(false);
        logs = new JTextArea();
        logs.setFont(font);
        logs.setSize((int) (size.width*0.99), size.height);
        logs.setVisible(true);
        logs.append("Лог работы программы: ");
        logs.setLineWrap(true);
        logs.setEditable(false);
        logs.setColumns(size.width/21);
        logs.setRows(10);
        compaire = new JTextArea();
        compaire.setFont(font);
        compaire.setSize((int) (size.width*0.99), size.height);
        compaire.setVisible(true);
        compaire.append("Сравнение результата с исходным: ");
        compaire.setLineWrap(true);
        compaire.setEditable(false);
        little = new JTextArea(3, size.width/21);
        little.setFont(font);
// little.setSize(size.width, size.height/2);
        little.setVisible(true);
        little.append("Простая форма сгенерированного выражения: ");
 little.setLineWrap(true);
        little.setEditable(false);

        full = new JTextArea(3, size.width/21);
        full.setFont(font);
// full.setSize(size.width, size.height/2);
        full.setVisible(true);
        full.append("Усложненная форма сгенерированного выражения: ");
 full.setLineWrap(true);
        full.setEditable(false);

        upN = new Button("+");
        upN.setFont(btnFont);
        upN.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                n++;
                nLable.setText(String.valueOf(n));
                generate();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        downN = new Button("-");
        downN.setFont(btnFont);
        downN.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(n > 0){
                    n--;
                    nLable.setText(String.valueOf(n));
                    generate();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        upK = new Button("+");
        upK.setFont(btnFont);
        upK.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                k++;
                kLable.setText(String.valueOf(k));
                generate();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }
            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        downK = new Button("-");
        downK.setFont(btnFont);
        downK.addMouseListener(new MouseListener() {
            @Override
            public
            void mouseClicked(MouseEvent e) {
                if(k > 0){
                    k--;
                    kLable.setText(String.valueOf(k));
                    generate();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        btnMake = new Button("упростить");
        btnMake.setFont(btnFont);
        btnMake.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                make();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        add(text);
        add(downN);
        add(nLable);
        add(upN);
        add(downK);
        add(kLable);
        add(upK);
        sp = new JScrollPane(little, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp2 = new JScrollPane(full, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp3 = new JScrollPane(answer, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp5 = new JScrollPane(logs, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(sp);
        add(sp2);
        add(btnMake);
        add(sp3);
        add(compaire);
        generate();
        treePanel = new TreePanel(null);
        treePanel.setPreferredSize(new Dimension(10000, 10000));
        sp4 = new JScrollPane(treePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp4.setPreferredSize(new Dimension(size.width, size.height/2-50));
        sp4.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                treePanel.repaint();
            }
        });
        sp4.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                treePanel.repaint();
            }
        });
        sp6 = new JScrollPane();
        sp6.setPreferredSize(new Dimension(size.width, size.height/2));
        JPanel pan = new JPanel();
        pan.setSize(new Dimension(size.width, size.height/2));
        pan.add(sp4);
        pan.add(sp5);
        sp6.setViewportView(pan);
        add(sp6);
    }

    private void generate(){
        upK.setEnabled(false);
        downK.setEnabled(false);
        upN.setEnabled(false);
        downN.setEnabled(false);
        data = g.generate(n, k);
        little.setText("");
        little.append("Простая форма сгенерированного выражения: ");
        little.append(Generator.getMin());
        full.setText("");
        full.append("Усложненная форма сгенерированного выражения: ");
        full.append(data);
        upK.setEnabled(true);
        downK.setEnabled(true);
        upN.setEnabled(true);
        downN.setEnabled(true);
    }

    private void make(){
        GenerateAutomaton automaton = new GenerateAutomaton();
        boolean successfull = automaton.start(data);
        if(successfull){
            GenerateTree tree = new GenerateTree(automaton.getRequest());
            answer.setText("");
            answer.append("Результат работы программы: " + tree.makeString());
            compaire.setText("");
            compaire.append("Сравнение результата с исходным: " + (tree.makeString().compareTo(Generator.getMin()) == 0 ? "результат совпадает" : "результат не совпадает"));
            treePanel.setTree(tree);
            treePanel.repaint();
            treePanel.revalidate();
            treePanel.setPreferredSize(new Dimension(24*tree.getFullCount(), 48 * tree.getHight()));
            sp4.revalidate();
            logs.setText("");
            logs.append("Лог работы программы: " + tree.getLog());
            pw.println(tree.getLog());
            pw.flush();
            sp6.revalidate();
        }else{
            answer.setText("");
            answer.append("Результат работы программы: " + automaton.getError());
            pw.println(automaton.getError());
            pw.flush();
        }

    }

    @Override
    public void resize(Dimension d) {
        super.resize(d);
        text.resize((int) (d.width*0.99), d.height);
        little.setColumns(d.width/21);
        full.setColumns(d.width/21);
        compaire.setColumns(d.width/21);
        answer.setColumns(d.width/21);
        sp4.setPreferredSize(new Dimension(d.width, d.height/2-50));
        sp6.setPreferredSize(new Dimension(d.width, d.height/2));
        logs.setColumns(d.width/21);
        logs.setRows(10);
// little.resize((int) (d.width*0.9), d.height/2);
    }
}