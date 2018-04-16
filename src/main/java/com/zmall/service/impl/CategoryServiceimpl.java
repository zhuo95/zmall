package com.zmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zmall.common.ServerResponse;
import com.zmall.dao.CategoryMapper;
import com.zmall.pojo.Category;
import com.zmall.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceimpl implements ICategoryService {

    private Logger logger = LoggerFactory.getLogger(CategoryServiceimpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse addCategory(String categoryName, Integer parentId){
        if(parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.creatByErrorMessage("Add category arguments error");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//分类可用

        int rowCount = categoryMapper.insert(category);
        if(rowCount>0){
            return ServerResponse.creatBySuccessMessage("Add category success");
        }
        return ServerResponse.creatByErrorMessage("Add category fail");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if(categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.creatByErrorMessage("Update category arguments error");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(rowCount>0){
            return ServerResponse.creatBySuccessMessage("Upadate success");
        }else {
            return ServerResponse.creatByErrorMessage("Update fail");
        }
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
        if(CollectionUtils.isEmpty(categoryList)){
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.creatBySuccess(categoryList);
    }

    //递归查询本节点id和孩子节点id
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category c:categorySet){
                categoryIdList.add(c.getId());
            }
        }
        return ServerResponse.creatBySuccess(categoryIdList);
    }

    //递归
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点
        List<Category> categoryList = categoryMapper.selectChildrenByParentId(categoryId);
        for(Category c : categoryList){
            findChildCategory(categorySet,c.getId());
        }
        return categorySet;
    }
}
