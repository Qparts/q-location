package q.rest.location.model.contract;

import q.rest.location.model.entity.City;
import q.rest.location.model.entity.Country;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="loc_region")
public class RegionReduced implements Serializable{
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


