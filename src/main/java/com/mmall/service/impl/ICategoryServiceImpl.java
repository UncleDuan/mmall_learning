package com.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mmall.common.ServerResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.pojo.Category;
import com.mmall.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by ionolab-DP on 2018/12/20.
 */
@Service("iCategoryService")
public class ICategoryServiceImpl implements ICategoryService{



    @Autowired
    private CategoryMapper categoryMapper;


    private org.slf4j.Logger logger= LoggerFactory.getLogger(ICategoryServiceImpl.class);

    @Override
    public ServerResponse addCategory(String categoryName, Integer parentId){

        if (parentId==null|| StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("Add category failed, parameters error");
        }
        Category category=new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        //分类可用
        category.setStatus(true);

        int rowCount=categoryMapper.insert(category);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("Add category successfully");
        }
        return ServerResponse.createByErrorMessage("Add category failed, database denied access");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){

        if (categoryId==null||StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("Update category name failed, parameters error");
        }

        Category category=new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int rowCount=categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            return ServerResponse.createBySuccess("Update category name successfully");
        }
        return ServerResponse.createByErrorMessage("Update category name failed,database denied access");
    }


    public ServerResponse<List<Category>> selectCategoryChildrenByParentId(Integer categoryId){

        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            logger.info("Children category not found for current category");
        }
        return ServerResponse.createBySuccess(categoryList);
    }


//    递归查询本节点的id和子节点的id
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){

        Set<Category> categorySet= Sets.newHashSet();
        findChildCategory(categorySet,categoryId);

        List<Integer> categoryIdList= Lists.newArrayList();
        if (categoryId!=null){
            for (Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }

        return ServerResponse.createBySuccess(categoryIdList);

    }

    private Set<Category> findChildCategory(Set<Category> categorySet, Integer categoryId){
        Category category =categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categorySet.add(category);
        }
        List<Category> categoryList=categoryMapper.selectCategoryChildrenByParentId(categoryId);
        for (Category categoryItem:categoryList){
            findChildCategory(categorySet,categoryItem.getId());
        }
        return categorySet;
    }
}
