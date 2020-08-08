package q.rest.location.operation;

import q.rest.location.dao.DAO;
import q.rest.location.filter.v3.annotation.InternalApp;
import q.rest.location.filter.v3.annotation.UserJwt;
import q.rest.location.model.contract.CityReduced;
import q.rest.location.model.entity.*;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Path("/internal/api/v3/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationInternalApiV3 implements Serializable {

    @EJB
    private DAO dao;


    @POST
    @UserJwt
    @Path("country")
    public Response createCountry(Country country){
        List<Country> check = dao.getCondition(Country.class, "name", country.getName());
        if(!check.isEmpty()){
            return Response.status(409).build();
        }
        dao.persist(country);
        return Response.status(200).build();
    }


    @PUT
    @UserJwt
    @Path("country")
    public Response updateCountry(Country country){
        dao.update(country);
        return Response.status(200).build();
    }

    @POST
    @UserJwt
    @Path("region")
    public Response createCountry(Region region){
        List<Region> check = dao.getTwoConditions(Region.class, "name", "country.id", region.getName(), region.getCountry().getId());
        if(!check.isEmpty()){
            return Response.status(409).build();
        }
        dao.persist(region);
        return Response.status(200).build();
    }

    @POST
    @UserJwt
    @Path("city")
    public Response createCity(City city){
        List<City> check = dao.getTwoConditions(City.class, "name", "country.id", city.getName(), city.getCountry().getId());
        if(!check.isEmpty()){
            return Response.status(409).build();
        }
        dao.persist(city);
        return Response.status(200).build();
    }

    @PUT
    @UserJwt
    @Path("region")
    public Response updateRegion(Region region){
        dao.update(region);
        return Response.status(200).build();
    }

    @GET
    @Path("countries")
    @UserJwt
    public Response getCountries(){
        try{
            List<Country> list = dao.getOrderBy(Country.class, "id");
            for(Country c : list){
                List<City> cities = dao.getCondition(City.class, "country", c);
                c.setCities(cities);
                List<Region> regions = dao.getCondition(Region.class, "country", c);
                for(Region region : regions) {
                    List<City> cities2 = dao.getCondition(City.class, "region", region);
                    region.setCities(cities2);
                }
                c.setRegions(regions);
            }
            return Response.status(200).entity(list).build();
        }
        catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @GET
    @Path("regions")
    @UserJwt
    public Response getAllRegions() {
        try {
            List<Region> regions = dao.getOrderBy(Region.class, "id");
            for(Region region : regions) {
                List<City> cities = dao.getCondition(City.class, "region", region);
                region.setCities(cities);
            }
            return Response.status(200).entity(regions).build();
        }catch(Exception ex) {
            return Response.status(500).build();
        }
    }

    @GET
    @Path("cities")
    @UserJwt
    public Response getCities(){
        try{
            List<City> list = dao.get(City.class);
            return Response.status(200).entity(list).build();
        }
        catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @POST
    @InternalApp
    @Path("cities/reduced")
    public Response getCityReduced(Map<String,Object> map){
        String sql = "select * from loc_city c where c.id in (0 ";
        for(int id : (ArrayList<Integer>)map.get("cityIds")){
            sql += ","+id;
        }
        sql += ")";
        List<CityReduced> reduced = dao.getNative(CityReduced.class, sql);
        return Response.ok().entity(reduced).build();
    }

}
