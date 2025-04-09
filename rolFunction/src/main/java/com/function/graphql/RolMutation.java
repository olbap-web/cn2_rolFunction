package com.function.graphql;

import com.function.dao.RolDAO;
import com.function.model.Rol;
import graphql.schema.DataFetcher;

public class RolMutation {

    public DataFetcher<Rol> crearRol() {
        return env -> {
            return RolDAO.crearRol(new Rol(
                env.getArgument("id"),
                env.getArgument("descripcion"),
                env.getArgument("estado")
            ));
        };
    }

    public DataFetcher<Rol> actualizarRol() {
        return env -> {
            return RolDAO.actualizarRol(new Rol(
                env.getArgument("id"),
                env.getArgument("descripcion"),
                env.getArgument("estado")
            ));
        };
    }

    public DataFetcher<Boolean> eliminarRol() {
        return env -> {
            int id = env.getArgument("id");
            return RolDAO.eliminarRol(id);
        };
    }
}
