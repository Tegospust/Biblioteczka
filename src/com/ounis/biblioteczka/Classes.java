

package com.ounis.biblioteczka;

import java.util.Date;

/**
 *
 * @author AndroidAppsPlatform
 */
public class Classes {

}

/**
 * Klasa Table odzwierciedlaj�ca tablic� w bazie danych
 * 
 * @author AndroidAppsPlatform
 */

class Table
{
    private String Name;
    private String DBName;
    
    public Table(String aName, String aDBName)
    {
        this.Name = aName;
        this.DBName = aDBName;
    }
    
    // akcesory
    public String getName()
    {
        return this.Name;
    }
    
    public String getDBName()
    {
        return this.DBName;
    }
    
}

class Author
{
   private int Id;
   private int oldId;
   private String surName;
   private String Name;
   private Date addDate;
   private Date modDate;
   
   // konstruktor domy�lny
   
   public Author()
   {
       Id = 0;
       oldId = 0;
       surName = "";
       Name = "";
       addDate = null;
       modDate = null;
       
   }
   
   
   public void setId(int value){
       this.Id = value;
   }
   public int getId()
   {
    return this.Id;
   }
   
   
}

class Book
{
   
}