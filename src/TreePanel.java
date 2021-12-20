import javax.swing.*;
import java.awt.*;

/**
 * Created by root on 10.01.18.
 */
public class TreePanel extends JPanel {
    private GenerateTree tree;
    public TreePanel(GenerateTree tree){
        this.tree = tree;
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (tree != null) {
            g.setColor(Color.lightGray);
            for (int x = 48; x < tree.getFullCount() * 24; x += 48) {
                g.drawLine(x, 0, x, tree.getHight() * 48);
            }
            for (int y = 48; y < tree.getHight() * 48; y += 48) {
                g.drawLine(0, y, tree.getFullCount() * 24, y);
            }
            tree.drawTree(g);
        }
    }

    public void setTree(GenerateTree tree) {
        this.tree = tree;
    }
}