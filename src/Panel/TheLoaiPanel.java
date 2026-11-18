package Panel;

import DAO.TheLoaiDAO;
import model.TheLoai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class TheLoaiPanel extends JPanel {
    private final TheLoaiDAO dao = new TheLoaiDAO();
    private final DefaultTableModel model = UI.tableModel("Mã", "Tên thể loại", "Mô tả");
    private final JTable table = UI.table(model);
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(),
            txtMoTa = new JTextField(), txtTim = new JTextField(20);

    public TheLoaiPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        txtMa.setEditable(false);

        JButton btnTim = new JButton("Tìm"), btnAll = new JButton("Hiện tất cả");
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(new JLabel("Tên thể loại:")); north.add(txtTim); north.add(btnTim); north.add(btnAll);

        JButton btnThem = new JButton("Thêm"), btnSua = new JButton("Sửa"),
                btnXoa = new JButton("Xóa"), btnMoi = new JButton("Làm mới form");
        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.form(new String[]{"Mã thể loại:", "Tên thể loại:", "Mô tả:"},
                new JComponent[]{txtMa, txtTen, txtMoTa}), BorderLayout.CENTER);
        south.add(UI.buttons(btnThem, btnSua, btnXoa, btnMoi), BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> { if (!e.getValueIsAdjusting()) fillForm(); });
        btnTim.addActionListener(e -> UI.safe(this, () -> load(dao.searchByName(txtTim.getText()))));
        btnAll.addActionListener(e -> refresh());
        btnThem.addActionListener(e -> UI.safe(this, this::them));
        btnSua.addActionListener(e -> UI.safe(this, this::sua));
        btnXoa.addActionListener(e -> UI.safe(this, this::xoa));
        btnMoi.addActionListener(e -> clearForm());
        refresh();
    }

    public void refresh() {
        UI.safe(this, () -> { load(dao.getAll()); clearForm(); });
    }

    private void load(List<TheLoai> ds) {
        model.setRowCount(0);
        for (TheLoai t : ds) model.addRow(new Object[]{t.getMaTheLoai(), t.getTenTheLoai(), t.getMoTa()});
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        txtMa.setText(String.valueOf(model.getValueAt(r, 0)));
        txtTen.setText(String.valueOf(model.getValueAt(r, 1)));
        Object moTa = model.getValueAt(r, 2);
        txtMoTa.setText(moTa == null ? "" : moTa.toString());
    }

    private void clearForm() {
        txtMa.setText(""); txtTen.setText(""); txtMoTa.setText("");
        table.clearSelection();
    }

    private void them() throws Exception {
        dao.insert(new TheLoai(0, txtTen.getText().trim(), txtMoTa.getText().trim()));
        UI.info(this, "Thêm thể loại thành công!");
        refresh();
    }

    private void sua() throws Exception {
        if (txtMa.getText().isEmpty()) throw new Exception("Hãy chọn một thể loại trong bảng.");
        dao.update(new TheLoai(Integer.parseInt(txtMa.getText()), txtTen.getText().trim(), txtMoTa.getText().trim()));
        UI.info(this, "Cập nhật thành công!");
        refresh();
    }

    private void xoa() throws Exception {
        if (txtMa.getText().isEmpty()) throw new Exception("Hãy chọn một thể loại trong bảng.");
        if (!UI.confirm(this, "Bạn có chắc muốn xóa thể loại này?")) return;
        try {
            dao.delete(Integer.parseInt(txtMa.getText()));
        } catch (java.sql.SQLException ex) {
            throw new Exception("Không thể xóa: thể loại này đang có sách.");
        }
        refresh();
    }
}
