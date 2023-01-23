
package com.ounis.biblioteczka;


import com.ounis.dbutils.DBConnect;
import com.ounis.ftools.FTools;
import java.awt.Dimension;
import java.awt.Toolkit;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author AndroidAppsPlatform
 */
public class Biblioteczka {
    
    
    public static final String BIBLIOTECZKA_INI = "biblioteczka.ini";
    public static final String INI_USER_ID = "USERID";
    public static final String INI_USER_PASS = "USERPASS";
    
        
    public static final String DB_PROTOCOL = "mysql";
    public static final String DB_SERVER = "localhost";
    public static final String DB_PORT = "3306";
    public static final String DB_SERVER_PORT = String.format("%s:%s" ,DB_SERVER, DB_PORT);
    
    public static final String DB_BIBLIO = "db_biblio";
    public static final String DB_TEST = "test";
    public static final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    public static final String DATA_BASE = DB_TEST; //DB_BIBLIO;
    public static final String DB_TIME_ZONE = "serverTimezone=UTC";
    
    public static final String DB_URL = "jdbc" + ":" +  DB_PROTOCOL + "://" + 
                  DB_SERVER_PORT + "/" + DATA_BASE;
    
    public static final String ERR_DB_ACCESS_DENIED_4_USER = "Access denied for user";
    
    private static final String DEF_INI_USER_ID = "test";//"root";
    private static final String DEF_INI_USER_PASS = "";
    
    
    public static final String TABLE_AUTORZY = "autorzy";
    public static final int TABLE_AUTORZY_IDX = 0;
    
    public static final String TABLE_DB_VER = "db_ver";
    public static final int TABLE_DB_VER_IDX = 1;
    
    public static final String TABLE_FILMY = "filmy";
    public static final int TABLE_FILMY_IDX = 2;
    
    
    public static final String TABLE_KSIAZKI = "ksiazki";
    public static final int TABLE_KSIAZKI_IDX = 3;
    
    public static final String TABLE_NAGRANIA = "nagrania";
    public static final int TABLE_NAGRANIA_IDX = 4;
    
    public static final String TABLE_OSOBY = "osoby";
    public static final int TABLE_OSOBY_IDX = 5;
    
    public static final String TABLE_RAPORTY = "raporty";
    public static final int TABLE_RAPORTY_IDX = 6;
    
    public static final String TABLE_SLOWN_KRAJE = "slown_kraje";
    public static final int TABLE_SLOWN_KRAJE_IDX = 7;
    
    public static final String TABLE_SLOWN_RODZ_MUZ = "slown_rodz_muz";
    public static final int TABLE_SLOWN_RODZ_MUZ_IDX = 8;
    
    public static final String TABLE_SLUCHOWISKA = "sluchowiska";
    public static final int TABLE_SLUCHOWISKA_IDX = 9;
    
    public static final String TABLE_WYKONAWCY = "wykonawcy";
    public static final int TABLE_WYKONAWCY_IDX = 10;
    
    public static final String TABLE_WYPOZYCZENIA = "wypozyczenia";
    public static final int TABLE_WYPOZYCZENIA_IDX = 11;
    
    public static final String[] DB_BIBLIO_TABLES = {TABLE_AUTORZY, TABLE_DB_VER,
        TABLE_FILMY, TABLE_KSIAZKI, TABLE_NAGRANIA, TABLE_OSOBY, TABLE_RAPORTY,
        TABLE_SLOWN_KRAJE, TABLE_SLOWN_RODZ_MUZ, TABLE_SLUCHOWISKA, 
        TABLE_WYKONAWCY, TABLE_WYPOZYCZENIA};
    
 //F:\Program Files (x86)\MySQL\Connector.J 5.1\mysql-connector-java-5.1.40-bin.jar   
    private static String dbProtocol = "";
    public static String getDbProtocol() {
        return dbProtocol;
    }
    
    private static String dataBase = "";
    public static String getDataBase() {
        return dataBase;
    }
    
    
    static {
        
       System.out.printf("\nLokacja uruchomieniowa: %s\n\n", FTools.getAppPath());

        
       Connection aConn = null;
       
       String driverName = loadIniValue("DRIVER_NAME", DRIVER_NAME);
       dbProtocol = loadIniValue("DB_PROTOCOL", DB_PROTOCOL);
       String dbServer = loadIniValue("DB_SERVER", DB_SERVER);
       String dbPort = loadIniValue("DB_PORT", DB_PORT);
       dataBase = loadIniValue("DATA_BASE", DATA_BASE);
       String timeZone = loadIniValue("TIME_ZONE", DB_TIME_ZONE);
       
       String _user = loadIniValue(INI_USER_ID, "");//DEF_INI_USER_ID); 
       String passwd = loadIniValue(INI_USER_PASS, "");//DEF_INI_USER_PASS); 

//       try
//       {
//        Class.forName(DRIVER_NAME); 
//        DriverManager.setLogWriter(new PrintWriter((System.err)));
//        System.out.println("Nawi¹zujemy po³¹czenie...");
//             aConn = 
//                DriverManager.getConnection(DB_URL, _user, passwd);        
////              MainConn = aConn;
//       }      
//        catch (ClassNotFoundException e)
//        {
//            System.out.println("Nie mo¿na pobraæ sterownika " + e);
//            aConn = null;
//        } 
//        catch (SQLException e) {
//            System.out.println("Nie mo¿na nawi¹zaæ po³¹czenia z baz¹ danych " + e);           
//            aConn = null;
//        }
////       catch (CommunicationException e){
////           System.out.printlnt("Nie mo¿na nawi¹zaæ po³¹czenia z serwerem " + e);
////           aConn = null;
////       }
//       finally
//       {
//           MainConn = aConn;
//           user = _user;
//       }
       
        
        
       DBConnect dbConnect = new DBConnect(driverName, dbProtocol, dbServer, dbPort, dataBase, timeZone);
                     //new DBConnect(DRIVER_NAME, DB_PROTOCOL, user, user, DATA_BASE);
        try {
            
          System.out.println("Nawi¹zujemy po³¹czenie...");
          dbConnect.Connect(_user, passwd);
        } catch (ClassNotFoundException ex) {
                   System.err.println("Nie mo¿na pobraæ sterownika " + ex);
                   aConn = null;
     
        } catch (SQLException ex) {
                   System.out.println("Nie mo¿na nawi¹zaæ po³¹czenia z baz¹ danych " + ex); 
                   if (ex.toString().contains(ERR_DB_ACCESS_DENIED_4_USER)) {
                       System.out.println(" - Odmowma dostêpu dla u¿ytkownika: " + _user);
                   }
                   
                   aConn = null;
        }
        finally {
           if (dbConnect.isConnect())
               System.out.printf("Po³¹czenie powiod³o siê...\n");
           MainConn = dbConnect.getConnection();
           USER = _user;
        }
       
    }  
       
    
    
    private static final Connection MainConn;
    public static final Connection getConn()
    {
        return MainConn;
    }
    private static final String USER;
    public static String getUser(){
        return USER;
    }

    private static String loadIniValue(String aKey, String aDefValue)
    {
        String result = "";
        boolean prefLoaded = false;
        
        FileInputStream in = null;
        Properties sett = new Properties();
        try
        {
            in = new FileInputStream(BIBLIOTECZKA_INI);
            sett.load(in);
            result = sett.getProperty(aKey, aDefValue);
            prefLoaded = !result.isEmpty();
            
            in.close();
            
        }
        catch (FileNotFoundException e)
        {
            prefLoaded = false;
            System.out.println(String.format("Problem z otwarciem pliku: %s - %s",
                          BIBLIOTECZKA_INI, e));
        }
        catch (IOException e)
        {
            prefLoaded = false;
            System.out.println(String.format("Problem z wczytaniem z pliku: %s - %s",
                          BIBLIOTECZKA_INI, e));
        }
        
        
        if (!prefLoaded) {
            if (!"".equals(aDefValue)) 
                result = aDefValue;
            else {
                System.out.print(String.format("Nale¿y podaæ wartoœæ parametru %s!\n>", aKey));
                Scanner scnr = new Scanner(System.in);
            result = scnr.next();
            result = result.strip();
          }
        }

        
        System.out.println(String.format("\n*********\n%s = %s%s\n************\n",aKey,result,
                (result == aDefValue) ? "*" : ""));
        
        return result;
    }
    
    
    public static void centerFrame(JFrame frm)
    {
        Toolkit kit = frm.getToolkit();
        Dimension frmSize = kit.getScreenSize();

        Dimension scrSize = kit.getScreenSize();

        frm.setLocation(frmSize.width / 2 - frm.getWidth() / 2,
                      frmSize.height / 2 - frm.getHeight() / 2);

    }
    
    public static boolean  isConnect()
    {
        boolean result = (MainConn != null);
        
        if (result)
            return true;
        else
            return false;
        //return  MainConn != null?true:false;
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
       //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TablesUtil.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        
        
        if (isConnect())
            MainFrame.showMainFrame(MainConn);
        else
        {
            // tutaj nic siê nie dzieje sterownik nie zosta³ wczytany albo
            // po³¹czenie z baz¹ danych nie zosta³o nawi¹zane
           JOptionPane.showMessageDialog(null,
              "B³¹d po³¹czenia z baz¹ danych: " + 
               Biblioteczka.getDataBase() + "\n"+
               "- Upewnij siê, ¿e nazwa: " + Biblioteczka.getDataBase() + " jest w³aœciwa, \n" +
               "- Upewnij siê ¿e server: " + Biblioteczka.getDbProtocol() + " jest uruchomiony, \n"+
               "- U¿ytkonik: '" + Biblioteczka.getUser() + "' mo¿e nie mieæ praw dostêpu, \n"+        
               "- Je¿eli kompilujesz podepnij .jar w "+
               "    Libraries | Add JAR/Folder\n "+
               "- Je¿eli uruchamiasz sprawdŸ czy CLASSPATH wskazuje\n"+
               "    na folder z paczk¹ (.jar) dostarczon¹ przez producenta\n"+
               "    sterownika. "+
               "(np.: D:\\Java\\mysql-connector-java-5.1.38\\*)"
            );
            System.out.println("Program przerywa dzia³anie...");  
        }
        /* Create and display the form */
//        if (MainConn != null)
//                java.awt.EventQueue.invokeLater(new Runnable() {
//                    public void run() {
//                        JFrame frmTabUtil;
//
//                        try
//                        {
//                            frmTabUtil = new TablesUtil(Biblioteczka.MainConn);
//                            frmTabUtil.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                            
//                            centerFrame(frmTabUtil);
//
//                            frmTabUtil.addWindowListener(new WindowAdapter() {
//                                 @Override
//                                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
//                                    try
//                                    {
//                                        if (MainConn != null)
//                                        {    
//                                            Biblioteczka.MainConn.close();
//                                            System.out.println(
//                                                      String.format("Zamykamy pol¹czenie: %s i opuszczamy program: %s ",
//                                                                    MainConn, Biblioteczka.class.getName()));
//                                        }
//                                    }
//                                    catch (SQLException e)
//                                    {
//                                        System.out.println("B³¹d koñcowego zamkniêcia po³¹czenia. " + e);
//                                    }
//                                }
//                            });
//
//
//                            frmTabUtil.setVisible(true);
//                        }
//                        catch (SQLException e)
//                        {
//                            System.out.println("B³¹d przekazania po³¹czenia " + e);
//                        }
//
//                    }
//                });
//        else
//        {
//            // tutaj nic siê nie dzieje sterownik nie zosta³ wczytany albo
//            // po³¹czenie z baz¹ danych nie zosta³o nawi¹zane
//           JOptionPane.showMessageDialog(null,
//              "B³¹d po³¹czenia z baz¹ danych: " + 
//               Biblioteczka.DB_BIBLIO + "\n"+
//               "- Upewnij siê, ¿e nazwa: " + DB_BIBLIO + " jest w³aœciwa, \n" +
//               "- Upewnij siê ¿e server: " + DB_PROTOCOL + " jest uruchomiony, \n"+
//               "- Je¿eli kompilujesz podepnij .jar w "+
//               "    Libraries | Add JAR/Folder\n "+
//               "- Je¿eli uruchamiasz sprawdŸ czy CLASSPATH wskazuje\n"+
//               "    na folder z paczk¹ (.jar) dostarczon¹ przez producenta\n"+
//               "    sterownika. "+
//               "(np.: D:\\Java\\mysql-connector-java-5.1.38\\*)"
//            );
//            System.out.println("Program przerywa dzia³anie...");
//        }
     
        
        //biblioteczka.MainConn.close();
        //System.out.println(DRIVER_NAME+"\n"+DB_URL + "\n" + Biblioteczka.MainConn);
        
    }

}
