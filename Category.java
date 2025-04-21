import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    private String name;
    private Category parent;
    private List<Category> subCategories;
    
    public Category(String name) {
        this.name = name;
        this.subCategories = new ArrayList<>();
    }
    
    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }
    
    public void addSubCategory(Category subCategory) {
        subCategories.add(subCategory);
        subCategory.setParent(this);
    }
    
    public String getName() {
        return name;
    }
    
    public Category getParent() {
        return parent;
    }
    
    public void setParent(Category parent) {
        this.parent = parent;
    }
    
    public List<Category> getSubCategories() {
        return subCategories;
    }
    
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " / " + name;
    }
    
    public List<Category> getAllSubCategories() {
        List<Category> allSubs = new ArrayList<>(subCategories);
        for (Category sub : subCategories) {
            allSubs.addAll(sub.getAllSubCategories());
        }
        return allSubs;
    }
    
    @Override
    public String toString() {
        return getFullPath();
    }
}