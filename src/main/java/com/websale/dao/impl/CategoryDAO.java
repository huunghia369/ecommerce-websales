package com.websale.dao.impl;

import org.springframework.stereotype.Repository;

import com.websale.dao.ICategoryDAO;
import com.websale.entity.Category;


@Repository
public class CategoryDAO extends GeneraDAO<Category, Integer> implements ICategoryDAO {

}
