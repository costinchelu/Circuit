package ro.ase.costin.ecomcommon.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ro.ase.costin.ecomcommon.data.SettingCategory;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Setting {

    @Id
    @Column(name = "`key`", nullable = false, length = 128)
    private String key;
    // key is a keyword in MySQL and so it has to be escaped by ``

    @Column(nullable = false, length = 1024)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 45)
    private SettingCategory category;

    public Setting(String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Setting other = (Setting) obj;
        if (key == null) {
            return other.key == null;
        } else return key.equals(other.key);
    }

    @Override
    public String toString() {
        return "Setare [cheie=" + key + ", valoare=" + value + "]";
    }
}
