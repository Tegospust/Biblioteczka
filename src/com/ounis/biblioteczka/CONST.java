

package com.ounis.biblioteczka;

/**
 *
 * @author AndroidAppsPlatform
 */
public class CONST {
    
    public static final String DEF_LETTER_FIELD_NAME = "field_1";
    public static final String DEF_SLUCH_LAST_ID_FIELD_NAME = "max_id";
    public static final String DEF_SLUCH_DATE_OF_NEW = "date_new";

    
    public static final String KEY_SLUCH_LAST_ID_FOR_A = "sluchowiska_last_id_for_autors";
    public static final String KEY_SLUCH_LAST_ID_FOR_T = "sluchowiska_last_id_for_titles";
    
    public static final String AUTORS_REP_FILE_NAME = "sort_list_by_autors.html";
    public static final String TITLES_REP_FILE_NAME = "sort_list_by_titles.html";
    public static final String FILE_BIBLIO_DAT = "biblioteczka.dat";
    
    
    public static final String CHOMIKUJ_URL = "http://chomikuj.pl/ounis";
    public static final String SLUCHOWISKA_URL = "http://chomikuj.pl/ounis/S*c5*82uchowiska";
    public static final String CD_URL = "http://chomikuj.pl/ounis/S*c5*82uchowiska/plyta_%s";
    public static final String IMG_HIT_URL = "/ImagePreview.aspx?e=HBU3FJqTQdljfkWBbU3JgA&amp;pv=2";//"/ImagePreview.aspx?id=698754706";
    public static final String IMG_NEW_URL = "/ImagePreview.aspx?e=oAPDxnJlP12QpaR80MdhqA&amp;pv=2";
                    //"http://chomikuj.pl/Image.aspx?id=698758114";
    public static final String HTML_ALPHA_AUTORS = "http://chomikuj.pl/ounis/S*c5*82uchowiska/Alfabetycznie+-+autorami";
    public static final String HTML_ALPHA_TITLES = "http://chomikuj.pl/ounis/S*c5*82uchowiska/Alfabetycznie+-+tytu*c5*82ami";
    
    
    public static final String SQL_4_LETTERS_AUTORS = "select nazwisko as " +
                                        DEF_LETTER_FIELD_NAME +
                                        " from autorzy as a, sluchowiska as s\n" +
                                        "where s.id_autora = a.id\n and s.id > %d\n" +
                                         "order by a.nazwisko";
    
    
    
    public static final String SQL_4_LIST_AUTORS = 
              "select s.id as s_id, imie, nazwisko, tytul, plyta, plik_mp3, hit, s.data_wpisu as "+DEF_SLUCH_DATE_OF_NEW+"\n"+
               "from autorzy as a, sluchowiska as s\n"+
              "where a.id = s.id_autora and nazwisko like ?\n"+ //\"%s\"
              "order by a.nazwisko, s.tytul";
    
    public static final String SQL_4_LETTERS_TITLES = "select tytul as " +
                                    DEF_LETTER_FIELD_NAME +
                                    " from sluchowiska as s, autorzy as a\n" +
                                    "where s.id_autora = a.id\n and s.id > %d\n" +
                                    "order by s.tytul";
    
    public static final String SQL_4_LIST_TITLES = 
                  "select s.id as s_id, imie, nazwisko, tytul, plyta, plik_mp3, hit, s.data_wpisu as "+DEF_SLUCH_DATE_OF_NEW+"\n"+
                  " from autorzy as a, sluchowiska as s\n" +
                  
                  "where a.id = s.id_autora and tytul like ?\n" +  //\"%s\"
                  "order by tytul";
    
    public static final String SQL_SLUCH_LAST_ID = "select id as "+
                                 DEF_SLUCH_LAST_ID_FIELD_NAME + "\n"+
                                "from sluchowiska\n" +
                                 "where id = (select max(id) from sluchowiska)";
    
    
    
    public static final String DEF_LEFT_ARROW_CAPT = "<==";
    public static final String DEF_RIGHT_ARROW_CAPT = "==>";
 
    public static final String HTML_FOOTER = "Aktualizacja: ";
    
    public static final int DEF_MAX_NEW_DAYS = 7;
    
}
