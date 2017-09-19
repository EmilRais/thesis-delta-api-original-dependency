package dk.developer.database;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@Collection(FakeDatabaseObject.COLLECTION)
@Entity
@Table(name = FakeDatabaseObject.COLLECTION)
@JsonAutoDetect(getterVisibility = NONE)
public class FakeDatabaseObject extends DatabaseObject {
    static final String COLLECTION = "People";

    @Id
    @Column(name = "_id")
    @JsonProperty("_id")
    private String _id;

    @Column(name = "name")
    private String name;

    /* Hibernate */
    public FakeDatabaseObject() {
    }

    @JsonCreator
    public FakeDatabaseObject(@JsonProperty("name") String name) {
        this.name = name;
        this._id = ObjectId.get().toString();
    }

    public FakeDatabaseObject(String id, String name) {
        this._id = id;
        this.name = name;
    }

    @Override
    @JsonProperty("_id")
    public String getId() {
        return _id;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;
        FakeDatabaseObject that = (FakeDatabaseObject) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name);
    }

    @Override
    public String toString() {
        return "FakeDatabaseObject{" +
                "_id=" + _id +
                ", name='" + name + '\'' +
                '}';
    }
}
