package com.TechShop.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    private Producto producto;
    private int cantidad;
    private BigDecimal precioHistorico;

    public Item() {
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioHistorico() {
        return precioHistorico;
    }

    public void setPrecioHistorico(BigDecimal precioHistorico) {
        this.precioHistorico = precioHistorico;
    }

    public BigDecimal getSubTotal() {
        return precioHistorico.multiply(new BigDecimal(cantidad));
    }
}
