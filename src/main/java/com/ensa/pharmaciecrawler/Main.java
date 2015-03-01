package com.ensa.pharmaciecrawler;

import java.io.IOException;
import java.sql.SQLException;

public class Main {

	public static void main(String[] args) throws SQLException, IOException {
		MyPageParser.viderTable();
		MyPageParser.cherchePharmacie();
		MyPageParser.cherchePharmacieGarde();
	}

}
