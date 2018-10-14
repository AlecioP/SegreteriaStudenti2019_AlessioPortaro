package persistence;


import java.util.Calendar;
import java.util.Date;

import model.Corso;
import model.CorsoDiLaurea;
import model.Dipartimento;
import model.Scuola;
import model.Studente;
import persistence.dao.CorsoDao;
import persistence.dao.CorsoDiLaureaDao;
import persistence.dao.DipartimentoDao;
import persistence.dao.ScuolaDao;
import persistence.dao.StudenteDao;

public class MainJDBC {
	public static void main(String args[]) {
		Calendar cal = Calendar.getInstance();
		cal.set(1995, Calendar.MARCH, 21); // // 21 marzo 1995
		Date date1 = cal.getTime();
		cal.set(1996, Calendar.APRIL, 12); // 12 aprile 1996
		Date date2 = cal.getTime();
		cal.set(1998, Calendar.OCTOBER, 1);  // 1 ottobre 1998
		Date date3 = cal.getTime();

		DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.POSTGRESQL);
		UtilDao util = factory.getUtilDAO();
		util.dropDatabase();
		
		util.createDatabase();		
		
		StudenteDao studenteDao = factory.getStudenteDAO();
		ScuolaDao scuolaDao = factory.getScuolaDAO();
		CorsoDao corsoDao = factory.getCorsoDAO();
		CorsoDiLaureaDao corsoDiLaureaDao = factory.getCorsoDiLaureaDAO();
		DipartimentoDao dipartimentoDao = factory.getDipartimentoDAO();

		Studente studente1 = new Studente("00000001","Rossi","Mario",date1);
		
		Studente studente2 = new Studente();
		studente2.setCognome("Verdi");
		studente2.setNome("Anna");
		studente2.setMatricola("00000002");
		studente2.setDataNascita(date2);

		Studente studente3 = new Studente();
		studente3.setCognome("Bianchi");
		studente3.setNome("Antonio");
		studente3.setMatricola("00000003");
		studente3.setDataNascita(date3);

		Scuola scuola1 = new Scuola();
		//l'id del gruppo e' gestito tramite la classe IDBroker
		scuola1.setCodiceMeccanografico("RCISSE45SDF2");
		scuola1.setNome("Istituto Melandri");
		
		Scuola scuola2 = new Scuola();
		//l'id del gruppo e' gestito tramite la classe IDBroker
		scuola2.setCodiceMeccanografico("RCIS673SDF2");
		scuola2.setNome("Istituto Gizzi");	
		
		Scuola scuola3 = new Scuola();
		//l'id del gruppo e' gestito tramite la classe IDBroker
		scuola3.setCodiceMeccanografico("RCA345S5SDF2");
		scuola3.setNome("Istituto Morchi");	
		
		studente1.setScuolaDiDiploma(scuola3);
		studente2.setScuolaDiDiploma(scuola3);
		studente3.setScuolaDiDiploma(scuola1);
				
		Corso corso1 = new Corso();
		corso1.setNome("Web Computing");
		corso1.addStudente(studente1);
		corso1.addStudente(studente2);
		corso1.addStudente(studente3);
		
		Corso corso2 = new Corso();
		corso2.setNome("Ingegneria del Software");
		corso2.addStudente(studente1);		
		corso2.addStudente(studente3);

		//CREATE
		scuolaDao.save(scuola1);
		scuolaDao.save(scuola2);
		scuolaDao.save(scuola3);
		
		studenteDao.save(studente1);
		studenteDao.save(studente2);
		studenteDao.save(studente3);
		
		corsoDao.save(corso1);
		corsoDao.save(corso2);
		
		Dipartimento dipartimento1 = new Dipartimento("Matematica e Informatica");
		Dipartimento dipartimento2 = new Dipartimento("Biologia");
		
		dipartimentoDao.save(dipartimento1);
		dipartimentoDao.save(dipartimento2);
		
		CorsoDiLaurea corsoDiLaurea1 = new CorsoDiLaurea();
		corsoDiLaurea1.setNome("Informatica");
		corsoDiLaurea1.addCorso(corso1);
		corsoDiLaurea1.setDipartimento(dipartimento1);
		
		CorsoDiLaurea corsoDiLaurea2 = new CorsoDiLaurea();
		corsoDiLaurea2.setNome("Scienze Biologiche");
		corsoDiLaurea2.addCorso(corso1);
		corsoDiLaurea2.addCorso(corso2);
		corsoDiLaurea2.setDipartimento(dipartimento2);
		
		corsoDiLaureaDao.save(corsoDiLaurea1);
		corsoDiLaureaDao.save(corsoDiLaurea2);
		
		System.out.println("Retrieve all Scuola");
		for(Scuola s : scuolaDao.findAll()) {
			System.out.println(s);
		}

//		gruppo1.addStudente(studente3);
//		gruppoDao.update(gruppo1);
		
		
		System.out.println("Elenco studenti");
		for(Studente s : studenteDao.findAll()) {
			System.out.println(s);
		}	
		
		System.out.println("Elenco Corsi");
		for(Corso c : corsoDao.findAll()) {
			System.out.println(c);
		}	
		
		System.out.println("Elenco Corsi di Laurea");
		for(CorsoDiLaurea cl : corsoDiLaureaDao.findAll()) {
			System.out.println(cl);
		}	
	}
}
