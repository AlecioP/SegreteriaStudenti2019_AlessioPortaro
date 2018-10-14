package persistence._JDBC_Dao;

import java.util.List;

import model.Dipartimento;
import persistence.DataSource;
import persistence.dao.DipartimentoDao;

public class DipartimentoDaoJDBC implements DipartimentoDao{

	public DipartimentoDaoJDBC(DataSource ds) {
		
	}

	@Override
	public void save(Dipartimento dipartimento) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Dipartimento findByPrimaryKey(Long codice) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Dipartimento> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Dipartimento dipartimento) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(Dipartimento dipartimento) {
		// TODO Auto-generated method stub
		
	}
}
