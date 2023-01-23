/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ounis.biblioteczka;

import com.ounis.utils.HTMLTags;
import com.ounis.ftools.FileWriterLn;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JScrollPane;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;


/**
 *
 * @author AndroidAppsPlatform
 */
public class TablesUtil extends javax.swing.JFrame implements ActionListener {

    enum EXPORT_2 {TXT, HTML};
    
    
//    private static final int EXPORT_TXT = 1;
//    private static final int EXPORT_HTML = 2;

    private static final String COL_SEP = ",";
    private static final String TXT_EXT = ".txt";
    private static final String HTM_EXT = ".html";
    
    private static final String TITLE = "Obs³uga tabel";
    private static final int SIZE_WIDTH = 900;
    private static final int SIZE_HEIGHT = 600;
    
    private Connection Conn;
    
    private JDialog dialog;
    
    //private static JFrame frmTablesUtil;
    
    
    /**
     * Creates new form TablesUtil
     */
    public TablesUtil(Connection aConn) throws SQLException {
        initComponents();
        setTitle(TITLE);
        //Biblioteczka.centerFrame(this);
        
        rbgGroup.add(rbExport2Txt);
        rbgGroup.add(rbExport2HTML);
        lblDBaseName.setText(Biblioteczka.DATA_BASE + 
                      " user: " + Biblioteczka.getUser());
        
        
        rbgReportGroup.add(rbRepHTMLbyA);
        rbgReportGroup.add(rbRepHTMLbyT);
        
        lstTables.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        
        this.Conn = aConn;
        
        if (Conn != null)
        {
            DefaultListModel<String> listModel = new DefaultListModel<String>();
            for (String table: getTables2Array())
            {
                listModel.addElement(table);
            }
            lstTables.setModel(listModel);
        }
        else;
 
        btnStart.addActionListener(this);
        btnReport.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // podstawowy eksport
        if (e.getSource().equals(btnStart))
        {
            if (lstTables.getSelectedIndex() > -1)
            {
                ListModel<String> lModel = lstTables.getModel();
                String tabName = lModel.getElementAt(lstTables.getSelectedIndex());

                if (rbExport2Txt.isSelected()) 
                {
                    JOptionPane.showMessageDialog(this,
                                  "Eksport '"+ tabName +"' do pliku .TXT");
                    exportTable(tabName, EXPORT_2.TXT);
                }
                if (rbExport2HTML.isSelected())
                {   
                    exportTable(tabName, EXPORT_2.HTML);
                    JOptionPane.showMessageDialog(this,
                                  "Eksport '"+ tabName + "' do pliku .HTML");
                }
            }
            else
                JOptionPane.showMessageDialog(this, "Wybierz tabelê do eksportu.");
        }
        
        // raport do HTML autorzy/tytu³y dla Chomika
        if (e.getSource().equals(btnReport))
        {
            
            
            if (rbRepHTMLbyA.isSelected()) {
//                RepHTMLbyAutor4CH repA = new RepHTMLbyAutor4CH(Conn, 
//                               
//                              CONST.SQL_4_LETTERS_AUTORS,
//                              CONST.SQL_4_LIST_AUTORS);
//                
//                repA.Generate();
//                repA.saveToFile(CONST.AUTORS_REP_FILE_NAME);
            }
            if (rbRepHTMLbyT.isSelected()) {
//                RepHTMLbyTitle4CH repT = new RepHTMLbyTitle4CH(Conn, 
//                               
//                              CONST.SQL_4_LETTERS_TITLES,
//                               CONST.SQL_4_LIST_TITLES );
//                repT.Generate();
//                repT.saveToFile(CONST.TITLES_REP_FILE_NAME);
            }
            
            if (rbRepHTMLbyT.isSelected() || rbRepHTMLbyA.isSelected()){
                RepHTML rHTML = new RepHTML(Conn, "", "");
                try {
                    String s = rHTML.lastRowData();
                    if (!s.isEmpty())
                        JOptionPane.showMessageDialog(null, s);
                    else
                        JOptionPane.showMessageDialog(null, 
                              "Nie mo¿na pobraæ wiersza");
                        
                }
                catch (SQLException except) {
                    JOptionPane.showMessageDialog(null, 
                          "B£¥D! Nie mo¿na pobraæ wiersza");
                    System.err.printf("\nB£¥D! Nie mo¿na pobraæ wiersza\n"+
                                  "%s", except.getLocalizedMessage());
                }
                rHTML.GenerateEx();
                if (rHTML.getCntTitles() + rHTML.getCntAutors() > 0) {
                    if (rHTML.saveToFile()) {
                        JOptionPane.showMessageDialog(this, 
                                      "Raporty HTML zosta³y zapisane!\n"
                                      + " - licznik autorów: " + String.valueOf(rHTML.getCntAutors())+"\n"
                                      + " - licznik tytu³ów: " + String.valueOf(rHTML.getCntTitles())+"\n"

                        );
                    }
                }
                else 
                    JOptionPane.showMessageDialog(this, "Listy s¹ puste, zapis anulowany...");
            }
                          
        }
     
    }
    
    private ArrayList<String> getTables2Array() throws SQLException
    {
        ArrayList<String> tab = new ArrayList<String>();
        if (!Conn.isClosed())
        {
            Statement stmt = Conn.prepareStatement("SHOW TABLES");
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            while (rs.next())
            {
                tab.add(rs.getString(1));
            }
        }
        
        return tab;
    }
    
    
    private boolean saveExport2File(ArrayList<String> aList, String aTable, String aExt) {
        boolean result = true;
        String fName = aTable + aExt;
        
        try {
                FileWriterLn fout = new FileWriterLn(String.format("%s",fName));
                for(String buff: aList) {
                    fout.writeln(buff);
                }
                fout.flush();
                fout.close();
        } catch (IOException ex) {
            System.err.printf("\nB³¹d zapisu eksportu do pliku: %s\n%s\n", fName,ex.getLocalizedMessage());
            result = false;
        }      
        return result;
    }
    
    
    private int getResultRowCount(ResultSet aRs) throws SQLException
    {
        // z jakiegoœ powodu nie dzia³a!!!!!
        // 22.01.2023
        int r = 0;
//        aRs.last();
//        r = aRs.getRow();
//        aRs.beforeFirst();
        return r;
    }
    
    private int getResultFieldCount(ResultSet aRs) throws SQLException
    {
        int result = 0;
        result = aRs.getMetaData().getColumnCount();
        return result;
    }
    
    private void table2TXT(String aTable, Statement stmt)
    {
        
        ArrayList<String> lines = new ArrayList<>(); 
        int rowNum = 1;
        
        
        
        //String timeStampFormat = "%d-%02d-%02d %02d:%02d:%02d";
        
        //Calendar cal = new GregorianCalendar();
        String timeStamp = RepHTML.getDateTimeStamp();
        
//        String timeStamp = String.format(timeStampFormat,
//                      cal.get(Calendar.YEAR),
//                      cal.get(Calendar.MONTH) + 1,
//                      cal.get(Calendar.DAY_OF_MONTH),
//                      cal.get(Calendar.HOUR_OF_DAY),
//                      cal.get(Calendar.MINUTE),
//                      cal.get(Calendar.SECOND));
        
        try {
            
            ResultSet rs = stmt.executeQuery(String.format("select * from %s", aTable));
            if (rs != null)
            {
                // ustalenie iloœci rekordów
                int rowCount = 0;
                rowCount = getResultRowCount(rs);
                // ustalanie iloœci pól
                int fieldCount = 0;
                fieldCount = getResultFieldCount(rs);
                String line = "";
                line = String.format(
                              "# %s\n# Tabela: %s\n# Liczba wierszy: %d\n#",
                              timeStamp, aTable, rowCount);
                System.out.println(line);
                lines.add(line);
                
                // generowanie nag³ówka tabelki
                line = "lp. ";
                for (int i = 1; i <= fieldCount; i++)
                {
                    line += rs.getMetaData().getColumnName(i) + COL_SEP;
                }
                line = line.substring(0, line.length() -1 );
                lines.add(line);
                System.out.println(line);
                
                while (rs.next())
                {
                    line = String.format("%d - ", rowNum);
                    for (int i = 1; i<= fieldCount;i++)
                    {
                        switch (rs.getMetaData().getColumnType(i))
                        {
                            case java.sql.Types.TIMESTAMP :
                                String tmp;
                                tmp = rs.getTimestamp(i).toLocalDateTime().toString();
                                tmp = tmp.replace("T", " ");
                                line += tmp + COL_SEP;
                                break;
                            default :
                                line += rs.getString(i) + COL_SEP;            
                        }
                    }
                    line = line.substring(0, line.length() - 1);
                    System.out.println(line);
                    lines.add(line);
                    
                    rowNum++;
                }
            }
        }
        catch (SQLException exception)
        {
            System.out.println("Problem z wykonaniem table2TXT(): " + exception);
        }

        String fName = aTable + TXT_EXT;
        try
        {
            FileWriter fw = new FileWriter(fName);
            for (String s: lines)
            {
                fw.write(s + System.getProperty("line.separator"));
            }
            fw.flush();
            fw.close();
        }
        catch (IOException e)
        {

            System.out.println(String.format("Problem z zapisem do pliku: %s - %s", fName, e));
        }
        
    }
    
    private void table2HTML(String aTable, Statement stmt)
    {
        boolean canSave = true;
        
        ArrayList<String> lines = new ArrayList<>();
        String line = "";
        int rowCount = 1;
        int colCount = 0;
        HTMLTags h = new HTMLTags();
        
        
        try {
            ResultSet rs = stmt.executeQuery(String.format("select * from %s", aTable));
            
            if (rs != null) {
                rowCount = getResultRowCount(rs);
                colCount = getResultFieldCount(rs);
                colCount++; // dodatkowa kolumna na liczbê porz¹dkow¹
                lines.add(h.getHTMLOPEN());
                lines.add(h.getHEADOPEN());
//                href=\"styl.css\" rel=\"stylesheet\" type=\"text/css\"
//                lines.add(h.getLINK("rel=stylesheet type=\"text/css\" href=\"styl.html\""));
                lines.add(h.getLINK("href=\"styl.css\" rel=\"stylesheet\" type=\"text/css\""));
                lines.add(h.getTITLE(String.format("Tabela: %s", aTable)));
                lines.add(h.getHEADCLOSE());
                lines.add(h.getBODYOPEN());
                
                line = h.getTABLEO(" border=\"0\"");
                lines.add(line);
                lines.add(h.getTRO(""));
                line = h.getTD(String.format("id=\"BIALY\" colspan=\"%d\"", colCount), 
                           "Tabela: " + h.getB(aTable) + "  Iloœæ wierszy: " + 
                          h.getB(h.getFont("color=\"#FF0000\"", String.valueOf(rowCount))));
                lines.add(line);
                lines.add(h.getTRC());
                
                lines.add(h.getTRO(""));
                lines.add(h.getTH("id=\"NAGINFO\"", "lp."));
                line = "";
                for (int y = 1;y <= colCount - 1; y ++) {
                    line += (h.getTH("id=\"NAGINFO\"", rs.getMetaData().getColumnName(y)));
                }
                lines.add(line);
                lines.add(h.getTRC());
                
                // dane z tabeli rekord po rekordzie
                int cnt = 1;
                while (rs.next()) {
                    line = h.getTRO("");
                    line += h.getTD("", String.valueOf(cnt));
                    for (int z = 1;z <= colCount - 1;z++) {
                        line += h.getTD("", rs.getString(z));
                    }
                    line += h.getTRC();
                    lines.add(line);
                    cnt++;
                }
                
                lines.add(h.getTRO(""));
                lines.add(h.getTR("", h.getTD(String.format("colspan=\"%d\"",colCount), 
                              h.getFont(1, String.format("Wygenerowano: %s", RepHTML.getDateTimeStamp()) ))));
                lines.add(h.getTABLEC());
                lines.add(h.getBODYCLOSE());
                lines.add(h.getHTMLCLOSE());
                
            }
          
//            for(String l: lines) {
//                System.out.printf("%s\n", l);
//            }
            
        } catch (SQLException ex) {
            System.err.printf("\nJakiœ b³¹d podczas exportu HTML\n%s\n", ex.getLocalizedMessage());
            canSave = false;
        }
        if (canSave) {
            if (!saveExport2File(lines, aTable, HTM_EXT))
                JOptionPane.showMessageDialog(this, String.format("B³¹d podczas zapisu do: %s%s", aTable, HTM_EXT));
        }
        else
            JOptionPane.showMessageDialog(this, "B³¹d podczas przygotowywania eksportu HTML!\nAnulowanie zapisu...");
        
    }
    
    private void exportTable(String aTable, EXPORT_2 exportMode)
    {
        Statement stmt = null;
        try
        {
//            stmt = Conn.createStatement();
//            stmt.execute(String.format("select * from %s", aTable));
            
            stmt = Conn.prepareStatement(String.format("select * from %s", 
                          aTable));
            
            
            switch (exportMode)
            {
                case TXT  :
                    table2TXT(aTable, stmt);
                    break;
                case HTML :
                    table2HTML(aTable, stmt);
                    break;
            }
            
            stmt.close();
        }
        catch(SQLException exception)
        {
            System.out.println("Problem z wykonaniem exportTable(): " + exception);
        }
        
        
    }
    

   
    
    
    @Override
    public void paint(Graphics g){
        super.paint(g);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rbgGroup = new javax.swing.ButtonGroup();
        rbgReportGroup = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        lblDBaseName = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstTables = new javax.swing.JList<>();
        rbExport2Txt = new javax.swing.JRadioButton();
        rbExport2HTML = new javax.swing.JRadioButton();
        btnStart = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        rbRepHTMLbyA = new javax.swing.JRadioButton();
        rbRepHTMLbyT = new javax.swing.JRadioButton();
        btnReport = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(SIZE_WIDTH, SIZE_HEIGHT);

        jLabel1.setText("Baza danych:");

        lblDBaseName.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N

        jScrollPane2.setViewportView(lstTables);

        rbExport2Txt.setText("Export do .TXT");

        rbExport2HTML.setText("Export do .HTML");

        btnStart.setText("Export");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel2.setText("S³uchowiska:");

        rbRepHTMLbyA.setText("Raport HTML - Autorzy");

        rbRepHTMLbyT.setText("Raport HTML - Tytu³y");

        btnReport.setText("Raportuj");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbRepHTMLbyA)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(54, 54, 54)
                                .addComponent(btnReport))
                            .addComponent(rbRepHTMLbyT))))
                .addContainerGap(66, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(rbRepHTMLbyA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbRepHTMLbyT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReport)
                .addContainerGap(58, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbExport2HTML)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rbExport2Txt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnStart))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(lblDBaseName, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(199, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDBaseName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnStart)
                    .addComponent(rbExport2Txt, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbExport2HTML, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(130, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    @Deprecated
    public void showModal(Component parent) {
        
        Frame owner = null;
        if (parent instanceof Frame)
            owner = (Frame) parent;
        else
            owner = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);
        
        if (dialog == null || dialog.getOwner() != owner)
        {
             dialog = new JDialog(owner, true);
             dialog.add(this);
             dialog.setTitle(TITLE);
             //dialog.getRootPane().setDefaultButton(btnClose);
             dialog.pack();
        } 
        
        dialog.setLocationRelativeTo(parent);
           //FramesUtils.centerWindow(dialog, -1, -1);
          // set title and show dialog

        dialog.setResizable(false);
        dialog.setVisible(true);
        
    }
    
    
    public static void showTablesUtil(Connection aConn) throws SQLException
    {
        final JFrame frmTablesUtil = new TablesUtil(aConn);
        //if (frmTablesUtil == null)
//            frmTablesUtil = new TablesUtil(aConn);
        
        //        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                frmTablesUtil.setResizable(false);

                frmTablesUtil.setSize(SIZE_WIDTH,SIZE_HEIGHT);
                Biblioteczka.centerFrame(frmTablesUtil);
                frmTablesUtil.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);


                frmTablesUtil.setVisible(true);
            }
        });
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TablesUtil().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnReport;
    private javax.swing.JButton btnStart;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblDBaseName;
    private javax.swing.JList<String> lstTables;
    private javax.swing.JRadioButton rbExport2HTML;
    private javax.swing.JRadioButton rbExport2Txt;
    private javax.swing.JRadioButton rbRepHTMLbyA;
    private javax.swing.JRadioButton rbRepHTMLbyT;
    private javax.swing.ButtonGroup rbgGroup;
    private javax.swing.ButtonGroup rbgReportGroup;
    // End of variables declaration//GEN-END:variables


}
