/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package GUI;

import java.io.File;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
/**
 *
 * @author Neo 16
 */
public class Nhanvienlaphoadon extends javax.swing.JPanel implements Refreshable {

    private final Service.IOrderService orderService = new Service.impl.OrderServiceImpl();
    private final Service.IProductService productService = new Service.impl.ProductServiceImpl();

    @Override
    public void refresh() {
        loadDuLieuGioHang();
    }

    /**
     * Creates new form Nhanvienlaphoadon1
     */
    public Nhanvienlaphoadon() {
        initComponents();
        styleUI();
        loadDuLieuGioHang();

        tblLaphoadon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblLaphoadon.getSelectedRow();
                if (row >= 0) {
                    txtMakhachhang.setText(tblLaphoadon.getValueAt(row, 0).toString());
                    txtMaquanao.setText(tblLaphoadon.getValueAt(row, 1).toString());
                    txtDongiaban.setText(tblLaphoadon.getValueAt(row, 2).toString());
                    txtSoluong.setText(tblLaphoadon.getValueAt(row, 3).toString());
                    txtTongtien.setText(tblLaphoadon.getValueAt(row, 4).toString());

                    try {
                        int maQA = Integer.parseInt(tblLaphoadon.getValueAt(row, 1).toString());
                        productService.getProductById(maQA).ifPresent(sp -> {
                            if (sp.getAnh() != null && !sp.getAnh().isEmpty()) {
                                hienThiAnh(sp.getAnh());
                            }
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        btnTimkiem.addActionListener(e -> {
            String maKH = txtMakhachhang.getText().trim();
            if (!maKH.isEmpty()) {
                String[] columnNames = {"Mã Khách hàng", "Mã Quần Áo", "Đơn Giá Bán", "Số Lượng Đặt", "Tổng Tiền"};
                DefaultTableModel model = new DefaultTableModel(columnNames, 0);
                try {
                    java.util.List<Model.ItemGioHang> items = orderService.getGioHang(maKH, 1);
                    for (Model.ItemGioHang item : items) {
                        model.addRow(new Object[] {
                            item.getMaKhachHang(),
                            item.getSanPham() != null ? item.getSanPham().getMaQuanAo() : "",
                            item.getDonGiaBan(),
                            item.getSoLuongDat(),
                            item.getTongTien()
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                tblLaphoadon.setModel(model);
            } else {
                loadDuLieuGioHang();
            }
        });

        btnLammoi.addActionListener(e -> {
            txtMakhachhang.setText("");
            txtMaquanao.setText("");
            txtDongiaban.setText("");
            txtTongtien.setText("");
            txtSoluong.setText("");

            Anh.removeAll();
            Anh.repaint();

            loadDuLieuGioHang();
        });

        btnLaphoadon.addActionListener(e -> {
            int row = tblLaphoadon.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần lập hóa đơn!");
                return;
            }

            try {
                String maKH = tblLaphoadon.getValueAt(row, 0).toString();
                int maQA = Integer.parseInt(tblLaphoadon.getValueAt(row, 1).toString());
                java.math.BigDecimal donGia = new java.math.BigDecimal(tblLaphoadon.getValueAt(row, 2).toString());
                int soLuong = Integer.parseInt(tblLaphoadon.getValueAt(row, 3).toString());

                String maNV = Service.UserSession.getInstance().getMaTaiKhoan();

                Model.HoaDonBan hdb = orderService.lapHoaDonDonLe(maKH, maNV, maQA, soLuong, donGia);
                JOptionPane.showMessageDialog(this, "Lập hóa đơn bán số " + hdb.getSoHoaDon() + " thành công!");
                loadDuLieuGioHang();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi lập hóa đơn: " + ex.getMessage());
            }
        });
    }

    public void hienThiAnh(String duongDan) {
        try {
            File file = new File(duongDan);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(null, "Ảnh không tồn tại tại: " + duongDan);
                return;
            }
    
            ImageIcon icon = new ImageIcon(duongDan);
            Image img = icon.getImage().getScaledInstance(Anh.getWidth(), Anh.getHeight(), Image.SCALE_SMOOTH);
            JLabel lbl = new JLabel(new ImageIcon(img));
            Anh.removeAll();
            Anh.setLayout(new BorderLayout());
            Anh.add(lbl, BorderLayout.CENTER);
            Anh.revalidate();
            Anh.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Không thể hiển thị ảnh: " + duongDan);
        }
    }

    public void loadDuLieuGioHang() {
        String[] columnNames = {"Mã Khách hàng", "Mã Quần Áo", "Đơn Giá Bán", "Số Lượng Đặt", "Tổng Tiền"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            java.util.List<Model.ItemGioHang> items = orderService.getGioHangByTrangThai(1);
            for (Model.ItemGioHang item : items) {
                model.addRow(new Object[] {
                    item.getMaKhachHang(),
                    item.getSanPham() != null ? item.getSanPham().getMaQuanAo() : "",
                    item.getDonGiaBan(),
                    item.getSoLuongDat(),
                    item.getTongTien()
                });
            }
            tblLaphoadon.setModel(model);
            GUI.theme.UITheme.styleTable(tblLaphoadon);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu giỏ hàng: " + ex.getMessage());
        }
    }

    private void styleUI() {
        GUI.theme.UITheme.styleContainer(this);
        GUI.theme.UITheme.stylePrimaryButton(btnLaphoadon);
        GUI.theme.UITheme.stylePrimaryButton(btnTimkiem);
        GUI.theme.UITheme.styleSecondaryButton(btnLammoi);
        GUI.theme.UITheme.styleTable(tblLaphoadon);
        Anh.setBackground(java.awt.Color.WHITE);
        Anh.setBorder(javax.swing.BorderFactory.createLineBorder(GUI.theme.UITheme.BORDER_COLOR, 1, true));
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
        tblLaphoadon = new javax.swing.JTable();
        lblMaquanao = new javax.swing.JLabel();
        txtMakhachhang = new javax.swing.JTextField();
        txtMaquanao = new javax.swing.JTextField();
        lblTenquanao = new javax.swing.JLabel();
        btnTimkiem = new javax.swing.JButton();
        Anh = new javax.swing.JPanel();
        lblMau = new javax.swing.JLabel();
        txtDongiaban = new javax.swing.JTextField();
        lblDongiaban = new javax.swing.JLabel();
        btnLaphoadon = new javax.swing.JButton();
        btnLammoi = new javax.swing.JButton();
        txtTongtien = new javax.swing.JTextField();
        lblDongiaban1 = new javax.swing.JLabel();
        txtSoluong = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(173, 216, 230));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setText("LẬP HÓA ĐƠN");

        tblLaphoadon.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblLaphoadon);

        lblMaquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblMaquanao.setText("Mã khách hàng:");

        lblTenquanao.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        lblTenquanao.setText("Mã quần áo");

        btnTimkiem.setText("Tìm kiếm");
        btnTimkiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimkiemActionPerformed(evt);
            }
        });

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

        btnLaphoadon.setText("Lập hóa đơn");

        btnLammoi.setText("Làm mới");

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
                                .addComponent(txtMakhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                .addGap(29, 29, 29)
                                .addComponent(btnTimkiem)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addGap(200, 200, 200)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(Anh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 19, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnLaphoadon)
                .addGap(199, 199, 199)
                .addComponent(btnLammoi, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(313, 313, 313))
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
                            .addComponent(txtMakhachhang, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTimkiem, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnLammoi, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                    .addComponent(btnLaphoadon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void btnTimkiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimkiemActionPerformed
    }//GEN-LAST:event_btnTimkiemActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Anh;
    private javax.swing.JButton btnLammoi;
    private javax.swing.JButton btnLaphoadon;
    private javax.swing.JButton btnTimkiem;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDongiaban;
    private javax.swing.JLabel lblDongiaban1;
    private javax.swing.JLabel lblMaquanao;
    private javax.swing.JLabel lblMau;
    private javax.swing.JLabel lblTenquanao;
    private javax.swing.JTable tblLaphoadon;
    private javax.swing.JTextField txtDongiaban;
    private javax.swing.JTextField txtMakhachhang;
    private javax.swing.JTextField txtMaquanao;
    private javax.swing.JTextField txtSoluong;
    private javax.swing.JTextField txtTongtien;
    // End of variables declaration//GEN-END:variables
}
