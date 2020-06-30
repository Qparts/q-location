package q.rest.location.operation;

import q.rest.location.dao.DAO;
import q.rest.location.filter.v3.annotation.UserJwt;
import q.rest.location.model.entity.City;
import q.rest.location.model.entity.Country;
import q.rest.location.model.entity.Region;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.List;


@Path("/internal/api/v3/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LocationInternalApiV3 implements Serializable {

    @EJB
    private DAO dao;


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

}
