package q.rest.location.operation;

import q.rest.location.dao.DAO;
import q.rest.location.filter.SecuredCustomer;
import q.rest.location.filter.SecuredUser;
import q.rest.location.filter.ValidApp;
import q.rest.location.model.contract.PublicCity;
import q.rest.location.model.contract.PublicCountry;
import q.rest.location.model.contract.PublicRegion;

import java.util.List;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationApiV2 {

	@EJB
	private DAO dao;

	@GET
	@Path("regions/country-id/{param}")
	@ValidApp
	public Response getRegionsFromCountryId(@PathParam(value = "param") int countryId) {
		try {
			List<PublicRegion> regions = dao.getCondition(PublicRegion.class, "countryId", countryId);
			for (PublicRegion region : regions) {
				List<PublicCity> cities = dao.getTwoConditions(PublicCity.class, "customerStatus", "regionId", 'A',
						region.getId());
				region.setCities(cities);
			}
			return Response.status(200).entity(regions).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@GET
	@ValidApp
	@Path("countries-only")
	public Response getActiveCountriesOnly() {
		try {
			String sql = "select b from PublicCountry b where b.customerStatus =:value0 order by b.id";
			List<PublicCountry> list = dao.getJPQLParams(PublicCountry.class, sql, 'A');
			return Response.status(200).entity(list).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void prepareRegionsCities(List<PublicRegion> regions) {
		for (PublicRegion region : regions) {
			List<PublicCity> cities = dao.getTwoConditions(PublicCity.class, "customerStatus", "regionId", 'A',
					region.getId());
			region.setCities(cities);
		}
	}

	private void prepareCountryRegions(List<PublicCountry> countries) {
		for (PublicCountry country : countries) {
			List<PublicRegion> regions = dao.getCondition(PublicRegion.class, "countryId", country.getId());
			prepareRegionsCities(regions);
			country.setRegions(regions);
		}
	}

	@GET
	@ValidApp
	@Path("countries")
	public Response getActiveCountriesFull() {
		try {
			String sql = "select b from PublicCountry b where b.customerStatus =:value0 order by b.id";
			List<PublicCountry> countries = dao.getJPQLParams(PublicCountry.class, sql, 'A');
			prepareCountryRegions(countries);
			return Response.status(200).entity(countries).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@GET
	@Path("find-city/name/{param}/country/{param2}")
	@SecuredCustomer
	public Response findCountryCityByName(@PathParam(value = "param2") int countryId,
			@PathParam(value = "param") String name) {
		try {
			//remove (al) in arabic and english, 
			//add another search condition
			//for example : Khobar, Al-Khobar, Alkhobar
			String sql = "select d from PublicCity d where lower(d.name) like :value0 "
					+ " or lower(d.nameAr) like :value0 and d.countryId = :value1 and d.customerStatus = :value2";
			List<PublicCity> cities = dao.getJPQLParams(PublicCity.class, sql, "%" + name.trim().toLowerCase() + "%",
					countryId, 'A');
			if (cities.isEmpty()) {
				return Response.status(404).build();
			}
			return Response.status(200).entity(cities).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

}
