package Panel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Các hàm tiện ích dùng chung cho các màn hình. */
public class UI {
    public interface Action { void run() throws Exception; }

    /** Chạy một hành động, nếu lỗi thì hiện hộp thoại báo lỗi. */
    public static void safe(Component parent, Action a) {
        try { a.run(); } catch (Exception e) { error(parent, e); }
    }

    public static DefaultTableModel tableModel(String... cols) {
        return new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
    }

    public static void error(Component p, Exception e) {
        String msg = e.getMessage() == null ? e.toString() : e.getMessage();
        JOptionPane.showMessageDialog(p, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static void info(Component p, String msg) {
        JOptionPane.showMessageDialog(p, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean confirm(Component p, String msg) {
        return JOptionPane.showConfirmDialog(p, msg, "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    /** Form nhập liệu: mỗi dòng gồm nhãn + ô nhập. */
    public static JPanel form(String[] labels, JComponent[] comps) {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 5, 3, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        for (int i = 0; i < labels.length; i++) {
            c.gridy = i;
            c.gridx = 0; c.weightx = 0; p.add(new JLabel(labels[i]), c);
            c.gridx = 1; c.weightx = 1; p.add(comps[i], c);
        }
        return p;
    }

    public static JPanel buttons(JButton... bs) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        for (JButton b : bs) p.add(b);
        return p;
    }

    public static JTable table(javax.swing.table.TableModel m) {
        JTable t = new JTable(m);
        t.setRowHeight(24);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return t;
    }
}
