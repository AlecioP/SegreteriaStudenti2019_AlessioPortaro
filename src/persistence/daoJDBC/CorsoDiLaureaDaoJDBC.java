package persistence.daoJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Corso;
import model.CorsoDiLaurea;
import model.Dipartimento;
import model.Studente;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.dao.CorsoDiLaureaDao;
import persistence.proxy.CorsoDiLaureaProxy;
//import utility.Utility;
import persistence.proxy.CorsoProxy;

public class CorsoDiLaureaDaoJDBC implements CorsoDiLaureaDao{

	DataSource ds;

	public CorsoDiLaureaDaoJDBC(DataSource ds) {
		this.ds = ds;
	}

	@Override
	public void save(CorsoDiLaurea corsoDiLaurea) {
		if ( (corsoDiLaurea.getCorsi() == null) 
				|| corsoDiLaurea.getCorsi().isEmpty()){
			throw new PersistenceException("Corso di laurea non memorizzato: un corso di laurea deve avere almeno un corso");
		}
		Connection connection = ds.getConnection();

		try {

			/*CREATES NEW CDL IN THE DB*/
			String insert_sql = "insert into corsodilaurea(codice, nome, dipartimento_codice) values (?,?,?)";
			PreparedStatement statement = connection.prepareStatement(insert_sql);
			Long id = IdBroker.getId(connection);
			corsoDiLaurea.setCodice(id);
			statement.setLong(1, corsoDiLaurea.getCodice());
			statement.setString(2, corsoDiLaurea.getNome());
			statement.setLong(3, corsoDiLaurea.getDipartimento().getCodice());
			statement.executeUpdate();

			/**/

			boolean atLeastOne = false;
			CorsoDaoJDBC corsoDAO = new CorsoDaoJDBC(this.ds);
			for(Corso c : corsoDiLaurea.getCorsi()) {
				atLeastOne = true;
				Corso result = corsoDAO.findByPrimaryKey(c.getCodice());
				if(result == null)
					corsoDAO.save(c);

				String afferisceExistingYet = "select * from afferisce where corso_codice = ? AND corsodilaurea_codice = ?";

				PreparedStatement exist_sql = connection.prepareStatement(afferisceExistingYet);

				exist_sql.setLong(1, c.getCodice());
				exist_sql.setLong(2, corsoDiLaurea.getCodice());

				ResultSet resultSet = exist_sql.executeQuery();

				if(resultSet.next()) {
					String update_sql_s = "UPDATE afferisce SET corsodilaurea_codice = ? WHERE id = ?";
					PreparedStatement update_sql = connection.prepareStatement(update_sql_s);
					update_sql.setLong(1, corsoDiLaurea.getCodice());
					update_sql.setLong(2, resultSet.getLong("id")); //specify column name
					update_sql.executeUpdate();
				}
				else {
					String insert_sql_s = "INSERT INTO afferisce(id, corso_codice, corsodilaurea_codice) values (?,?,?)";
					PreparedStatement insert_afferisce_sql = connection.prepareStatement(insert_sql_s);
					insert_afferisce_sql.setLong(1, IdBroker.getId(connection));
					insert_afferisce_sql.setLong(2, c.getCodice());
					insert_afferisce_sql.setLong(3,  corsoDiLaurea.getCodice());
					insert_afferisce_sql.executeUpdate();
				}
			}
			if(!atLeastOne)
				throw new PersistenceException("TABLE \"Corso di Laurea\" needs to be referenced at least to 1 instance of TABLE \"Afferisce\" ");

		} catch (SQLException e) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch(SQLException excep) {
					throw new PersistenceException(e.getMessage());
				}
			} 
		}finally {
			try {
				connection.close();
			}catch(SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public CorsoDiLaurea findByPrimaryKey(Long codice) {

		String selectCDL_sql_s = "SELECT * FROM corsodilaurea WHERE codice = ?";
		Connection connection = ds.getConnection();
		CorsoDiLaurea cdl = null;
		try {
			PreparedStatement selectCDL_sql = connection.prepareStatement(selectCDL_sql_s);
			selectCDL_sql.setLong(1, codice);

			ResultSet result = selectCDL_sql.executeQuery();

			if(result.next()) {
				cdl = new CorsoDiLaurea();
				cdl.setCodice(result.getLong("codice"));
				cdl.setNome(result.getString("nome"));
				DipartimentoDaoJDBC dip_jdbc = new DipartimentoDaoJDBC(ds);
				Dipartimento dip = dip_jdbc.findByPrimaryKey(result.getLong("dipartimento_codice"));
				cdl.setDipartimento(dip);

				String select_corsiCDL_sql_s =    "SELECT corso.codice as CodiceCorso, corso.nome as NomeCorso"

												+ "FROM afferisce, corso "

												+ "WHERE afferisce.corsodilaurea_codice = ? "

												+ "AND afferisce.corso_codice = corso.codice";

				PreparedStatement select_corsiCDL_sql = connection.prepareStatement(select_corsiCDL_sql_s);
				select_corsiCDL_sql.setLong(1, cdl.getCodice());
				ResultSet corsi = select_corsiCDL_sql.executeQuery();

				Set<Corso> corsiCDL = new HashSet<Corso>();

				while(corsi.next()) {
					//					Corso corso = new Corso();
					Corso corso = new CorsoProxy(ds);
					corso.setCodice(corsi.getLong("CodiceCorso"));
					corso.setNome(corsi.getString("NomeCorso"));
					/*NOW GETS ALL THE STUDENT IN CORSO*/

					Set<Studente> studenti = new HashSet<Studente>();


					/*
					String select_studenteCorso_sql_s = "SELECT   studente.matricola as MatricolaStudente"
																+"studente.nome as NomeStudente"
																+ "studente.cognome as CognomeStudente"
																+ "studente.data_nascita as NascitaStudente"
																+ "studente.scuola_id as IDScuolaStudente"
													  + "FROM iscritto, studente"
													  + "WHERE iscritto.corso_codice = ?"
													  + "AND iscritto.matricola_studente = studente.matricola";

					PreparedStatement select_studenteCorso_sql = connection.prepareStatement(select_studenteCorso_sql_s);
					select_studenteCorso_sql.setLong(1, corso.getCodice());

					ResultSet result_studenteCorso = select_studenteCorso_sql.executeQuery();

					while(result_studenteCorso.next()) {
						String matricola = result_studenteCorso.getString("MatricolaStudente");
						String nome = result_studenteCorso.getString("NomeStudente");
						String cognome = result_studenteCorso.getString("CognomeStudente");
						java.util.Date dataNascita = Utility.convertToJava(result_studenteCorso.getDate("NascitaStudente"));

						Studente stud = new Studente(matricola, nome, cognome, dataNascita);

						studenti.add(stud);
					}
					 */
					studenti = corso.getStudenti();
					corso.setStudenti(studenti);
					corsiCDL.add(corso);
				}

				cdl.setCorsi(corsiCDL);
			}

		} catch (SQLException e) {
			try {
				connection.rollback();;
			}catch(SQLException e1) {
				throw new PersistenceException(e.getMessage());
			}
		}finally {
			try {
				connection.close();
			}catch(SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
		return cdl;
	}

	@Override
	public List<CorsoDiLaurea> findAll() {
		Connection connection = ds.getConnection();

		String selectAll_CDL_sql_s = "SELECT * FROM corsodilaurea";

		try {
			PreparedStatement selectAll_CDL_sql = connection.prepareStatement(selectAll_CDL_sql_s);
			ResultSet queryResult = selectAll_CDL_sql.executeQuery();
			List<CorsoDiLaurea> resultCDL = new ArrayList<CorsoDiLaurea>();
			while(queryResult.next()) {
				CorsoDiLaureaProxy proxyCDL = new CorsoDiLaureaProxy(ds);
				proxyCDL.setCodice(queryResult.getLong("codice"));
				proxyCDL.setNome(queryResult.getString("nome"));
				DipartimentoDaoJDBC dipJdbc = new DipartimentoDaoJDBC(ds);
				Dipartimento dip = dipJdbc.findByPrimaryKey(queryResult.getLong("dipartimento_codice"));
				proxyCDL.setDipartimento(dip);
				resultCDL.add(proxyCDL);
			}
			return resultCDL;
		} catch (SQLException e) {
			try {
				connection.rollback();
				return null;
			} catch (SQLException e1) {
				throw new PersistenceException(e.getMessage());
			}
		}finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public void update(CorsoDiLaurea corsoDiLaurea) {
		Connection connection = ds.getConnection();

		String exists_CDL_sql_s = "SELECT * FROM corsodilaurea WHERE codice = ?";

		try {
			PreparedStatement exists_CDL_sql = connection.prepareStatement(exists_CDL_sql_s);
			exists_CDL_sql.setLong(1, corsoDiLaurea.getCodice());
			ResultSet result_exist_CDL_sql = exists_CDL_sql.executeQuery();
			DipartimentoDaoJDBC dipJdbc = new DipartimentoDaoJDBC(ds);
			if(result_exist_CDL_sql.next()) {
				Dipartimento dip = dipJdbc.findByPrimaryKey(result_exist_CDL_sql.getLong("dipartimento_codice"));
				if(dip==null)
					dipJdbc.save(corsoDiLaurea.getDipartimento());


				//UPDATE ONLY CDL's TUPLE

				String update_cdl_sql_s = "UPDATE corsodilaurea SET (nome, dipartimento_codice) = (?,?) WHERE codice = ?";

				PreparedStatement update_cdl_sql = connection.prepareStatement(update_cdl_sql_s);

				update_cdl_sql.setString(1, corsoDiLaurea.getNome());
				update_cdl_sql.setLong(2, corsoDiLaurea.getDipartimento().getCodice());
				update_cdl_sql.setLong(3, corsoDiLaurea.getCodice());
				update_cdl_sql.executeUpdate();


			}else {
				Dipartimento dip = dipJdbc.findByPrimaryKey(corsoDiLaurea.getDipartimento().getCodice());
				if(dip==null)
					dipJdbc.save(corsoDiLaurea.getDipartimento());
				
				save(corsoDiLaurea);
			}

		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new PersistenceException(e.getMessage());
			}

		}finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public void delete(CorsoDiLaurea corsoDiLaurea) {
		String delete_afferisce_sql_s = "DELETE FROM afferisce WHERE corsodilaurea_codice = ?";
		Connection connection  = ds.getConnection();
		try {
			PreparedStatement delete_afferisce_sql = connection.prepareStatement(delete_afferisce_sql_s);
			delete_afferisce_sql.setLong(1, corsoDiLaurea.getCodice());
			delete_afferisce_sql.executeUpdate();
			
			String delete_CDL_sql_s = "DELETE FROM corsodilaurea WHERE codice = ?";
			PreparedStatement delete_CDL_sql = connection.prepareStatement(delete_CDL_sql_s);
			delete_CDL_sql.setLong(1, corsoDiLaurea.getCodice());
			delete_CDL_sql.executeUpdate();
			
		}catch(SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new PersistenceException(e.getMessage());
			}
		}finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

	@Override
	public List<CorsoDiLaurea> findByReferencedKey(Dipartimento dip) {
		Connection connection = ds.getConnection();
		String select_CDL_sql_s = "SELECT corsodilaurea.codice as CodiceCDL,"
				+ "corsodilaurea.nome as NomeCDL,"
				+ "corsodilaurea.dipartimento_codice as DipartimentoCDL "
				+ "FROM corsodilaurea "
				+ "WHERE corsodilaurea.dipartimento_codice = ?";
		
		try {
			PreparedStatement select_CDL_sql = connection.prepareStatement(select_CDL_sql_s);
			select_CDL_sql.setLong(1, dip.getCodice());
			ResultSet result_select_CDL_sql = select_CDL_sql.executeQuery();
			ArrayList<CorsoDiLaurea> cdls = new ArrayList<CorsoDiLaurea>();
			while(result_select_CDL_sql.next()) {
				CorsoDiLaureaProxy cdl = new CorsoDiLaureaProxy(ds);
				cdl.setCodice(result_select_CDL_sql.getLong("CodiceCDL"));
				cdl.setNome(result_select_CDL_sql.getString("NomeCDL"));
				cdl.setDipartimento(dip);
				
				cdls.add(cdl);
			}
			
			return cdls;
		} catch (SQLException e) {
			try {
				connection.rollback();
				return null;
			} catch (SQLException e1) {
				throw new PersistenceException(e.getMessage());
			}
		}finally {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new PersistenceException(e.getMessage());
			}
		}
	}

}
