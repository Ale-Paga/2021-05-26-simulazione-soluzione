package it.polito.tdp.yelp.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.yelp.model.Adiacenza;
import it.polito.tdp.yelp.model.Business;
import it.polito.tdp.yelp.model.Review;
import it.polito.tdp.yelp.model.User;

public class YelpDao {

	public List<Business> getAllBusiness(){
		String sql = "SELECT * FROM Business";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Business> getBusinessByCityAndYear(String city, Year anno){
		//String sql = "SELECT b.* "
		//		+ "FROM business AS b, reviews AS r "
		//		+ "WHERE b.business_id=r.business_id AND b.city='Phoenix' AND YEAR(r.review_date)=2005 "
		//		+ "GROUP BY b.business_id ";
		String sql = "SELECT * "
				+ "FROM business AS b "
				+ "WHERE  b.city= ? "
				+ "AND ( "
				+ "		SELECT COUNT(*) "
				+ "		FROM reviews AS r "
				+ "		WHERE b.business_id = r.business_id "
				+ "		AND YEAR(r.review_date) = ? "
				+ "	) >0 "
				+ "ORDER BY b.business_name ASC ";
		List<Business> result = new ArrayList<Business>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, city);
			st.setInt(2, anno.getValue());
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Business business = new Business(res.getString("business_id"), 
						res.getString("full_address"),
						res.getString("active"),
						res.getString("categories"),
						res.getString("city"),
						res.getInt("review_count"),
						res.getString("business_name"),
						res.getString("neighborhoods"),
						res.getDouble("latitude"),
						res.getDouble("longitude"),
						res.getString("state"),
						res.getDouble("stars"));
				//idMap.put(business.getBusinessId(), business);
				result.add(business);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Review> getAllReviews(){
		String sql = "SELECT * FROM Reviews";
		List<Review> result = new ArrayList<Review>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Review review = new Review(res.getString("review_id"), 
						res.getString("business_id"),
						res.getString("user_id"),
						res.getDouble("stars"),
						res.getDate("review_date").toLocalDate(),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("review_text"));
				result.add(review);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<User> getAllUsers(){
		String sql = "SELECT * FROM Users";
		List<User> result = new ArrayList<User>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				User user = new User(res.getString("user_id"),
						res.getInt("votes_funny"),
						res.getInt("votes_useful"),
						res.getInt("votes_cool"),
						res.getString("name"),
						res.getDouble("average_stars"),
						res.getInt("review_count"));
				
				result.add(user);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllCity(){
		String sql = "SELECT DISTINCT(city) "
				+ "FROM business "
				+ "ORDER BY city ";
		List<String> result = new ArrayList<String>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {
				result.add(res.getString("city"));
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			throw new RuntimeException("Error in DB", e);
			//e.printStackTrace();  il throw stampa un errore più comprensibile
			//return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(String city, int year, Map<String, Business> idMap){
		String sql = "SELECT b1.business_id AS b1, b2.business_id AS b2, (AVG(r1.stars)-AVG(r2.stars)) AS diff "
				+ "FROM business AS b1, business AS b2, reviews AS r1, reviews AS r2 "
				+ "WHERE b1.business_id=r1.business_id AND b2.business_id=r2.business_id AND b1.business_id!=b2.business_id AND YEAR(r1.review_date)=? AND YEAR(r2.review_date)=? "
				+ "		AND b1.city=? AND  b2.city=? "
				+ "GROUP BY b1.business_id, b2.business_id "
				+ "HAVING (AVG(r1.stars)-AVG(r2.stars))>0 ";
		List<Adiacenza> result = new ArrayList<Adiacenza>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, year);
			st.setInt(2, year);
			st.setString(3, city);
			st.setString(4, city);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Adiacenza a = new Adiacenza(idMap.get(res.getString("b2")), idMap.get(res.getString("b1")), res.getInt("diff"));
				result.add(a);
			}
			res.close();
			st.close();
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
