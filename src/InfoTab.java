import javax.swing.*;
import java.awt.*;

/**
 * Created by root on 10.01.18.
 */
public class InfoTab extends JPanel {
    private final String info = "Работа студентов 6374 группы Макогона Николая и Тимофея Гераймовича" + "\n" +
            "Упрощение логических выражений включающих импликацию, отрицание, И, ИЛИ, константы и переменные" + "\n" +
            "Пример выражения !(c=>e)=>(!(a|b=>d)&!(z|1))" + "\n" +
            "переменные при вводе состоят из одной строчной буквы и любого набора цифр, пробелы вводить нельзя"  + "\n" +
            "остальное видно из примера" + "\n" +
            "Вы можете ввести свое выражение на вкладке \"ввод\" или проверить программу на генераторе на вкладке \"генерация\"";
    private JTextArea text;
    public InfoTab(Dimension size){
        Font font = new Font(null, Font.ITALIC, 21);
        text = new JTextArea();
        text.setFont(font);
        text.setSize(size);
        text.setVisible(true);
        text.append(info);
        text.setLineWrap(true);
        text.setEditable(false);
        add(text);
    }

    @Override
    public void resize(Dimension d) {
        super.resize(d);
        text.resize(d);
    }
}
