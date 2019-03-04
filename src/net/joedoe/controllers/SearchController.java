package net.joedoe.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import net.joedoe.model.Item;

@Named
@RequestScoped
public class SearchController implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(SearchController.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;

    public List<Item> findAll() {
        try {
            TypedQuery<Item> query = em.createNamedQuery("Item.findAll", Item.class);
            return query.getResultList();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error: " + e.getMessage());
        }
        return new ArrayList<Item>();
    }

    public List<Item> getItems() {
        return findAll();
    }
}
