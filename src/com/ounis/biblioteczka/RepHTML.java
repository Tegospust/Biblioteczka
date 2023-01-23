

package com.ounis.biblioteczka;

import com.ounis.utils.HTMLTags;
//import com.mysql.jdbc.PreparedStatement;
import com.mysql.cj.jdbc.PreparedStatementWrapper;
import com.ounis.ftools.FTools;
import com.ounis.ftools.FileWriterLn;
import com.ounis.utils.ArrayListSaver;
import com.ounis.utils.StrOper;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import javax.swing.JOptionPane;

/**
 *
 * @author AndroidAppsPlatform
 */
public class RepHTML {
    

    enum REPORT4 {Autors, Titles};
    
    //const
    
    // dom. numer kolumny w zapytaniu z którego bêd¹ pobierane litery do nag³ówków
    // sekcji raportu
    private static final int DEF_LETTER_FIELD_POS = 1;  
    //private static final String INI_KEY_SLUCH_MAX_ID = "";                                                   
    
    
    
    
    protected String htmlFileName;
    protected String sqlList;
    protected String sqlLetters;
    protected Connection mainConn;
    protected int sluchLastID;
    
    
    private ArrayList<Object> HTMLLinesA;
    private ArrayList<Object> HTMLLinesT;
    
    
    
    private int letterColNum = DEF_LETTER_FIELD_POS;
    /**
     * numer kolumny z której bêd¹ powbierane litery do nag³ówków
     * sekcji raportu, domyœlny: <b>1</>
     * @return - numer kolumny
     */
    public int getLetterColNum(){
        return letterColNum;
    }
    /**
     * ustaw numer kolumny w zapytaniu z której bêd¹ pobierane litery do 
     * nag³ówków sekcji raportu inny ni¿ domyœlny: <b>1</b>
     * 
     * @param aValue - u¿ytkownik podaje swój numer kolumny
     */
    void setLetterColNum(int aValue){
        letterColNum = aValue;
    }
    
    protected String navLeftArrowValue; // http://chomikuj.pl/ounis/S*c5*82uchowiska/Alfabetycznie+-+autorami/A
    protected String navLeftArrowCaption;  // <==
    
    protected String navRightArrowValue;  // http://chomikuj.pl/ounis/S*c5*82uchowiska/Alfabetycznie+-+autorami/B
    protected String navRighttArrowCaption; // ==>
    
    
    private Date nowDate;
    
    private int maxNewDays;
    public int getMaxNewDays() {
        return this.maxNewDays;
    }
    public void setMaxNewDays(int aValue) {
        this.maxNewDays = aValue;
    }
    
    
    private int cntAutors = 0;
    public int getCntAutors() {
        return cntAutors;
    }
    
    private int cntTitles = 0;
    public int getCntTitles() {
        return cntTitles;
    }
    
    
    public RepHTML(Connection aConn, String sqlLetters ,String sqlList) {
        
        
        this.sqlList = sqlList;
        this.sqlLetters = sqlLetters;
        mainConn = aConn;
        
        loadSluchLastID();
        HTMLLinesA = new ArrayList<>();
        HTMLLinesT = new ArrayList<>();
        
        Calendar cal = new GregorianCalendar();
        nowDate = cal.getTime();
        
        this.maxNewDays = CONST.DEF_MAX_NEW_DAYS;
    }
    
    
    
    private int getSluchLastID() {
        int result;
        int lastid = 0;
        
        Statement sqlStmt;
        ResultSet sqlRs;
        
        try {
            sqlStmt = mainConn.prepareStatement("");
            sqlRs = sqlStmt.executeQuery(CONST.SQL_SLUCH_LAST_ID);
            if (sqlRs.next())
                lastid = sqlRs.getInt(CONST.DEF_SLUCH_LAST_ID_FIELD_NAME);
            else
                lastid = 0;
            
            System.out.printf("sluchLastID: %d\n", lastid);
            sqlRs.close();
            sqlStmt.close();
        }
        catch (SQLException sql_e) {
            lastid = 0;
            System.out.printf("B³¹d pobrania max(id) z tab. Sluchowiska\n%", 
                          sql_e.getLocalizedMessage());
        }
        finally {
            result = lastid;
        }
        
        
        return result;
    }
    
    private void saveSluchLastID() {
        FileWriterLn fout;
            
        try {
            fout = new FileWriterLn(CONST.FILE_BIBLIO_DAT);
            fout.writeln(String.valueOf(getSluchLastID()));
            fout.flush();
            fout.close();
        }
        catch (IOException except) {
          System.err.printf("Problem z zapisem do pliku: %s\n%s",
                        CONST.FILE_BIBLIO_DAT, except.getLocalizedMessage());
        }
        
    }
    
    private void loadSluchLastID() {
        BufferedReader dataFile;
        String line;
        
        try {
            dataFile = new BufferedReader(
                   new InputStreamReader(new FileInputStream(CONST.FILE_BIBLIO_DAT)));
                if ((line = dataFile.readLine()) != null) {
                   sluchLastID = Integer.valueOf(line);
                }
                else 
                  sluchLastID = 0;
                
                System.out.printf("sluchLastID: %d\n", sluchLastID);
                
                dataFile.close();
            }
        catch (IOException e) {
            System.err.printf("B³¹d: Nie mo¿na wczytaæ sluchLastID\n%s\n",
                          e.getLocalizedMessage());
        }
        
    }
    
    private boolean saveArrayList(String fileName, ArrayList<Object> aList) throws IOException {
        boolean result = false;
        
        if (aList.size() > 0)
        {
            FileWriterLn fout = new FileWriterLn(fileName);
            for (Object s: aList){
                fout.writeln(s.toString());
            }
            fout.flush();
            fout.close();
            
            result = true;
        }
        
        return result;
    }
    
    /**
     * zapis danych do pliku tekstowego
     * @param fileName - nazwa pliku
     * @return true - jeœli siê powiedzie
     */
    public boolean saveToFile() {
        boolean result = false;
        try
        {   
            if (HTMLLinesA.size() > 0) {
                result = saveArrayList(CONST.AUTORS_REP_FILE_NAME, HTMLLinesA);
                System.out.printf("Zapis do pliku raportu: %s\n", FTools.getAppPath() + CONST.AUTORS_REP_FILE_NAME);
                result = true;
            }
            if (HTMLLinesT.size() > 0) {
                result = saveArrayList(CONST.TITLES_REP_FILE_NAME, HTMLLinesT);
                System.out.printf("Zapis do pliku raportu: %s\n", FTools.getAppPath() + CONST.TITLES_REP_FILE_NAME);
                result |= true;
            }
        }
        catch (IOException except)
        {
            result = false;
            String e_msg = String.format("Problem z zapisem do plików raportów HTML!\n%s", except.getLocalizedMessage());

            JOptionPane.showMessageDialog(null, e_msg);
            System.err.printf("%s\n", e_msg);
        }
        if (!result) 
            System.out.printf("\nListy raportów puste. Zapis anulowany...\n");
        
        return result;
    }
    
    
    private int GenerateReport(REPORT4 repKind, ArrayList aList) {
        
        ArrayList<String> lettersList = new ArrayList<>();
        
        int result = 0;

        Statement stmt = null;
        PreparedStatementWrapper pstmt = null;
        //PreparedStatement pstmt = null;
        ResultSet rs = null;
        //ResultSet rs1 = null;
        
        HTMLTags h = new HTMLTags();
        aList.clear();
        
        try {
            stmt = mainConn.prepareStatement("");
            //stmt = mainConn.prepareStatement("");
            
             rs = stmt.executeQuery(String.format(sqlLetters, sluchLastID));
             
             
             if (rs != null)
             {
                 pstmt = (PreparedStatementWrapper) mainConn.prepareStatement(sqlList);
                 //pstmt = (PreparedStatement) mainConn.prepareStatement(sqlList);
                 while (rs.next())
                 {
                     String l; 
                     l = rs.getString(letterColNum);
                     
                     l = l.substring(0,1);
                     if (lettersList.indexOf(l) == -1)
                     {
                          lettersList.add(l);
                          pstmt.setString(1, l+"%");
                          ResultSet rs1 = pstmt.executeQuery();

//                          stmt1 = mainConn.createStatement();
//                          rs1 = stmt1.executeQuery(String.format(sqlList, l+"%"));
                          if (rs1 != null) {
                              switch(repKind) {
                                  case Autors : result += FillArrayListAutors(aList, rs1);
                                                break;
                                  case Titles : result += FillArrayListTitles(aList, rs1);
                                                break;
                              }
                               
                              rs1.close();
//                               stmt1.close();
                          }
                              
                     }                  
                 }
                 pstmt.close();
                 rs.close();
                 stmt.close();
             }
             else 
                 result = 0;
        }
        catch (SQLException except) {
           System.err.printf("%s\n", except.getLocalizedMessage());
           result = 0; 
           aList.clear();
        }
        
        return result;
    }
    
    
    public void GenerateEx() {
        boolean result = true;
        
        sqlLetters = CONST.SQL_4_LETTERS_AUTORS;
        sqlList = CONST.SQL_4_LIST_AUTORS;
        System.out.printf("\nGenerowanie listy autorów...\n");
        cntAutors = GenerateReport(REPORT4.Autors, HTMLLinesA);
        result = (cntAutors > 0);
        System.out.printf(" - elementów: %d\n", cntAutors);
        
        
        sqlLetters = CONST.SQL_4_LETTERS_TITLES;
        sqlList = CONST.SQL_4_LIST_TITLES;
        System.out.printf("\nGenerowanie listy tytu³ów...\n");
        cntTitles = GenerateReport(REPORT4.Titles, HTMLLinesT);
        result &= (cntTitles > 0);
        System.out.printf(" - elementów: %d\n", cntTitles);        
        
        if (result) saveSluchLastID();
        else
            JOptionPane.showMessageDialog(null, 
             String.format("Problem z wygenerowaniem list raportów\n"
                           + "  - licznik autorów: %d\n"
                           + "  - licznik tytu³ów: %d", 
                           this.cntAutors, cntTitles));
    }
    
    /**
     * g³ówna pêtla generuj¹ca listê przechodz¹ca po wszystkich wierszach
     * dostarczonych z zapytania przekazanego w konstruktorze w zmiennej
     * sqlList
     */
    public void Generate(){
        ArrayList<Object> lettersList = new ArrayList<>();
        

        Statement stmt = null;
        Statement stmt1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        
        HTMLTags h = new HTMLTags();
        HTMLLinesA.clear();
        
        
        try {
            stmt = mainConn.prepareStatement("");
            
             rs = stmt.executeQuery(String.format(CONST.SQL_4_LETTERS_AUTORS, sluchLastID));
             
             System.out.printf("\nGenerowanie spisu autorów...\n");
             if (rs != null)
             {
                 while (rs.next())
                 {
                     String l; 
                     l = rs.getString(letterColNum);
                     
                     l = l.substring(0,1);
                     if (lettersList.indexOf(l) == -1)
                     {
                          lettersList.add(l);
                          
                          stmt1 = mainConn.prepareStatement("");
                          rs1 = stmt1.executeQuery(String.format(CONST.SQL_4_LIST_AUTORS, l+"%"));
                          if (rs1 != null) {
                               FillArrayListAutors(HTMLLinesA, rs1);
                               rs1.close();
                               stmt1.close();
                          }
                              
                     }                  
                 }
                 
                 rs.close();
                 stmt.close();
                 //saveToFile(CONST.AUTORS_REP_FILE_NAME, HTMLLinesA);
                 
                 
                 
                 stmt = mainConn.prepareStatement("");
                 rs = stmt.executeQuery(String.format(CONST.SQL_4_LETTERS_TITLES, sluchLastID));
                 HTMLLinesT.clear();
                 lettersList.clear();
                 
                 System.out.printf("\nGenerowanie spisu tytu³ów...\n");
                 if (rs != null)
                 {
                     while (rs.next())
                     {
                         String l; 
                         l = rs.getString(letterColNum);

                         l = l.substring(0,1);
                         if (lettersList.indexOf(l) == -1)
                         {
                              lettersList.add(l);

                              stmt1 = mainConn.prepareStatement("");
                              rs1 = stmt1.executeQuery(String.format(CONST.SQL_4_LIST_TITLES, l+"%"));
                              if (rs1 != null) {
                                   FillArrayListTitles(HTMLLinesT, rs1);
                                   
                                   rs1.close();
                                   stmt1.close();
                              }

                         }                  
                     }

                     rs.close();
                     stmt.close();
                     //saveToFile(CONST.TITLES_REP_FILE_NAME, HTMLLinesT);

                 
                 //saveArrayList(fileName, HTMLLines);
             
             }      
                 

                 
                 //saveArrayList(fileName, HTMLLines);
             
             }

             
             
             
             // tutaj zapisujemy dane 
             saveSluchLastID();
             
             
             
//             else
//               System.out.println("Brak wpisów do wygnerowania raportu");
        }
        catch(SQLException except)
        {
            System.err.println("Jakiœ problem z wykonaniem execute" + except.getLocalizedMessage());
        }


        
        
//        if (lettersList.size() > 0){
//            try{
//            //System.out.println(h.getA(CONST.SLUCHOWISKA_URL, "Chomikuj -> OuNiS -> S³uchowiska" ));
//            
//                for (String s: lettersList) {
//                    System.out.println(h.getH2(s));
//                        
//                    String sql = String.format(CONST.SQL_4_LIST_AUTORS, s+"%");
//                    rs = stmt.executeQuery(sql);
//                    if (rs != null)
//                    {
//                        HTMLLines.add(h.getBR());
//                        HTMLLines.add(h.getBR());
//                        HTMLLines.add(h.getH2(s));
//                        while (rs.next())
//                        {
////                          System.out.printf("%s %s %s %s\n", 
////                                        rs.getString("nazwisko") + " " + rs.getString("imie"),
////                                        rs.getString("tytul"),
////                                        rs.getString("plyta"),
////                                        rs.getString("plik_mp3"));  
//                            
//                            int cd_num = rs.getInt("plyta");
//                            String cd_s_num = cd_num < 10?"0" + String.valueOf(cd_num):String.valueOf(cd_num);
//                            
//                            String file_mp3 = rs.getString("plik_mp3");
//                            String title = "\"" + rs.getString("tytul") + "\"";
//                            
//                            String line;
//                            line = h.getB(rs.getString("nazwisko") + " " + rs.getString("imie"));
//                            line += " - ";
//                            line += h.getA(String.format(CONST.CD_URL, cd_s_num)
//                                          + "/" + file_mp3 ,
//                                          title);
//                            line += " - ";
//                            line += h.getA(String.format(CONST.CD_URL, cd_s_num)
//                                           , "P³yta nr.: " + String.valueOf(cd_num));
//                            if (rs.getInt("hit") == 1)
//                                line += HTMLTags.RCHAR_SPACE + HTMLTags.RCHAR_SPACE + h.getIMG(CONST.IMG_HIT_URL, "HIT!");
//                            line += h.getBR();
//                            
//                            HTMLLines.add(line);
//                            
//                        }
//                        HTMLLines.add(h.getBR());
//                        HTMLLines.add(h.getFont(1, "Generated: "+ RepHTML.getDateTimeStamp()));
//                        HTMLLines.add(h.getBR());
//                    }
//
//                }
        
    }
    
    
    //abstract public void FillArrayList(ArrayList<String> aList, ResultSet aRs) throws SQLException;
    
    private String getNEW(Date dateFrom, Date dateTo, int days) {
        String result = "";
        HTMLTags h = new HTMLTags();
             if (Math.abs(daysBetween(dateFrom, dateTo)) < days)
                result += " - " + h.getIMG(CONST.IMG_NEW_URL, "Nowoœæ!");       
        return result;
    }
    
    
    private String convertSpecChar2Tag(char c)
    {
        String result = "";
        
        int i = Character.getNumericValue(c);
        //System.out.printf("\n - %d\n", i);
        
        
        switch (c) {
            case '"': result = HTMLTags.RCHAR_QUOT;
                      break;
            case '\'': result = HTMLTags.RCHAR_APOS;
                      break;
            case '&': result = HTMLTags.RCHAR_AMP;
                      break;
            case '<': result = HTMLTags.RCHAR_LT;
                      break;
            case '>': result = HTMLTags.RCHAR_GT;
                      break;
            case ' ': result = HTMLTags.RCHAR_SPACE;
                      break;
        }
        
        return result;
        
        
        
    }
    
     private String getStringFromFieldWithoutQout(ResultSet aRs, String fieldName,
                  char cValue) throws SQLException
    {
        String result;
        ArrayList<Integer> idxList = new ArrayList<>();
        
        String s_file_mp3 = aRs.getString(fieldName);
        int idx;
        

        do {
             idx = s_file_mp3.indexOf(cValue);
             if (idx > -1) 
                 s_file_mp3 = StrOper.delCharAt(s_file_mp3, idx);
        } 
        while (idx != -1);

        result = s_file_mp3;
        return result;
    }
    
    public int FillArrayListAutors(ArrayList<Object> aList, ResultSet aRs) throws SQLException {
        int result = 0;
        HTMLTags h = new HTMLTags();
        
        boolean wasfirstrow = false;
        String hHead = "";
        int itemCnt = 0;
        
        aList.add(h.getA(CONST.HTML_ALPHA_AUTORS, "Alfabetycznie - autorami"));
        aList.add(h.getHR());
        aList.add(h.getBR());
        aList.add(h.getBR());
                      
        while (aRs.next())
        {
            if (!wasfirstrow) {
                wasfirstrow = true;
                String l = aRs.getString("nazwisko");
                l = l.substring(0, 1);
                aList.add(h.getH2(l));
                hHead = l;
            }
//                          System.out.printf("%s %s %s %s\n", 
//                                        rs.getString("nazwisko") + " " + rs.getString("imie"),
//                                        rs.getString("tytul"),
//                                        rs.getString("plyta"),
//                                        rs.getString("plik_mp3"));  

            int cd_num = aRs.getInt("plyta");
            String cd_s_num = cd_num < 10?"0" + String.valueOf(cd_num):String.valueOf(cd_num);

//            String file_mp3 = aRs.getString("tytul") + ".mp3";//aRs.getString("plik_mp3");
              String file_mp3 = 
                            getStringFromFieldWithoutQout(aRs, "tytul", '"') + ".mp3";
            
//            String s_file_mp3 = "";
//            for (char c: file_mp3.toCharArray())
//            {
//                if (c != '"') 
//                    s_file_mp3 += c;
//            }
//            file_mp3 = s_file_mp3;
            
            String title = "\"" + aRs.getString("tytul") + "\"";

            String line = "";
            line = h.getREM("s_id="+aRs.getString("s_id"));
            line += h.getB(aRs.getString("nazwisko") + " " + aRs.getString("imie"));
            line += " - ";
            line += h.getA(String.format(CONST.CD_URL, cd_s_num)
                          + "/" + file_mp3 ,
                          title);
            
            line += " - ";
            line += "P³yta nr.: ";
            line += h.getA(String.format(CONST.CD_URL, cd_s_num)
                           , String.valueOf(cd_num));
            
            if (aRs.getInt("hit") == 1)
                line += HTMLTags.RCHAR_SPACE + HTMLTags.RCHAR_SPACE + h.getIMG(CONST.IMG_HIT_URL, "HIT!");
            
            //if (aRs.getInt("s_id") > sluchLastID)
            Date regDate = aRs.getDate(CONST.DEF_SLUCH_DATE_OF_NEW);
            line += getNEW(this.nowDate, regDate, this.maxNewDays);
                              
            line += h.getBR();

            aList.add(line);

            itemCnt++;
        }
        aList.add(h.getBR());
        aList.add(h.getFont(1, CONST.HTML_FOOTER+ RepHTML.getDateTimeStamp()));
        aList.add(h.getBR());
     
        System.out.println(String.format("%s -> %d", hHead, itemCnt));
        result = itemCnt;
        
        return result;
        
    }
    public int FillArrayListTitles(ArrayList<Object> aList, ResultSet aRs) throws SQLException {
        int result = 0;
        HTMLTags h = new HTMLTags();
        
        boolean wasfirstrow = false;
        String hHead = "";
        int cntItem = 0;
        
        
        aList.add(h.getA(CONST.HTML_ALPHA_TITLES, "Alfabetycznie - tytu³ami"));
        aList.add(h.getHR());
        aList.add(h.getBR());
        aList.add(h.getBR());
        
        while (aRs.next()) {
            if (!wasfirstrow) {
                wasfirstrow = true;
                String l = aRs.getString("tytul");
                l = l.substring(0, 1);
                aList.add(h.getH2(l));
                
                hHead = l;
            }
            
            int cd_num = aRs.getInt("plyta");
            String cd_s_num = cd_num < 10?"0" + String.valueOf(cd_num):String.valueOf(cd_num);

//            String file_mp3 = aRs.getString("tytul") + ".mp3";//aRs.getString("plik_mp3");
            String file_mp3 = 
                          getStringFromFieldWithoutQout(aRs, "tytul", '"') + ".mp3";

            String title = "\"" + aRs.getString("tytul") + "\"";

            String line = "";
            line = h.getREM("s_id="+aRs.getString("s_id"));
            line += h.getA(String.format(CONST.CD_URL, cd_s_num) + 
                          "/" + file_mp3, title  );
            line += " - ";
            line += h.getB(aRs.getString("nazwisko") + " " + aRs.getString("imie"));
            
            line += " - ";
            line += "P³yta nr.: ";
            line += h.getA(String.format(CONST.CD_URL, cd_s_num)
                           , String.valueOf(cd_num));
            
            if (aRs.getInt("hit") == 1)
                line += HTMLTags.RCHAR_SPACE + HTMLTags.RCHAR_SPACE + 
                h.getIMG(CONST.IMG_HIT_URL, "HIT!");
            
            Date regDate = aRs.getDate(CONST.DEF_SLUCH_DATE_OF_NEW);
            line += getNEW(this.nowDate, regDate, this.maxNewDays);
            
            
            line += h.getBR();

            aList.add(line);
            
            cntItem++;
        }
        aList.add(h.getBR());
        aList.add(h.getFont(1, CONST.HTML_FOOTER + RepHTML.getDateTimeStamp()));
        aList.add(h.getBR());
        
        System.out.printf("%s -> %d\n", hHead, cntItem);
        result = cntItem;
        
        return result;
        
    }
    
    public String lastRowData() throws SQLException {
        String line = "";
        String fnDataRejestracji = "data_rejestracji";
        Date regDate;
        Date nowDate;
        Statement sqlStmt = mainConn.prepareStatement("");
        ResultSet sqlRs = sqlStmt.executeQuery(
                      "select * from sluchowiska where id = " + String.valueOf(sluchLastID));
        if (sqlRs != null && sqlRs.getRow() > 0) {
            sqlRs.next();

            Calendar cal = new GregorianCalendar(Locale.ITALY);
           
            if (sqlRs.getObject(fnDataRejestracji) != null )
                regDate = sqlRs.getDate(fnDataRejestracji);
            else
                regDate = cal.getTime();
            
            nowDate = cal.getTime();
            
            long diff = nowDate.getTime() - regDate.getTime();
            
            float div = (1000 % 60) / 3600;
            
            long days = diff / (24 * 60 * 60 * 1000);
            
            line = String.valueOf(Math.abs(daysBetween(regDate, nowDate)));
            
            
            LocalDate date = LocalDate.now().minusDays(14);
            line = date.toString();
            
            Date endOfTime = new Date(Long.MAX_VALUE);
            line = endOfTime.toString();
            
            LocalDateTime ldt = LocalDateTime.ofInstant(regDate.toInstant(), ZoneId.systemDefault());
            line = String.valueOf(daysFromNow(ldt, 14));
            //line = nowDate.toString();
            
//            int colCount = sqlRs.getMetaData().getColumnCount();
//            if (sqlRs.next()) {
//                
//                for (int y = 1;y <= colCount; y++) {
//                    line += sqlRs.getString(y) + ", ";
//                    
//                }
//                line = line.substring(0, line.length() - 2);
//            }
            
            
            sqlRs.close();
            sqlStmt.close();
        }
        
        return line;
    }              

    /**
     * iloœæ dni pomiêdzy datami obliczane dateFrom - dateTo
     * @param dateFrom data pocz¹tkowa
     * @param dateTo data koñcowa
     * @return iloœæ dni
     */
    private long daysBetween(Date dateFrom, Date dateTo) {
        // Date format: YYYY-MM-DDTHH:MM:SS
        
        
        long result = 0;
        long dayDiv = 24 * 60 * 60 * 1000;
        
        long diff = dateFrom.getTime() - dateTo.getTime();
        result = diff / dayDiv;
       // if (result < 0) result *= -1;
       
       
       
        return result;
    }
    

    private long daysFromNow(LocalDateTime dateTo, int numOfDays) {
        long result = 0;
       
        LocalDateTime ld = LocalDateTime.now().minusDays(numOfDays);
        
        
        
        
        return result;
    }
    
    //abstract public void AddNavLinks(ArrayList<String> aLetterList, int pos, ArrayList<String> aList);
    
    public static String getDateTimeStamp() { 
        String dts = "";
        String timeStampFormat = "%d-%02d-%02d %02d:%02d:%02d";
        
        Calendar cal = new GregorianCalendar();
        dts = String.format(timeStampFormat,
                      cal.get(Calendar.YEAR),
                      cal.get(Calendar.MONTH) + 1,
                      cal.get(Calendar.DAY_OF_MONTH),
                      cal.get(Calendar.HOUR_OF_DAY),
                      cal.get(Calendar.MINUTE),
                      cal.get(Calendar.SECOND));      
        return dts;
    }
    
    public static String getDateStamp() {
        String ds = "";
        Calendar cal = new GregorianCalendar();
        ds = String.format("%d-%02d-%02d", 
                      cal.get(Calendar.YEAR),
                      cal.get(Calendar.MONTH) + 1,
                      cal.get(Calendar.DAY_OF_MONTH));
        return ds;
    }
    
    public static String getTimeStamp() {
        String ts = "";
        Calendar cal = new GregorianCalendar();
        ts = String.format("%02d:%02d:%02d", 
                      cal.get(Calendar.HOUR_OF_DAY),
                      cal.get(Calendar.MINUTE),
                      cal.get(Calendar.SECOND));
        return ts;
    }
    
}
