package q.rest.location.model.contract;

import q.rest.location.model.entity.Country;
import q.rest.location.model.entity.Region;

import javax.persistence.*;
import java.io.Serializable;

@Table(name="loc_city")
@Entity
public class CityReduced implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="name")
	private String name;
	@Column(name="name_ar")
	private String nameAr;
	@JoinColumn(name="country_id")
	@ManyToOne
	private CountryReduced country;
	@JoinColumn(name="region_id")
	@ManyToOne
	private RegionReduced region;

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

	public CountryReduced getCountry() {
		return country;
	}

	public void setCountry(CountryReduced country) {
		this.country = country;
	}

	public RegionReduced getRegion() {
		return region;
	}

	public void setRegion(RegionReduced region) {
		this.region = region;
	}
}
