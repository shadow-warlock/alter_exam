import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * Created by root on 10.01.18.
 */
public class InputTab extends JPanel {
    private String data = "";
    private JScrollPane sp, sp3, sp4, sp5, sp6;
    private JTextArea text, little, answer, logs;
    private TreePanel treePanel;
    private int n, k;
    private Button btnMake;
    private Generator g;
    private final String info = "Введите ваше выражение";
    public InputTab(Dimension size){
        setLayout(new FlowLayout());
        Font font = new Font(null, Font.ITALIC, 21);
        Font btnFont = new Font(null, Font.PLAIN, 21);
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new File("output.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("ошибка в файле вывода");
        }
        g = new Generator(pw);
        text = new JTextArea();
        text.setFont(font);
        text.setSize((int) (size.width*0.99), size.height);
        text.setVisible(true);
        text.append(info);
        text.setLineWrap(true);
        text.setEditable(false);
        answer = new JTextArea(1, size.width/21);
        answer.setFont(font);
        answer.setVisible(true);
        answer.append("Результат работы программы: ");
//        answer.setLineWrap(true);
        answer.setEditable(false);
        logs = new JTextArea();
        logs.setFont(font);
        logs.setSize((int) (size.width*0.99), size.height);
        logs.setVisible(true);
        logs.append("Лог работы программы: ");
        logs.setLineWrap(true);
        logs.setEditable(false);
        little = new JTextArea(1, size.width/21);
        little.setFont(font);
//        little.setSize(size.width, size.height/2);
        little.setVisible(true);
//        little.setLineWrap(true);

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
        sp = new JScrollPane(little, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp3 = new JScrollPane(answer, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp5 = new JScrollPane(logs, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(sp);
        add(btnMake);
        add(sp3);
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


    private void make(){
        GenerateAutomaton automaton = new GenerateAutomaton();
        boolean successfull = automaton.start(little.getText());
        if(successfull){
            GenerateTree tree = new GenerateTree(automaton.getRequest());
            answer.setText("");
            answer.append("Результат работы программы: " + tree.makeString());
            treePanel.setTree(tree);
            treePanel.repaint();
            treePanel.revalidate();
            treePanel.setPreferredSize(new Dimension(24 *2*(int)Math.pow(2,tree.getHight()-1), 48 * tree.getHight()));
            System.out.println(size().getHeight()/2);
            System.out.println(24 * tree.getHight());
            sp4.revalidate();
            sp3.revalidate();
            logs.setText("");
            logs.append("Лог работы программы: " + tree.getLog());
            sp6.revalidate();
        }else{
            answer.setText("");
            answer.append("Результат работы программы: " + automaton.getError());
        }

    }

    @Override
    public void resize(Dimension d) {
        super.resize(d);
        text.resize((int) (d.width*0.99), d.height);
        little.setColumns(d.width/21);
        answer.setColumns(d.width/21);
        sp4.setPreferredSize(new Dimension(d.width, d.height/2-50));
        sp6.setPreferredSize(new Dimension(d.width, d.height/2));
        logs.setColumns(d.width/21);
        logs.setRows(10);
//        little.resize((int) (d.width*0.9), d.height/2);
    }
}
