package ro.ase.costin.ecomcommon.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "roles", schema = "ecomdb")
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Role extends IdBasedEntity {

    @Column(length = 40, nullable = false, unique = true)
    private String name;

    @Column(length = 150, nullable = false)
    private String description;

    public Role(Integer id) {
        this.id = id;
    }

    public Role(String name) {
        this.name = name;
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getProcessedName() {
        if (name.equals("Editor")) return "Operator";
        if (name.equals("Sales")) return "Vânzări";
        if (name.equals("Shipper")) return "Logistică";
        if (name.equals("Assistant")) return "Asistent";
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
