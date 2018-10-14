package persistence.proxy;

import model.CorsoDiLaurea;
import persistence.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import model.Corso;

public class CorsoDiLaureaProxy extends CorsoDiLaurea {

	private DataSource ds;

	public CorsoDiLaureaProxy(DataSource ds) {
		this.ds = ds;
	}

	public Set<Corso> getCorsi(){
		Connection connection = ds.getConnection();
		String select_corsiCDL_sql_s =    "SELECT corso.codice as CodiceCorso, corso.nome as NomeCorso"

												+ "FROM afferisce, corso "

												+ "WHERE afferisce.corsodilaurea_codice = ? "

												+ "AND afferisce.corso_codice = corso.codice";

		try {
			PreparedStatement select_corsiCDL_sql = connection.prepareStatement(select_corsiCDL_sql_s);
			select_corsiCDL_sql.setLong(1, this.getCodice());
			ResultSet corsi = select_corsiCDL_sql.executeQuery();
			
			Set<Corso> result = new HashSet<Corso>();
			
			while(corsi.next()) {
				Corso corso = new CorsoProxy(ds);
				corso.setCodice(corsi.getLong("CodiceCorso"));
				corso.setNome(corsi.getString("NomeCorso"));
				
				result.add(corso);
			}
			return result;
		} catch (SQLException e) {
			
		}
		
		return null;

	}

}
