package persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Studente;

public class StudenteCredenziali extends Studente {
	private DataSource dataSource;

	public StudenteCredenziali(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public String getPassword(){						
		Connection connection = this.dataSource.getConnection();
		try {
			PreparedStatement statement;
			String query = "select * from studente where matricola = ?";
			statement = connection.prepareStatement(query);
			statement.setString(1, this.getMatricola());
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				return result.getString("password");
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}	
		return null;
	}

}
