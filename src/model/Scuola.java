package model;

public class Scuola {
	private Long id;
	private String codiceMeccanografico;
	private String nome;	

	public Scuola(){
	}
	
	public Scuola(String nome){
		this.nome=nome;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}

	public String getCodiceMeccanografico() {
		return codiceMeccanografico;
	}
	
	public void setCodiceMeccanografico(String codiceMeccanografico) {
		this.codiceMeccanografico = codiceMeccanografico;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String toString() {
		StringBuffer str = new StringBuffer("Scuola[");
		str.append(this.getId() + ", " + this.getCodiceMeccanografico() + ", " + this.getNome());
		return str.toString();
	}
}