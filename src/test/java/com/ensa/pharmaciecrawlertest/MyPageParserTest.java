package com.ensa.pharmaciecrawlertest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ensa.pharmaciecrawler.MyPageParser;
import com.ensa.pharmaciecrawler.SingletonConnection;

import junit.framework.TestCase;

public class MyPageParserTest extends TestCase {
	
	private static Connection connection = SingletonConnection.getConnection();
	protected void setUp() throws Exception {	
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testViderTable() throws SQLException {
		MyPageParser.viderTable();
		String sql = "select * from pharmacie";
		Statement statement = connection.createStatement();
		ResultSet resultat = statement.executeQuery(sql);
		//on place le curseur sur le dernier tuple
		resultat.last();
		//on récupère le numéro de la ligne
		int nombreLignes = resultat.getRow();
		//on repace le curseur avant la première ligne
		resultat.beforeFirst();
		assertEquals(nombreLignes, 0);
	}

	public void testCherchePharmacie() throws IOException, SQLException {
		MyPageParser.cherchePharmacie();
		String sql = "select * from pharmacie";
		Statement statement = connection.createStatement();
		ResultSet resultat = statement.executeQuery(sql);
		//on place le curseur sur le dernier tuple
		resultat.last();
		//on récupère le numéro de la ligne
		int nombreLignes = resultat.getRow();
		//on repace le curseur avant la première ligne
		resultat.beforeFirst();
		assertEquals(nombreLignes, 174);
	}

	public void testCherchePharmacieGarde() throws SQLException, IOException {
		MyPageParser.cherchePharmacieGarde();
		String sql = "select * from pharmacie where garde ='true';";
		Statement statement = connection.createStatement();
		ResultSet resultat = statement.executeQuery(sql);
		//on place le curseur sur le dernier tuple
		resultat.last();
		//on récupère le numéro de la ligne
		int nombreLignes = resultat.getRow();
		//on repace le curseur avant la première ligne
		resultat.beforeFirst();
		assertEquals(nombreLignes, 13);
	}

}
