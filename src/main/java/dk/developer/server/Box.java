package dk.developer.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Box {
    private final String status;
    private final Object entity;

    @JsonCreator
    Box(@JsonProperty("status") String status, @JsonProperty("content") Object entity) {
        this.status = status;
        this.entity = entity;
    }

    public String getStatus() {
        return status;
    }

    public Object getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        Box box = (Box) o;
        return Objects.equals(status, box.status) &&
                Objects.equals(entity, box.entity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, entity);
    }
}
