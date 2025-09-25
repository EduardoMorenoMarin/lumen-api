package com.librerialumen.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.librerialumen.api.ActiveTestProfile;
import com.librerialumen.api.domain.model.Category;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
@ActiveTestProfile
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

  @Autowired
  private CategoryRepository categoryRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  @DisplayName("findByActiveTrueOrderByNameAsc returns only active categories sorted by name")
  void findByActiveTrueOrderByNameAsc_shouldReturnSortedActiveCategories() {
    persistCategory("Comics", true);
    persistCategory("Novels", true);
    persistCategory("Technology", false);

    List<Category> categories = categoryRepository.findByActiveTrueOrderByNameAsc();
    List<String> names = categories.stream().map(Category::getName).collect(Collectors.toList());

    assertEquals(List.of("Comics", "Novels"), names);
  }

  private void persistCategory(String name, boolean active) {
    Category category = new Category();
    category.setName(name);
    category.setDescription(name + " category");
    category.setActive(active);
    entityManager.persist(category);
  }
}
