package com.rizki.mufrizal.aplikasi.inventory.controller;

import com.rizki.mufrizal.aplikasi.inventory.App;
import com.rizki.mufrizal.aplikasi.inventory.abstractTableModel.BarangAbstractTableModel;
import com.rizki.mufrizal.aplikasi.inventory.abstractTableModel.TableAutoResizeColumn;
import com.rizki.mufrizal.aplikasi.inventory.domain.Barang;
import com.rizki.mufrizal.aplikasi.inventory.domain.JenisBarang;
import com.rizki.mufrizal.aplikasi.inventory.view.BarangView;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.swing.JOptionPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @Author Rizki Mufrizal <mufrizalrizki@gmail.com>
 * @Since Mar 18, 2016
 * @Time 10:56:00 PM
 * @Encoding UTF-8
 * @Project Aplikasi-Inventory
 * @Package com.rizki.mufrizal.aplikasi.inventory.controller
 *
 */
public class BarangController {

    private final BarangView barangView;
    private static final Logger LOGGER = LoggerFactory.getLogger(BarangController.class);
    private BarangAbstractTableModel barangAbstractTableModel;
    private final TableAutoResizeColumn tableAutoResizeColumn = new TableAutoResizeColumn();

    public BarangController(BarangView barangView) {
        this.barangView = barangView;
    }

    //paging
    private Integer totalRows = 0;
    private Integer pageNumber = 1;
    private Integer totalPage = 1;
    private Integer rowsPerPage = 10;

    public void ambilDataBarang() {
        LOGGER.info("Ambil data barang");
        rowsPerPage = Integer.valueOf(this.barangView.getPerPage().getSelectedItem().toString());
        totalRows = App.barangService().jumlahBarang();
        Double dbTotalPage = Math.ceil(totalRows.doubleValue() / rowsPerPage.doubleValue());
        totalPage = dbTotalPage.intValue();

        if (pageNumber == 1) {
            this.barangView.getFirst().setEnabled(Boolean.FALSE);
            this.barangView.getPrevious().setEnabled(Boolean.FALSE);
        } else {
            this.barangView.getFirst().setEnabled(Boolean.TRUE);
            this.barangView.getPrevious().setEnabled(Boolean.TRUE);
        }

        if (pageNumber.equals(totalPage)) {
            this.barangView.getNext().setEnabled(Boolean.FALSE);
            this.barangView.getLast().setEnabled(Boolean.FALSE);
        } else {
            this.barangView.getNext().setEnabled(Boolean.TRUE);
            this.barangView.getLast().setEnabled(Boolean.TRUE);
        }

        this.barangView.getLabelPaging().setText("Page " + pageNumber + " of " + totalPage);
        this.barangView.getLabelTotalRecord().setText("Total Record " + totalRows);

        barangAbstractTableModel = new BarangAbstractTableModel(App.barangService().ambilBarangs(pageNumber, rowsPerPage));
        this.barangView.getTabelBarang().setModel(barangAbstractTableModel);
        tableAutoResizeColumn.autoResizeColumn(this.barangView.getTabelBarang());
        LOGGER.info("Paging : {}", pageNumber);
    }

    public void firstPaging() {
        pageNumber = 1;
        ambilDataBarang();
        LOGGER.info("Paging awal : {}", pageNumber);
    }

    public void PreviousPaging() {
        if (pageNumber > 1) {
            pageNumber -= 1;
            ambilDataBarang();
            LOGGER.info("Paging sebelum : {}", pageNumber);
        }
    }

    public void nextPaging() {
        if (pageNumber < totalPage) {
            pageNumber += 1;
            ambilDataBarang();
            LOGGER.info("Paging selanjutnya : {}", pageNumber);
        }
    }

    public void lastPaging() {
        pageNumber = totalPage;
        ambilDataBarang();
        LOGGER.info("Paging akhir : {}", pageNumber);
    }

    public void refresh() {
        ambilDataBarang();
        LOGGER.info("refresh paging : {}", pageNumber);
    }
    //end paging

    public void tampilkanBarang() {
        try {
            Integer index = this.barangView.getTabelBarang().getSelectedRow();
            this.barangView.getIdBarang().setText(String.valueOf(this.barangView.getTabelBarang().getValueAt(index, 1)));
            this.barangView.getNamaBarang().setText(String.valueOf(this.barangView.getTabelBarang().getValueAt(index, 2)));
            this.barangView.getJenisBarang().setSelectedItem(String.valueOf(this.barangView.getTabelBarang().getValueAt(index, 3)));
            java.util.Date tanggal = new SimpleDateFormat("yyyy-MM-d").parse(String.valueOf(this.barangView.getTabelBarang().getValueAt(index, 4)));
            this.barangView.getTanggalKadaluarsa().setDate(tanggal);

            String hargaSatuanBarang = this.barangView.getTabelBarang().getValueAt(index, 5).toString();
            this.barangView.getHargaSatuan().setText(hargaSatuanBarang.split(" ")[1]);
            this.barangView.getJumlahBarang().setText(String.valueOf(this.barangView.getTabelBarang().getValueAt(index, 6)));
        } catch (ParseException ex) {
            LOGGER.error("error di : {}", ex);
        }
    }

    public void clear() {
        this.barangView.getIdBarang().setText(null);
        this.barangView.getNamaBarang().setText(null);
        this.barangView.getJenisBarang().setSelectedIndex(0);
        this.barangView.getTanggalKadaluarsa().setDate(null);
        this.barangView.getHargaSatuan().setText(null);
        this.barangView.getJumlahBarang().setText(null);
    }

    public void simpanBarang() {
        Barang barang = new Barang();
        barang.setNamaBarang(this.barangView.getNamaBarang().getText());
        barang.setJenisBarang(JenisBarang.valueOf(this.barangView.getJenisBarang().getSelectedItem().toString()));
        barang.setTanggalKadaluarsa(this.barangView.getTanggalKadaluarsa().getDate());
        barang.setHargaSatuanBarang(BigDecimal.valueOf(Long.parseLong(this.barangView.getHargaSatuan().getText())));
        barang.setJumlahBarang(Integer.parseInt(this.barangView.getJumlahBarang().getText()));

        App.barangService().simpanBarang(barang);

        LOGGER.debug("prosess simpan barang : {}", barang);

        JOptionPane.showMessageDialog(null, "Data barang berhasil disimpan", "Info", JOptionPane.INFORMATION_MESSAGE);
        ambilDataBarang();
        clear();
    }

    public void editBarang() {
        Barang barang = new Barang();
        barang.setIdBarang(this.barangView.getIdBarang().getText());
        barang.setNamaBarang(this.barangView.getNamaBarang().getText());
        barang.setJenisBarang(JenisBarang.valueOf(this.barangView.getJenisBarang().getSelectedItem().toString()));
        barang.setTanggalKadaluarsa(this.barangView.getTanggalKadaluarsa().getDate());
        barang.setHargaSatuanBarang(BigDecimal.valueOf(Double.valueOf(this.barangView.getHargaSatuan().getText())));
        barang.setJumlahBarang(Integer.parseInt(this.barangView.getJumlahBarang().getText()));

        App.barangService().editBarang(barang);

        LOGGER.debug("prosess edit barang : {}", barang);

        JOptionPane.showMessageDialog(null, "Data barang berhasil diedit", "Info", JOptionPane.INFORMATION_MESSAGE);
        ambilDataBarang();
        clear();
    }

    public void hapusBarang() {
        Barang barang = new Barang();
        barang.setIdBarang(this.barangView.getIdBarang().getText());
        barang.setNamaBarang(this.barangView.getNamaBarang().getText());
        barang.setJenisBarang(JenisBarang.valueOf(this.barangView.getJenisBarang().getSelectedItem().toString()));
        barang.setTanggalKadaluarsa(this.barangView.getTanggalKadaluarsa().getDate());
        barang.setHargaSatuanBarang(BigDecimal.valueOf(Double.valueOf(this.barangView.getHargaSatuan().getText())));
        barang.setJumlahBarang(Integer.parseInt(this.barangView.getJumlahBarang().getText()));

        int pilih = JOptionPane.showConfirmDialog(null, "Apakah data ingin dihapus ?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (pilih == JOptionPane.YES_OPTION) {

            App.barangService().hapusBarang(barang);
            LOGGER.debug("prosess hapus barang : {}", barang);

            JOptionPane.showMessageDialog(null, "Data barang berhasil dihapus", "Info", JOptionPane.INFORMATION_MESSAGE);
            ambilDataBarang();
            clear();
        }
    }
}
