package ro.ase.costin.ecomcommon.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
public class Category extends IdBasedEntity {

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 64, nullable = false, unique = true)
    private String alias;

    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;

    @Column(name = "all_parent_ids", length = 256, nullable = true)
    private String allParentIDs;

    @OneToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    @OrderBy("name asc")
    private Set<Category> children = new HashSet<>();

    @Transient
    private boolean hasChildren;

    public Category(Integer id) {
        this.id = id;
    }

    public Category(Integer id, String name, String alias) {
        this.id = id;
        this.name = name;
        this.alias = alias;
    }

    public Category(String name) {
        this.name = name;
        this.alias = name;
        this.image = "default.png";
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    public static Category categoryFactory (Category category) {
        Category newCategory = new Category();
        newCategory.setId(category.getId());
        newCategory.setName(category.getName());
        return newCategory;
    }

    public static Category categoryFactory (Integer id, String name) {
        Category newCategory = new Category();
        newCategory.setId(id);
        newCategory.setName(name);
        return newCategory;
    }

    public static Category prototypeCategory(Category category) {
        Category prototypeCategory = categoryFactory(category);
        prototypeCategory.setAlias(category.getAlias());
        prototypeCategory.setImage(category.getImage());
        prototypeCategory.setEnabled(category.isEnabled());
        prototypeCategory.setHasChildren(category.getChildren().size() > 0);
        return prototypeCategory;
    }

    public static Category prototypeCategory(Category category, String name) {
        Category prototypeCategory = prototypeCategory(category);
        prototypeCategory.setName(name);
        return prototypeCategory;
    }

    @Transient
    public String getImagePath() {
        if (id == null || image == null || image.trim().isEmpty()) {
            return "/images/image_thumbnail.png";
        }
        return "/category-images/" + id + "/" + image;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
