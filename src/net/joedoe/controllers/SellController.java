package net.joedoe.controllers;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
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
    public final static int MAX_IMAGE_LENGTH = 400;

    @PersistenceContext
    private EntityManager em;
    @Resource
    private UserTransaction ut;
    @Inject
    private Item item;
    private Part part;

    public String persist(SigninController controller) {
        try {
            ut.begin();
            InputStream input = part.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
            item.setFoto(scale(output.toByteArray()));
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

    public byte[] scale(byte[] foto) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(foto);
        BufferedImage originalBufferedImage = ImageIO.read(byteArrayInputStream);
        double originalWidth = (double) originalBufferedImage.getWidth();
        double originalHeight = (double) originalBufferedImage.getHeight();
        double relevantLength = originalWidth > originalHeight ? originalWidth : originalHeight;
        double transformationScale = MAX_IMAGE_LENGTH / relevantLength;
        int width = (int) Math.round(transformationScale * originalWidth);
        int height = (int) Math.round(transformationScale * originalHeight);
        BufferedImage resizedBufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resizedBufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        AffineTransform affineTransform = AffineTransform.getScaleInstance(transformationScale, transformationScale);
        g2d.drawRenderedImage(originalBufferedImage, affineTransform);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedBufferedImage, "PNG", baos);
        return baos.toByteArray();
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
