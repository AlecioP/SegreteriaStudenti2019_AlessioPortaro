package persistence;



import persistence.dao.CorsoDao;
import persistence.dao.CorsoDiLaureaDao;
import persistence.dao.DipartimentoDao;
import persistence.dao.ScuolaDao;
import persistence.dao.StudenteDao;
import persistence.daoJDBC.CorsoDaoJDBC;
import persistence.daoJDBC.CorsoDiLaureaDaoJDBC;
import persistence.daoJDBC.DipartimentoDaoJDBC;
import persistence.daoJDBC.ScuolaDaoJDBC;
import persistence.daoJDBC.StudenteDaoJDBC;

class PostgresDAOFactory extends DAOFactory {

	
	
	private static  DataSource dataSource;
	

	// --------------------------------------------

	static {
		try {
			Class.forName("org.postgresql.Driver").newInstance();
			//questi vanno messi in file di configurazione!!!	
//			dataSource=new DataSource("jdbc:postgresql://52.39.164.176:5432/xx","xx","p@xx");
			dataSource=new DataSource("jdbc:postgresql://localhost:5432/Segreteria2019","postgres","postgres");
		} 
		catch (Exception e) {
			System.err.println("PostgresDAOFactory.class: failed to load MySQL JDBC driver\n"+e);
			e.printStackTrace();
		}
	}

	
	// --------------------------------------------
	
	@Override
	public StudenteDao getStudenteDAO() {
		return new StudenteDaoJDBC(dataSource);
	}

	@Override
	public ScuolaDao getScuolaDAO() {
		return new ScuolaDaoJDBC(dataSource);
	}
	
	@Override
	public CorsoDao getCorsoDAO() {
		return new CorsoDaoJDBC(dataSource);
	}
	
	@Override
	public CorsoDiLaureaDao getCorsoDiLaureaDAO() {
		return new CorsoDiLaureaDaoJDBC(dataSource);
	}
	
	@Override
	public DipartimentoDao getDipartimentoDAO() {
		return new DipartimentoDaoJDBC(dataSource);
	}
	
	@Override
	public UtilDao getUtilDAO(){
		return new UtilDao(dataSource);
	}
}
