package q.rest.location.operation;

import q.rest.location.dao.DAO;
import q.rest.location.filter.v2.SecuredUser;
import q.rest.location.model.entity.City;
import q.rest.location.model.entity.Country;
import q.rest.location.model.entity.Region;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Path("/internal/api/v2/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationInternalApiV2 implements Serializable {

    @EJB
    private DAO dao;


    @GET
    @Path("countries")
    @SecuredUser
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



    //idempotent
    @POST
    @Path("country")
    @SecuredUser
    public Response createCountry(Country country){
        try{
            List<Country> l = dao.getCondition(Country.class, "name", country.getName());
            if(!l.isEmpty()){
                return Response.status(409).build();//conflict! Resource exists
            }
            dao.persist(country);
            return Response.status(201).build();
        }
        catch(Exception ex){
            return Response.status(500).build();
        }
    }



    @GET
    @Path("regions")
    @SecuredUser
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



    @POST
    @Path("region")
    @SecuredUser
    public Response createRegion(Region region) {
        try {
            List<Region> l = dao.getCondition(Region.class, "name", region.getName());
            if (!l.isEmpty()) {
                return Response.status(409).build();//conflict! Resource exists
            }
            dao.persist(region);
            return Response.status(201).build();
        }catch (Exception ex){
            return Response.status(500).build();
        }
    }




    //idempotent
    @POST
    @Path("city")
    @SecuredUser
    public Response createCity(City city){
        try{
            List<City> l = dao.getCondition(City.class, "name", city.getName());
            if(!l.isEmpty()){
                return Response.status(409).build();//conflict! Resource exists
            }
            dao.persist(city);
            return Response.status(201).build();
        }
        catch(Exception ex){
            ex.printStackTrace();
            return Response.status(500).build();
        }
    }


    @GET
    @Path("cities")
    @SecuredUser
    public Response getCities(){
        try{
            List<City> list = dao.get(City.class);
            return Response.status(200).entity(list).build();
        }
        catch (Exception ex){
            return Response.status(500).build();
        }
    }

    @GET
    @Path("city/{param}/names-only")
    @SecuredUser
    public Response getCityNameVariables(@PathParam(value = "param") int id){
        try{
            City city = dao.find(City.class, id);
            Map<String,String> map = new HashMap<String, String>();
            map.put("cityName", city.getName());
            map.put("cityNameAr", city.getNameAr());
            map.put("regionName", city.getRegion().getName());
            map.put("regionNameAr", city.getRegion().getNameAr());
            map.put("countryName", city.getCountry().getName());
            map.put("countryNameAr", city.getCountry().getNameAr());
            return Response.status(200).entity(map).build();
        }
        catch (Exception ex){
            return Response.status(500).build();
        }
    }

}
