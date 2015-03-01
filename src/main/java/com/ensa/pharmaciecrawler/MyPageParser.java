package com.ensa.pharmaciecrawler;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MyPageParser {
	//connection à la base de données
	private static Connection connection = SingletonConnection.getConnection();
	
    //inserer les données dans la base de données
	private static void insertToTable(String nom, String tel, String adresse,String latitude, String longitude, boolean garde)throws SQLException {
		String sql = null;
		//'false' pour l'insertion des pharmacies
		if (garde) {
			sql = "insert into pharmacie (NOM,NUM,ADRESSE,LATITUDE,LONGITUDE,GARDE)"+ "values(?,?,?,?,?,'true')";
			
		//'true' pour l'insertion des pharmacies de garde
		} else {sql = "insert into pharmacie (NOM,NUM,ADRESSE,LATITUDE,LONGITUDE,GARDE)"+ "values(?,?,?,?,?,'false')";}
		PreparedStatement preparedStatement = connection.prepareStatement(sql);
		preparedStatement.setString(1, nom);
		preparedStatement.setString(2, tel);
		preparedStatement.setString(3, adresse);
		preparedStatement.setString(4, latitude);
		preparedStatement.setString(5, longitude);
		preparedStatement.executeUpdate();
	}

	//vider la base de données
	public static void viderTable() throws SQLException {
		PreparedStatement ps = connection.prepareStatement("truncate pharmacie;");
		ps.executeQuery();
	}

	public static void cherchePharmacie() throws IOException, SQLException {
		
		//initialisation des langitude & latitude & l'indice des pages url
		String langitude = null;
		String latitude = null;
		int i = 0;
		
		                              //trouver toutes les pharmacies de la ville d'agadir
		
		//On se connecte au site http://www.anahna.com ,on charge le document html
		Document doc = Jsoup.connect("http://www.anahna.com/pharmacies-agadir-ca7-qa0.html").timeout(10 * 1000).get();
		// On récupère dans ce document la premiere balise ayant comme nom div
		// et pour attribut class="right"
		Elements links = doc.select("div .right");
		for (Element link : links) {
			
			//on récupere le 1er l'element fils qui est le nom de la pharmacie
			String nomPharmacie = link.child(0).text();
			
			//on récupere le 2eme element fils qui est le l'adress de la pharmacie	
			String adress = link.child(1).text();
			
			//on récupere le 3eme element fils qui est le num de tel de la pharmacie
			String tel = link.child(2).text();
			
			//On récupère dans ce document la premiere balise ayant comme nom div et pour attribut class="left"
			//on récupère la balise a [href] qui contient les urls des cordonnées lat et long
			//Deux représentations de l'url ***.attr("abs:href"); ou la deuxième ***.attr("abs:href");
			String absHref = doc.select("div .left").select("a").get(i).attr("abs:href");
			
			//On se connecte au url absHref,on charge le script qui contient les coordonées 
			Document docs = Jsoup.connect(absHref).timeout(10000).get();
			
			// on vise le dernier tag 'script'de la page
			Element scriptElement = docs.select("script").last();
			
			// charger tout le script
			String jsCode = scriptElement.html();
			
			// extraire la ligne qui nous interesse,dans ce cas les coordonées de la pharmacie
			jsCode = jsCode.substring(jsCode.indexOf('['), jsCode.indexOf(']'));
			
			// il existe quelque pharmacies sont coordonnées qui declanche une
			// exeption dans le traitement
			if (jsCode.length() > 6) {
				latitude = jsCode.substring(1, jsCode.indexOf(','));
				langitude = jsCode.substring(jsCode.indexOf(',')).substring(2);
			}

			//insertion des donneés de la pharmacie
			insertToTable(nomPharmacie, tel, adress, latitude, langitude, false);

			i++;
		}
	}

	public static void cherchePharmacieGarde() throws SQLException, IOException {
		
		//initialisation des langitude & latitude
		String langitude = null;
		String latitude = null;
		
		                                      // trouver les pharmacies de garde depuis http://www.blanee.com
		
		//On se connecte au site http://www.blanee.com ,on charge le document html 
		Document gard = Jsoup.connect("http://www.blanee.com/guides/pharmacies-de-garde-a-agadir-du-24-au-30-decembre-agadir").timeout(10 * 1000).get();
		
		// On récupère dans ce document la premiere balise ayant comme nom div
		// et pour attribut class="info"
		Elements gos = gard.select("div .info");
		
		for (Element go : gos) {
			
			// on récupère la balise a [href] qui contient les noms des pharmacies de gardes
			String nomPharmacie2 = go.select("a[href]").get(0).text();
			
			// on récupère la balise a [href] qui contient les urls des cordonnées lat et long
			String urlgard = go.select("a").attr("href");
			
			// trouver coordonner depuis script
			Document phppage = Jsoup.connect("http://www.blanee.com" + urlgard).timeout(10000).get();
			
			// on récupère l'element adress de la pharmacie
			String adress2 = phppage.select("li").get(12).text();
			
			// on récupère l'element telephone de la pharmacie
			String tell = phppage.select("li").get(15).text();
			
			//  on récupère les coordonés de geolocalisation de la pharmacie
			String lat = phppage.select("div").attr("lat");
			String lng = phppage.select("div").attr("lng");

			//on test l'existance de chaine
			if (lat.isEmpty() && lng.isEmpty()) {
				
				//recuperation depuis un autre url
				Document phppage1 = Jsoup.connect("http://www.blanee.com" + urlgard+ "/set_latlng.js").timeout(10000).get();
				latitude = phppage1.select("input").get(1).attr("value").substring(2,9);
				langitude = phppage1.select("input").get(2).attr("value").substring(2,9);
			} else {
				latitude = lat;
				langitude = lng;
			}
			
			//insertion des donneés de la pharmacie
			insertToTable(nomPharmacie2, tell, adress2, latitude, langitude,true);
		}
	}

}