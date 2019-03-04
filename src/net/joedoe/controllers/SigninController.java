package net.joedoe.controllers;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;

import net.joedoe.model.Customer;

@Named
@SessionScoped
public class SigninController implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(SigninController.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction ut;
    @Inject
    private Customer customer;
    private String email;
    private String password;

    public String find() {
        try {
            TypedQuery<Customer> query = em.createQuery(
                    "select c from Customer c where c.email=:email and c.password=:password", Customer.class);
            query.setParameter("email", getEmail());
            query.setParameter("password", getPassword());
            List<Customer> list = query.getResultList();
            if (list != null && list.size() > 0) {
                customer = list.get(0);
                LOGGER.info("SignIn erfolgt: " + customer.toString());
                FacesMessage m = new FacesMessage("Successfully signed in: " + customer.getEmail());
                FacesContext.getCurrentInstance().addMessage("signinForm", m);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Speicherungfehler: " + e.getMessage());
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage("signinForm", m);
        }
        return "signin";
    }
    
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
