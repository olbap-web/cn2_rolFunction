package com.function.graphql;

import com.function.dao.RolDAO;
import com.function.model.Rol;
import com.function.model.Usuario;

import graphql.schema.DataFetcher;

import java.util.List;

public class RolDataFetcher {

    public static DataFetcher<Rol> obtenerRol() {
        return env -> {
            int id = env.getArgument("id");
            return RolDAO.obtenerRol(id);
        };
    }

    public static DataFetcher<List<Rol>> listarRoles() {
        return env -> RolDAO.listarRoles();
    }

    public static DataFetcher<List<Usuario>> usuariosByRol() {
        return env -> {
            int id = env.getArgument("id");
            return RolDAO.getUsuariosByRol(id);
        };
    }

    public static DataFetcher<Rol> crearRol() {
        return env -> {
            return RolDAO.crearRol(new Rol(
                env.getArgument("id"),
                env.getArgument("descripcion"),
                env.getArgument("estado")
            ));
        };
    }

    public static DataFetcher<Rol> actualizarRol() {
        return env -> {
            return RolDAO.actualizarRol(new Rol(
                env.getArgument("id"),
                env.getArgument("descripcion"),
                env.getArgument("estado")
            ));
        };
    }

    public static DataFetcher<Boolean> eliminarRol() {
        return env -> {
            int id = env.getArgument("id");
            return RolDAO.eliminarRol(id);
        };
    }
}
