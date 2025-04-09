package com.function.graphql;

import com.function.dao.RolDAO;
import com.function.model.Rol;
import com.function.model.Usuario;

import graphql.schema.DataFetcher;
import java.util.List;

public class RolQuery {

    public DataFetcher<Rol> obtenerRol() {
        return dataFetchingEnvironment -> {
            int id = dataFetchingEnvironment.getArgument("id");
            return RolDAO.obtenerRol(id);
        };
    }

    public DataFetcher<List<Rol>> listarRoles() {
        return dataFetchingEnvironment -> RolDAO.listarRoles();
    }
    
    public DataFetcher<List<Usuario>> getUsuariosByRol(){
        return dataFetchingEnvironment -> {
            
            int id = dataFetchingEnvironment.getArgument("id");
            
            return RolDAO.getUsuariosByRol(id);

        };
    }
}

