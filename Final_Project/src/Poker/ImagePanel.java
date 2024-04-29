package Poker;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    private Image img;
    
    public ImagePanel(String img) {
        this(new ImageIcon(img).getImage());
    }
    
    public ImagePanel(Image img) {
        setImage(img);
    }
    
    public void setImage(Image img) {
        this.img = img;
        Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        revalidate(); // Notification that the Layout Manager component has changed size
        repaint(); // Request a repaint of the component
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Drawing panel backgrounds
        if (img != null) {
            g.drawImage(img, 0, 0, getWidth(), getHeight(), this); // Make sure the image fills the panel
        }
    }
    
    public void updateImage(Image newImg) {
        setImage(newImg);
    }
}
