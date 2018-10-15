package persistence.daoJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Scuola;
import model.Studente;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.dao.ScuolaDao;
//import persistence.dao.StudenteDao;


public class ScuolaDaoJDBC implements ScuolaDao {
	private DataSource dataSource;

	public ScuolaDaoJDBC(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void save(Scuola scuola) {
		Connection connection = this.dataSource.getConnection();
		try {
			Long id = IdBroker.getId(connection);
			scuola.setId(id); 			
			String insert = "insert into scuola(id, codice_meccanografico, nome) values (?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert);
			statement.setLong(1, scuola.getId());
			statement.setString(2, scuola.getCodiceMeccanografico());
			statement.setString(3, scuola.getNome());

			//connection.setAutoCommit(false);
			//serve in caso gli studenti non siano stati salvati. Il DAO studente apre e chiude una transazione nuova.
			//connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);			
			statement.executeUpdate();
			// salviamo anche tutti gli studenti del gruppo in CASACATA
			this.updateStudenti(scuola, connection);
			//connection.commit();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			} 
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}  

	private void updateStudenti(Scuola scuola, Connection connection) throws SQLException {
//		StudenteDao studenteDao = new StudenteDaoJDBC(dataSource);
//		for (Studente studente : scuola.getStudenti()) {
//			if (studenteDao.findByPrimaryKey(studente.getMatricola()) == null){
//				studenteDao.save(studente);
//			}
//			String update = "update studente SET gruppo_id = ? WHERE matricola = ?";
//			PreparedStatement statement = connection.prepareStatement(update);
//			statement.setLong(1, gruppo.getId());
//			statement.setString(2, studente.getMatricola());
//			int s=statement.executeUpdate();
//			
//		}
	}

	private void removeForeignKeyFromStudente(Scuola scuola, Connection connection) throws SQLException {
//		for (Studente studente : scuola.getStudenti()) {
//			String update = "update studente SET gruppo_id = NULL WHERE matricola = ?";
//			PreparedStatement statement = connection.prepareStatement(update);
//			statement.setString(1, studente.getMatricola());
//			statement.executeUpdate();
//		}	
	}

	/* 
	 * versione con Join
	 */
	public Scuola findByPrimaryKeyJoin(Long id) {
		Connection connection = this.dataSource.getConnection();
		Scuola gruppo = null;
		try {
			PreparedStatement statement;
			String query = "select g.id as g_id, g.nome as g_nome, s.matricola as matricola, s.nome as nome, "
					+ "s.cognome as cognome, s.data_nascita as data_nascita, s.indirizzo_codice as indirizzo_codice "
					+ "from gruppo g left outer join studente s on g.id=s.gruppo_id "
					+ "where g.nome = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			boolean primaRiga = true;
			while (result.next()) {
				if (primaRiga) {
					gruppo = new Scuola();
					gruppo.setId(result.getLong("g_id"));				
					gruppo.setNome(result.getString("g_nome"));
					primaRiga = false;
				}
				if(result.getString("matricola")!=null){
					Studente studente = new Studente();
					studente.setMatricola(result.getString("matricola"));
					studente.setNome(result.getString("nome"));
					studente.setCognome(result.getString("cognome"));
					long secs = result.getDate("data_nascita").getTime();
					studente.setDataNascita(new java.util.Date(secs));
					
//					ScuolaDao scuolaDao = new ScuolaDaoJDBC(dataSource);
//					Scuola scuola = scuolaDao.findByPrimaryKey(result.getString("scuola_codice"));
//					studente.setScuolaDiDiploma(scuola);
//					
//					scuola.addStudente(studente);
				}
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
		return gruppo;
	}



	/* 
	 * versione con Lazy Load
	 */
	public Scuola findByPrimaryKey(Long id) {
		Connection connection = this.dataSource.getConnection();
		Scuola scuola = null;
		try {
			PreparedStatement statement;
			String query = "select * from scuola where id = ?";
			statement = connection.prepareStatement(query);
			statement.setLong(1, id);
			ResultSet result = statement.executeQuery();
			if (result.next()) {
				scuola = new Scuola();
				scuola.setId(result.getLong("id"));				
				scuola.setNome(result.getString("nome"));
				scuola.setCodiceMeccanografico(result.getString("codice_meccanografico"));
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
		return scuola;
	}

	public List<Scuola> findAll() {
		Connection connection = this.dataSource.getConnection();
		List<Scuola> scuole = new ArrayList<>();
		try {
			Scuola scuola;
			PreparedStatement statement;
			String query = "select * from scuola";
			statement = connection.prepareStatement(query);
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				scuola = new Scuola();
				scuola.setId(result.getLong("id"));				
				scuola.setNome(result.getString("nome"));
				scuola.setCodiceMeccanografico(result.getString("codice_meccanografico"));
				scuole.add(scuola);
			}
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		}	 finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return scuole;
	}

	public void update(Scuola scuola) {
		Connection connection = this.dataSource.getConnection();
		try {
			String update = "update gruppo SET nome = ?, codice_meccanografico = ? WHERE id = ?";
			PreparedStatement statement = connection.prepareStatement(update);
			statement.setString(1, scuola.getNome());
			statement.setLong(3, scuola.getId());

			//connection.setAutoCommit(false);
			//connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			statement.executeUpdate();
			this.updateStudenti(scuola, connection); // se abbiamo deciso di propagare gli update seguendo il riferimento
			//connection.commit();
		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			} 
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	public void delete(Scuola scuola) {
		Connection connection = this.dataSource.getConnection();
		try {
			String delete = "delete FROM scuola WHERE id = ? ";
			PreparedStatement statement = connection.prepareStatement(delete);
			statement.setLong(1, scuola.getId());

			/* 
			 * rimuoviamo gli studenti dal gruppo (ma non dal database) 
			 * potevano esserci soluzioni diverse (ad esempio rimuovere tutti gli studenti dal database
			 * (ma in questo caso non avrebbe senso)
			 * La scelta dipende dalla semantica delle operazioni di dominio
			 * 
			 * */
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);			
			this.removeForeignKeyFromStudente(scuola, connection);     			
			/* 
			 * ora rimuoviamo il gruppo
			 * 
			 * */
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			throw new PersistenceException(e.getMessage());
		} finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}
}
