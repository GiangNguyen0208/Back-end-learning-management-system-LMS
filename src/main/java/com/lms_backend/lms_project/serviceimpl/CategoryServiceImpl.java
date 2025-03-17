package com.lms_backend.lms_project.serviceimpl;


import java.util.List;
import java.util.Optional;

import com.lms_backend.lms_project.dao.CategoryDao;
import com.lms_backend.lms_project.entity.Category;
import com.lms_backend.lms_project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Category addCategory(Category category) {
        return this.categoryDao.save(category);
    }

    @Override
    public Category updateCategory(Category category) {
        return this.categoryDao.save(category);
    }

    @Override
    public Category getCategoryById(int categoryId) {

        Optional<Category> optionalCategory = this.categoryDao.findById(categoryId);

        if (optionalCategory.isPresent()) {
            return optionalCategory.get();
        } else {
            return null;
        }

    }

    @Override
    public List<Category> getCategoriesByStatusIn(List<String> status) {
        return this.categoryDao.findByStatusIn(status);
    }

}

