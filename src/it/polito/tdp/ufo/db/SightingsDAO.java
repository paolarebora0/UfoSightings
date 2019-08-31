package it.polito.tdp.ufo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.ufo.model.AnnoCount;
import it.polito.tdp.ufo.model.Sighting;

public class SightingsDAO {
	
	public List<Sighting> getSightings() {
		
		String sql = "SELECT * FROM sighting" ;
		List<Sighting> list = new ArrayList<>() ;			
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;			
			ResultSet res = st.executeQuery() ;
			
			while(res.next()) {
				try {
					list.add(new Sighting(res.getInt("id"),
							res.getTimestamp("datetime").toLocalDateTime(),
							res.getString("city"), 
							res.getString("state"), 
							res.getString("country"),
							res.getString("shape"),
							res.getInt("duration"),
							res.getString("duration_hm"),
							res.getString("comments"),
							res.getDate("date_posted").toLocalDate(),
							res.getDouble("latitude"), 
							res.getDouble("longitude"))) ;
				} catch (Throwable t) {
					t.printStackTrace();
					System.out.println(res.getInt("id"));
				}
			}
			
			conn.close();
			return list ;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null ;
		}
	}

	public List<AnnoCount> getAnni(){
		
		String sql = "SELECT Year(sighting.datetime) as anno, COUNT(id) AS cnt " + 
				"FROM sighting " + 
				"WHERE country = \"us\" " + 
				"GROUP BY Year(sighting.datetime)";
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;			
			List<AnnoCount> anni = new LinkedList<AnnoCount>();
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				anni.add(new AnnoCount(Year.of(res.getInt("anno")), res.getInt("cnt")));
			}

			conn.close();
			return anni ;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getStati(Year anno) {
		String sql = "SELECT distinct state " + 
				"FROM sighting " + 
				"WHERE country = \"us\" "
				+ "AND Year(datetime) = ?";
		
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;	
			st.setInt(1, anno.getValue());
			List<String> stati = new LinkedList<String>();
			
			ResultSet res = st.executeQuery() ;
			while(res.next()) {
				stati.add(res.getString("state"));
			}

			conn.close();
			return stati ;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	public boolean esisteArco(String s1, String s2, Year anno) {
		String sql = "SELECT COUNT(*) AS cnt " + 
				"FROM Sighting s1, Sighting s2 " + 
				"WHERE YEAR(s1.datetime) = YEAR(s2.datetime) " + 
				"AND YEAR(s1.datetime) = ? " + 
				"AND s1.state = ? AND s2.state= ? " + 
				"AND s1.country = \"us\" AND s2.country = \"us\" " + 
				"AND s2.datetime > s1.datetime";
		try {
			Connection conn = DBConnect.getConnection() ;
			PreparedStatement st = conn.prepareStatement(sql) ;	
			st.setInt(1, anno.getValue());
			st.setString(2, s1);
			st.setString(3, s2);

			//Non ho bisogno di una lista perche devo solo ricevere vero o falso
			
			ResultSet res = st.executeQuery() ; //ritorna un singolo valore quindi uso una if

			if(res.next()) {
				if(res.getInt("cnt")> 0) {
					conn.close();
					return true;
				}
				else {
					conn.close();
					return false;
				}
			} else {
				return false;
			}
						
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
}
