/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.awt.*;

/**
 *
 * @author Neo 16
 */
public class Giohang extends javax.swing.JPanel implements Refreshable {

    /**
     * Creates new form Nhanvienlaphoadon1
     */
    private final Service.IOrderService orderService = new Service.impl.OrderServiceImpl();
    private final Service.IProductService productService = new Service.impl.ProductServiceImpl();

    @Override
    public void refresh() {
        loadGioHang();
    }

    public Giohang() {
        initComponents();
        styleUI();
        loadGioHang();
        tblGiohang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblGiohang.getSelectedRow();
                if (row != -1) {
                    hienThiChiTietSanPham(row);
                }
            }
        });

        btnXoa.addActionListener(e -> {
            int selectedRow = tblGiohang.getSelectedRow();
            if (selectedRow != -1) {
                String maQuanAo = tblGiohang.getValueAt(selectedRow, 0).toString();
                String maKhachHang = Service.UserSession.getInstance().getMaTaiKhoan();

                orderService.xoaKhoiGioHang(maKhachHang, Integer.parseInt(maQuanAo));
                loadGioHang();
                txtMaquanao.setText("");
                txtDongiaban.setText("");
                txtSoluong.setText("");
                txtTenquanao.setText("");
                txtTongtien.setText("");
                hienThiAnh("src/GUI/icons/Ảnh chụp màn hình 2025-05-07 194832.png");
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn sản phẩm để xóa.");
            }
        });
    }

    private void loadGioHang() {
        String maTaiKhoan = Service.UserSession.getInstance().getMaTaiKhoan();

        if (maTaiKhoan == null || maTaiKhoan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bạn chưa đăng nhập!");
            return;
        }

        try {
            List<Model.ItemGioHang> items = orderService.getGioHang(maTaiKhoan, null);
            DefaultTableModel model = new DefaultTableModel();
            model.setColumnIdentifiers(new String[]{
                "Mã Quần Áo", "Tên Quần Áo", "Đơn Giá Bán", "Số Lượng Đặt", "Tổng Tiền", "Trạng Thái"
            });

            for (Model.ItemGioHang item : items) {
                String trangThaiText = (item.getTrangThai() == 0) ? "Chưa đặt" : "Đã đặt";
                Object[] row = new Object[]{
                    item.getSanPham().getMaQuanAo(),
                    item.getSanPham().getTenQuanAo(),
                    item.getDonGiaBan(),
                    item.getSoLuongDat(),
                    item.getTongTien(),
                    trangThaiText
                };
                model.addRow(row);
            }
            tblGiohang.setModel(model);
            GUI.theme.UITheme.styleTable(tblGiohang);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi tải giỏ hàng: " + ex.getMessage());
        }
    }

    private void styleUI() {
        GUI.theme.UITheme.styleContainer(this);
        GUI.theme.UITheme.styleDangerButton(btnXoa);
        GUI.theme.UITheme.styleTable(tblGiohang);
        Anh.setBackground(java.awt.Color.WHITE);
        Anh.setBorder(javax.swing.BorderFactory.createLineBorder(GUI.theme.UITheme.BORDER_COLOR, 1, true));
    }

    private void hienThiChiTietSanPham(int row) {
        String maQuanAo = tblGiohang.getValueAt(row, 0).toString();
        String tenQuanAo = tblGiohang.getValueAt(row, 1).toString();
        String donGiaBan = tblGiohang.getValueAt(row, 2).toString();
        String soLuong = tblGiohang.getValueAt(row, 3).toString();
        String tongTien = tblGiohang.getValueAt(row, 4).toString();

        txtMaquanao.setText(maQuanAo);
        txtTenquanao.setText(tenQuanAo);
        txtDongiaban.setText(donGiaBan);
        txtSoluong.setText(soLuong);
        txtTongtien.setText(tongTien);

        productService.getProductById(Integer.parseInt(maQuanAo)).ifPresent(sp -> {
            if (sp.getAnh() != null && !sp.getAnh().isEmpty()) {
                hienThiAnh(sp.getAnh());
            }
        });
    }

    public void hienThiAnh(String duongDan) {
        try {
            // Đảm bảo đường dẫn hợp lệ (kiểm tra xem tệp có tồn tại không)
            File file = new File(duongDan);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Ảnh không tồn tại tại: " + duongDan);
                return;
            }
    
            // Tạo đối tượng ImageIcon từ đường dẫn ảnh
            ImageIcon icon = new ImageIcon(duongDan);
            // Tải ảnh và thay đổi kích thước phù hợp với JPanel (Anh)
            Image img = icon.getImage().getScaledInstance(Anh.getWidth(), Anh.getHeight(), Image.SCALE_SMOOTH);
    
            // Tạo một JLabel chứa ảnh đã thay đổi kích thước
            JLabel lbl = new JLabel(new ImageIcon(img));
    
            // Xóa hết nội dung cũ của JPanel trước khi thêm ảnh mới
            Anh.removeAll();
            Anh.setLayout(new BorderLayout()); // Đảm bảo layout hợp lý
            Anh.add(lbl, BorderLayout.CENTER); // Thêm JLabel vào giữa JPanel
    
            // Cập nhật lại giao diện
            Anh.revalidate();
            Anh.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể hiển thị ảnh: " + duongDan);
        }
    }



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblGiohang = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        txtTenquanao = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        lblTenquanao = new javax.swing.JLabel();
        Anh = new javax.swing.JPanel();
        lblMau = new javax.swing.JLabel();
        txtDongiaban = new javax.swing.JTextField();
        lblDongiaban = new javax.swing.JLabel();
        btnXoa = new javax.swing.JButton();
        txtTongtien = new javax.swing.JTextField();
        lblDongiaban1 = new javax.swing.JLabel();
        txtSoluong = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("Giỏ hàng");

        tblGiohang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tblGiohang);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Tên quần áo:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Mã quần áo");

        javax.swing.GroupLayout AnhLayout = new javax.swing.GroupLayout(Anh);
        Anh.setLayout(AnhLayout);
        AnhLayout.setHorizontalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 165, Short.MAX_VALUE)
        );
        AnhLayout.setVerticalGroup(
            AnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 254, Short.MAX_VALUE)
        );

        lblMau.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMau.setText("Số lượng:");

        lblDongiaban.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongiaban.setText("Đơn giá bán:");

        btnXoa.setText("Xóa");

        lblDongiaban1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblDongiaban1.setText("Tổng tiền:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblMaquanao)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblMau)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblDongiaban)
                                    .addComponent(lblTenquanao))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(lblDongiaban1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtTongtien, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(29, 29, 29))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(200, 200, 200)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 40, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(402, 402, 402)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMaquanao)
                            .addComponent(txtTenquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTenquanao)
                            .addComponent(txtMaquanao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDongiaban)
                            .addComponent(txtDongiaban, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblMau)
                            .addComponent(txtSoluong, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDongiaban1)
                            .addComponent(txtTongtien, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(107, 107, 107))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Anh;
    private javax.swing.JTextField txtTenquanao;
    private javax.swing.JButton btnXoa;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDongiaban;
    private javax.swing.JLabel lblDongiaban1;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblMau;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblGiohang;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtTongtien;
    // End of variables declaration//GEN-END:variables
}
