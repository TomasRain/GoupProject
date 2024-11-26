package com.example.demo.dto;

public class CategoryDTO {
    private Long id;
    private String name;
    private String description;

    // 默认构造器
    public CategoryDTO() {}

    // 参数化构造器
    public CategoryDTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Getter 和 Setter 方法

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
 
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
 
    public void setDescription(String description) {
        this.description = description;
    }
}
