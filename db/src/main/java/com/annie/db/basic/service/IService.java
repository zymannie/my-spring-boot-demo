package com.annie.db.basic.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface IService<T> {

	List<T> selectAll();

	T selectByKey(Object key);

	List<T> selectByExample(Object example);

	int save(T entity);

	int delete(Object key);

	int batchDelete(List<String> list, String property, Class<T> clazz);

	int updateAll(T entity);

	int updateNotNull(T entity);
}