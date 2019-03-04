package net.joedoe.controllers;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;

import net.joedoe.model.Customer;

@Named
@RequestScoped
public class RegisterController implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(RegisterController.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction ut;

    @Inject
    private Customer customer;

    public String persist() {
        try {
            ut.begin();
            em.persist(customer);
            ut.commit();
            LOGGER.info("Speicherung: " + customer.toString());
            FacesMessage m = new FacesMessage("Successfully registered " + customer.getEmail());
            FacesContext.getCurrentInstance().addMessage("registerForm", m);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Registration failed: " + e.getMessage());
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage("registerForm", m);
        }
        return "register";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
