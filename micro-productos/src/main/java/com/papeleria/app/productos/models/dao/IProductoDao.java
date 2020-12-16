package com.papeleria.app.productos.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.papeleria.app.commons.models.entity.Producto;

public interface IProductoDao extends CrudRepository<Producto, Long>{

}
