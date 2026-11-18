package Panel;

import DAO.DocGiaDAO;
import model.DocGia;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class DocGiaPanel extends JPanel {
    private final DocGiaDAO dao = new DocGiaDAO();
    private final DefaultTableModel model = UI.tableModel("Mã", "Họ tên", "Ngày sinh", "Giới tính", "Địa chỉ", "SĐT", "Email", "Ngày đăng ký");
    private final JTable table = UI.table(model);
    private final JTextField txtMa = new JTextField(), txtTen = new JTextField(), txtNgaySinh = new JTextField(),
            txtDiaChi = new JTextField(), txtSdt = new JTextField(), txtEmail = new JTextField(), txtTim = new JTextField(20);
    private final JComboBox<String> cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});

    public DocGiaPanel() {
        setLayout(new BorderLayout(8, 8));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        txtMa.setEditable(false);

        JButton btnTim = new JButton("Tìm"), btnAll = new JButton("Hiện tất cả");
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(new JLabel("Họ tên:")); north.add(txtTim); north.add(btnTim); north.add(btnAll);

        JButton btnThem = new JButton("Thêm"), btnSua = new JButton("Sửa"),
                btnXoa = new JButton("Xóa"), btnMoi = new JButton("Làm mới form");
        JPanel south = new JPanel(new BorderLayout());
        south.add(UI.form(
                new String[]{"Mã độc giả:", "Họ tên:", "Ngày sinh (yyyy-MM-dd):", "Giới tính:", "Địa chỉ:", "Số điện thoại:", "Email:"},
                new JComponent[]{txtMa, txtTen, txtNgaySinh, cbGioiTinh, txtDiaChi, txtSdt, txtEmail}), BorderLayout.CENTER);
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

    private void load(List<DocGia> ds) {
        model.setRowCount(0);
        for (DocGia d : ds) model.addRow(new Object[]{d.getMaDocGia(), d.getTenDocGia(), d.getNgaySinh(),
                d.getGioiTinh(), d.getDiaChi(), d.getSoDienThoai(), d.getEmail(), d.getNgayDangKy()});
    }

    private void fillForm() {
        int r = table.getSelectedRow();
        if (r < 0) return;
        txtMa.setText(model.getValueAt(r, 0).toString());
        txtTen.setText(model.getValueAt(r, 1).toString());
        txtNgaySinh.setText(model.getValueAt(r, 2).toString());
        cbGioiTinh.setSelectedItem(model.getValueAt(r, 3));
        txtDiaChi.setText(model.getValueAt(r, 4).toString());
        txtSdt.setText(model.getValueAt(r, 5).toString());
        txtEmail.setText(model.getValueAt(r, 6).toString());
    }

    private void clearForm() {
        txtMa.setText(""); txtTen.setText(""); txtNgaySinh.setText("");
        txtDiaChi.setText(""); txtSdt.setText(""); txtEmail.setText("");
        table.clearSelection();
    }

    private LocalDate ngaySinh() throws Exception {
        try { return LocalDate.parse(txtNgaySinh.getText().trim()); }
        catch (DateTimeParseException e) { throw new Exception("Ngày sinh phải đúng dạng yyyy-MM-dd (ví dụ 2003-05-20)."); }
    }

    private void them() throws Exception {
        DocGia d = new DocGia(txtTen.getText(), ngaySinh(), (String) cbGioiTinh.getSelectedItem(),
                txtDiaChi.getText(), txtSdt.getText(), txtEmail.getText());
        dao.insert(d);
        UI.info(this, "Thêm độc giả thành công!");
        refresh();
    }

    private void sua() throws Exception {
        if (txtMa.getText().isEmpty()) throw new Exception("Hãy chọn một độc giả trong bảng.");
        DocGia d = dao.getById(Integer.parseInt(txtMa.getText()));
        d.setTenDocGia(txtTen.getText());
        d.setNgaySinh(ngaySinh());
        d.setGioiTinh((String) cbGioiTinh.getSelectedItem());
        d.setDiaChi(txtDiaChi.getText());
        d.setSoDienThoai(txtSdt.getText());
        d.setEmail(txtEmail.getText());
        dao.update(d);
        UI.info(this, "Cập nhật thành công!");
        refresh();
    }

    private void xoa() throws Exception {
        if (txtMa.getText().isEmpty()) throw new Exception("Hãy chọn một độc giả trong bảng.");
        if (!UI.confirm(this, "Bạn có chắc muốn xóa độc giả này?")) return;
        try {
            dao.delete(Integer.parseInt(txtMa.getText()));
        } catch (java.sql.SQLException ex) {
            throw new Exception("Không thể xóa: độc giả này đã có phiếu mượn.");
        }
        refresh();
    }
}
