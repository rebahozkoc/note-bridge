package ut.twente.notebridge.routes;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import ut.twente.notebridge.dao.BaseUserDao;
import ut.twente.notebridge.dao.PersonDao;
import ut.twente.notebridge.model.BaseUser;
import ut.twente.notebridge.model.Person;
import ut.twente.notebridge.utils.Security;

@Path("/persons")
public class PersonRoute {

    //When person wants to create an account for the first time
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Person createPerson(Person person) {
        System.out.println("PersonRoute.createPerson is called");
        try {

            if(Security.checkPasswordValidity(person.getPassword())){
               throw new BadRequestException("Password is not valid");
            }else{
                BaseUser baseUser = BaseUserDao.INSTANCE.create(person);
                person.setBaseUser(baseUser);
                return PersonDao.INSTANCE.create(person);
            }


        } catch (Exception e) {
            System.out.println("Error while creating user");
            e.printStackTrace();
            throw new BadRequestException("Error while creating user");

        }
    }

    //When user wants to view their account
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Person getPerson(@PathParam("id") int id) {
        return PersonDao.INSTANCE.getUser(id);
    }

    //When person wants to make update on their account
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Person updatePerson(@PathParam("id") String id, Person person) {
        return PersonDao.INSTANCE.update(person);
    }

    @DELETE
    @Path("/{id}")
    public void deletePerson(@PathParam("id") String id) {
        PersonDao.INSTANCE.delete(id);
    }



}
