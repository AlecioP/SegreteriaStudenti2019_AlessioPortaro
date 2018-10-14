package persistence.dao;

import java.util.List;

import model.Studente;
import persistence.StudenteCredenziali;

public interface StudenteDao {
	
	public void save(Studente studente);  // Create
	public Studente findByPrimaryKey(String matricola);     // Retrieve
	public List<Studente> findAll();       
	public void update(Studente studente); //Update
	public void delete(Studente studente); //Delete	
	
	public void setPassword(Studente studente, String password);
	public StudenteCredenziali findByPrimaryKeyCredential(String matricola);     // Retrieve
}
