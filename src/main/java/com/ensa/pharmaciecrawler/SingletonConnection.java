package com.ensa.pharmaciecrawler;

import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonConnection {
	protected static final Logger LOGGER = LoggerFactory.getLogger(SingletonConnection.class);
	 private static Connection connection;
	 static{
		 //creaction d'une seule connction a la base de données 
		 try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/pharmacies","root","");
		 } catch (Exception e) {
			LOGGER.debug("La connexion n'a pas pu être établie",e);
		}
	 }
	 
	 // recuperation de la connection
	public static Connection getConnection() {
		return connection;
	}
}
