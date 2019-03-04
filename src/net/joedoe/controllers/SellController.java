package net.joedoe.controllers;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
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
import javax.servlet.http.Part;
import javax.transaction.UserTransaction;

import net.joedoe.model.Customer;
import net.joedoe.model.Item;

@Named
@RequestScoped
public class SellController implements Serializable {
    private static final long serialVersionUID = 1L;
    private final static Logger LOGGER = Logger.getLogger(SellController.class.getCanonicalName());

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction ut;

    private Part part;
    @Inject
    private Item item;

    public String persist(SigninController controller) {
        try {
            ut.begin();
            InputStream input = part.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
            item.setFoto(output.toByteArray());
            Customer customer = controller.getCustomer();
            customer = em.find(Customer.class, customer.getId());
            item.setSeller(customer);
            em.persist(item);
            ut.commit();
            LOGGER.info("Speicherung: " + item.toString());
            FacesMessage m = new FacesMessage("Successfully added item: " + item.toString());
            FacesContext.getCurrentInstance().addMessage("sellForm", m);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Speicherungfehler: " + e.getMessage());
            FacesMessage m = new FacesMessage(FacesMessage.SEVERITY_WARN, e.getMessage(), e.getCause().getMessage());
            FacesContext.getCurrentInstance().addMessage("sellForm", m);
        }
        return "sell";
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
}
