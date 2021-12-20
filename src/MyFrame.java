import javax.swing.*;
import java.awt.*;

/**
 * Created by root on 10.01.18.
 */
public class MyFrame extends JFrame {
    private BorderLayout borderLayout1 = new BorderLayout();
    private JTabbedPane tabs;
    private InputTab keyInput;
    private GenerateTab generate;
    private InfoTab info;

    public MyFrame(){
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setSize((int)dim.getWidth() - insets.left - insets.right, (int)dim.getHeight() - insets.top - insets.bottom);
        setTitle("Упрощение логических выражений");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        makeTabs();
        setResizable(false);
    }

    public void makeTabs() {
        tabs = new JTabbedPane();
        info = new InfoTab(getSize());
        generate = new GenerateTab(getSize());
        keyInput = new InputTab(getSize());
        this.setLayout(borderLayout1);
        this.add(tabs, BorderLayout.CENTER);
//        JScrollPane sp = new JScrollPane(generate);
//        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        tabs.addTab("О программе", info);
        tabs.addTab("Генерация", generate);
        tabs.addTab("Ввод", keyInput);
    }
}
