package q.rest.location.operation;

import q.rest.location.dao.DAO;
import q.rest.location.filter.v2.SecuredUser;
import q.rest.location.filter.v3.annotation.SubscriberJwt;
import q.rest.location.filter.v3.annotation.V3ValidApp;
import q.rest.location.model.contract.PublicCity;
import q.rest.location.model.contract.PublicCountry;
import q.rest.location.model.contract.PublicRegion;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/api/v3/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationApiV3 {

	@EJB
	private DAO dao;


	@GET
	@Path("regions/country-id/{param}")
	@V3ValidApp
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
	@V3ValidApp
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
	@V3ValidApp
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
	@V3ValidApp
	@Path("regions")
	public Response getActiveRegions() {
		try {
			String sql = "select b from PublicRegion b order by b.name";
			List<PublicRegion> regions = dao.getJPQLParams(PublicRegion.class, sql);
			prepareRegionsCities(regions);
			return Response.status(200).entity(regions).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@GET
	@V3ValidApp
	@Path("cities")
	public Response getActiveCities() {
		try {
			String sql = "select b from PublicCity b where b.customerStatus =:value0 order by b.name";
			List<PublicCity> cities = dao.getJPQLParams(PublicCity.class, sql, 'A');
			return Response.status(200).entity(cities).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@POST
	@SubscriberJwt
	@Path("search-locations")
	public Response searchLocations(Map<String,String> map){
		String query = "%" +map.get("query").trim() + "%";
		String sql = "select b from PublicCountry b where b.customerStatus =:value0 and (lower(b.name) like :value1 or lower(b.nameAr) like :value1)";
		List<PublicCountry> countries = dao.getJPQLParams(PublicCountry.class, sql, 'A', query);
		sql = "select b from PublicRegion b where lower(b.name) like :value0 or lower(b.nameAr) like :value0";
		List<PublicRegion> regions = dao.getJPQLParams(PublicRegion.class, sql, query);
		sql = "select b from PublicCity b where b.customerStatus =:value0 and (lower(b.name) like :value1 or lower(b.nameAr) like :value1)";
		List<PublicCity> cities = dao.getJPQLParams(PublicCity.class, sql, 'A', query);
		List<Map<String,Object>> result = new ArrayList<>();
		for(var c : countries) {
			Map<String,Object> mp = new HashMap<>();
			mp.put("type", 'C');
			mp.put("object", c);
			result.add(mp);
		}
		for(var c : regions) {
			Map<String,Object> mp = new HashMap<>();
			mp.put("type", 'R');
			mp.put("object", c);
			result.add(mp);
		}
		for(var c : cities) {
			Map<String,Object> mp = new HashMap<>();
			mp.put("type", 'T');
			mp.put("object", c);
			result.add(mp);
		}
		return Response.status(200).entity(result).build();
	}

	public Response getSecuredRequest(String link, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.get();
		return r;
	}

}
