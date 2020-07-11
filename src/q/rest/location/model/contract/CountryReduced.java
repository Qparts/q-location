package q.rest.location.model.contract;

import q.rest.location.model.entity.City;
import q.rest.location.model.entity.Region;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Table(name="loc_country")
@Entity
public class CountryReduced implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="name")
	private String name;
	@Column(name="name_ar")
	private String nameAr;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameAr() {
		return nameAr;
	}

	public void setNameAr(String nameAr) {
		this.nameAr = nameAr;
	}
}
