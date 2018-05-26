package com.example.dao;

import com.example.bean.Person;

public interface PersonDao {

    Person getPerson(int id);

    boolean update(Person person);

}
