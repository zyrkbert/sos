package Recursos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import org.apache.naming.NamingContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import Datos.*;
//import clase.recursos.memoria.*;
//import clase.recursos.memoria.Usuario;

//import clase.datos.Garajes;


@Path("/usuario")
public class UsuarioRec {

    @Context
    private UriInfo uriInfo;

    private DataSource ds;
    private Connection conn;

    public UsuarioRec() {
        InitialContext ctx;
        try {
            ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/WineMasters");
            conn = ds.getConnection();
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


   
    


    // Obtener una lista de usuarios filtrados por patron de nombre

    @GET
    
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getUsuarios() {
    	System.out.println("entre");
    	try {
            String sql = "SELECT * FROM Usuario ORDER BY ID_usuario;";
            System.out.println("entre desatao");
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Usuarios u = new Usuarios();
            ArrayList<Link> usuarios = u.getUsuarios();

            while (rs.next()) {
                usuarios.add(new Link(uriInfo.getAbsolutePath()+"/"+rs.getInt("id"),"self"));
                System.out.println("Added user link:"+uriInfo.getAbsolutePath()+"/"+rs.getInt("ID_usuario"));
            }

            return Response.status(Response.Status.OK).entity(u).build();
        } catch (NumberFormatException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números").build();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
        }

    }

    @POST
    
    @Consumes({MediaType.APPLICATION_XML,MediaType.APPLICATION_JSON})
    public Response addUsuario(Usuario usuario) {
        try {
        	String sql = "INSERT INTO Usuario (nombre_usuario, fecha_nacimiento, correo, descripcion) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, usuario.getNombre());
            ps.setDate(2, new java.sql.Date(usuario.getFechaN().getTime()));
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getDescripcion());
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario").build();
            }
            ResultSet generatedID = ps.getGeneratedKeys();
            if (generatedID.next()) {
                usuario.setId(generatedID.getInt(1));
                String location = uriInfo.getAbsolutePath() + "/" + usuario.getId();
                return Response.status(Response.Status.CREATED).entity(usuario)
                        .header("Location", location)
                        .header("Content-Location", location)
                        .build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.getStackTrace()).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{ID_usuario}")
    public Response updateUsuario(@PathParam("ID_usuario") String id, Usuario usuarioNuevo) {
        try {
        	String sql = "SELECT * FROM Usuario WHERE ID_usuario=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            int int_id=Integer.parseInt(id);
            ps.setInt(1, int_id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                sql = "UPDATE Usuario SET nombre_usuario=?, fecha_nacimiento=?, correo=?, descripcion=? WHERE ID_usuario=?";
                ps = conn.prepareStatement(sql);
                ps.setString(1, usuarioNuevo.getNombre());
                ps.setDate(2, new java.sql.Date(usuarioNuevo.getFechaN().getTime()));
                ps.setString(3, usuarioNuevo.getCorreo());
                ps.setString(4, usuarioNuevo.getDescripcion());
                ps.setInt(5, int_id);
                ps.executeUpdate();
                String location = uriInfo.getBaseUri() + "usuario/" + id;
                return Response.status(Response.Status.OK).entity(usuarioNuevo)
                        .header("Content-Location", location)
                        .build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el usuario\n" + e.getStackTrace()).build();
        }
    }

    @DELETE
    @Path("{ID_usuario}")
    public Response deleteUsuario(@PathParam("ID_usuario") String id) {
        try {
        	String sql = "DELETE FROM Usuario WHERE ID_usuario=?";
        	int int_id = Integer.parseInt(id);
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, int_id);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1) {
                return Response.status(Response.Status.NO_CONTENT).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el Usuario \n" + e.getStackTrace()).build();
        }
    }

    // Anadir un usuario a la lista de seguidores de otro
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{ID_usuario}/seguidores")
    public Response addSeguido(@PathParam("ID_usuario") String seguidorId,@PathParam("ID_usuario") String seguidoId) {
        try {
            // Comprobamos que ambos usuarios existen
            if (!existsUsuario(seguidorId)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo "
                    + "realizar la operacion porque el usuario " + seguidorId + " no existe\n").build();
            }
            if (!existsUsuario(seguidoId)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo "
                    + "realizar la operacion porque el usuario " + seguidoId + " no existe\n").build();
            }
            // Realizamos la consulta
            String sql = "INSERT INTO `DB.`.`sigue` (`ID_seguidor`, `ID_seguido`) " + "VALUES ('"
                    + seguidorId + "', '" + seguidoId + "';";
            PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1)
                return Response.status(Response.Status.NO_CONTENT).build();
            else if (affectedRows==0) return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            // Obtener el ID del elemento recién creado.
            ResultSet generatedID = ps.getGeneratedKeys();
            if (generatedID.next()) {
                String location = uriInfo.getAbsolutePath() + "usuario/"+ seguidorId + "/seguidores";
                return Response.status(Response.Status.NO_CONTENT).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo anadir al usuario " + seguidoId + " a la lista de seguidores del usuario " + seguidorId).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el usuario\n" + e.getStackTrace()).build();
        }
    }

    // Elimina un usuario de la lista de seguidores de otro
    @DELETE
    @Path("{ID_usuario}/seguidores")
    public Response deleteSeguido(@PathParam("ID_usuario")
            String seguidorId, @PathParam("ID_usuario") String seguidoId) {
        try {
            // Comprobamos que ambos usuarios existen
            if (!existsUsuario(seguidorId)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo "
                    + "realizar la operacion porque el usuario " + seguidorId + " no existe\n").build();
            }
            if (!existsUsuario(seguidoId)) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo "
                    + "realizar la operacion porque el usuario " + seguidoId + " no existe\n").build();
            }
            // Realizamos la consulta
            String sql = "DELETE FROM `DB`.`sigue` WHERE `ID_seguidor`='"
                + seguidorId + " `AND ID_segudio`=`" + seguidoId + "';";
            PreparedStatement ps = conn.prepareStatement(sql);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el Usuario \n" + e.getStackTrace()).build();
        }
    }

    // Obtener lista de seguidores de un usuario


    //Anadir un vino a la lista de vino de un usuario
    @POST
    @Path("{usuarioId}/vinos/{vinoId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response createVino(Vino vino) {
        try {
            String sql = "INSERT INTO `DB`.`Vino` (`nombre_botella`, `anada_botella`,`tipo_vino`,`bodega`,`denominacion_origen`,`descripcion) " + "VALUES ('"
                    + vino.getNombreBotella() + "', '" + vino.getAnadaBotella() + "', '"+
                    vino.getTipoVino() + "', '"+
                    vino.getBodega() + "', '"+ vino.getDenominacionUOrigen() + "', '" + vino.getDescripcion() + "');";
            PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            int affectedRows = ps.executeUpdate();
            // Obtener el ID del elemento recién creado.
            // Necesita haber indicado Statement.RETURN_GENERATED_KEYS al ejecutar un statement.executeUpdate() o al crear un PreparedStatement
            ResultSet generatedID = ps.getGeneratedKeys();
            if (generatedID.next()) {
                vino.setId(generatedID.getInt(1));
                String location = uriInfo.getAbsolutePath() + ""+ vino.getId();
                return Response.status(Response.Status.CREATED).entity(vino).header("Location", location).header("Content-Location", location).build();
            }
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el garaje").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo crear el vino\n" + e.getStackTrace()).build();
        }
    }

    // Eliminar un vino de la lista de un usuario
    @DELETE
    @Path("{usuarioId}/vinos/{vinoId}")
    public Response deleteGaraje(@PathParam("vino_id") String id) {
        try {
            Vino vino;
            int int_id = Integer.parseInt(id);
            String sql = "DELETE FROM `WineMasters`.`Vino` WHERE `id`='" + int_id + "';";
            PreparedStatement ps = conn.prepareStatement(sql);
            int affectedRows = ps.executeUpdate();
            if (affectedRows == 1)
                return Response.status(Response.Status.NO_CONTENT).build();
            else
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo eliminar el vino\n" + e.getStackTrace()).build();
        }
    }

    @PUT
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{usuarioId}/vinos/{vinoId}")
    public Response updateVino(@PathParam("usuario_id") String idU,@PathParam("vino_id") String id, int nuevaPuntuacion) {
        try {
            Anade anade;
            int int_id = Integer.parseInt(id);
            int int_idU = Integer.parseInt(idU);
            String sql = "SELECT * FROM `WineMasters`.`Anade` where ID_vino=" + int_id +"AND ID_usuario="+int_idU+ ";";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                anade = new Anade(rs.getInt("ID_usuario"),rs.getInt("ID_vino"),rs.getDate("fecha_adicion"),nuevaPuntuacion);
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
            }


            // UPDATE `GarajesyEmpleados`.`Garaje` SET `nombre`='No es Torres', `telefono`='910234567' WHERE `id`='3';

            sql = "UPDATE `WineMasters`.`Anade` SET "
                    + "', `puntuacion`='" + nuevaPuntuacion + "WHERE ID_vino="+anade.getIdVino()+"AND ID_usuario="+anade.getIdUsuario() +"';";
            ps = conn.prepareStatement(sql);
            int affectedRows = ps.executeUpdate();

            // Location a partir del URI base (host + root de la aplicación + ruta del servlet)
            String location = uriInfo.getBaseUri() + idU + "vinos/" + anade.getIdVino();
            return Response.status(Response.Status.OK).entity(anade).header("Content-Location", location).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("No se pudo actualizar el vino\n" + e.getStackTrace()).build();
        }
    }

 
 // Obtener lista de seguidores de un usuario
 	@GET
 	@Produces({MediaType.APPLICATION_JSON})
 	@PathParam("/{ID_usuario}/vinos") 
 	public Response getVinos(
 			/*@PathParam("usuario_id") String seguidorId,
 			@PathParam("seguido_id") String seguidoId,*/
 			@PathParam("seguido_id") String id,
 			@QueryParam("fecha_adicion") String fecha_adicion,
 			@QueryParam("nombre_botella") String nombreBotella,
 			@QueryParam("tipo_vino") @DefaultValue("") String tipoVino,
 			@QueryParam("tipo_uva") @DefaultValue("") String tipoUva,
 			@QueryParam("anada_botella") @DefaultValue("") String anadaBotella,
 			@QueryParam("bodega") @DefaultValue("") String bodega,
 			@QueryParam("limit") @DefaultValue("0") String limit,
 			@QueryParam("offset") @DefaultValue("1") String offset) {
 		/*int int_seguidorId=Integer.parseInt(seguidorId);
 		int int_seguidoId=Integer.parseInt(seguidoId);*/
 		int int_id=Integer.parseInt(id);
 		int int_limit=Integer.parseInt(limit);
 		int int_offset=Integer.parseInt(offset);
 		try {
 			
 			String sql = "SELECT * FROM vino" 
 				+ "WHERE ID_vino=("
 				+ "SELECT (ID_vino)"
 				+ "FROM anade WHERE ID_usuaio='" + int_id + "'"; 
 			if (!fecha_adicion.equals(""))
 				sql+=" AND fehcha_adicion='" + fecha_adicion + "'";
 			sql+="')";
 			int filtersCount=0; // Variable para contar el numero de filtros especificado en los argumentos
 			String filters="";
 			if (!nombreBotella.equals("")) {
 				filters+="nombre_botella='" + nombreBotella + "'"; 
 				filtersCount++;
 			}
 			if (!tipoVino.equals("")) {
 				filters+=filtersCount>0?" AND ":"WHERE " + "tipo_vino='" + tipoVino + "'"; 	
 				filtersCount++;
 			}
 			if (!bodega.equals("")) {
 				filters+=filtersCount>0?" AND ":"WHERE " + "bodega='" + bodega + "'"; 		
 				filtersCount++;
 			}
 			if (!anadaBotella.equals("")) {
 				filters+=filtersCount>0?" AND ":"WHERE " + "anada_botella='" + anadaBotella + "'"; 		
 				filtersCount++;
 			}
 			if (!tipoUva.equals("")) {
 				filters+=filtersCount>0?" AND ":"WHERE " + "anada_botella=(" 
 					+ "SELECT * "
 					+ "FROM Vino v " 
 					+ "INNER JOIN esta_formado e ON v.ID_vino=e.ID_vino" 
 					+ "INNER JOIN tipo_uva t ON e.ID_tipo_uva = t.ID_tipo_uva"
 					+ "WHERE t.nombre_tipo_uva=" + tipoUva + "`)";
 				filtersCount++;
 			}
 			if (filtersCount>0)
 				sql+="\n" + filters;
 			if (int_limit!=0) {
 				if (int_offset>1) 
 					sql+="\nLIMIT '" + int_offset + "','" + int_limit + "')";
 				else sql+="\nLIMIT '" + int_limit + "')";
 			}
 			sql+=";";
 			PreparedStatement ps = conn.prepareStatement(sql);
 			ResultSet rs = ps.executeQuery();
 			Vinos us = new Vinos();
 			ArrayList<Link> vinos = us.getVinos();
 			while (rs.next()) 
 				vinos.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("ID_vino"),"self"));
 			return Response.status(Response.Status.OK).entity(us).build(); // No se puede devolver el ArrayList (para generar XML)
 		} catch (NumberFormatException e) {
 			return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números").build();
 		} catch (SQLException e) {
 			System.out.println(e.getMessage());
 			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
 		}
 	}
     // Obtener una lista de usuarios filtrados por patron de nombre
     @GET
     @Path("usuario_id/seguidores")
     @Produces({MediaType.APPLICATION_JSON})
     public Response getSeguidores(
             @PathParam("ID_usuario") String id,
             @QueryParam("patron") @DefaultValue("") String patron,
             @QueryParam("patron") @DefaultValue("0") String limit,
             @QueryParam("patron") @DefaultValue("1") String offset) {
         int int_id=Integer.parseInt(id);
         int int_limit=Integer.parseInt(limit);
         int int_offset=Integer.parseInt(offset);

         try {
             String sql = "SELECT (nombre_usuario,fecha_nacimiento,correo,descripcion) "
                 + "FROM DB.Usuario WHERE ID_usuario=(" +
                 "SELECT (ID_seguido) FROM DB.sigue "
                 + "WHERE ID_seguidor='" + int_id + "'";
             if (int_limit!=0) {
                 if (int_offset>1)
                     sql+="\nLIMIT '" + int_offset + "','" + int_limit + "')";
                 else sql+="\nLIMIT '" + int_limit + "')";
             } else sql+=")";
             if (!patron.equals(""))
                 sql+="\nWHERE nombre_usuario LIKE '" + patron + "'%";
             sql+="ORDER BY nombre_usuario;";
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();
             Usuarios us = new Usuarios();
             ArrayList<Link> usuarios = us.getUsuarios();
             while (rs.next()) {
                 usuarios.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("ID_usuario"),"self"));
             }
             return Response.status(Response.Status.OK).entity(us).build(); // No se puede devolver el ArrayList (para generar XML)
         } catch (NumberFormatException e) {
             return Response.status(Response.Status.BAD_REQUEST).entity("No se pudieron convertir los índices a números")
                     .build();
         } catch (SQLException e) {
             System.out.println(e.getMessage());
             return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
         }
     }

    //Consultar sistema de recomendacion personalizado
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("{usuarioId}/recomendaciones")
    public Response getRecomendacion(@PathParam("usuario_id") String id) {
    	try {
    		int id_usuario = Integer.parseInt(id);
    		String sql = "SELECT (nombre_usuario,fecha_nacimiento,correo,descripcion)"
    				+ "FROM Usuario"
    				+ "WHERE ID_usuaio='"+ id_usuario 
    				+ "';";
    		PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Usuario usuario;
			if (rs.next()) {
				 usuario = usuarioFromRS(rs);
			}else {
				return Response.status(Response.Status.NOT_FOUND).entity("Elemento no encontrado").build();
			}
			Vinos g = new Vinos();
			ArrayList<Link> vinos = g.getVinos();
			vinos.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
			//Ultimos vinos añadidos a su lista persona
			sql = "SELECT * FROM vino"
					+ "WHERE ID_vino=("
					+ "SELECT (ID_vino)"
					+ "FROM anade\n"
					+ "WHERE ID_usuario="+id_usuario
					+ ")ORDER BY fecha_adicion DESC"
					+ "LIMIT 5;";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			g = new Vinos();
			vinos = g.getVinos();
			
			while (rs.next()) {
				vinos.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
			}
			
			sql = "SELECT * FROM vino\n"
					+ "			WHERE ID_vino=("
					+ "			SELECT (ID_vino)"
					+ "			FROM anade"
					+ "			WHERE ID_usuaio="+id_usuario+")"
					+ "			ORDER BY puntuacion DESC"
					+ "			LIMIT 5;";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				vinos.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
			}
			sql = "SELECT * FROM vino"
					+ "WHERE ID_vino=("
					+ "SELECT (ID_vino)"
					+ "FROM anade"
					+ "WHERE ID_usuaio=("
					+ "SELECT (ID_usuario) "
					+ "FROM anade"
					+ "WHERE id_seguidor="+id_usuario+"))"
					+ "ORDER BY puntuacion DESC"
					+ "LIMIT 5;";
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				vinos.add(new Link(uriInfo.getAbsolutePath() + "/" + rs.getInt("id"),"self"));
			}
			return Response.status(Response.Status.OK).entity(vinos).build();
    	}catch (NumberFormatException e) {
			return Response.status(Response.Status.BAD_REQUEST).entity("No puedo parsear a entero").build();
		} catch (SQLException e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error de acceso a BBDD").build();
		}
    }
    // Metodos auxiliares

    // Saber si un usuario existe
    public boolean existsUsuario(String id) {
    	try {
            String sql = "SELECT * FROM Usuario WHERE ID_usuario=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            int int_id=Integer.parseInt(id);
            ps.setInt(1, int_id);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("{usuarioId}/vinos/{vinoId}")
    private Vino vinoFromRS(ResultSet rs) throws SQLException {
        Vino vino = new Vino(rs.getInt("ID_vino"), rs.getString("nombreBotella"), rs.getString("anadaBotella"),
                rs.getString("tipoVino"),rs.getString("bodega"),rs.getString("denominacionUOrigen"),rs.getString("descripcion"));
        return vino;
    }

    private Usuario usuarioFromRS(ResultSet rs) throws SQLException {
        return new Usuario(
        		rs.getInt("ID_usuario")
        		, rs.getString("nombre")
        		, rs.getDate("fecha_nacimiento")
        		, rs.getString("correo")
        		, rs.getString("descripcion"));
        
    }
}