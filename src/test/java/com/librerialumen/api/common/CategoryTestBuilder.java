package com.librerialumen.api.common;

import com.librerialumen.api.domain.model.Category;
import java.util.UUID;

public final class CategoryTestBuilder {

  private UUID id = UUID.randomUUID();
  private String name = "Category";
  private String description = "Category description";
  private boolean active = true;

  private CategoryTestBuilder() {
  }

  public static CategoryTestBuilder aCategory() {
    return new CategoryTestBuilder();
  }

  public CategoryTestBuilder withId(UUID id) {
    this.id = id;
    return this;
  }

  public CategoryTestBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public CategoryTestBuilder withDescription(String description) {
    this.description = description;
    return this;
  }

  public CategoryTestBuilder asInactive() {
    this.active = false;
    return this;
  }

  public Category build() {
    Category category = new Category();
    category.setId(id);
    category.setName(name);
    category.setDescription(description);
    category.setActive(active);
    return category;
  }
}

