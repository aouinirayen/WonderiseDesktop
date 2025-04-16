package tn.esprit.interfaces;

import tn.esprit.models.Experience;

import java.util.List;

public interface IService<E> {

    //CRUD
    //1
    public void add(Experience experience);
    //2
    public void update(Experience experience);
    //3
    void delete(Experience experience);
    //4
    List<Experience> getAll();
    //5: one
    Experience getOne(int id);
    //by criteria

}
