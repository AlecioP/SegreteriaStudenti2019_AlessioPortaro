package persistence.daoJDBC;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.CorsoDiLaurea;
import model.Dipartimento;
import persistence.DataSource;
import persistence.IdBroker;
import persistence.PersistenceException;
import persistence.dao.DipartimentoDao;

public class DipartimentoDaoJDBC implements DipartimentoDao{

	private DataSource ds;

	public DipartimentoDaoJDBC(DataSource ds) {
		this.ds=ds;
	}

	@Override
	public void save(Dipartimento dipartimento) {
		Connection connection = ds.getConnection();
		try {
			if(dipartimento.getCodice()==null)
				dipartimento.setCodice(IdBroker.getId(connection));
//			CorsoDiLaureaDaoJDBC cdlJdbc = new CorsoDiLaureaDaoJDBC(ds);
//			ArrayList<CorsoDiLaurea> cdls = (ArrayList<CorsoDiLaurea>) cdlJdbc.findByReferencedKey(dipartimento);

//			if(cdls == null || cdls.isEmpty())
//				throw new PersistenceException("REFERENCE ERROR : THE TUPLE IN TABLE \"dipartimento\" NEEDS TO BE REFERENCED AT LEAST FROM ONE TUPLE IN TABLE \"corsodilaurea\"");

			String insert_dipartimento_sql_s = "INSERT INTO dipartimento(codice, nome) values (?, ?)";

			PreparedStatement insert_dipartimento_sql = connection.prepareStatement(insert_dipartimento_sql_s);

			insert_dipartimento_sql.setLong(1, dipartimento.getCodice());
			insert_dipartimento_sql.setString(2, dipartimento.getNome());

			insert_dipartimento_sql.executeUpdate();

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
	public Dipartimento findByPrimaryKey(Long codice) {

		Connection connection = ds.getConnection();
		String select_dip_sql_s = "SELECT * "
				+ "FROM dipartimento "
				+ "WHERE codice = ?";
		try{
			PreparedStatement select_dip_sql = connection.prepareStatement(select_dip_sql_s);
			select_dip_sql.setLong(1, codice);
			System.out.println("QUERY IS : "+select_dip_sql.toString());
			ResultSet  result_select_dip_sql = select_dip_sql.executeQuery();

			Dipartimento dip = null;

			if(result_select_dip_sql.next()) {
				dip = new Dipartimento();
				dip.setCodice(codice);
				dip.setNome(result_select_dip_sql.getString("nome"));
			}
			return dip;
		}catch(SQLException e) {
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
	public List<Dipartimento> findAll() {
		Connection connection = ds.getConnection();
		String selectAll_sql_s = "SELECT * FROM dipartimento";
		try{
			PreparedStatement selectAll_sql = connection.prepareStatement(selectAll_sql_s);
			ResultSet result_selectAll_sql = selectAll_sql.executeQuery();
			ArrayList<Dipartimento> dips = new ArrayList<Dipartimento>();
			while(result_selectAll_sql.next()) {
				Dipartimento dip = new Dipartimento();
				dip.setCodice(result_selectAll_sql.getLong("codice"));
				dip.setNome(result_selectAll_sql.getString("nome"));
				dips.add(dip);
			}

			return dips;
		}catch(SQLException e) {
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
	public void update(Dipartimento dipartimento) {
		Connection connection = ds.getConnection();
		String update_dip_sql_s = "UPDATE dipartimento SET nome = ? WHERE codice = ?";
		try{
			PreparedStatement update_dip_sql = connection.prepareStatement(update_dip_sql_s);
			update_dip_sql.setString(1, dipartimento.getNome());
			update_dip_sql.setLong(2, dipartimento.getCodice());
			update_dip_sql.executeUpdate();
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
	public void delete(Dipartimento dipartimento) {
		CorsoDiLaureaDaoJDBC cdlJdbc = new CorsoDiLaureaDaoJDBC(ds);
		ArrayList<CorsoDiLaurea> cdls = (ArrayList<CorsoDiLaurea>) cdlJdbc.findByReferencedKey(dipartimento);
		
		for(CorsoDiLaurea cdl : cdls) {
			cdlJdbc.delete(cdl);
		}
		
		Connection connection = ds.getConnection();
		
		String delete_dip_sql_s = "DELETE FROM dipartimento WHERE codice = ?";
		try{
			PreparedStatement delete_dip_sql = connection.prepareStatement(delete_dip_sql_s);
			delete_dip_sql.setLong(1, dipartimento.getCodice());
			delete_dip_sql.executeUpdate();
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
}
