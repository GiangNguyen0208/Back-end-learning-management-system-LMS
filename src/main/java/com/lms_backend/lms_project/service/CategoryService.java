package com.lms_backend.lms_project.service;

import com.lms_backend.lms_project.entity.Category;

import java.util.List;

public interface CategoryService {
    Category addCategory(Category category);

    Category updateCategory(Category category);

    Category getCategoryById(int category);

    List<Category> getCategoriesByStatusIn(List<String> status);

}
