package Panel;

import javax.swing.*;
import java.awt.*;

/** Cửa sổ chính - chạy file này để mở chương trình. */
public class MainFrame extends JFrame {
    public MainFrame() {
        super("Quản Lý Thư Viện");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        TheLoaiPanel pTheLoai = new TheLoaiPanel();
        SachPanel pSach = new SachPanel();
        DocGiaPanel pDocGia = new DocGiaPanel();
        PhieuMuonPanel pPhieu = new PhieuMuonPanel();

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(tabs.getFont().deriveFont(Font.BOLD, 14f));
        tabs.addTab("Thể loại", pTheLoai);
        tabs.addTab("Sách", pSach);
        tabs.addTab("Độc giả", pDocGia);
        tabs.addTab("Phiếu mượn", pPhieu);
        // Mỗi lần chuyển tab thì tải lại dữ liệu mới nhất
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c == pTheLoai) pTheLoai.refresh();
            else if (c == pSach) pSach.refresh();
            else if (c == pDocGia) pDocGia.refresh();
            else if (c == pPhieu) pPhieu.refresh();
        });
        add(tabs);
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
