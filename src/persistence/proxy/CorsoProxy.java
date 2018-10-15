package persistence.proxy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import model.Corso;
import model.Studente;
import persistence.DataSource;
import persistence.PersistenceException;
import persistence.dao.StudenteDao;
import persistence.daoJDBC.StudenteDaoJDBC;


public class CorsoProxy extends Corso {
	private DataSource dataSource;

	public CorsoProxy(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Set<Studente> getStudenti() {						
		Set<Studente> studenti = new HashSet<>();
		Connection connection = this.dataSource.getConnection();
		try {
			PreparedStatement statement;
			String query = "select matricola_studente from iscritto where corso_codice = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, this.getCodice());
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				StudenteDao studenteDao = new StudenteDaoJDBC(dataSource);
				Studente studente = studenteDao.findByPrimaryKey(result.getString("matricola_studente"));
				studenti.add(studente);
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
		this.setStudenti(studenti);
		return super.getStudenti(); 
	}
}
