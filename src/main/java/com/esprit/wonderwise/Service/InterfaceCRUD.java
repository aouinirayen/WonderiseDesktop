package com.esprit.wonderwise.Service;

import java.util.List;

public interface InterfaceCRUD<T>{
    public void add(T t);
    public void update(T t);
    public boolean delete(T t);
    public List<T> find();
}
